---
title: "Advent 2019 part 4, A useful idiom"
slug: "advent-of-parens-4-a-useful-idiom"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2019-12-04T16:47:39.000Z"
updated_at: "2026-05-16T13:41:59.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-15 08:10"]
---

_This post is part of[Advent of Parens 2019](https://lambdaisland.com/blog/2019-11-25-advent-of-parens), my attempt to publish one blog post a day during the 24 days of the advent._

Here’s a little Clojure idiom that never fails to bring me joy.
    
    
    (into {} (map (juxt key val)) m)
    

The keen observer may notice this does exactly nothing. It’s a complicated way of taking a map and returning that same map. However by changing parts of that expression you can do so many different things, because what this really does is pull a map into its individual bits: map entries, and keys, and values, and then putting it back together, so by plugging into this you can do almost any map-to-map transformation imaginable, as well as many other collection-to-collection transformations.

Let’s start with a simple `map-vals` as you might find in utility libraries like Medley.
    
    
    (defn map-vals
      "Maps a function over the values of an associative collection."
      [f m]
      (into {} (map (juxt key (comp f val))) m))
    
    (map-vals inc {:x 1 :y 2})
    ;; => {:x 2, :y 3}
    

Or how about `keywordize-keys`?
    
    
    (defn keywordize-keys
      [m]
      (into {} (map (juxt (comp keyword key) val)) m))
    
    (keywordize-keys {"x" 1 "y" 2})
    ;; => {:x 1, :y 2}
    

Or imagine you have a sequence of maps, and you want to turn it into a map using the ids as keys, so you can quickly look them up by id.
    
    
    (let [coll [{:id 456 :x "hello"}
                {:id 641 :x "world"}
                {:id 941 :x "wide"}]]
      (into {} (map (juxt :id identity)) coll))
    ;; => {456 {:id 456, :x "hello"}
    ;;     641 {:id 641, :x "world"}
    ;;     941 {:id 941, :x "wide"}}
    

We’ve only scratched the surface here. You could take this in any direction you like.

I called this pattern an idiom at the top of the post. In natural language idioms are things like “I’ll show you the ropes” or “I’m about to hit the sack”. Their meaning is not self-evident, but for native speakers they convey a lot of meaning and connotation in a short phrase. Even though they seem to obscure meaning, they actually require less parsing and processing because of the familiarity of the phrase, making them a very useful tool for communication.

If however you want to be understood by a wide and diverse audience then you probably want to go easy on the idioms. In programming you have to make a similar trade-off.

**[Comment on ClojureVerse](https://clojureverse.org/t/advent-of-parens-2019/5159)**
