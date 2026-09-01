---
title: "Simple and Happy; is Clojure dying, and what has Ruby got to do with it?"
slug: "simple-and-happy-is-clojure-dying-and-what-has-ruby-got-to-do-with-it"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2017-05-25T15:55:39.000Z"
updated_at: "2026-05-16T13:42:09.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-09 12:20"]
---

The past week or so a lot of discussion and introspection has been happening in the Clojure community. Eric Normand responded to my [one year Lambda Island post](https://lambdaisland.com/blog/13-05-2017-lambda-island-turns-one) with [some reflections](https://purelyfunctional.tv/issues/purelyfunctional-tv-newsletter-225-spec-deep-learning-new-orleans/) on the size and growth of the community.

And then Zack Maril lamented on Twitter: [“I’m calling it, clojure’s dying more than it is growing”](https://twitter.com/ZackMaril/status/863915988424589312). This sparked a mega-thread, which was still raging four days later. A [parallel discussion thread](https://www.reddit.com/r/Clojure/comments/6brhsq/interesting_discussion_on_clojure_growth_and/) formed on Reddit. Someone asked if their were any [Clojure failure stories](https://twitter.com/mark_engelberg/status/865370061489356800). They were pointed at [this talk](https://www.youtube.com/watch?v=doZ0XAc9Wtc) from RubyConf 2016, which seemed to hit a lot of people right in the feels, and [sparked a subthread](https://twitter.com/ztellman/status/865649146476113923) with a life of its own.

Meanwhile Ray, one of the hosts of the [defn](https://defn.audio/) podcast [reacted](https://twitter.com/raymcdermott/status/866216201709662208) to the original tweet: “I’m calling it: Clojure is alive and well with excellent defaults for productive and sustainable software development.” This sparked another big thread.

Finally [@phillmv](https://twitter.com/phillmv), the person who did the talk that sparked so much debate, wrote his own summary of the discussion while reiterating some of his points in a blog post: [Simple Ain’t Easy, but Hard Ain’t Simple: Leaving Clojure for Ruby](https://blog.appcanary.com/2017/hard-isnt-simple-ruby-clojure.html).

It was all a bit much to be honest, but if we care about a healthy and growing Clojure community then it’s important that we have these conversations. Let’s try to unpack some of the points that were raised.

## Clojure is dying

This statement initially set fire to the whole debate, but there seems to be little proof that this is true. This [ask HN thread](https://news.ycombinator.com/item?id=14302762&__s=r8nvw64mp3jsgweqgmcs) about who’s using Clojure shows plenty of companies adopting it recently. The [list of companies on Clojure.org](https://clojure.org/community/companies) mentions Amazon, Facebook, Deutsche Bank, eBay, PayPal, Oracle, ThoughtWorks, Red Hat, Salesforce, Walmart, and 271 others. Onyx raised [500’000$US in funding](http://www.onyxplatform.org/jekyll/update/2016/08/17/Funding.html) last year, and the Taiwanese government is [using Clojure](https://blog.pol.is/pol-is-in-taiwan-da7570d372b5) for its citizen participation platform.

The [community Slack](http://clojurians.slack.com/) grew from just over 6000 to close to 10K members in the past 12 months. Meetup.com lists 432 [meetup groups](https://www.meetup.com/find/?allMeetups=false&keywords=clojure&radius=Infinity) that mention Clojure. We could definitely use more Clojure conferences, but with 8 annual conferences worldwide there are already twice as many as a few years back, and most of them seem to be growing in size year on year.

One personal observation: at my first EuroClojure in Berlin in 2013, most people I spoke to were enthusiasts. They programmed Java or Ruby or C# at their day jobs, and were dreaming of landing a Clojure job one day. At last year’s EuroClojure in Bratislava it was the opposite, it seemed most people were doing Clojure professionally in some shape or form.

So it seems industry adoption has grown significantly, and continues to gather steam. Mindshare in the open source and developer enthusiast communities has grown more slowly, and it’s worthwhile asking why that is.

## “Simple vs. Easy” vs. “Programmer Happiness”

What also hit the internet last week was David Nolen’s talk from GOTO 2017, [Idée Fixe](https://www.youtube.com/watch?v=lzXHMy4ewtM). It’s a great talk. In his casual style he delivers some great insights. The central theme of the talk is “fixed ideas” standing in the way of progress. By becoming fixed on ideas which worked in the past, we lose the openness of mind needed to come up with novel ideas, even when experience or a changing context demonstrate that these old ideas no longer serve us.

Much of last week’s debate has centered on AppCanary’s choice to move back to Ruby, after a bad experience trying to build their platform in Clojure, and so it was a lot of “Clojure vs Ruby”, both on a language level, and on a cultural and philosophical level. I have been doing Ruby since 2006 and Clojure since 2013, I spoke at Ruby and Clojure conferences, and I’m intimately familiar with both of these languages and communities. _I have some feels about this, y’all_.

Each of these has their most prominent mantra. For Clojure it’s “Simple vs. Easy”, while the Rubyist chant is “Optimized for Programmer Happiness”. What do these mean, really? And what are the blind spots, the things we are simply unable to see, because these fixed ideas are in the way?

## Simple vs. Easy

At Strange Loop 2011 Rich Hickey delivered one of his classic talks, [Simple Made Easy](https://www.infoq.com/presentations/Simple-Made-Easy), which would have a lasting effect on the discourse around Clojure. In it he contrasts “simple” with “easy”. Simple is the opposite of complex, it is a measure of how entangled things are, of how much is folded and twisted into one. Simple software is focused, it does not interleave multiple concerns.

Easy is the opposite of hard, it is a measure of the effort required to perform a task. This is largely subjective, something we are familiar with requires less effort, and so is perceived as easy.

When the selling point of a framework is that you can “build a blog in 15 minutes”, or that a DSL “reads like English”, then you’re talking about ease. Ease helps us when initially writing code. It lets us move fast, and that is not undesirable.

In the longer run other aspects come to the foreground. Why is it that, as projects age, the speed of development tends to slow down? That it becomes harder to make changes? How do you build something non-trivial over time, while remaining confident in its quality and correctness?

For that you need to be able to reason about the system. Human minds can only juggle so many things at the same time, and because of this simplicity reigns supreme. We can become familiar with hard things and thus make them easy, but dealing with complexity will continue to hurt your brain. Years of practice won’t change that.

And so Rich’s assertion is that many balls of mud sprang from a focus on ease, while sweeping complexity under the mat. And so he decided to build a language based on those features that promote simplicity, and rejecting those that lead to complexity.

Mutable state intertwines everything it touches. Objects mix functionality and state. Variables add a dimension of time to any code that references them.

Contrast that with immutable values, which are always local in time and space; pure functions, which can be reasoned about in isolation; reference types which make the dimension of time explicit by [separating values from identities](https://www.youtube.com/watch?v=-6BsiVyC1kM).

Clojure is not designed to be hard, to the contrary, I think a lot of thought has gone into making it easy to use as well, but when ease and simplicity are at odds, then simplicity always wins.

## Programmer Happiness

Ruby’s creator, Yukihiro Matsumoto (Matz for short), has [stated repeatedly](https://siliconangle.com/blog/2011/08/31/qa-with-yukihiro-matz-matsumoto-the-creator-of-ruby/) that Ruby is designed to make programmers happy.

> The goal of Ruby is to make programmers happy. I started out to make a programming language that would make me happy, and as a side effect it’s made many, many programmers happy.

He doesn’t really spell out what those things are that make him happy, but we can gather some insight by looking at what he borrowed from other languages, and what he left out. What may surprise you is that, before creating Ruby, Matz’s primary language was Lisp. Perl and Python are also cited as major influences. He wanted to build a modern object oriented scripting language, more suitable for building large systems than Perl, which he considered a toy language, and more pervasively object-oriented than Python, and so he borrowed the object model from Smalltalk.

When it comes to modeling software Ruby comes closest to Smalltalk, while syntactically, Ruby feels more like Perl, once the favorite scripting language of hackers and tinkerers the world over. Perl is the brainchild of [Larry Wall](https://en.wikipedia.org/wiki/Larry_Wall), by education a linguist. Perl is heavily inspired by natural language, it championed the idea that programming languages should be designed for the human doing the programming, not for the machine running the program. In his talk [Perl, the first postmodern computer language](http://www.wall.org/~larry/pm.html), Larry says,

> So, to drag the subject back to computers, one of the characteristics of a postmodern computer language is that it puts the focus not so much onto the problem to be solved, but rather onto the person trying to solve the problem. I claim that Perl does that, and I also claim that, on some level or other, it was the first computer language to do that.

Much of that talk goes into the contrast of a reductionist Modernism vs a more holistic postmodernism, it’s a fascinating read, I highly recommend it.

> Modernism, as a form of Classicalism, was always striving for simplicity, and was therefore essentially reductionistic. That is, it tended to take things to pieces. That actually hasn’t changed much. It’s just that Modernism tended to take one of the pieces in isolation and glorify it, while postmodernism tries to show you all the pieces at once, and how they relate to each other.

This still hasn’t given us a clear sense of what is meant with “programmer happiness”, but I’ll venture that it has to do with approaching things in a natural, organic way. Designing a language that appeals to the analytic and the creative and emotional brain equally. A language that loosens the straightjacket and lets you express yourself freely. Ruby promotes [humane interfaces](https://martinfowler.com/bliki/HumaneInterface.html)

> The essence of the humane interface is to find out what people want to do and design the interface so that it’s really easy to do the common case.

Even when picking up a library for the first time you can often guess method names without looking at the docs. It just “does what you would expect”. Some even add aliases. Would you like some `is_a?` or do you prefer `instance_of?`? It doesn’t matter, they do the exact same thing!

## Fixed ideas

Is there any surprise that these two sides seem to be speaking past each other? It’s like they live in different worlds. How can a Clojurist wearing their “Simple vs Easy” sunglasses ever comprehend the joy of creation sparking in the heart of a Rubyist? And how do we explain to the Ruby programmer the beauty of functional code, of having code and data laid bare, nothing up the sleeve, nothing tucked under the covers, just plain values, endlessly flowing, when all they can see is boilerplate code and single letter variable names?

And yet each side has much to learn from the other, once we get past our fixed ideas.

“Simple Made Easy” has instilled in many minds the notion that simple and easy are two sides of a spectrum, that they are necessarily at odds. But simple/complex and easy/hard are separate dimensions. While there’s great merit in striving for simplicity, it would be a mistake to disregard ease. I think as Clojure programmers, as library authors, we have been too focused on the reductionist, on building solid parts. But it’s only when these parts come together and meet the real world that magic happens.

The real world is messy, it’s a complex tangle, a braiding of concerns and constraints. Dealing with that is why we are programmers, this is the real measure of our worth. At the heart of our systems we must strive for “simple”, but at the edges we must inspire, delight, make the impossible effortless. We must do this for our customers, and we must do this for our peers.

Every tool, every library forms an interface, a language that people must first master to convey their intent. Pay as much attention to designing these interfaces as you would designing their functional internals. This is the face with which your code looks upon the world. Make it smile with its eyes.

To [quote](http://www.wussu.com/laotzu/laotzu64.html) Laozi,

> People usually fail when they are on the verge of success.  
> So give as much care to the end as to the beginning;  
> Then there will be no failure.

And Rubyists, you too have something to gain from this exchange. Yes, you’ve been hearing for years of the benefits of immutable data, the elegance of pure functions. These are interesting ideas, but don’t just read about them, try them out, use them in anger, and pay attention to how this influences the resulting system. You can [do this in Ruby](https://leanpub.com/happylambda), but it will be hard to break out of old habits, so instead I recommend picking up a simpler, functional language, Clojure, OCaml, Elixir, and putting it through its paces. Afterwards you’ll look at your Ruby projects with fresh eyes, and a new sense of where complexity comes from.

I’ll leave you with one more data point to ponder: Clojure has polymorphism, but it [eschews concrete derivation](https://groups.google.com/forum/#!msg/clojure/pZFl8gj1lMs/qVfIjQ4jDDMJ). It has interfaces and protocols, but no inheritance chains. It’s not a design decision that gets put in the limelight much, but it is one I encourage you to study. This alone has tremendous impact on how maintainable systems are.

## MINSWAN

One interesting thing that surfaced in the debates is that while some describe the Clojure community as “incredibly friendly and helpful”, others describe it as hostile. Well, which one is it? Again I think it’s interesting to compare and contrast with the Ruby community.

From my experience, the average interaction on Clojure’s Slack, mailing lists or Github issues is polite and friendly. I see people tirelessly and patiently answering questions in the `#beginners` channel and elsewhere. People really want to help.

And yet it’s a very different vibe from what I see in the Ruby world. There people really go out of their way to be nice to each other. Github issues are accompanied by a bit more elaborate explanations, some emoji or animated gifs, because it’s a low bandwith medium and you really want to make sure the human on the other side doesn’t take things the wrong way. A phrase that was popular in the early days was “MINSWAN”, “Matz is Nice So We Are Nice”. It’s something that deeply permeates this community, it’s part of what made Rails Girls such a huge success, and it’s why I still love going to Ruby conferences. It’s a vibe that’s hard to beat.

There are trolls in Rubyland too, but they get called out immediately. There is very little tolerance for less than friendly behavior. Virtually every conference and meetup has, and enforces, a Code of Conduct. Being welcoming, especially to beginners, is an essential part of Ruby’s DNA. How to be and remain open and welcoming is an important part of the discourse.

Clojure is different, it’s a mixed bunch. There are old-school lispers, converted Java programmers, people coming from other dynlangs like Ruby or Python, and they all carry different expectations. If you’ve been active in open source for more than a decade you know that a thick skin was a basic requirement. Harsh interactions were par for the course. People do what they are used to, and without a guiding principle like MINSWAN it’s harder to call people out and hold everyone to higher standards.

In the Clojure world, as in Clojure code, brevity is considered a feature, but there’s a thin line between being concise, and being curt. People still try to be friendly and nice, but the dial isn’t turned up all the way to 11. This is painfully true when it comes to the official docs, which seem to be written for machines rather than humans, it shows in the error messages, and it shows in many small online interactions.

Languages that draw a younger, more homogeneous crowd: Elm, Elixir, JavaScript, seem to be better at prioritizing the humanistic aspect. Both in the affordances of the software (Elm’s famous error messages), and in person-to-person interactions. I think we can, and should, take a leaf out of their book.

## Catering for the Novice

People cite many pain points when coming to Clojure. Chas Emerick [summarized](https://twitter.com/cemerick/status/865657364317298690) it well:

> ✅ jvm pain  
> ✅ docs/error msgs  
> ✅ deployment pain  
> ✅ tooling/debugging pain  
> ✅ domain modeling difficulty

There’s truth in all of these, and we as a community will have to stand up if we want to change these, but I want to focus on one more thing in particular: meeting the needs of the absolute beginner.

The [Dreyfus model](https://en.wikipedia.org/wiki/Dreyfus_model_of_skill_acquisition) distinguishes five levels of competence, from novice to mastery. At the absolute beginner level people execute tasks based on “rigid adherence to taught rules or plans”. Beginners need _recipes_. They don’t need a list of parts, or a dozen different ways to do the same thing. Instead what works are step by step instructions that they can internalize. As they practice them over time they learn the reasoning behind them, and learn to deviate from them and improvise, but they first need to _feel like they’re doing something_.

I have taught many people Ruby on Rails. Rails is tremendously complex (and complected), but it gets this part right. In a few sessions you can teach a person to create an interactive website backed by a database. It’s frustrating to teach because you’re walking a tight rope. Just follow the steps. Don’t peek under the covers. Don’t stare into the abyss. But that feeling of instantly being productive is what draws people in, what makes them think, “hey, maybe I _can_ do this”.

People criticize this as optimizing the wrong thing. Yes, initial prototyping goes really fast, but long term maintainability suffers. This criticism isn’t without merit, but it tends to overlook this important aspect. “The Rails Way” provides a framework that makes teaching easy. It means you can just dive in and ignore all the peripheral stuff until later.

For ClojureBridge we tend to teach graphics programming with Quil, with Nightcode as the editor. There the experience is similar. Within the first hour people are doing stuff, they’re building things. And there are plenty of recipes out there for Quil that people can adopt and run with.

But in general, and for web development in particular, Clojure fails to deliver that experience. This may sound like a case for more of a “framework” and less of a “library” approach, but I think the bigger issue is documentation. We need more practical tutorials and how-to guides, that provide clear recommendations of tools and libraries to use, that explain things step by step, with empathy, with beginners in mind. Guides that you can follow from top to bottom and that _actually work_ , even when you’re on Windows, or Linux, or have never heard of a classpath.

We’re also missing a dialogue about _how to program_ , both in the small: how do you structure a function, what are common idioms, how do you manipulate data structures; and in the large: doing domain modeling, architecting systems, designing interfaces. Ruby folks talk about these things all the time, but talks like these are eerily absent from Clojure conferences, and yet it’s something people consistently cite as a stumbling block.

If we want Clojure to continue to grow, we need to take responsibility for how it is perceived. Clojure is not always perceived as a friendly language. We can retort, “it was very friendly to _me_ ”, or we can listen without interrupting the speaker, believe they are speaking from good faith, and do better.

[🡅 You can comment (and upvote) on Reddit.com](https://www.reddit.com/r/Clojure/comments/6d9say/simple_and_happy_is_clojure_dying_and_what_has/)

* * *

**Update** : The <https://clojure.org> web site has a guide section for tutorials and guides and accepts pull requests.

  * <https://clojure.org/community/contributing_site>
  * <https://clojure.org/guides>



_Many thanks to[Chas Emerick](https://twitter.com/cemerick), [Eric Normand](https://twitter.com/ericnormand), and [Tobias Pfeiffer](https://twitter.com/pragtob) for reviewing an earlier version of this article._
