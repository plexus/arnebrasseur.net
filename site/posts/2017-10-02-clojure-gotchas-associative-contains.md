---
title: "Clojure Gotchas: \"contains?\" and Associative Collections"
slug: "clojure-gotchas-associative-contains"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2017-10-02T18:01:45.000Z"
updated_at: "2026-05-16T13:42:07.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-09 12:23"]
---

When learning a programming language we rarely read the reference documentation front to back. Instead someone might follow some tutorials, and look at sample code, until they’re confident enough to start a little project for practice.

From that point on the learning process is largely “just in time”, looking up exactly the things you need to perform the task at hand. As this goes on you might start to recognize some patterns, some internal logic that allows you to “intuit” how one part of the language works, based on experience with another part.

Developing this “intuition” — understanding this internal logic — is key to using a language effectively, but occasionally our intuition will be off. Some things are simply not obvious, unless someone has explained them to us. In this post I will look at something that frequently trips people up, and attempt to explain the underlying reasoning.

## `contains?`

Let’s start with perhaps the greatest source of frustration in `clojure.core`, the `contains?` function. Based on its name you’d be excused for thinking that it checks whether a collection contains a certain value.
    
    
    (def beatles ["John" "Paul" "Ringo" "George"])
    
    (contains? beatles "Ringo")
    ;;=> false
    

Ahum… what the h*ck, Clojure?

A symptom of the same underlying cause is confusion over `get` vs `nth`. Why do we need both? When you do you use which?
    
    
    (get beatles 1) ;;=> "Paul"
    (nth beatles 1) ;;=> "Paul"
    

## Collection Traits

To understand what’s going on we need to look at the _underlying abstractions_ to Clojure’s data types. All of Clojure’s concrete types like vectors, sets, or maps implement one or more of these “abstractions”. You can also think of them as “having certain traits”.

  * Seq(uential)
  * Seqable
  * Associative
  * Counted
  * Indexed
  * Sorted
  * Reversible



Functions in `clojure.core` do their work based on these abstractions. For instance, you can call `first` on any Clojure collection because they all implement Seq or Seqable, whereas `get` or `update` will only work on collections that implement Associative.

This [blog post by Alex Miller](http://insideclojure.org/2016/03/16/collections/) explains these traits in great detail.

Of these Seq and Seqable are arguable the most important to understand, closely followed by Associative. There’s a [free Lambda Island episode](https://lambdaisland.com/episodes/clojure-seq-seqable) with everything you need to know about seqs, which is a trait shared by all Clojure collections, as well as some other things like strings or Java arrays.
    
    
    (seqable? '(1 2))  ;;=> true
    (seqable? [1 2])   ;;=> true
    (seqable? {1 2})   ;;=> true
    (seqable? #{1 2})  ;;=> true
    (seqable? "12")    ;;=> true
    

Functions like `first`, `rest`, `map`, or `cons` can operate on any seq/seqable. That’s also why the return type of `map` or `cons` may be different from what you passed it. The only guarantee is that the result is again a seq.

Maps and vectors are associative. You can think of the first as a mapping from keys to values, and the second as a mapping from indexes to values. `get`, `assoc`, `dissoc`, `update`, and any of their `*-in` variants work on associative data structures.
    
    
    (associative? '(1 2))  ;;=> false
    (associative? [1 2])   ;;=> true
    (associative? {1 2})   ;;=> true
    (associative? #{1 2})  ;;=> false
    (associative? "12")    ;;=> false
    
    
    
    (get {1 2} 1) ;;=> 2
    (get [1 2] 0) ;;=> 1
    
    (update-in {:x {:y [0 0 0]}} [:x :y 1] inc)
    ;;=> {:x {:y [0 1 0]}}
    

`contains?` works on Associative collections, where it checks if the collection contains a mapping for the given input. For maps that means it will check if a given key is there, and for vectors it returns true if the _index_ exists.
    
    
    (contains? {:a :b} :a)                         ;;=> true
    (contains? {:a :b} :c)                         ;;=> false
    
    (contains? [:a :b] 0)                          ;;=> true
    (contains? [:a :b] 1)                          ;;=> true
    (contains? [:a :b] 2)                          ;;=> false
    

## Sets as funny maps

Sets are a bit of a special case. While sets are not associative, several of the associative operations, including `contains?`, will also work on sets.

Suppose that Clojure didn’t have sets, in that case you could make do by using maps, with each value identical to its key. It turns out that for many operations that is exactly how sets behave. Whenever I’m not sure of whether a set would work in a given context, I try to imagine what would happen if I used a map instead.
    
    
    (def real-set #{:a :b})
    (def pseudo-set {:a :a, :b :b})
    
    (real-set :a)                                  ;;=> :a
    (pseudo-set :a)                                ;;=> :a
    
    (get real-set :a)                              ;;=> :a
    (get pseudo-set :a)                            ;;=> :a
    
    (contains? real-set :a)                        ;;=> true
    (contains? pseudo-set :a)                      ;;=> true
    

Now that you know exactly how `contains?` works, you can attempt to make sense of its docstring.
    
    
    cljs.user=> (doc contains?)
    -------------------------
    cljs.core/contains?
    ([coll v])
      Returns true if key is present in the given collection, otherwise
      returns false.  Note that for numerically indexed collections like
      vectors and arrays, this tests if the numeric key is within the
      range of indexes. 'contains?' operates constant or logarithmic time;
      it will not perform a linear search for a value.  See also 'some'.
    nil
    

So to recap, for maps `contains?` checks for the presence of a key, for sets it does what you would expect, since keys=values, and for vectors it checks whether the _index_ is present.
    
    
    (contains? #{:a :b} :a)                        ;;=> true
    (contains? #{:a :b} :c)                        ;;=> false
    
    (contains? {:a :b} :a)                         ;;=> true
    (contains? {:a :b} :b)                         ;;=> false
    
    (contains? [1 2] 1)                            ;;=> true
    (contains? [1 2] 2)                            ;;=> false
    

If you do want to search a collection that is “only” a seq, the common idiom is to use `some` \+ a set literal. This returns the matched value, which is truthy.
    
    
    (def beatles ["John" "Paul" "Ringo" "George"])
    
    (some #{"Ringo"} beatles)
    ;;=> "Ringo"
    

An important caveat: if the value you’re looking for could be `nil` or `false`, then the set literal won’t work, and you need a more explicit check.
    
    
    (def haystack [false])
    
    (some #{false} haystack)
    ;;=> nil
    
    (some #(= false %) haystack)
    ;;=> true
    

The difference between `get` and `nth` is now hopefully also clear. While `get` does a lookup in an associative collection, `nth` will find the nth element in a sequential collection. If the collection happens to also be indexed, then `nth` works in constant time, otherwise it will take linear time to walk the sequence.
    
    
    ;; Vectors: they're roughly the same
    (get [:a :b :c] 1)                             ;;=> :b
    (nth [:a :b :c] 1)                             ;;=> :b
    
    ;; Lists: only nth makes sense
    (get '(:a :b :c) 1)                            ;;=> nil
    (nth '(:a :b :c) 1)                            ;;=> :b
    
    ;; Sets: only get makes sense
    (get #{:a :b :c} :a)                           ;;=> :a
    (nth #{:a :b :c} 1)                            ;;=> ! Exception
    
    ;; Maps: only get makes sense
    (get {:a :b} :a)                               ;;=> :b
    (nth {:a :b} 1)                                ;;=> ! Exception
    

This diagram from a [presentation by Alex Miller](https://www.slideshare.net/alexmiller/clojure-the-art-of-abstraction-7161663) gives you an overview of which functions work on which abstractions.

![Collection Traits](https://devblog.arnebrasseur.net/images/collection_traits.png)

This may seem overly complicated, but in practice most of these are pretty intuitive, so you don’t need to hang this diagram over your bed to study it every evening. Some functions “color outside the lines”, like `contains?` working on Associative collections, but also sets, or `nth` really being more of an Indexed operation, but also accepting seqs. In practice this isn’t as confusing as it may seem. The main cases that do trip people up I’ve tried to explain above.

_Many thanks to[Daiyi](http://www.daiyi.co/) for providing feedback on an earlier draft._

_[Try out an interactive version of this post in Maria](https://www.maria.cloud/gist/ecb5c59087477ec5c4fb92f1c2f68e42?eval=true)_

 _[🡑 Discuss/vote for this post on Reddit](https://www.reddit.com/r/Clojure/comments/73tpoe/clojure_gotchas_contains_and_associative/)_
