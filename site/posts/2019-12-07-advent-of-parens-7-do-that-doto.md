---
title: "Advent 2019 part 7, Do that doto"
slug: "advent-of-parens-7-do-that-doto"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2019-12-07T20:43:35.000Z"
updated_at: "2026-05-16T13:41:58.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-15 08:10"]
---

_This post is part of[Advent of Parens 2019](https://lambdaisland.com/blog/2019-11-25-advent-of-parens), my attempt to publish one blog post a day during the 24 days of the advent._

`doto` is a bit of an oddball in the Clojure repertoire, because Clojure is a functional language that emphasizes pure functions and immutabilty, and `doto`only makes sense when dealing with side effects.

To recap, `doto` takes a value and a number of function or method call forms. It executes each form, passing the value in as the first argument. At the end of the ride it returns the original value.

The typical example of using `doto` is for dealing with mutable Java objects
    
    
    (doto (java.util.HashMap.)
      (.put "hello" "world")
      (.put "hi" "earth"))
    ;; => {"hi" "earth", "hello" "world"}
    

And it really cleans up invoking the Builder pattern.
    
    
    (.build
     (doto (FooBuilder.)
       (.setBar 123)
       (.toggleQux)
       (.setName "hello")))
    

But what I want to show is that there are a few places where `doto` is useful when you’re not doing Java interop.

The first is with `prn` or `println`. These functions return `nil`, so if you want to quickly add a `prn` to check what a function is returning you have to work around that, for instance by adding a let.
    
    
    ;; say you have something like this
    (foo (bar (baz x)))
    
    ;; what's coming back from `baz`?
    ;; this does not work:
    (foo (prn (bar (baz x))))
    
    ;; you could do this, but that's a mouthful
    (let [x (bar (baz x))]
      (prn x)
      (foo x))
    
    ;; instead throw in a `(doto ... prn)`
    (foo (doto (bar (baz x)) prn))
    

The cool thing is this also works in thread-first forms (not in thread-last though), just throw that `(doto prn)` in between any two forms.
    
    
    (-> x
        baz
        (doto prn)
        bar
        foo)
    

Or with some markers to make the output a bit more obvious:
    
    
    (-> x
        baz
        (doto (prn '<-baz))
        bar
        (doto (prn '<-bar))
        foo)
    

You can also throw in a `(doto ... assert)` if you’re getting paranoid that there’s a `nil` somewhere where you don’t expect it.

And if you’re using Datomic from a REPL or from tests, and you want to be able to quickly recreate an in-memory database, you can do this.
    
    
    (let [uri (doto "datomic:mem:test1" d/delete-database d/create-database)
          conn (d/connect uri)]
      ,,,)
    

**[Comment on ClojureVerse](https://clojureverse.org/t/advent-of-parens-2019/5159)**
