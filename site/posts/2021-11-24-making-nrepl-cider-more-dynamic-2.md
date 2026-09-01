---
title: "Making nREPL and CIDER More Dynamic (part 2)"
slug: "making-nrepl-cider-more-dynamic-2"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2021-11-24T10:19:18.000Z"
updated_at: "2026-05-16T13:41:39.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-15 08:16"]
---

By Arne Brasseur

In [part 1](https://lambdaisland.com/blog/2021-11-03-making-nrepl-cider-more-dynamic-1) I set the stage with a summary of what nREPL is and how it works, how editor-specific tooling like CIDER for Emacs extends nREPL through middleware, and how that can cause issues and pose challenges for users. Today we’ll finally get to the “dynamic” part, and how it can help solve some of these issues.

To sum up again what we are dealing with: depending on the particulars of the nREPL client (i.e. the specific editor you are using, or the presence of specific tooling like `refactor-clj`), or of the project (shadow-cljs vs vanilla cljs), certain nREPL middleware needs to present for things to function as they should. When starting the nREPL server you typically supply it with a list of middlewares to use. This is what plug-and-play “jack-in” commands do behind the scenes. For nREPL to be able to load and use those middlewares they need to be present on the classpath, in other words, they need to be declared as dependencies. This is the second part that jack-in takes care of.

This means that nREPL servers are actually specialized to work with specific clients, which is a little silly if you think about it. You can’t connect with vim-iced to a server that expects CIDER clients, or at least not without reduced functionality.

Instead what we want is for the nREPL server to be client-agnostic. Once a client connects it can then tell the server of its needs, and “upgrade” the connection appropriately. It can even upgrade the connection incrementally, loading support for extra operations when it first needs them.

Let’s unpack what is needed to make this a reality, we need to be able to

  * add extra middleware to a running server or connection
  * add entries to the classpath at runtime
  * resolve and download (transitive) dependencies



#### Add Middleware to a Running Server

Turns out this problem has already been solved! Yay! Over a year ago Shen Tian implemented a `dynamic-loader` middleware (see [nrepl/nrepl#185](https://github.com/nrepl/nrepl/pull/185)), which provides an `add-middleware` operation.
    
    
    (-->
      id         "23"
      op         "add-middleware"
      session    "33d052f6-04dd-4f0e-916f-ed94aa0188ec"
      time-stamp "2021-11-19 09:40:42.092185482"
      middleware ("cider.nrepl/wrap-apropos" "cider.nrepl/wrap-classpath" "cider.nrepl/wrap-clojuredocs" "cider.nrepl/wrap-complete" "cider.nrepl/wrap-content-type" "cider.nrepl/wrap-debug" "cider.nrepl/wrap-enlighten" "cider.nrepl/wrap-format" "cider.nrepl/wrap-info" "cider.nrepl/wrap-inspect" "cider.nrepl/wrap-macroexpand" "cider.nrepl/wrap-ns" "cider.nrepl/wrap-out" "cider.nrepl/wrap-slurp" "cider.nrepl/wrap-profile" "cider.nrepl/wrap-refresh" "cider.nrepl/wrap-resource" "cider.nrepl/wrap-spec" "cider.nrepl/wrap-stacktrace" "cider.nrepl/wrap-test" "cider.nrepl/wrap-trace" "cider.nrepl/wrap-tracker" "cider.nrepl/wrap-undef" "cider.nrepl/wrap-version" "cider.nrepl/wrap-xref")
    )
    (<--
      id         "23"
      session    "33d052f6-04dd-4f0e-916f-ed94aa0188ec"
      time-stamp "2021-11-19 09:40:42.958165047"
      status     ("done")
    )
    

If you are a CIDER user and are fairly up-to-date (this may require using `master`) you can try this out today.
    
    
    (add-hook 'cider-connected-hook #'cider-add-cider-nrepl-middlewares)
    

Install this hook, then connect to a vanilla nREPL server. You can run one in your project with:
    
    
    clojure -Sdeps '{:deps {nrepl/nrepl {:mvn/version "RELEASE"}}}' -M: -m nrepl.cmdline
    

However, chances are you’ll see something like this in the cider-repl buffer:
    
    
    WARNING: middleware cider.nrepl/wrap-trace was not found or failed to load.
    WARNING: middleware cider.nrepl/wrap-macroexpand was not found or failed to load.
    WARNING: middleware cider.nrepl/wrap-inspect was not found or failed to load.
    ...
    

That’s because the `dynamic-loader` middleware tries to `(require 'cider.nrepl)`, and fails. We need to first get `cider-nrepl` onto the classpath.

#### Adding Entries to the Classpath at Runtime

I have written at great length recently about the classpath and classloaders, (see [The Classpath is a Lie](https://lambdaisland.com/blog/2021-08-25-classpath-is-a-lie)). Simply adding entries to the classpath is fairly easy, Clojure’s `DynamicClassLoader`has a public `addURL` method. [dynapath](https://github.com/tobias/dynapath)provides an abstraction around this. You need a few lines of code to check if you have the right kind of classloader, and if not instantiate a new one, and you’re good.

The harder part is controlling which classloader is in use at the point where `require` gets called. Typically this is the “context classloader”, which is a _mutable thread local variable_. As if mutability alone wasn’t tricky enough. Inside an nREPL request you’re generally covered, since nREPL creates a `DynamicClassLoader` for you for each session. It used to be a little too eager about creating new classloaders, which I addressed in [nrepl/nrepl#248](https://github.com/nrepl/nrepl/pull/248). However there is still the issue mentioned in _The Classpath is a Lie_ , which is that `clojure.main` creates a new `DynamicClassLoader` for each call to `repl`, which in nREPL means on every eval. We do some recursing to find the `DynamicClassLoader` which sits directly above the system classloader, and use that. This tends to give fairly predictable results. There has been talk of forking `clojure.main/repl` for nREPL’s use, which would allow us to get rid of this annoying behavior, which would help simplify things.

To try this at home first find the location of the `cider-nrepl` JAR. This is a fat jar, it includes all its dependencies inlined and shaded with [MrAnderson](https://github.com/benedekfazekas/mranderson), so we don’t need to worry about resolving transitive dependencies.
    
    
    find ~/.m2 -name 'cider-nrepl-*.jar'
    

Now we end up with something like this.
    
    
    ;; remove the previous one if necessary
    (pop 'cider-connected-hook)
    
    ;; install the new hook
    (add-hook 'cider-connected-hook
              (lambda ()
                (cider-sync-tooling-eval
                 (parseedn-print-str
                  '(.addURL (loop ; shenanigans to find the "root" DCL
                             [loader (.getContextClassLoader (Thread/currentThread))]
                             (let [parent (.getParent loader)]
                               (if (instance? clojure.lang.DynamicClassLoader parent)
                                   (recur parent)
                                 loader)))
                            (java.net.URL. "file:/home/arne/.m2/repository/cider/cider-nrepl/0.27.2/cider-nrepl-0.27.2.jar"))))
                (cider-add-cider-nrepl-middlewares)))
    

And there you go, you’ve successfully turned a vanilla nREPL connection into a cider-nrepl connection. You can now make full use of CIDER’s capabilities!

#### Resolving Dependencies

The previous solution assumes that you already have `cider-nrepl*.jar` on your system, that you know where to find it, that it matches the CIDER version Emacs is using (or at least is compatible, they no longer need to match exactly), and that it doesn’t need any additional dependencies.

A more generic solution would allow you to simply provide dependency coordinates, the type you supply in `deps.edn` or `project.clj`, and let Clojure figure out and download what it needs. Something like this:
    
    
    (add-hook 'cider-connected-hook
              (lambda ()
                (cider-sync-tooling-eval
                 (parseedn-print-str
                  `(update-classpath! ((cider/cider-nrepl . ,cider-injected-nrepl-version)))))
                (cider-add-cider-nrepl-middlewares)))
    

This is called “dependency resolution”. It means taking a set of artifact-name+version coordinates, trying to find the given versions in one or more repositories (like Clojars or Maven Central), downloading their `.pom`files to figure out any transitive dependencies (recursively), and finally downloading the actual jars.

This requires a good deal of machinery, machinery that is not present in every Clojure process out of the box. You could start your nREPL process with [tools.deps.alpha](https://github.com/clojure/tools.deps.alpha) as a dependency, or lean on other libraries that are lower-level (org.apache.maven, Aether), or higher level (lambdaisland/classpath, Pomegranate). In any case we need these to be declared and loaded at boot time, if they are not present than we have a chicken-and-egg problem, our connection upgrade is once again blocked.

It’s also worth pointing out that this is by far the most complicated part of this whole endeavor. Adding tools.deps.alpha adds about 11MB of dependencies. Maybe that’s fine, many apps will pull in hundreds of megabytes of dependencies, what’s a dozen more? Still, people often have good reasons to keep their dependencies to a minimum, so this is not a decision that nREPL can make for them.

But we can sidestep the issue, in the case of CIDER we only need to download a single jar. We can just do that and be done with it. In fact, CIDER already contains code to do just that:
    
    
    (add-hook 'cider-connected-hook
              (lambda ()
                (cider-sync-tooling-eval
                 (parseedn-print-str
                  `(.addURL (loop
                             [loader (.getContextClassLoader (Thread/currentThread))]
                             (let [parent (.getParent loader)]
                               (if (instance? clojure.lang.DynamicClassLoader parent)
                                   (recur parent)
                                 loader)))
                            (java.net.URL. ,(concat "file:" (cider-jar-find-or-fetch "cider" "cider-nrepl" cider-injected-nrepl-version))))))
                (cider-add-cider-nrepl-middlewares)))
    

Alternatively we could shell out to a tool that can do this work for us. We actually have a lot of choice there at this point. There’s of course `clojure`(i.e. the Clojure CLI), but Babashka can do the same thing (`bb clojure -Sdeps {} -Spath`), and there’s [deps.clj](https://github.com/borkdude/deps.clj), available as standalone binaries, or as an Uberjar, which could be invoked in a separate process/JVM, or loaded onto the classpath and invoked from Clojure directly.

It would be neat to wrap this in a little library which looks for one of these executables in some default places, and falls back to downloading `deps.clj`. This way you could get the functionality of a 11MB dependency for perhaps a hundred lines of Clojure, although this may seem like an unsavory approach to some.

It’s probably clear by now that there’s more than one way to shear a sheep, and you may be wondering why we don’t just go and hide all these details behind a facade that _Just Works_ ™. To an extent that will probably happen, these are early days and we are still figuring out how to best fit these pieces together. But we’re also likely to find that there’s no one size that fits all, whether it’s all tools that build on nREPL, or all users of those tools.

In an upcoming blog post I’ll be talking a lot more about _Mechanisms_ vs _Policies_. I think at this point it’s ok to focus on the mechanisms, make sure we have the individual ingredients, and let people experiment with combining them in the way that makes most sense to them and their project.

Speaking of ingredients, there’s one more piece we haven’t covered yet, the Sideloader!

#### The Role of the Sideloader

Besides implementing the `dynamic-loader` middleware, Shen Tian also implemented another nREPL piece, meant to complement it, the Sideloader. Inspired by a similar mechanism in [unrepl](https://github.com/Unrepl/unrepl), the Sideloader is a special kind of ClassLoader which requests the resources it needs from a connected nREPL client.

The way this works is that the client first installs the sideloader by invoking the `sideloader-start` op. Now every time you `require` a namespace, access an `io/resource`, or load a Java class, the nREPL client (i.e. your editor) gets a `sideloader-lookup` message. If it is able to provide the requested resource, then it responds by sending a `sideloader-provide` message back.

The idea is that instead of adding cider-nrepl to the classpath directly, we let CIDER (Emacs) supply each namespace or resource to nREPL on demand, over the network.

I’ve put considerable effort into the client side implementation of this, see [clojure-emacs/cider#3037](https://github.com/clojure-emacs/cider/issues/3037), and the half a dozen PRs linked from there. This is why the aforementioned `cider-jar-find-or-fetch` exists, we download the `cider-nrepl` JAR from Emacs, so that we can supply its contents piecemeal to the Clojure process.

You can try this out as well:
    
    
    (add-hook 'cider-connected-hook #'cider-upgrade-nrepl-connection)
    

This will enable the sideloader, and inject the necessary middleware as before.

So far I find the results rather underwhelming. Doubly so when connecting to an nREPL server on a different machine, which is the use case where this approach would actually make the most sense. Round-tripping over the network, and extracting file contents from the JAR from Emacs Lisp, then base64 encoding and decoding them to go over the wire… it all adds a lot of overhead. Note that this impacts **all** classloader lookups, since we only fall back to the system classloader when the Sideloader has determined that CIDER is unable to supply the given resource. It’s also worth nothing that for every `.clj` file that needs to be loaded this way, we round-trip three times: once to look for an `__init.class` file, once for a `.cljc`, and only then does Clojure look for the `.clj` file.

There are two obvious ways to improve this, one is to only have the sideloader active for a limited amount of time. You activate it, inject the necessary middleware, and turn it back off. Currently this does not work because of cider-nrepl’s deferred middleware. Most middleware only gets `require`d the first time it is actually used, at which point the sideloader has long been disabled.

What I’ve also experimented with is providing a list of prefixes, so that only cider-nrepl’s namespaces are fetched via the sideloader, it helps of course, but the results are still underwhelming.

So as it stands I’m not convinced the sideloader is going to be an important piece in making this dynamic upgrading of nREPL connections a reality. I think the approaches I’ve set out above, where we make sure any resources required are present on the classpath directly, are much to be preferred. Faster and more reliable.

I do however think the Sideloader could become a cool piece of kit for loading files from the user’s code base, especially when connected to a remote process.

I run a Minecraft server on a cloud instance, and use [witchcraft-plugin](https://github.com/lambdaisland/witchcraft-plugin) to endow Minecraft with Clojure superpowers, including an nREPL server. Locally I have my [cauldron](https://github.com/plexus/cauldron) repo where I do my Minecraft creative coding. It’s a collection of repl sessions, and of namespaces with utility functions. When I eval `(require 'net.arnebrasseur.cauldron.structures)`then that currently fails, because this Cauldron repo isn’t present on the server. I need to go into each namespace I need and manually eval them in topological order before I can use them. Not great. In this case I think it would be fantastic if CIDER could spoon feed the server the necessary namespaces on request.

#### Conclusion

With this post I hope to draw some attention to all the work that’s been happening. We’ve been laying the groundwork for really improving the user experience, now we need to figure out how to bring it all together in a way that makes sense.

There’s a risk though that pushing for these changes will initially negatively impact the user experience, because change is hard, and we can’t anticipate everyone’s use case and needs.

So I expect “jack-in” to stay around, and to remain the default recommendation. It’s not perfect, but it works well for the vast majority of users.

At the same time we want to invite power users and tooling authors, especially those that have experienced the limitations and frustrations that come with the current approach, to consider these alternatives. To try them out and report back, so that we can shave off the rough edges, abstract away some of the plumbing, and gradually make this ready for broad consumption.
