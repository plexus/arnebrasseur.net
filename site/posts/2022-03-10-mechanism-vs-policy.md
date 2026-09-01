---
title: "Improve your code by separating mechanism from policy"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2022-03-10T12:34:31.000Z"
updated_at: "2026-05-16T13:41:36.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-15 08:16"]
---

by Arne Brasseur

Years ago when I was still a programming baby, I read about a concept, a way of thinking about code, and it has greatly influenced how I think about code to this day. I’ve found it tremendously helpful when thinking about API design, or how to compose and layer systems.

Unfortunately I’ve found it hard at times to explain succinctly what I mean. I’ve also realized that I’ve built up my own mental framework around this concept that goes I think beyond the original formulation. So, in the hope of inspiring someone else, to maybe elicit some interesting discussion, and also just so that I have a convenient place on the web to link to, here’s my take on “policy” vs “mechanism”.

It’s a concept I first came across some 15 years ago in [The Art of UNIX Programming](https://www.linuxtopia.org/online_books/programming_books/art_of_unix_programming/ch01s06_3.html), where it’s referred to as the “Rule of Separation”. I’ve always been hesitant to refer to this book, because the author is a highly problematic character, but it’s the only place where I’ve seen this concept explained, and I would really like it to be more widely known. Wikipedia has a page on [Separation of mechanism and policy](https://en.wikipedia.org/wiki/Separation_of_mechanism_and_policy), but it mostly talks about security policies, rather than software development in general.

The basic idea is very simple: make a clear distinction between code that implements primitive functionality (mechanism), and code that implements application and business logic (policy).

Mechanism are things like parsing JSON, sending an email, or GUI rendering primitives; policy are things like a checkout flow, or a bulk data processing task that reads and writes from an S3 bucket, or the implementation of the look and feel of your application.

The distinction here isn’t so much in _what_ the code does, as in _how_ it is written. There is a qualitative difference between pure Mechanism code vs Policy code, and a lot of real-world code ends up somewhere in the middle, thus creating a spectrum. I believe that pushing your code to live closer to the edges of that spectrum will improve maintainability, and make your application easier to evolve over time.

Here are some of the ways I think mechanism code differs from policy code:

Mechanism Code| Policy Code  
---|---  
Unopinionated| Opinionated  
Context-free| Contextual  
Individual pieces| Integrated whole  
Simple| Easy  
Explicit parameters| Hard coded values and defaults  
Functional| Procedural  
Abstract| Concrete  
High level of reuse (rewiring)| Reuse through copy-paste-edit  
“Model”| “Controller”  
Low level| High level  
Easy to test| Tedious to test  
  
Mechanism code solves one specific problem, or implements a specific primitive, in a generic, unopinionated way. It doesn’t make assumptions about the context it will be called from, or how it will be combined with other primitives. All of those decisions are left to the caller.

It is truly “Simple” code (in the [Simple Made Easy](https://www.youtube.com/watch?v=SxdOUGdseq4) sense), but is often not “Easy”. Because it tries to be unopionated and makes no assumption you need to tell it exactly what you want. Everything it depends on needs to be passed in explicitly. There is a level of compromise possible here where default values are provided for the most common case. This does make it less “purely” mechanism code, since this way aspects of your policy may start trickling in to your mechanisms, but it can be the pragmatic thing to do.

Ideally I want my mechanism code to be functional, in other words: data in, data out. Even if the underlying mechanism is actually side-effectful, I still like to see a functional API that essentially takes instructions as input, and returns a status report as output.

Policy code, on the other hand, can be very procedural. First do this, then do that, and if that goes wrong, then do this other thing. It’s fine if stuff is “hard coded”, it just needs to be clear and high level enough that it’s easy to change. So where mechanism code tends to be highly abstracted, policy code is the opposite, it deals very concretely with a specific use case. Mechanism code gets reused by calling it with different parameters, and wiring it up in different ways. Policy code doesn’t typically see actual reuse, the nearest you have is finding a piece of code that does something somewhat similar, copying it over, and then adjusting it to the task at hand.

So the rate of change is very different. Good mechanism code can live on for years with hardly any changes. You may fix a bug from time to time, or make it more efficient, or perhaps extend it to support more features, but the core thing that it does stays the same.

Policy code on the other hand is really just a bunch of opinions. This is how we decided that things should currently work. Expect to be tweaking and changing this almost constantly.

Obviously mechanism code makes a prime candidate for extraction into a library, and indeed many of the mechanisms we rely on we simply get by pulling in the right library. Part of assessing whether you want to adopt a certain library can be to look at how much it applies this philosophy of separation. As I mentioned before, a lot of code ends up somewhere in the muddy middle ground, being somewhat generic and abstracted, but still making a lot of assumptions that might be true for the library author, but not necessarily for other library consumers.

Extracting mechanism code into a library can be a valuable exercise because it allows you to consider the code in isolation, to think about its API, its input domain, and so forth. You can test it really rigorously to make sure you have a piece of kit you feel confident about, and then later leverage it worry-free in your policy code.

You get a lot of value out of writing tests for mechanism code, especially with techniques like property-based testing where you can make assertions over a large part of the possible inputs. Since the code tends to be fairly stable you don’t have the problem of constantly having to adjust your tests. The main thing you do over time is shaving off edge cases, patching them up and adding a regression test.

Testing policy code is a lot harder, it often integrates multiple application layers, from UI to database, so there is more setup, state, timing concerns, and more chance of flaky tests. And since the code is expected to change continuously, maintaining these kind of tests can add a significant maintenance burden. That is not to say you should never test your policy code, but think carefully about the trade-offs you are making. Maybe a smaller amount of tests covering the things that are most likely to cause issues will give you more bang for your buck.

I hope this exposition about mechanism vs policy was helpful. If this resonates with you then please come join the conversation [on Discord](https://discord.gg/pCuZBDRruW).
