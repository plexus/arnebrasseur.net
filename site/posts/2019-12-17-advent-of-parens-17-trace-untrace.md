---
title: "Advent 2019 part 17, trace! and untrace!"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2019-12-17T11:02:59.000Z"
updated_at: "2026-05-16T13:41:52.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-18 14:51"]
---

_This post is part of[Advent of Parens 2019](https://lambdaisland.com/blog/2019-11-25-advent-of-parens), my attempt to publish one blog post a day during the 24 days of the advent._

Here’s a little REPL helper that you may like.
    
    
    (defn trace! [v]
      (let [m    (meta v)
            n    (symbol (str (ns-name (:ns m))) (str (:name m)))
            orig (:trace/orig m @v)]
        (alter-var-root v (constantly (fn [& args]
                                        (prn (cons n args))
                                        (apply orig args))))
        (alter-meta! v assoc :trace/orig orig)))
    
    (defn untrace! [v]
      (when-let [orig (:trace/orig (meta v))]
        (alter-var-root v (constantly orig))
        (alter-meta! v dissoc :trace/orig)))
    

Pass `trace!` a function var and it will print out the invocation each time the function gets called.

Drop it in `user.clj` and `trace!` to your heart’s content!
    
    
    user> (trace! #'foo.bar/baz)
    user> (foo.bar/baz 123)
    (foo.bar/baz 123)
    nil
    user>
    

_Hi, my name is Arne (aka @plexus) and I consult companies and teams about application architecture, development process, tooling and testing. I collaborate with other talented people under the banner[Gaiwan](__GHOST_URL__/). If you like to have a chat about how we could help you with your project then please [get in touch!](__GHOST_URL__/p/contact)_

**[Comment on ClojureVerse](https://clojureverse.org/t/advent-of-parens-2019/5159)**
