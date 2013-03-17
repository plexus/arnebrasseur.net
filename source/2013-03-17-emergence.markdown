---
title: Emergence for Developers
date: 2013-03-17
---

In this post I want to dig into "Emergence", what it is, how it applies to software development, and why it matters.

## What it is

Simply put, emergence explains how some things are "more than the sum of their parts." When something is made up out of simple components performing simple interactions, and yet the end result exhibits complex, seemingly intelligent properties, then we call these emergent properties.

The reason that sentence is stated in such an abstract way is that this concept applies to very different things, such as games, birds and free markets. A few examples will help.

### Go!

Take the game of Go, an East Asian game that's been around for centuries. The rules of Go are very simple. One player plays with white stones, the other with black, and taking turns you place one of your stones on the crossing points of a raster. When you have completely surrounded a group of stones from your opponent, she loses those stones.

That's pretty much all there is to it, you could be up and playing in ten minutes. And yet the game of Go is known for being incredibly deep. It takes years of practice to become good at it, and a lot of that time is spent studying the behavior of specific patterns. One of the first things you'll learn is that a group of stones can no longer be slain once it has two "eyes", two holes in an otherwise connected group. Yet this is not part of the rules, it is a higher order property that emerges by applying the rules.

### Flocks of Birds

There are lots of good examples of emergence in nature. There are emergent structures like sand dunes or water crystals. Organisms like animals and plants could be called emergent, since how they behave is not immediately apparent by looking at the organs, cells and molecules they are composed of.

Flocks of birds can appear to have a mind of their own, changing directions, dodging and diving with wondrous coordination. Yet the basic "rules" that govern are simply that each bird 1) flies in the sames direction as its neighbors, 2) remains close to its neighbors and 3) avoids collision.

### Bitcoin

A favorite topic among programmers! While there is some very ingenious cryptography involved in Bitcoin, the general principles are relatively easy to understand. Yet the fact that Bitcoin "works" is because some of the amazing properties that emerge through its network of cooperating clients. Essentially Bitcoin consists of a long history of financial transactions. This history is continuously shared between all clients.

The clients have some simple rules to determine what they accept as the correct version of history. With enough processing power, and enough luck, you can "find" the next chunk of history, one that has all the necessary properties to be accepted by the others. It can happen however that two valid "chunks" are found at the same time, creating two versions of history. Yet again by some simple rules, before long all will "agree" which version is correct, and the other one will be discarded.

(There has been a recent case of history actually "forking", requiring human intervention. This was due to a difference in client implementations, it shows though that small changes in the system can be enough to make emergent properties disappear again.)

### Emergentism vs Reductionism

Can everything be explained by sufficiently understanding its parts? This has been a topic of discussion in natural sciences, and the reductionists, those on the "divide and conquer" side of the argument, have come out victorious.

But that doesn't mean interest in emergence has disappeared. The properties of complex systems could be explained using the properties of their parts, but often the interactions are so intricate and complex that it makes more sense to look at things at a higher level, and study these emergent properties for their own sake.

## Emergent Software

Software is one of the most complex things that humans build, and a lot has been said and written about managing that complexity. But as we have seen in previous examples, complex behavior doesn't imply complex systems. Can we take lessons home from other emergent systems on how to achieve end results while keeping it simple?

In some ways we already have. Programmers and mathematicians like to aspire for elegance in their work. It implies that things fall into place in a natural, harmonic way. That a lot is accomplished without trying too hard. This harks back to ancient Taoist philosophy.

> When nothing is done, nothing is left undone. - Lao Tze

The Taoists have other interesting things to say that relate to this. For instance they often refer to the properties of water, a prime example of emergence in nature! I can highly recommend some Lao Tze or Zhuangzi when in need of some metaphysical inspiration.

### Interacting Objects

I find that once you start framing things in the picture of emergence, many rules of thumb in modern day programming become self obvious. Take the Single Responsibility Principle (let each object do one thing well) and the rule of Encapsulation (have clear defined boundaries between objects). They simply prepare the stage for your emergent feature to unfold.

In computer science literature it seems emergence is really only talked about in cases that take direct inspiration from nature, such as genetic algorithms, neural networks or Conway's Game of Life. But we do have a term for many of the emergent properties our programs exhibit. These are called "non-functional requirements", such as security, performance, maintainability, and being adaptable to change.

### Agile!

Everyone remember the [Agile Manifesto](http://agilemanifesto.org/)? It talks about "individuals and interactions", and "responding to change", among other things. By bringing the humans back into the picture, the Agile movement has paved the way for a whole new type of emergence. I'm no longer talking about objects in memory interacting with each other, I'm talking about you interacting with others, and with your code.

Take pair programming. At first it takes some time to get into it, but after a while a dynamic interaction emerges between the two people coding, and the code. I've heard people say that it can almost feel like you've become "one brain". There are no hard rules on how to do pairing, because every pair is different. In that sense it's a self organizing system, with three agents (the pair, and the code) interacting and organically coming up with a way of working that feels right.

In fact, I would argue that there is a fourth agent that comes into play : your test suite. Ever felt that your tests were "pushing back"? That by trying to come up with good tests, you realized you had to improve the design? Those are your tests talking to you, informing you, and your code. And by improving testability you will decrease coupling, factor out new methods and classes, and pave the way for more emergence!
