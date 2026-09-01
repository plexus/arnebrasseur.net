---
title: "Lambda Island Open Source Update May 2020"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2020-06-05T15:46:30.000Z"
updated_at: "2026-05-16T13:41:47.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-18 14:54"]
---

[Felipe](https://twitter.com/flpbar) and I have been busy again, read all about what we’ve been up to in this month’s Lambda Island Open Source update.

We currently have two major projects we are working to get out: Regal and Kaocha-cljs2. They are both still a work in progress, but we’ve again made some good progress this month. Regal is not officially out yet but is at the stage where people can safely start incorporating it into their projects. Kaocha-cljs2 is a big undertaking, but we’re splitting this work into smaller self-contained pieces, and two of those saw their first release this month: Chui and Funnel.

## Regal

[Regal](https://github.com/lambdaisland/regal) is a regular expression Swiss army knife, built around “regal syntax”, a way of representing regexes as Clojure data structures, similar to what Hiccup syntax does for HTML.

For instance the regex `#"(\w+)=(\w+)"` would look like this:
    
    
    [:cat
     [:capture [:+ :word]]
     "="
     [:capture [:+ :word]]]
    

Regal can convert regal forms to regexes, but also parse regexes back to regal. It can create generators for use with [test.check](https://github.com/clojure/test.check), and integrates with clojure.spec or [Malli](github.com/metosin/malli).

Regal also knows about different regex flavors, it currently supports JavaScript, Java <= version 8, or >= version 9, and can parse and generate regexes for each flavor. This means the semantics of a given regal form are constant, it will always match the same strings, no matter the platform. It also means you can do things like convert JavaScript regexes so you can use them in Clojure.

All the major pieces of Regal of there, and we don’t expect the syntax of Regal forms to change going forward. The tricky part is that we have multiple pieces of code that encode the semantics of these regal forms. The regex generator, the parser, the test.check generators, as well as specs for validating, and possibly generating regal forms. These all need to match up.

Most of the recent changes have been about fine tuning these different implementations. We might come across a regex that trips up the parser, or cases where generating and then parsing a regex doesn’t yield the same result.

So far we’ve dealt with this by having data-driven [test-cases](https://github.com/lambdaisland/regal/blob/master/resources/lambdaisland/regal/test_cases.edn)that cover all the regex features we support. These are used in multiple ways to test the different parts of Regal, and we still have some more cases we need to check for to make sure all our bases are covered.

But the best way to check that multiple implementations match is through generative testing. We were unable to do this so far because we weren’t able to reliably generate regal expressions. Their recursive nature means that generators easily overflow the stack. However we’ve [managed to work around this](https://github.com/lambdaisland/regal/blob/master/src/lambdaisland/regal/spec_alpha.cljc#L12-L33), and so the road to more property-based testing is now open.

## Kaocha-cljs2

Kaocha seeks to be a complete solution to all your Clojure testing needs, and of course that includes ClojureScript. When we came out with Kaocha-cljs this drastically lowered the bar for people to start writing tests in ClojureScript. All you needed was a single line in your `tests.edn`, and things just worked …
    
    
    #kaocha/v1
    {:tests [{:type :kaocha.type/cljs}]}
    

… unless they didn’t. Dealing with ClojureScript is inherintly complex. You can’t just look for tests on the filesystem and run them. The ClojureScript compiler needs to be involved to compile your tests to JavaScript, and then a JavaScript runtime is needed to execute them, and meanwhile Kaocha needs to stay in control, deciding which tests to run, and collecting their results.

Kaocha-cljs managed to push that complexity away by building on top of ClojureScript’s repl-env abstraction, in particular the REPL implementations for the browser and for Node.js. These repl-envs take care of launching a JavaScript runtime and communicating with it. They also handle the ClojureScript compilation, so that we can just send snippets of code their way, and they will compile them, send them on to the browser or node, and notify us of the result.

In a way this worked better than we had expected, and many people are quite content with what kaocha-cljs currently offers, but we had some persistent complaints.

People who used Shadow-cljs were out of luck, shadow has its own compilation pipeline and in particular differs from standard ClojureScript in how it deals with npm packages. That means that code written for shadow-cljs may not be compatible with the built-in repl-envs.

There’s a wide spectrum of how people set up their ClojureScript compilation. We did allow people to specify their own compiler settings, but they had to be compatible with what the repl-envs expected. You can’t for instance use advanced compilation. Given how many problems only show up when using `:advanced` this is a legitimate ask, and something we were unable to support.

Finally the repl-envs internals are opaque and hard to debug. People routinely had trouble getting them to work headless on CI, without any meaningful feedback or error message to diagnose the issue.

So we went back to the drawing board. We started by creating a new ClojureScript test runner, [Chui](https://github.com/lambdaisland/chui). Chui lets you run specific tests on demand, dynamically at runtime. This is different from the runner that ClojureScript ships with, which determines which tests to run at compile time through macros.

`chui-core` provides the test runner API. On top of that we built `chui-ui`, a browser based UI for running tests and inspecting their results.

We also built `chui-remote`, a small components which allows controlling `chui-core` over a websocket. This means that by including `chui-remote` into your build it will be able to talk to Kaocha.

This gets us out of the business of invoking the ClojureScript compiler, or of starting a JavaScript runtime. People can use whatever tool they like (cljs.main, Figwheel, Shadow-cljs), whatever compiler settings they like, and whatever runtime they like.

To facilitate the communication between Kaocha and Chui, and to make it easier to do things like reuse existing browser tabs, we created [Funnel](https://github.com/lambdaisland/funnel), you can read all about Funnel in its [release announcement (ClojureVerse)](https://clojureverse.org/t/announcing-the-first-release-of-funnel/6023).

This is all starting to come together. Kaocha-cljs2 is not ready yet for primetime, but the main parts are implemented.

All of this does mean that using Kaocha-cljs2 will be a bit more involved in terms of setup, at least in the beginning. Once the dust settles we may look into providing conveniences for common scenarios. The big win however is that by separating these different responsibilities we can cater for many more use cases.

## Glögi

Last but not least this month also saw a small release of [Glögi](https://github.com/lambdaisland/glogi), a ClojureScript logging library based on Google Closure’s `goog.log`. Glögi does two things: it provides an idiomatic logging API inspired by pedestal.log, and it implements a `console.log`-based logging backend, which provides synatax highlighting for ClojureScript data structures.

This latest releases makes Glögi more suitable suitable for use outside of a browser, e.g. in Node.js or GraalVM, where CSS formatting for `console.log` is not available.

Glögi is a great lightweight alternative to cljs-devtools for printing ClojureScript data structures in the console, and contrary to cljs-devtools it works in (nearly) all browsers.

## Closing words

That’s it for this months. If you like what we’re doing, or if your company relies on our projects, then please consider pitching in through [Open Collective](https://opencollective.com/lambda-island), or by [signing up](https://lambdaisland.com/pricing) for Lambda Island Education.

Our projects warmly welcome contributions. If you would like to get involved but you are not sure where to start then get in touch, we are always happy to help.
