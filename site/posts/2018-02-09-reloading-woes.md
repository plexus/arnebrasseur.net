---
title: "Reloading Woes"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2018-02-09T07:48:13.000Z"
updated_at: "2026-05-16T13:42:06.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-09 12:23"]
---

_Update: seems Stuart Sierra’s blog post has dropped off the internet. I’ve updated the link to refer to the Wayback Machine’s version instead._

## Setting the Stage

When doing client work I put a lot of emphasis on tooling and workflow. By coaching people on their workflow, and by making sure the tooling is there to support it, a team can become many times more effective and productive.

An important part of that is having a good story for code reloading. Real world projects tend to have many dependencies and a large amount of code, making them slow to boot up, so we want to avoid having to restart the process.

Restarting the process is done when things get into a bad state. This can be application state (where did my database connection go?), or Clojure state (I’m redefining this middleware function, but the webserver isn’t picking it up.)

### tools.namespace and component/integrant

We can avoid the cataclysm of a restart if we can find some other way to get to a clean slate. To refresh Clojure’s view of the world, there’s [clojure.tools.namespace](https://github.com/clojure/tools.namespace), which is able to _unload_ and then _reload_ namespaces.

To recreate the app state from scratch there’s the “reloaded workflow” as [first described by Stuart Sierra](https://web.archive.org/web/20160315031517/http://thinkrelevance.com/blog/2013/06/04/clojure-workflow-reloaded), who created [Component](https://github.com/stuartsierra/component) for this reason.

Combining these two allows you to tear down the app state, reload all your Clojure code, then recreate the app state, this time based on the freshly loaded code. Combining them like this is important because when you use tools.namespace to reload your code, it effectively creates completely new and separate versions of functions, records, multimethods, vars, even namespace objects, and any state that is left around from before the reload might still be referencing the old ones, making matters even worse than before.

So you need to combine the two, which (naively) would look something like this.
    
    
    (ns user
      (:require [com.stuartsierra.component :as component]
                [clojure.tools.namespace.repl :as c.t.n.r]))
    
    (def app-state nil)
    
    (defn new-system []
      (component/system-map ,,,))
    
    (defn go []
      (alter-var-root! app-state (fn [_]
                                   (-> (new-system)
                                       component/start))))
    
    (defn reset
      "Stop the app, reload all changed namespaces, start the app again."
      []
      (component/stop app-state)
    
      ;; Clojure tools namespace takes a symbol, rather than a function, so that it
      ;; can resolve the function *after code reloading*. That way it's sure to get
      ;; the latest version.
      (c.t.n.r/refresh :after 'user/go))
    

This pattern is encoded in [reloaded.repl](https://github.com/weavejester/reloaded.repl); [System](https://github.com/danielsz/system) also contains an implementation. I recommend using an existing implementation over rolling your own, as there are some details to get right. It’s also interesting to note that reloaded.repl uses [suspendable](https://github.com/weavejester/suspendable), an extension to Component that adds a suspend/resume operation.

To dig deeper into this topic, check out the Lambda Island episodes on [Component and System](https://lambdaisland.com/collections/clojure-component-system).

[![](https://i.vimeocdn.com/video/637685834_295x166.jpg?r=pad)](https://lambdaisland.com/collections/clojure-component-system)

The author of reloaded.repl, James Reeves, eventually became dissatisfied with Component, and created [Integrant](https://github.com/weavejester/integrant) as an alternative, with [integrant-repl](https://github.com/weavejester/integrant-repl) as the counterpart of reloaded.repl.

With reloaded.repl your own utility code now looks like this:
    
    
    (ns user
      (:require reloaded.repl
                com.stuartsierra.component))
    
    (defn new-system []
      (com.stuartsierra.component/system-map ,,,))
    
    (reloaded.repl/set-init! new-system)
    

Now you start the system with `(reloaded.repl/go)`. To stop the system, reload all changed namespaces, and start the system again, you do `(reloaded.repl/reset)`.

With integrant-repl things look similar.
    
    
    (ns user
      (:require integrant.repl
                clojure.edn))
    
    (defn system-config []
      (-> "system_config.edn" slurp clojure.edn/read-string))
    
    (integrant.repl/set-prep! system-config)
    

Now `(integrant.repl/go)` will bring your system up, and with `(integrant.repl/reset)` you can get back to a clean slate.

### cider-refresh

CIDER (Emacs’s Clojure integration) supports tools.namespace through the `cider-refresh` command. This only does code reloading though, but you can tell it to run a function before and after the refresh, to achieve the same effect.
    
    
    ;; emacs config
    
    
    ;; reloaded.repl
    (setq  cider-refresh-before-fn "reloaded.repl/suspend")
    (setq  cider-refresh-after-fn "reloaded.repl/resume")
    
    ;; integrant-repl
    (setq  cider-refresh-before-fn "integrant.repl/suspend")
    (setq  cider-refresh-after-fn "integrant.repl/resume")
    

You can also configure this on a per-project basis, by creating a file called `.dir-locals.el` in the project root, which looks like this.
    
    
    ((nil . ((cider-refresh-before-fn . "integrant.repl/suspend")
             (cider-refresh-after-fn . "integrant.repl/resume"))))
    

Now calling `cider-refresh` (Spacemacs: `, s x`) will again stop the system, reload all namespaces, and restart.

## Plot twist

So far so good, this was a lengthy introduction, but I wanted to make sure we’re all on the same page, now let’s look at some of the things that might spoil our lovely reloading fun.

### AOT

Clojure has a nifty feature called AOT or “ahead of time compilation”. This causes Clojure to pre-compile namespaces to Java classes and save those out to disk. This can be a useful feature as part of a deployment pipeline, because it can speed up booting your app. It has some serious drawbacks though, as it messes with Clojure’s dynamic nature.

What tends to happen is that at some point people find out about AOT, they think it’s amazing, and enable it everywhere. A bit later errors start popping up during development that just make no sense.

AOT should only be used for deployed applications. Don’t use it during development, and don’t use it for libraries you publish.

Even when following this advice you can get into trouble. Say you have aot enabled as part of the process of building an uberjar for deployment.
    
    
    (defproject my-project "0.1.0"
      ,,,
      :profiles {:uberjar
                 {:aot true}})
    

You do a `lein uberjar` to test your build locally. This will create AOT compiled classes and put them on the classpath (under `target/` to be precise). Next time you try to `(reset)` it will tell you it can’t find certain Clojure source files, even though they’re _right there_. I have lost hours of my life figuring this out, and have more than once found my [own Stackoverflow question+answer](https://stackoverflow.com/questions/44246924/clojure-tools-namespace-refresh-fails-with-no-namespace-foo) when googling for this. Watch out for the ghost of AOT!

By the way, here’s a handy oneliner: `git clean -xfd`. It removes any files not tracked by git, including files in `.gitignore`. It’s the most thorough way of cleaning out a repository. Do watch out, this might delete files you still want to keep! With `git clean -nxfd` you can do a dry run to see what it plans to delete.

### `defonce` won’t save you

Ideally all your state is inside your system, but maybe there’s something else that you want to carry over between reloads. No need to judge, life is compromise.

You might think, “I know, I’ll just `defonce` it!”, but `defonce` won’t save you. tools.namespace will completely remove namespaces with everything in them before loading them again, so that var created by `defonce` the first time is long gone by the time `defonce` gets called again, and so it happily defines it anew.

What you can do instead is add some namespace metadata telling tools.namespace not to unload the namespace.
    
    
    (ns my-ns
      (:require [clojure.tools.namespace.repl :as c.t.n.r]))
    
    (c.t.n.r/disable-unload!) ;; this adds :clojure.tools.namespace.repl/unload false
                              ;; to the namespace metadata
    
    (defonce only-once (Date.))
    

This namespace will still get reloaded, so if you have functions in there then you’ll get the updated definitions, but it won’t be unloaded first, so `defonce` will work as expected. This does also mean that if you remove (or rename) a function, the old one is still around.

To completely opt-out of reloading, use `(c.t.n.r/disable-reload!)`. This implies `disable-unload!`.

### cljc + ClojureScript source maps

When ClojureScript generates source maps, it needs a way to point the browser at the original source files, so it copies `cljs` and `cljc` files into your web server document root. (your “public/” directory). Seems innocent enough, but it can confuse tools.namespace.

It is quite a common practice to search for files that are requested over HTTP in the classpath, so the “public/” directory tends to be on the classpath as well. tools.namespace scans the classpath for Clojure files (`clj` or `cljc`) and finds those `cljc` files that ClojureScript copied there, but their namespace name doesn’t correspond with their location, and things breaks. There is a JIRA ticket for this for tools.namespace: [TNS-45](https://dev.clojure.org/jira/browse/TNS-45), with several proposed patches, but no consensus yet about the right way forward.

The easiest way around this is to limit tools.namespace to only scan your own source directories.
    
    
    (c.t.n.r/set-refresh-dirs "src/clj")
    

### CIDER does its own thing

`cider-refresh` roughly does the same thing as `clojure.tools.namespace.repl/refresh`, and it uses tools.namespace under the hood, but it reimplements `refresh` using lower level tools.namespace primitives. This leads to subtle differences.

Currently `cider-refresh` does not honor `set-refresh-dirs`. It honors the unload/load metadata, but does not follow the convention that `:clojure.tools.namespace.repl/load false` implies `:clojure.tools.namespace.repl/unload false`.

I proposed [a patch](https://github.com/clojure-emacs/cider-nrepl/pull/503) to address both of these issues. In general if `cider-refresh` seems to have issues, then try to use `(reset)` directly in the REPL.
