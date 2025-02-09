---
title: Open Source Diary C.V.4
draft: true
date: 2025-02-06
---

Sometimes when I want to relax I just start hacking on whatever I feel like
hacking on most at that moment, but then allow myself to get sidetracked to fix
whatever small annoyances I come across.

When working for a client you can only afford so many of these diversions, cause
at the end of the day you need to something you can ship. And so you suck it up,
and live with all the little sharp edges in the tools and libraries you use. In
my own time on my own projects there's no such external pressure. Of course I
don't want to only yak shave on my own projects either, it's nice to have
something to show for, but I can allow myself greater leeway to spend time
polishing my tools, so when I do sit down and try to get the project over the
line, it's a much more enjoyable process.

This is also why I build and use many of my own tools. It allows me to create a
happy space where things just work the way I like them to. And if they don't I
have greater power to do something about it.

One such long time annoyance that I finally sat down to fix is
`*print-namespace-maps*`. This var was introduced in clojure 1.9. If it's set to
`true` it changes the behaviour of clojure.pprint, so that namespaced maps are
printed with a prefix designating the namespace. However since that prefix moves
the whole map over, nested maps are increasingly pushed to the right, leading to
wrapped lines and messed up formatting.

```clj
;; (set! *print-namespace-maps* false)
{:foo/bar "t'was brillig and the slythie toves",
 :foo/baq "did gyre and gimble in the wabe",
 :foo/baz
 {:xxx/yyy "do not go gentle into that good night",
  :xxx/zzz "rage, rage against the dying of the light"}}
 
;; (set! *print-namespace-maps* true)
#:foo{:bar "t'was brillig and the slythie toves",
      :baq "did gyre and gimble in the wabe",
      :baz
      #:xxx{:yyy "do not go gentle into that good night",
            :zzz "rage, rage against the dying of the light"}}
```

I really just don't think this is an improvement, so I want it to be always off,
and neither Clojure nor nREPL seem to provide a convenient setting to control
that. You can only set it once your REPL has started.

[Launchpad](https://github.com/lambdaisland/launchpad) seemed like an obvious
place to address this. The whole idea with launchpad is that it's a
one-stop-shop Clojure environment launcher, which you can configure to your
liking, either globally or on the project level.

So launchpad now has a new flag, `--[no-]namespace-maps`. This will add an nREPL
middleware that rebinds the offending var. As with any Launchpad flags you can
provide a default value on multiple levels. Do you want to set it for everyone
on your team? Then do so directly in `bin/launchpad`

```clj
#!/usr/bin/env bb

(require
 '[lambdaisland.launchpad :as launchpad])

(launchpad/main {:namespace-maps false})
```

To make this a default whenever you start your REPL on a given project, set it
in `deps.local.edn`.

```clj
{:launchpad/options {:namespace-maps false}}
```

And do you want it to be always off, you can set this globally in
`~/.clojure/deps.edn`.

Note that these sensibly override one another. CLI flag overrides local
overrides global overrides per-project.

This made me think that this should really be the case for all of launchpad's
boolean flags. For instance, if you have `{:launchpad/options {:go true}}`, so
that `(user/go)` is called automatically at startup, then `bin/launchpad
--no-go` should turn that off again.

This in turn led to some improvements in
[lambdaisland/cli](https://github.com/lambdaisland/cli). In particular in
Launchpad we have the `--emacs` option, which is a shorthand for `--cider-nrepl --refactor-nrepl --cider-connect`. In a case like this it would be sensible to allow later flags to override earlier ones, so that `--emacs --no-refactor-nrepl` injects the CIDER nREPL middleware, and instructs emacs to connect to your process, but without injecting the refactor-nrepl middleware.

In lambdaisland/cli, you can optionally define a `:handler` for each command
line flags, to override the default behavior. In the case of no-argument flags,
this is a single arity function, it just receives the options map, and for
instance `assoc`'es something onto it. If the flag takes an argument, then
`:handler` should take two arguments.

The change I made is that handlers for `--[no-]...` style flags also get a
second argument, to differentiate between true and false.

```clj
(def flags
  ["--[no-]emacs" {:doc     "Shorthand for --cider-nrepl --refactor-nrepl --cider-connect"
                   :handler (fn [ctx v]
                              (assoc ctx
                                     :cider-nrepl v
                                     :refactor-nrepl v
                                     :cider-connect v))}])
```

I also made sure that handlers and middleware for flags and subcommands are
processed in a sensible order. Middleware for commands runs after ("inside")
middleware for flags, and middleware for subcommands runs after ("inside")
middleware for parent commands. That way parents and flags can provide context
(through `opts` or through dynamic vars) to child commands.

----

[Makina](https://github.com/lambdaisland/makina) is coming along nicely, see my
[last post](https://arnebrasseur.net/2025-02-06-open-source-diary.html) for
background. There's a CLJC purely functional `lambdaisland.makina.system`
namespace (mechanism), and a much more opinionated and integrated CLJ-only
namespace `lambdaisland.makina.app`. I also added a tiny example app in the repo
to show what idiomatic use of Makina (+ lambdaisland/config + lambdaisland/cli)
would look like.

At this point the next step is to write a README. Now that I'm starting to
settle on a design writing a README is a great way to check myself and see if
the design is sensible. A good design can be explained clearly and concisely.

But in the meanwhile I'll sketch a little bit of what I'm working towards. At
first things will look a lot like integrant, you start with a config map, with
keys identifying your various components, values being configuration for said
components, and with refs between them to designate dependencies.

```clj
{:my.app/db {:jdbc-url "..."}
 :my.app/http-server {:db #makina/ref :my.app/db}}
```

Note that we have data printers and readers for these tagged literals, you
should be able to use them in most contexts without issue, and they'll round
trip from the printer to the reader.

This we're calling a "config" or "system config". Each inner map is a "component
config".

No surprises so far. The first thing Makina will do with this is expand it to
look like this.

```clj
{:my.app/db {:makina/type   :my.app/db
             :makina/id     :my.app/db
             :makina/state  :stopped
             :makina/config {:jdbc-url "..."}
             :makina/value  {:makina/type :my.app/db
                             :makina/id   :my.app/db
                             :jdbc-url    "..."}}
 
 :my.app/http-server {:makina/type   :my.app/http-server
                      :makina/id     :my.app/http-server
                      :makina/state  :stopped
                      :makina/config {:db #makina/ref :my.app/db}
                      :makina/value  {:db          #makina/ref :my.app/db
                                      :makina/type :my.app/http-server
                                      :makina/id   :my.app/http-server}}}
```

This is a "system" or "system map". The `:makina/id` is the key used in the
system config, the `:makina/type` is too, but can also be supplied explicitly.

```clj
{:my.app/db {:makina/type :my.app/jdbc-database
             :jdbc-url "..."}
 :my.app/http-server {:db #makina/ref :my.app/db}}
```

It's this type that's used to find start/stop handlers (and possibly others down
the line, including suspend/resume, and user-defined ones). In the purely
functional API you do that by passing in a map with handlers.

```clj
(lambdaisland.makina.system/start
  system-config
  {:my.app/db (fn [{:keys [jdbc-url]}] ,,,)
   :my.app/http-server {:start (fn [{:keys [db port]}] ,,,)
                        :stop #(.stop %)}})
```

These can either be a map with per-signal handlers, or just a start handler as a
function. On both level it's also possible to declare a `:default`.

```clj
(lambdaisland.makina.system/start
  system-config
  {:default ,,, ;; component that doesn't have an explicit handler
   :my.app/foo {:default ,,, ;; Used for any signal
   }})
```

Makina adds a bunch of extra keys onto your component config before calling the
handler (if it's a map at least, if not we leave it alone), so you can easily do
your own dispatch on id, type, signal.

The `lambdaisland.makina.app` API is used like this:

```clj
(ns my.app
  (:require
   [my.app.config :as config] ;; lambdaisland.config wrapper
   [lambdaisland.makina.app :as app]))

(defonce app
  (app/create
   {:prefix "my.app"
    :data-readers {'config config/get} ;; in system.edn use `#config :http/port` etc
    :handlers {,,,}))

;; app is an atom with keys
;; (:makina/system :makina/extra-handlers :makina/config-source :makina/data-readers)

(def load! (partial app/load! system))
(def start! (partial app/start! system))
(def stop! (partial app/stop! system))
(def value (partial app/value system))
(def state (partial app/state system))
(def component (partial app/component system))
(def refresh (partial app/refresh `system)) ;; symbol containing the app atom
(def refresh-all (partial app/refresh-all `system))
```

This will load the system config from the classpath (i.e. `resources/`), under
`my.app/system.edn`. If you are following `lambdaisland.config` conventions this
file will be sitting next to `config.edn`, `dev.edn`, etc. Note that instead of
`:prefix` you can also pass a `:config-source`, a relative path resolved on the
classpath.

You _can_ pass handlers here explicitly, but if you don't Makina will try to
resolve them. If the type is a keyword or symbol, it'll look for a var with a
corresponding name, or for a var in a namespace with that name named component.
So given `:my.app/db` it could be 

```clj
(ns my.app)

(def db {:start ,,, :stop})
;; OR
(defn db [opts] ,,,)

;;;;;;;;;;;;;;;;;;;;;;
(ns my.app.db)

(def component {:start ,,, :stop})
;; OR
(defn component [opts] ,,,)
```

`refresh`/`refresh-all` are wrappers for
`clojure.tools.namespace.repl/refresh(-all)`. This is a BYO dependency, you need
to add it to your project deps, it gets loaded dynamically when needed. This
prevents is gumming up production builds, while being available in dev with
minimal boilerplate.

A final thing I'll point out is that if a component errors, startups stops at
that point. The component in question will be in an `:error` state, with the
exception under `:makina/error` (it also gets rethrown by `start!`). At this
point you can use your REPL to fix things, and rerun `(start!)`, starting the
remaining components.
