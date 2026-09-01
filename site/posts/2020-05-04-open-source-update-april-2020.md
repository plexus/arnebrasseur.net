---
title: "Lambda Island Open Source Update April 2020"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2020-05-04T09:26:32.000Z"
updated_at: "2026-05-16T13:41:47.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-18 14:51"]
---

With people across the globe isolating themselves it feels sometimes like time is standing still, but looking back it’s been a busy month again for the folks here at Lambda Island. Besides all the maintenance releases we shipped some new features for [lambdaisland.uri](https://github.com/lambdaisland/uri), our JS logging library [Glögi](https://github.com/lambdaisland/glogi), and a new micro-library, [edn-lines](https://github.com/lambdaisland/edn-lines). We also have some exciting work in progress, including a brand new browser-based test runner for ClojureScript, written with shadow-cljs in mind.

## Funding

A big shout goes out to two of our clients, [Nextjournal](http://nextjournal.com/) and [Pitch](https://pitch.com). They have made a lot of this work possible either by funding projects directly, or by dogfooding our tools and libraries and giving us an opportunity to improve them. Thank you both for investing in this great ecosystem.

## Contributions

We got again some great contributions this month!

[Daniel Compton](https://twitter.com/danielwithmusic) contributed two plugins to [kaocha](https://github.com/lambdaisland/kaocha), an `:orchestra` plugin for instrumenting functions with spec and orchestra, and a `:preloads` plugin, useful for preloading files with specs.

[JarrodCTaylor](https://github.com/JarrodCTaylor) contributed a fix to [deep-diff2](https://github.com/lambdaisland/deep-diff2) to make it usable in a browser context, and we’re already using this in Chui to show nice diffs, similar to how Kaocha does it.

[Jack Rusher](https://twitter.com/jackrusher/) contributed a patch to [parseedn](https://github.com/clojure-emacs/parseedn), our EDN parser/printer for Emacs lisp, with support for converting dotted pairs to EDN, using similar heuristics as Emacs’s JSON library.

Of course we also contribute to the projects we use. This month [Davide Taviani](https://twitter.com/Helios) contributed patches to clj-kondo.

We absolutely love getting contributions big and small, from first-timers or Clojure veterans. We’ve started adding instructions about the contribution process to all of our project READMEs to make it clear what to expect as a contributor.

If you are not able to contribute code but you still want to support our projects then consider pitching in to the [Open Collective](http://opencollective.com/lambda-island).

## Glögi

In Clojure our go-to logging solution is pedestal.log + logback. Having logs that are just EDN data makes filtering and processing them trivial, and being able to fine tune the log level per namespace or hierarchy is invaluable.

To provide the same experience on the ClojureScript side we created [Glögi](https://github.com/lambdaisland/glogi). Glögi detects if you are using cljs-devtools, so you can navigate and inspect your data, while doing regular console logging when cljs-devtools is not available.

We’ve now adding syntax highlighting to the console logging, so you get pretty colorized log messages, no matter which browser you are using.

![Glogi screenshot](https://img.lambdaisland.com/44082c318f1e53c08827a8961623385b.png)

## Uri

[lambdaisland/uri](https://github.com/lambdaisland/uri) was the first open source library we released. An idiomatic and cross-platform library for manipulating URIs, that sticks closely to the RFC.

Uri has served us well over the years, but one bit that was still missing was the manipulation of individual query parameters. According to the URI RFC a query string can really be anything, so we never attempted to further parse it into components, but the convention of having pairs separated by `&` and `=` has become so ubiquitous that it made sense to support that directly.
    
    
    (uri/assoc-query "//example.com"
                     :id 1
                     :name "Arne")
    ;;=> #lambdaisland/uri "//example.com?name=Arne&id=1"
    
    (uri/query-map "?foo=%20%2B%26%3D")
    ;; => {:foo " +&="}
    

We’ve also improved the normalization of query parameters to make sure these semantics are preserved.

## Regal

We’ve continued to work towards a first release of [Regal](https://github.com/lambdaisland/regal), our regular expression library, painstakingly going through all Regex features that are supported by (but sometimes implemented differently on) Java and JavaScript, to make sure we provide the same semantics on both platforms.

After talking to Malcolm Sparks from Juxt at ClojureD we realized it would be valuable to decouple the regex flavor from the runtime platform, so you can for instance parse or generate JavaScript-flavored regular expressions in Clojure. The use case Malcolm pointed out is dealing with JSON Schema on the Clojure/JVM side. What Regal now allows you to do is parse a JavaScript regex to a Regal expression (Hiccup), then convert that to a Java regex (or vice versa).

We’ve also been fine tuning our generators and parser, using generative testing to make sure all parts of the library match up in terms of regex semantics.

## Chui

A while ago we were approached by [Pitch](https://pitch.com), to see if we could help improve their testing setup. Pitch writes a lot of ClojureScript, and uses shadow-cljs as its main build tool. They were using shadow-cljs’s `:browser-test` target together with `cljs-test-display` as UI, but the experience was sub-par. With little affordances to focus in on specific tests people were forced to re-run the whole test suite over and over, which was actually discouraging people to write tests at all.

Test tooling for ClojureScript has always been somewhat of a challenge. Because ClojureScript needs to be compiled up-front, and needs to be loaded in a separate JavaScript runtime environment, it lacks the dynamic nature and runtime inspection that Clojure has.

Most existing cljs test tooling takes the approach of building a single blob containing both test runner and tests, and then running that as a unit.

After some hammock time we figured the way forward would be to capture as much metadata about tests as possible during compilation, so that at runtime it becomes possible to run tests on demand. The result is Chui. The core of Chui is a new test runner API for ClojureScript (`lambdaisland.chui.runner`). On top of this we then provide a browser-based GUI (`lambdaisland.chui.ui`), and a shadow-specific `:runner-ns` which combines the two (`lambdaisland.chui.shadowrun`).

Chui can in principle be used with any build tool, but it works particularly well with shadow-cljs because of the functionality provided by `:browser-test`, which handles finding tests on the file system and injecting them into the build. Without this (or something similar) you will have to manually add `:require` statements for all your test namespaces. Hopefully other tooling like Figwheel-main will add similar functionality in the future, or perhaps we can even get it into the ClojureScript compiler itself.

Chui is not released yet, but we are in the final stretch, and you can find instructions on Github to already try it out yourself.

The Chui runner will also form the basis for the next generation of kaocha-cljs, which we are working on with support from Nextjournal. I’m sure we’ll have more to say about that next month.

![Chui screenshot](https://img.lambdaisland.com/2ec6384708d83d6116fd7a0df987cda7.png)

## Kaocha

![](https://img.lambdaisland.com/368d754b43d22ad1b9e719b8c6055df3.png)

Kaocha got a new logo! The talented [Stefanie Grunwald](https://twitter.com/moertel) has started work on a series of pixel-art logos for our projects, the first one being this anvil for Kaocha. We can’t wait to see this on a sticker!
