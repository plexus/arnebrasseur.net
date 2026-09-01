---
title: "Advent 2019 part 11, Integrant in Practice"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2019-12-11T10:55:25.000Z"
updated_at: "2026-05-16T13:41:55.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-15 08:10"]
---

_This post is part of[Advent of Parens 2019](https://lambdaisland.com/blog/2019-11-25-advent-of-parens), my attempt to publish one blog post a day during the 24 days of the advent._

I’ve been a fan of [Integrant](https://github.com/weavejester/integrant) pretty much ever since it came out. For me there is still nothing that can rival it.

The recently released [clip](https://github.com/juxt/clip) by the folks from Juxt does deserve an honorable mention. It has an interesting alternative approach which some may prefer, but it does not resonate with me. I prefer my system configuration to be just data, rather than code wrapped in data.

However there’s another Juxt library that I do use on pretty much any project these days, and that combines wonderfully with Integrant: [Aero](https://github.com/juxt/aero). Aero is basically just an EDN reader, but it provides half a dozen reader tags that make it ideal for system configuration, and so it pairs with Integrant like camembert with Bordeaux.

Aero provides things like `#env MY_ENV_VAR`, `#include "my_other_file.edn"`, or `#profile {:prod 80 :dev 8080}`.

So the first thing I’ll do on a project is usually to set up `integrant`, `integrant-repl`, and `aero`. Let’s see what that looks like.

The starting point for using Integrant is your configuration map, which goes in a file named `resources/app_name/system.edn`. So if your main namespace is `feralberry.core`, then this goes into `resources/feralberry/system.edn`. This way it can be found on the classpath, and bundled into a jar, without potentially clashing with other files on the classpath.

For instance:
    
    
    {:feralberry.http/router {:routes [["/" {:get {:interceptors :index}}]]}
    
     :feralberry.http/server {:port   2533
                              :router #ig/ref :feralberry.http/router}
    
     :feralberry.storage/crux {:crux.node/topology           :crux.kafka/topology
                               :crux.node/kv-store           crux.kv.rocksdb/kv
                               :crux.kv/db-dir               "data/db-dir-1"
                               :crux.kafka/bootstrap-servers "localhost:9092"}}
    

Next we need a way to read this configuration:
    
    
    (ns feralberry.system
      (:require [aero.core :as aero]
                [clojure.java.io :as io]
                [integrant.core :as ig]))
    
    (defmethod aero/reader 'ig/ref
      [_ tag value]
      (ig/ref value))
    
    (defn config [profile]
      (aero/read-config (io/resource "feralberry/system.edn") {:profile profile}))
      
    (defn prep [profile]
      (let [config (config profile)]
        (ig/load-namespaces config)
        config))
    

Here I define an extra reader tag for Integrant references, and a helper to read in the configuration for a given profile (like `:dev`, `:prod`, `:test`). Finally I add a helper function `prep` which tries to load any namespaces referenced in the configuration map, and then returns the configuration.

Next we need a way to start the system from the REPL, this is done in the `user`namespace, since this gets loaded automatically during development, so these functions are immediately available after booting up.
    
    
    (ns user)
    
    (defmacro jit
      "Just in time loading of dependencies."
      [sym]
      `(requiring-resolve '~sym))
    
    (defn set-prep! []
      ((jit integrant.repl/set-prep!) #((jit feralberry.system/prep) :dev)))
    
    (defn go []
      (set-prep!)
      ((jit integrant.repl/go)))
    
    (defn reset []
      (set-prep!)
      ((jit integrant.repl/reset)))
    
    (defn system []
      @(jit integrant.repl.state/system))
    
    (defn config []
      @(jit integrant.repl.state/config))
    

This code looks a bit weird because of the `jit` thing. The reason I do this is I don’t like having any `:require` in my `user` ns, since it slows down the TTR (Time To REPL), so I only load stuff on demand. I also added two helpers to easily inspect the system and the current configuration from the REPL.

So now I have a nice reloaded workflow. Just `(go)` after booting the REPL, or `(reset)` to reload namespaces and restart.

With that the boilerplate is out of the way and I can start implementing my system components, for instance:
    
    
    (ns feralberry.storage
      (:require [integrant.core :as ig]
                [crux.api :as crux]))
    
    (defmethod ig/init-key ::crux [_ config]
      (crux/start-node config))
    
    (defmethod ig/halt-key! ::crux [_ crux]
      (.close crux))
    

Note that I don’t need to explicitly require this namespace, it will get loaded by integrant based on the configuration key `:feralberry.storage/crux`. Alternatively I could have put these methods in a `feralberry.storage.crux`namespace, that would have worked as well.

Since Integrant is simply a library it isn’t always clear to people how to use it in practice. I hope this post was helpful in showing at least one pattern/convention for setting things up.

_Hi, my name is Arne (aka @plexus) and I consult companies and teams about application architecture, development process, tooling and testing. I collaborate with other talented people under the banner[Gaiwan](__GHOST_URL__/). If you like to have a chat about how we could help you with your project then please [get in touch!](__GHOST_URL__/p/contact)_

**[Comment on ClojureVerse](https://clojureverse.org/t/advent-of-parens-2019/5159)**
