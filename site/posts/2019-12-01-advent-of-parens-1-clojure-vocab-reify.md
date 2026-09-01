---
title: "Advent 2019 part 1, Clojure Vocab: to Reify"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2019-12-01T22:46:44.000Z"
updated_at: "2026-05-16T13:42:01.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-15 08:10"]
---

_This post is part of[Advent of Parens 2019](https://lambdaisland.com/blog/2019-11-25-advent-of-parens), my attempt to publish one blog post a day during the 24 days of the advent._

An interesting aspect of the Clojure community, for better or for worse, is that it forms a kind of linguistic bubble. We use certain words that aren’t particularly common in daily speech, like “accretion”, or use innocuous little words to refer to something very specific. Even a simple word like “simple” is no longer that simple.

We can thank Rich Hickey for this. He seems to care a great deal about language, and is very careful in picking the words he uses in his code, documentation, and in his talks.

The word I want to look at today is “reify”. Pronunciation tip: it has three syllables, re-i-fy.

To reify is to “make something a thing”, it comes from the Latin “res”, meaning “thing”. When you reify you have a _reification_ on your hands. It can mean to make something abstract concrete. This is what Clojure’s `reify` macro does, it takes a Java Interface, and turns it into a concrete object.

Take for instance the `reify` macro in Clojure. You pass it an interface name and method definitions, and what you get back is a concrete object that implements this interface.
    
    
    (let [listener (reify java.awt.event.ActionListener
                     (actionPerformed [this action-event]
                       ,,,))]
      (.addActionListener button listener))
    

Reify can also mean to make something “first class”, i.e. to take something implicit, and represent it in an explicit way so it can be referred to and manipulated like other parts of the system.

Most languages have some version of global variables, but Clojure vars are a little different because they are _reified_. You can refer to a var itself, pass it as an argument or return it from a function.
    
    
    (def xxx "foo" 123)
    
    (defn set-doc! [v doc]
      (alter-meta! v assoc :doc doc))
    
    (set-doc! #'xxx "An important number")
    
    (:doc (meta #'xxx))
    

In Datomic transactions are reified. They are represented as entities, you can query them, refer to them, etc.

Reifying in this sense can be a powerful way to allow for a higher order of programming. The interceptor queue in Pedestal is a reified execution stack, allowing you to inspect and change that stack at runtime.

And of course when you decomplect you’ll often find that there’s a concept waiting to be reified.

**[Post your comments on ClojureVerse](https://clojureverse.org/t/advent-of-parens-2019/5159)**
