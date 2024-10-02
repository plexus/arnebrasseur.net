---
title: The Unhappy Lambda
date: 2014-06-04
---

tl;dr: Progress on The Happy Lambda is currently blocked, because I didn't think it through before starting, and because functional programming in Ruby sucks.

It's over half a year now since I started this project. I've gotten a lot of support since then, and I'm very grateful for that. However lately progress has been slacking. I still intend to finish the book, but I need to take a step back and rethink some assumptions.

I'm much more a Rubyist than a functional programmer, however I've dabbled with Haskell and a few LISPs, and I find a lot to like there. When I started work on [Hexp](https://github.com/plexus/hexp) I decided to make all the core types immutable. This forced me to do certain things in a functional style, and I came up with some interesting patterns and techniques along the way. Parallel to that I spent time toying around trying to bring more ideas from functional languages to Ruby, getting creative with lambdas, that sort of thing. I combined all this in a lightning talk and delivered it at a few conferences, and people were enthusiastic. So on the long train ride from ArrrrCamp back to Berlin I started writing The Happy Lambda.

My original scope for the book was roughly this

* explain and demystify FP terms and concepts
* a "practical" part with patterns and tips on how to use those FP concepts to write better code
* an "experimental" part where I use Ruby's malleability to make it resemble as much a functional programming language as possible

I've written little bits of all three parts, but now I find I'm unsure how to continue. What I really didn't think through enough is who this book is for, and what the book's goal is.

One of the things I had in mind originally was that I would teach functional programming using Ruby, so you can learn all that stuff "in the comfort of your own home", your known language, so to speak. The thing is that Ruby makes doing stuff in a functional way very complicated. I'll get to that in a bit but basically there is a lot of syntax that just works against you. So while it's possible to explain and showcase a lot of the concepts in Ruby, it just seems really pointless. It's hard to demonstrate the benefits of a technique when taken at face value all it does is make your code more cryptic.

I also tried to make the book very beginner friendly, assuming nothing but a basic knowledge of Ruby. But even for beginners, maybe especially for beginners, it makes more sense to explain laziness, partial application, functional composition, etc, in a language that makes these things elegant. So if one of the book's goals is teaching these concepts to people that never came across them, maybe I should be using Haskell or Clojure or ML for my code samples.

So what is it that sucks so much? Well let's see, if FP is about *functions*, and the elegance of treating functions as first class citizens, then Ruby surely offers us *even more than we could wish for* (note my sarcasm). Instead of the unifying concept of a function, and the simple mechanism of passing functions around, we have procs, lambdas, methods and method objects. Three of these are kind of like first class functions. Each is very much unlike the other. We have a million different syntactic constructs to arrive at them, and two separate mechanisms for passing a *function-like-thing* to another *function-like-thing*.

On the other hand we have very little general purpose higher order functions, unless you count the stuff in Enumerator. There's not even a general purpose "compose" (f comes after g). Let alone a "juxtapose", "bind", whatever. Procs and lambdas have "curry", except that it throws together currying and partial application, has pretty funny semantics when used on varargs, and is described by core developers as an "[easter egg](https://bugs.ruby-lang.org/issues/6253)". But it's better than nothing. A [patch](https://bugs.ruby-lang.org/issues/9783) to add "curry" to Method, so at least these three *function-like-things* are a little more like one another, hasn't received any feedback more than a month later. Which just goes to show that while the situation is bad, no one in a position to do something about it cares a single fuck.

Another small but annoying thing you've probably never realized until you started making your classes immmutable, the `foo.bar=baz` syntax is completely unusable. No matter what a method ending on `=` returns, the actual return value is always what was passed in. So the only side-effect free method you can make that ends on an `=` is the identity function. A persistent hashmap structure can't use `hashmap[:foo]=:bar`, it has to use something like `hashmap.put(:foo, :bar)`. Sure it's a tiny thing, but it's the straw that breaks my pseudo-functional Ruby's back.

So now you're sitting back and grinning thinking, "but Arne, please, surely you could have realized all of that earlier". And fair enough, I chose Ruby+functional for the topic of this book even though it's not the most natural fit, because I felt there was something there that was worth exploring. And I still do.

I'm still undecided how much of the stuff you do with functions in an FP language can be easily brought to Ruby, but apart from functions FP brings something else to the table, something maybe even more important: values (the "immutable" is implied).

FP is a collection of techniques that together have some interesting emergent properties, but that doesn't mean it's all or nothing. And while some will say that ["mostly functional" programming does not work](http://queue.acm.org/detail.cfm?id=2611829), I think getting used to building systems based on value semantics is something all programmers should be doing, and it's probably the biggest lesson Ruby can take home from the functional world. If you're not following there please watch Rich Hickey's excellent [The Value of Values](http://www.youtube.com/watch?v=-6BsiVyC1kM).

I'm clearly not the only one reasoning in this direction. To have value semantics of composed data types without sacrificing too much in terms of performance you need good persistent data types, and several projects are underway to bring these to Ruby. I've started a humble effort to [coordinate and align these efforts by having shared specs and benchmarks](https://github.com/plexus/rubydataspec).

So maybe that should form the core of the book? Less about functions and lambdas, more about values? It's certainly more practical advice then trying to write lisp-with-ruby-syntax. Except there's no implementation of persistent data structures I would recommend to use in a production setting today, so how pragmatic are we talking, really? But yeah, maybe starting from the "value object" section I already have, and turn that into half a book, show how it composes into bigger systems. Demonstrate helpful gems like Anima, Adamantium, Equalizer. Show step by step how to implement a cons based list, a hash array mapped trie, a zipper, that kind of thing.

And for the "functional" stuff, I think for that there's a bright (or at least dimly lit) future as well. I've been trying stuff out for over a year, for Hexp, for Yaks, for other projects. I bundled a bunch of utility functions [here](https://github.com/plexus/fp/blob/master/lib/fp.rb), but I can't say I've found a sweet spot of expressive syntax just yet.

Conclusion: I need to clarify who this book is for, and what it tries to achieve. I need to write an outline, and basically (almost) start from scratch. I've also been doing a lot of traveling the past year, which has really cut into my productivity. I will be back in Berlin in a few days and plan to travel a lot less the coming months. I also went from contracting five days a week to four days a week. All of that means that I should have some time on my hands. I intend to start working a bit harder on my open source projects, especially [Hexp](https://github.com/plexus/hexp) and [Yaks](https://github.com/plexus/yaks), and also on [RubyDataSpec](https://github.com/plexus/rubydataspec), which should indirectly keep me involved in [Hamster](https://github.com/hamstergem/hamster), [Persistent](https://github.com/Who828/persistent_data_structures) (working title) and [Clojr](https://github.com/headius/clojr). Hopefully I can then return to writing on the Happy Lambda with more experience, a better battle plan, and higher confidence.

Thanks for listening.