---
title: "Making nREPL and CIDER More Dynamic (part 1)"
slug: "making-nrepl-cider-more-dynamic-1"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2021-11-03T17:59:10.000Z"
updated_at: "2026-05-16T13:41:39.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-15 08:16"]
---

_This first part is a recap about nREPL, nREPL middleware, and some of the issues and challenges they pose. We’ll break up the problem and look at solutions in part 2._

The REPL is a Clojurists [quintessential tool](https://lambdaisland.com/blog/2017-12-29-the-bare-minimum-clojure-mayonnaise), it’s what we use to do Interactive Development, the hallmark of the LISP style of development.

In Interactive Development (more commonly but somewhat imprecisely referred to as REPL-driven development), the programmer’s editor has a direct connection with the running application process. This allows evaluating pieces of code in the context of a running program, directly from where the code is written (and so not in some separate “REPL place”), inspecting and manipulating the innards of the process. This is helped along by the dynamic nature of Clojure in which any var can be redefined at any point, allowing for quick incremental and iterative experimentation and development.

This is why it’s essential to the Clojure development experience to have proper editor support, a plugin which bridges the gap between where the code is written and where the code is run. So we have CIDER for Emacs, Calva for VS Code, Cursive for IntelliJ, Conjure or Iced for Vim, and so forth. Often these will also leverage the same (or a parallel) connection into the process for other editor affordances, like navigation and completion.

But for these editor plugins to connect to the Clojure process something needs to be listening on the other side, accepting connections, allowing the initiation of a program-to-program dialogue. The most common way to achieve this is by leveraging the nREPL protocol, an asynchronous message-based network protocol for driving interactive development. The application process is started with an embedded nREPL server, so that the editor can connect as an nREPL client.

## nREPL Server and Middleware

nREPL is an extensible protocol, the reference server implementation understands certain core _operation types_ like `"eval"`. More operations can be supported, or existing operations can be modified or augmented, through nREPL middleware. For example: the Piggieback middleware can intercept `"eval"` messages, and forward them to a ClojureScript environment, rather than evaluating them in the Clojure process itself.

Which middleware to use will mostly depend on the editor you are using. You’ll typically find that the Clojure-specific functionality for a given editor is partly implemented as a typical editor extension, for instance CIDER written in Emacs LISP, or Calva written in Typescript, and partly as nREPL middleware, providing the functionality the editor extension relies on. For instance, both CIDER and Calva rely on functionality provided by cider-nrepl.

Other tooling can also mandate specific middleware. When using Shadow-cljs you need to use Shadow’s particular nREPL middleware to enable ClojureScript eval, rather than the standard Piggieback. The clj-refactor Emacs package requires the refactor-nrepl middleware to be able to do its work.

This has sometimes lead to confusion or frustration. Why are some editor features not working? Which middleware do I need in my situation? How do I make sure (and verify) that it’s loaded? What if my colleague uses a different editor, does that change our setup? And so forth.

## Easy, not Simple: Jacking In

What we have seen in response is that editors have mostly tried to relieve users from having to start up their application process, instead the editor does it for you, providing all the right flags and dependencies to make sure nREPL gets started, with all the necessary middleware in tow. For instance when I do `cider-jack-in` in Emacs in the context of a Clojure CLI (`deps.edn`) based project it does something like this:
    
    
    /usr/local/bin/clojure \
      -Sdeps '{:deps {nrepl/nrepl {:mvn/version "0.9.0-beta3"}
                      cider/cider-nrepl {:mvn/version "0.27.2"}
                      refactor-nrepl/refactor-nrepl {:mvn/version "2.5.1"}}}' \
      -m nrepl.cmdline \
      --middleware '[shadow.cljs.devtools.server.nrepl/middleware
                     refactor-nrepl.middleware/wrap-refactor
                     cider.nrepl/cider-middleware]
    

First it provides Clojure CLI, based on clojure/tools.deps.alpha, with extra dependencies: the nREPL server, and the cider-nrepl and refactor-nrepl middlewares. This will cause tools.deps to find these artifacts (poms and jars) on Clojars or Maven Central, download them, download any dependencies they rely on, and then add all those jars to the classpath. (The lookup path where Clojure can find namespaces to load, or the JVM looks for compiled classes).

Then it invokes the `nrepl.cmdline` namespace’s `-main` function, passing it a `--middleware` flag, which tells it which middleware vars to load and add to the server when it boots up.

Note that in this case it also includes the shadow-cljs middleware. If nREPL is started by shadow-cljs itself then this is already provided, but in this case I want to use Clojure CLI, so I need extra project-level configuration to tell CIDER that I need this middleware. Scenarios like this show that despite the ease of use that “jack-in” commands have brought, this whole thing will still regularly cause issues for people.
    
    
    ;; Project-specific setup via .dir-locals.el
    ((nil . 
      (cider-preferred-build-tool . clojure-cli)
      (cider-clojure-cli-global-options . "-A:dev")
      (eval .
       (progn
        (make-variable-buffer-local 'cider-jack-in-nrepl-middlewares)
         (add-to-list 'cider-jack-in-nrepl-middlewares "shadow.cljs.devtools.server.nrepl/middleware")))))
    

And for all its convenience, this jack-in approach where the editor plugin takes care of starting the process, nREPL, adding the necessary middleware, and then connecting once the server is up and running is still not suitable in every scenario.

For one it means your Clojure process runs “inside” your editor, while some much prefer to run these separate, with the Clojure process running in its own terminal window. If you have specific requirements for how to start the process, say a specific Java version, or providing `deps.edn` aliases, then now you need to figure out how to make your editor do those things. It may seem a lot easier to just take care of the startup yourself. Maybe your process is even running somewhere else, inside a container, through WSL, or on a server in the cloud. In that case the jack-in approach might be firmly out of reach.

If you still want to have the rich editor support you have come to depend on, then you will have to take care yourself of making sure nREPL and all necessary middleware is on the classpath, to load the necessary namespaces, to start the nREPL server, and to add the right middleware.

## Running Your own nREPL Server

This isn’t all that hard, there was a time when jack-in wasn’t a thing, and so we all did this. If you look at old `project.clj` files you’ll often find `cider-nrepl` or `piggieback` in there as a dependency, and nREPL options to include the right middleware. Leiningen comes bundled with its own nREPL server, which is configured through keys inside the `defproject`, which contributes to many `project.clj` files being so bulky and full of boilerplate.
    
    
    (defproject ,,,
      :dependencies [[cider/piggieback "..."] 
                     [cider/cider-nrepl "..."]]
    
      :repl-options
      {:nrepl-middleware [cider.nrepl/cider-middleware,cider.piggieback/wrap-cljs-repl]})
    

But this causes its own issues. These dependencies can easily get out of sync with what your editor needs. And what if your teammates use different editors? Or don’t upgrade their editors at the same pace? It quickly gets messy.

So can we do better? I believe we can! What we really want is to get rid of these editor and tooling specific nREPL connections. Instead we provide a “vanilla” nREPL server which any client can connect to. The client can ask the server which capabilities it has, and if it needs more functionality, then it can “upgrade” its connection. So that’s the lodestar we are aiming for. In the next article we’ll break this up into three sub-problems, and look at the options we have available for solving them.
