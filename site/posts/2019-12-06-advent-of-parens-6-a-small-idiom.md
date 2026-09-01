---
title: "Advent 2019 part 6, A small idiom"
slug: "advent-of-parens-6-a-small-idiom"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2019-12-06T13:09:03.000Z"
updated_at: "2026-05-16T13:41:58.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-15 08:10"]
---

_This post is part of[Advent of Parens 2019](https://lambdaisland.com/blog/2019-11-25-advent-of-parens), my attempt to publish one blog post a day during the 24 days of the advent._

As an avid tea drinker I’ve been poring (pouring?) over this catalog of teas.
    
    
    (def teas [{:name "Dongding"
                :type :oolong}
               {:name "Longjing"
                :type :green}
               {:name "Baozhong"
                :type :oolong}
               {:name "Taiwan no. 18"
                :type :black}
               {:name "Dayuling"
                :type :oolong}
               {:name "Biluochun"
                :type :green}])
    

I like all kinds of teas, but my favorites are definitely oolongs, so I want to filter this list. Here’s a common way to do that.
    
    
    (filter #(= :oolong (:type %)) teas)
    ;; => ({:name "Dongding", :type :oolong}
    ;;     {:name "Baozhong", :type :oolong}
    ;;     {:name "Dayuling", :type :oolong})
    

I’m sure many of you have written anonymous functions just like this one, I certainly have written my fair share, but nowadays I would write this differently.
    
    
    (filter (comp #{:oolong} :type) teas)
    

This may or may not look strange, depending on how used you are to using sets as functions, and composing with `comp`, but it just looks so much cleaner to me. It also doesn’t create a closure. Anonymous functions close over all local variables, so as long as there’s a live reference to it none of those local variables can be garbage collected.

The real kicker however is that this is so nicely and naturally extensible.
    
    
    ;; get all black or oolong teas
    (filter (comp #{:black :oolong} :type) teas)
    ;; => ({:name "Dongding", :type :oolong}
    ;;     {:name "Baozhong", :type :oolong}
    ;;     {:name "Taiwan no. 18", :type :black}
    ;;     {:name "Dayuling", :type :oolong})
    
    ;; get all non-oolong teas
    (filter (comp (complement #{:oolong}) :type) teas)
    ;; => ({:name "Longjing", :type :green}
    ;;     {:name "Taiwan no. 18", :type :black}
    ;;     {:name "Biluochun", :type :green})
    

If `(some #{:foo} coll)` can be mainstream, then surely `(comp #{:foo} :bar)`can be too, so go forth and `comp`!

**[Comment on ClojureVerse](https://clojureverse.org/t/advent-of-parens-2019/5159)**
