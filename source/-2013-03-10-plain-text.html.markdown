---
title: Plain text
published: false
---

Take this example of escaping text :

````ruby
Shellwords.escape(%q{it's safe to say that *many* disagree})
````

Isn't it wonderful.


## TL;DR

* Plain text is the universal interface
* Most of the strings we deal with have semantics
* Often strings with one set of semantics become embedded in another context
* Strings with semantics can be parsed into data structures
* these are often intended to be executed by some virtual or physical machine

* This is super powerfull
* This is super primitive
* This leads to security problems

Today I want to talk about something that's been on my mind lately, something deep and profound : plain text. The name is a bit unfortunate, since a lot of it isn't very plain at all. But let's first see what we mean by "plain text".

Computers can only "think" in numbers, but for most use cases numbers aren't the most practical way of representing things. So some time around the 1950s people came up with a splendid idea : let us represent numbers by letters and symbols. By stringing these together we get something that's easier to understand by a human, but as far as the computer is concerned it's still a bunch of numbers underneath. And so plain text was born, and there was much rejoicing.

Fast-forward to 2013. This abstraction that's at the basis of pretty much everything we do as programmers is still basically the same. We have replaced "strings of bytes" with "strings of characters", and added another conversion layer to "encode" a certain "character set", but as far as our programs and tools are concerned it's still the same old thing.

We write programs in plain text, which when receiving a plain text request will generate more plain text that gets sent back to the browser. Our program in the meanwhile queries the database, writes log files, sends email, all in plain text. All of our programs are just there to create and transform more and more elaborate strings.

Except they're not "just strings". A program needs to be well formed

A lot of these string have special semantics, they are actually data structures in disguise. These data structures usually have the notion of a "plain text value" Suppose you have a HTTP response, with a body containing HTML, with embedded Javascript, which contains snippets of HTML and CSS.

# Emergent properties

Emergence
: Emergence is the way complex systems and patterns arise out of a multiplicity of relatively simple interactions.

:trollface:
