---
title: "Why Clojure?"
slug: "why-clojure"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2025-02-18T08:54:08.000Z"
updated_at: "2026-05-16T13:41:21.000Z"
authors: "Arne Brasseur"
feature_image: "__GHOST_URL__/content/images/2025/02/Clojure_logo.svg.png"
---

_This is about a 17 minute read. Feel free to pause and come back to it later._

Clojure is not one of the handful of "big" mainstream languages. This means that sometimes people are surprised that we are _all in_ on Clojure. Why go against the grain? Why make it harder for yourself by building on niche technology?

[Gaiwan](__GHOST_URL__/hi-were-gaiwan/) is mostly known as a Clojure consultancy, but we don't consider ourselves as being defined by Clojure. Rather, we are group of experienced technologists (10+ years of industry experience on average) who are deliberate and intentional about the technologies we build upon. Rather than choosing tech that is fashionable, or that has the biggest marketing budget, **we choose tech that gives us the highest leverage**. Tech that allows a small team like ours to be maximally productive, to maintain velocity as systems grow, and that allows us to keep overall complexity low. Right now, that tech is Clojure.

In this article I want cover some of the reasons of why that is. In the first place I'm writing this for engineers or technical leaders who are trying to decide if Clojure is worth investing time in. It should for the most part also be understandable by business leaders, who want to understand the business benefits of building on Clojure.

The reasons I'll outline below fall into three main categories:

  * **Developer productivity** : Clojure development is interactive, low ceremony, and high leverage. Clojure developers are happy developers that can ship quickly.
  * **Long-term maintainability** : the Clojure language and ecosystem are mature and stable, with a culture of stability that no other language ecosystem I'm aware of can match. This lets you build high-quality systems that last, while keeping maintenance costs down.
  * **Culture of Ideas** : while not a benefit of the language per se, adopting Clojure means you become part of a community which actively explores ideas from the past and present, academia and industry, to find better ways of building software. Clojure will challenge you in the best way possible.



## (hello 'clojure)

Clojure is a language in the Lisp family (also styled LISP). Lisp was conceived in the 1950s as a theoretical model for reasoning about computability, similar to the Turing machine or Lambda Calculus. It soon turned out that this theoretical model also made an excellent practical language to program in, one with a high conceptual elegance. The Lisp syntax has a one-to-one correspondence with the syntax tree data structure used to represent it, which provides several benefits compared to languages with more ad-hoc grammars. This notably made it the language of choice for AI applications during the previous big AI boom.

Interest in Lisp languages has waxed and waned over time. Over the past decade Clojure has come to prominence. The main Clojure implementation is built on top of Java's underlying machinery (the JVM), and incorporates several modern innovations in programming language design, including a complete set of well performing functional ("immutable") data types, and first class concurrency primitives. While Clojure forms a small language community and ecosystem compared to the major languages people are familiar with, it has done remarkably well for a language with no major corporate backing, and with a syntax and appearance that can seem wholly alien to people steeped in imperative curly-bracket languages or ML variants.

A host of alternative implementations exist or are under development, including ClojureScript (compile-to-js), ClojureCLR (targeting Microsoft's .NET), Babashka (a fast-booting interpreter for scripting, compiled to native using GraalVM), and Jank (native compilation), which provides reach and leverage. Clojure knowledge will transfer to multiple contexts and circumstances, and will give you access to multiple large open source ecosystems. This article takes as its reference the JVM implementation, but much of it is true for the other variants as well, with some nuance.

**What follows are some of the reasons why we find Clojure the most compelling programming language offering that exists today.**

## Interactive Development

Programming is a constant cycle of _writing_ code, and _validating_ said code. Without a feedback mechanism it is near impossible to write anything but the most trivial program and still be confident that it does what it's supposed to do.

These feedback loops come in many flavors. At its most basic people simple run their script-style programs over and over. For interactive programs they might click through its (web) UI, maybe putting some print or logging calls in the code to better see what is going on. Unit testing provides a more rigorous and repeatable feedback loop. Compilers, linters, and other analysis tools can provide a different kind of validation, a coarse grained assessment that a program is at least structurally sound. These cycles take from seconds to hours, and generally necessitate a context switch, from the editor to a terminal, UI, or CI, and back.

Short, quick feedback cycles are preferable over long, slow feedback cycles, and this feedback cycle speed is one of the biggest predictors of a programmer's productivity. Without quick and early feedback, you end up in a slow write/debug cycle, where as the cycles get slower, you end up spending ever more time debugging, compared to the time spent writing code.

All of the mentioned validation techniques are available in the Clojure world as well, with for instance sophisticated tooling for unit and property-based testing. **At the heart of Clojure development however lies the practice of _interactive development_.**

Before a single letter is written, the Clojure programmer starts up the Clojure runtime, which is connected to their editor. From here a program is "grown" by writing/running small pieces of it, and seeing the result (or error), directly and immediately, without leaving the editor.

Here the Lisp syntax is a great help, since it provides a Lego-block like uniform syntax that makes it easy to "take a program apart", in a sense, executing individual bits or large composite pieces, merely by placing the cursor in the right spot.

It's hard to overstate the impact of this style of interactive development, as it provides the quickest feedback cycle, and thus most immediate feedback possible. You will also see this referred to as "REPL driven development", which obscures its true power. Many programming languages have a REPL (also referred to as a console or command line interface) somewhere "over there", in a terminal emulator or browser devtools. Few allow you to execute arbitrary pieces of code "over here", right where you are writing them, as you are writing them, against a complete running system.

And this is only the tip of the iceberg, as this ability to connect to a running system and manipulate it has more far reaching consequences. It provides an ad-hoc inspection, debugging, and manipulation interface to any Clojure program running in any environment.

## Culture of Stability

When choosing Clojure, you don't just get a piece of powerful tech. You also become part of a community of practice, with its own notions and dogma. Even more than the tech itself it's this community of practice that really makes the choice for Clojure so compelling, and teams that adopt the tech in isolation without engaging with the wider culture and community sell themselves short. They would have been better off not choosing for Clojure at all.

One strong cultural tenet is a commitment to stability and backwards compatibility. This starts from the core language, where breaking changes are virtually unseen, despite releasing regular improvements and extensions. This has become a deeply ingrained value in the open source ecosystem surrounding the language as well, and stands in sharp contrast with almost every other modern programming ecosystem, where a certain amount of _churn_ — change for the sake of change — is taken for granted. This churn is a hard to overstate waste of resources, the global cost of which has to be measured in billions, and it's wholly avoidable.

Not so in Clojure, where it's normal to upgrade to the latest version of the language, and other project dependencies as a matter of course. You simply carry on with your day. **You can get the benefits of bug fixes, security, and performance improvements, without having to rewrite parts of your code base, or wonder what hidden subtle bugs have been introduced by breaking changes** , even in point releases, often not even documented.

I imagine at this point some eyebrows may be raised sceptically. Isn't change necessary to allow for progress? This shows a confusion between stability and stagnation. In software it is absolutely possible to have progress, to do new things, or improve existing things, without breaking the things that are already there. We live and breathe this every day.

## Information Systems / Knowledge Representation

In the space of web and business applications in particular we write programs that deal with _information_ about the world. Gathering, accessing, and processing of information, facts, is at the heart of what we do, and yet it's staggering how poor many mainstream languages perform in this area. Either they provide data representation and manipulation primitives that are needlessly low-level, or they insist on a statically typed worldview leading to parochial, snowflake APIs that defy abstraction and higher level manipulation, or both.

**Clojure's functional data structures and core set of data manipulation functions make information truly first class.** Clojure is dynamically typed, and idiomatically follows the open world assumption. RDF, the data modeling framework originally developed for the Semantic Web, has an outsized influence in the community. This is visible in the preference for triple stores/graph databases, notably Datomic. It's also visible in the language itself, where namespaced keywords are preferred, providing fully qualified identifiers for attributes that can be assigned context-free semantics.

This isn't as heavy a lift as it may sound. A Clojure map with namespaced keywords is no more complex than a bit of JSON, but it can carry precise semantics without out of band contextualization, and it can be safely extended with additional information without risking naming conflicts.

## Small composable functions over immutable data

This is another aspect that is cultural as much as it is technical. Clojure is not a purely functional language, and it's easy to translate Java, Ruby, or C code directly into Clojure. **But** **an _idiomatic_ Clojure program looks very different from an idiomatic Java program, consisting for the most part out of pure functions over immutable data.**

Immutable data provides value semantics (as opposed to reference or identity semantics), and pure functions compute a result value based purely on a tuple of input values, without having an influence on, or being influenced by, the world outside of the function. (Like reading/writing global data, or causing side effects).

This leads to several corrolaries.

## Concurrency Handling

Contemporary computing is inherently concurrent, and has been for close to 20 years. We have dealt with the limits of Moore's law by stacking processors with ever more cores, and our programs have had to keep up.

Clojure helps with this in the first place by emphasizing immutability. Operations which involve mutable memory locations introduce timing and ordering dependencies, which need to be carefully controlled when introducing parallelization. A pure data-in data-out transformation on the other hand can always run safely, regardless of what else is going on.

But programs do need to maintain state over time. For this the JVM has had excellent concurrency primitives since `java.util.concurrent` shipped in Java 5, but using them correctly still requires the care of an expert. **Clojure provides higher level abstractions on top of these that provide specific concurrency and correctness guarantees.** _Atoms_ are the most commonly used ones, providing serialization of read-then-write style operations, through Compare-and-Set (CAS) combined with automatic retries. _Refs_ provide Software Transactional Memory (STM), _Agents_ provide serialization of updates which are applied asynchronously, _Futures_ provide a fork-and-join interface backed by a thread pool. These all rely on Clojure's functional data structures (including immutable queues), providing elegant thread-safe abstractions that can be used easily without shooting yourself in the foot.

For data processing or event-driven systems there is _core.async_ , available as a library maintained by the core team, providing Communicating Sequential Processes (CSP), similar to Go's goroutines, and comparable to actor systems as found in Erlang/Elixir, or in Scala's Akka.

Of course you don't _have_ to use these higher level abstractions (see also Move up and down the trade-off/abstraction ladder), the lower level primitives are still available, including concurrent queues, atomic references, locks and semaphores, various types of thread pools, all the way down to manual thread locking and marking of synchronized critical sections, for when you do need that fine-grained control.

## Local reasoning

There's only so much even the most gifted programmer can keep into their frame of mind at any given time. Each additional piece of context that needs to be considered to assess the impact of a change, the harder it becomes to confidently and correctly make that change. This curve is a hockey stick, things go from easy to hard to impossible quickly the more distinct pieces of code and state need to be considered at the same time to understand what a program is doing.

**The fact that most of a Clojure program consists of pure functions means that one only needs to understand what the inputs for a given function are to understand the function's full behavior.**

Another aspect that helps here is that Clojure generally avoids polymorphism. There is no superclass implementing part of the behavior, you don't need to know the runtime type of objects to understand which implementation is being invoked. There are only concrete functions in namespaces. It's been said that in object oriented programming _everything happens somewhere else_. **In Clojure there is much less of this kind of indirection, making navigating around a code base to understand control flow straightforward.**

Of course you can write code that has this property in other languages, when taking sufficient care. But in non-functional languages this often means going against the grain of the language, and adopting a coding style that is not considered common or idiomatic. Other functional languages do promote this kind of purity, but lack some of the other benefits outlined in this article.

This local reasoning, together with Lisp's Lego-block-like uniform internal structure, makes it easy to refactor and evolve a code base. When refactoring the programmer improves a code base by changing its structure and organization, without changing its behavior. This can be quite challenging, since there might be implicit dependencies between different parts of the code base, through shared mutable state. Clojure encourages having a small amount of imperative code handling mutable state, separated from the otherwise purely functional code base. This makes both sides easier to develop and test, and provides some confidence that changes won't have unintended side-effects.

## Ease of testing

When working with functional code, whether during interactive programming or in a unit test, validating that a piece of your program works as expected is a matter of pushing values in and seeing which values come out. There is no careful setup and teardown of state, and loading of fixtures, no stubbing out communication channels or delicately managing timing requirements, all **common sources of the dreaded _flakiness_ in tests**. No code is easier to test than purely functional code.

This also opens the door to **higher leverage techniques** like Property Based Testing, also known as Generative Testing, where a random sequence of ever more complex input values is fed into the program, to find values that violate certain known properties or invariants, followed by a crucial shrinking phase, so the programmer is presented with a minimal examplar of the unsupported edge case.

Clojure has no unique claim to these techniques, in fact Property Based Testing originated in the Haskell community, and QuickCheck-inspired libraries are available for most major languages now. It does however **synergize with some of the other benefits outlined** , especially the emphasis on simple, immutable data structures, and with the interactive style of development.

## Positive self selection for hiring candidates

"But what about hiring?" When you use any language that isn't in the top 3 of currently most popular languages, you will get this question. JavaScript programmers are counted in the millions, Clojure programmers in the tens of thousands. How will you ever find the required talent?

It seems like a logical question, but it's overly focused on the supply side. Yes, there are fewer Clojure developers, but there are also fewer Clojure jobs. It's not useful to look at absolute numbers, you need to consider the balance between the two. Anecdotally this balance seems to be ok. From what we've seen companies looking for Clojure talent are generally able to find people, and developers looking for jobs are able to get hired.

In specific locales the story may be different. In smaller cities there might be no Clojure programmers at all, if you are intent on hiring locally. That is certainly a factor to consider. Even in bigger cities, if you are looking at hiring a lot (dozens to hundreds of people), this will be a factor. In either case you may have to find other suitable candidates, and train them into the specifics of Clojure. Nubank famously has trained hundreds of Brazilian developers to pick up Clojure out of necessity, but they describe it as a positive experience, for the company and the developers.

In either case, whether you're hiring people with the requisite Clojure experience, or training people up, what we hear over and over again is that **the quality of applicants for a Clojure job is higher** than when hiring say for JavaScript or Python. You may get only a handful of CVs instead of a few hundred, but they'll be quality CVs. Remember that Clojure is a community of ideas. It attracts people who think deeply about their craft, who are interested in finding better ways to do things, who are keen on learning advanced somewhat alien looking technologies. What we find is that both people who have studied Clojure in their own time, or people who are drawn in by the prospect of learning Clojure on the job, tend to be **curious and open minded problem solvers**. Exactly the kind of people you'd want to have on your team.

This is of course all very anecdotal, which is all we have to go on in the absence of large scale studies. We leave it to the reader to decide if they find these claims credible, or at least plausible. What I can say from working with dozens of Clojure teams over the years is that while hiring is a concern that's frequently voiced by people not (yet) doing Clojure, I have rarely heard it expressed as a major problem by teams actually doing Clojure.

## Move up and down the trade-off/abstraction ladder

Clojure is, quite decisively, a high-level language. Idiomatically, code is concise and expressive, with little ceremony or incidental complexity, in large part thanks to the functional (immutable) data structures, and accompanying data manipulation API.

Clojure's data structures perform very well _for functional (immutable) data structures_ , but **you still pay a cost for the convenience and guarantees** they provide. Clojure's maps and vectors are internally represented as trees ([Hash array mapped tries](https://en.wikipedia.org/wiki/Hash_array_mapped_trie) to be precise), and there is a certain amount of path copying involved in every update. When done in bulk this puts pressure on the garbage collector.

Clojure also provides seamless interop with Java types (see the section below on Host Interop), using runtime reflection, and automatically boxing/unboxing primitives, if necessary. This all comes at a cost.

For everyday applications **this cost is negligable, and easily justifiable** given the ease of use you get in return. Used well, functional data structures let you write smarter algorithms, so you do less work, offsetting some of these costs. But there are certainly use cases where this style of programming is not suitable. If you are writing a game graphics engine, doing realtime signal processing, or doing anything else that could be described as _number crunching_ , then you want to get down to the metal.

The good thing is that **you can get down to the metal, without leaving your familiar environment**. Providing some type hints to the compiler can eliminate runtime reflection and boxed math. You can work with contiguous arrays of primitive types, amenable to L1/L2 caching in the CPU. Optimized numeric vector/matrix types are available as libraries, including GPU backed.

Contrast this with other high level languages where when the needs get high, you may be forced to switch to native extensions in C or Rust. In Clojure instead of this dichotomy you get a sliding scale. Maybe you have an event loop that needs to be able to handle high loads. A bit of profiling, type hinting, and sprinkles of interop may be all you need. At the end of the day Clojure runs as Java bytecode, which gets optimized on the fly (JIT compilation) by the JVM. You may be surprised how much you can squeeze out of that event loop with minimal changes.

And even when doing this kind of lower level coding, you still get access to Clojure's excellent metaprogramming support, to handle some of the drudgery for you. Which brings us to the next point.

## Move up and down the metaprogramming ladder

It's been commented on a few times that Clojure is a Lisp. What makes it a Lisp isn't (just) the superficial stuff of where the parentheses go. It's the fact that in a very real sense code is a datastructure. It's like JSON, if JSON was designed to represent programs in a readable way. Instead of Javascript's objects, arrays, strings, and so forth, Clojure code is represented as nested lists, with symbols to represent functions, variables, and reserved keywords. (When used as a JSON-like data format, this syntax is known as EDN).

What's unique about Lisp is that facilities for converting between a string and a data representation of code are built into the language (known as the Reader and Writer), as well as facilities to evaluate such data structures as code, or, in the case of Clojure, compile and run them as JVM bytecode.

Macros allow the programmers to extend the syntax, essentially augmenting the compiler, by writing functions that transform this code-as-data, and this is probably the most well-known example of Lisp metaprogramming. But it's not the only option available, given these building blocks. **The cultural trend in Clojure is to use macros sparingly** , reserving them for key high leverage constructs, since macros are opaque and difficult to debug. They also make life harder for tools that do static analysis.

Instead in the Clojure open source ecosystem in particular **there's a trend towards _data driven_ interfaces**, where instead of providing concrete functions and macros, an API is provided which takes a data structures, usually some combination of nested vectors and map, and lets that drive the library's behavior. Examples are HTTP routing, HTML and CSS generation, data validation and coercion, and many more.

Superficially and syntactially the distinction is small, but **the leverage gained is significant**. Behavior is now driven through data, rather than invoked directly, and data, information, can be generated and manipulated. In fact, Clojure excels at this, as we pointed out earlier.

You now have the full power of the language to create dynamic and adaptive systems. You can transform this data specification to deal with cross-cutting concerns, or make it end-user editable by storing it in a database, which is in turn trivial because you have the Clojure Reader and Writer available at runtime.

Again this is a sliding scale, where programs and programmers will generally start out on the concrete and verbatim end of the spectrum, and stepping down a rung into metaprogramming territory when called for.

It makes Clojure particularly suitable for highly dynamic and simulation-type systems, which can be reconfigured or rewired at runtime to exhibit new behaviors. In general these techniques provides a high amount of leverage, empowering people to do much more with the same tools and libraries, without being beholding to the library's author to support their specific use case a priori.

## Host (Java) Interop

Modern applications are more glue than substance. We take a language's standard library, a few hundred open source libraries, a dozen SaaS APIs, and a handful of off the shelf components like databases and message queues, then add a bit of code on top to make it all work together. For application programmers (as opposed to system programmers) the bulk of their work is calling into APIs written by others, and wiring them together.

This means it matters a lot which open source ecosystem you have access to. By leveraging the JVM and providing excellent interop capabilities, Clojure can **leverage the millions of packages** available on Maven Central, Java's package repository, and the biggest single open source package repository in the world.

It does so with little to no ceremony. Clojure is concise compared to Java, and the interactive programming facilities make it **easy to explore APIs, and quickly wire them together**. It's not controversial to say that in this kind of exploratory glue programming Clojure beats Java hands down.

With ClojureScript all the same arguments can be made for JavaScript and the NPM package repository.

## Culture of Ideas

At the end of the day does your choice of language really matter that much? Teams and companies can be succesfull in virtually any language, and conversely no language can stop a well intentioned engineer from creating a huge mess. **A sharp blade does not make you a master chef** , and in the wrong hands may do more harm than good. And Clojure certainly has a few sharp edges. The language attempts very little hand-holding, expecting the programmer to know what they are doing. While strides have been made to improve the onboarding and learning experience, it can still feel like a trial by fire, especially with insufficient mentoring. This does lead to people becoming reasonably proficient, but still missing out on a lot of Clojure's benefits.

Indeed we've come across a good few Clojure code bases of questionable merit. Often these are written by teams with a different language background, say Java or Python, who adopted Clojure's syntax, but failed to steep themselves in the ideas and idioms of Clojure's community of practice, resulting in a LISP flavored pidgin.

On the other hand those who do embrace this culture of ideas will find they gain a more refined **mental framework for reasoning about software design** , one which transfers remarkably well to other languages and ecosystems.

We've pointed out a few ways already in which the appeal of Clojure is at least in part cultural, rather than merely technical. Much of Clojure's relative success despite major corporate backing is due to Rich Hickey's conference talks, in which he explores the ideas that influenced the design of Clojure and Datomic, as well as his own insights distilled from decades in the industry. Similarly at Clojure conferences talks tend to explore ideas, revisit influential papers, or share experiences, rather than simply presenting libraries and tools.

Fundamentally Clojure's community is one which isn't afraid to second guess itself. Here you find professionals working at the outer edge of their capabilities, always striving to learn and to find better ways of building software together, rather than merely coasting along. I am deeply grateful I can be part of it.
