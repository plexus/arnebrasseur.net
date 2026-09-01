---
title: "Advent 2019 part 3, `every-pred` and `some-fn`"
slug: "advent-of-parens-3-some-fn-every-pred"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2019-12-03T16:07:22.000Z"
updated_at: "2026-05-16T13:42:00.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-15 08:10"]
---

_This post is part of[Advent of Parens 2019](https://lambdaisland.com/blog/2019-11-25-advent-of-parens), my attempt to publish one blog post a day during the 24 days of the advent._

Ah `clojure.core`, it’s like an all you can eat hot-pot. Just when you think you’ve scooped up all it has to offer, you discover another small but delicious delicacy floating in the spicy broth.

In exactly the same way I recently became aware of two functions that until now had only existed on the periphery of my awareness. I’ve since enjoyed using them on several occasions, and keep finding uses for them.

These are `every-pred`, and `some-fn`. I’ll demonstrate them with an example. Say you have a discussion forum. People can be admins or moderators, and besides their username they can optionally set a nickname.
    
    
    (def people [{:name   "Elsie"
                  :admin? true
                  :mod?   true}
                 {:name     "Lin Shiwen"
                  :nickname "Rocky"
                  :mod?     true}])
    

If you want to grab people’s nickname if they have one, or fall back to their regular name otherwise, then this is pretty elegant with `some-fn`.
    
    
    (map (some-fn :nickname :name) people)
    ;; => ("Elsie" "Rocky")
    

If on the other hand you want to find everyone who’s both an admin and a mod, then you can do so easily with `every-pred`.
    
    
    (filter (every-pred :admin? :mod?) people)
    ;; => ({:name "Elsie", :admin? true, :mod? true})
    

Clojure has a bunch of functions with `every` and `some` in their names, which can be a bit confusing. In `some?` and `some->` it refers to a value being something other than `nil`, whereas in `some` and `some-fn` it means “the first one that applies”.

I think this overloading is part of the reason why these two have eluded me so long. Only recently did I have the aha insight that while their names are very different they are really each other’s complement. One combines functions with a logical `or`, the other does so with a logical `and`.
    
    
    (map #(or (:nickname %) (:name %)) people)
    ;; => ("Elsie" "Rocky")
    
    (filter #(and (:admin? %) (:mod? %)) people)
    ;; => ({:name "Elsie", :admin? true, :mod? true})
    

So next time you find yourself writing an anonymous function like this, consider reaching for `some-fn` or `every-pred` instead.

The only difference is that `every-pred` coerces its result to a boolean, making it a little less general purpose. Say we had an `every-fn` instead, you could combine it with `some` to get for instance the name of the first admin in the list.
    
    
    (some (every-fn :admin? :name) people)
    

The implementation of `every-fn` is left as an exercise to the reader. (hint: it’s just like `every-pred`, but without the calls to `boolean`).

**[Comment on ClojureVerse](https://clojureverse.org/t/advent-of-parens-2019/5159)**
