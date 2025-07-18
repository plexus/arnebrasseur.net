---
title: Come try out Piglet
date: 2025-07-18
---

For the last ~2.5 years I've been working on Piglet, a Clojure-like LISP written
in JavaScript. I haven't been too public about it, apart from some fediverse
posts, mostly because it always felt like it wasn't quite ready yet for public
scrutiny.

But today I want to come out and say, "hey, Piglet is actually really cool, and
we'd love to find some curious folks who want to give it a spin!" (that "we" is
my and my colleagues at Gaiwan, who similarly think it's actually a pretty neat
project).

Does that mean Piglet is "ready" now? No, clearly not. But can you build real
working software with it, and generally have a lot of fun? Absolutely!

It's a big ambitious project, and it's going to need a lot more work. It's also
not quite delivering on its full potential yet because there are a few things I
haven't gotten to at all that will make it way more cool, and which will make
for a more compelling sales pitch, but today I don't want to dwell on
speculative stuff.

So let's instead focus on what's there:

- A LISP runtime with embedded reader and compiler, that can run in Node.js or the browser
- A pretty decent start at a bundled standard library
  - Command line argument handling
  - DOM and CSS generation
  - Reactive primitives
  - CBOR
  - Nodejs http server wrapper
- Tooling
  - Starting point of a treesitter grammar
  - Emacs mode which does syntax highlighting based on that
  - Piglet Dev Protocol (PDP), a client-server model for interactive (REPL-based) development, completion, navigation
  - Emacs PDP server. [Laurence](https://replware.dev/) is starting to look into making one for nvim
  - `pig` command line tool, start CLI REPL, PDP connection, run modules, etc.
  - `piglet` command line tool, for use in shebangs
- Some demo code to look at
  - [Piglet-contrib](https://github.com/piglet-lang/piglet-contrib) contains some useful namespaces, especially for webdev
  - [Pigrot](https://arnebrasseur.net/pigrot.html) is the beginnings of a piglet roguelike
  - [Fogio](https://github.com/plexus/fogio) is a web app that lets you browse directories, and which can serve media files. I run this on my local network.

This post is too short to really go into language-level details, but [the
README](https://github.com/piglet-lang/piglet/blob/main/README.md) lists some of
the most important features.

Recently I've mostly just been building stuff with Piglet, rather than working
on the language itself. When I notice stuff that's broken or missing I go back
and fix it, but for increasingly long stretches of time I can just focus on
building things. Some of the more recent things I fixed were `try`/`catch`
semantics (it wasn't properly returning the value of the last form), adding
`cycle` to the core namespace `piglet:lang`, and making `filter` lazy instead of
eager.

Working on web apps on nodejs has made me realize that actually we have a
stronger story there than in the browser, and that really it's more apt to think
of Piglet as a Clojure-for-JS-runtimes, rather than a ClojureScript alternative,
since the architecture is much closer to Clojure. There's no separate compiler
process. You start the runtime, and then load modules, or eval forms. Everything
runs from source. Of course there's compilation to JS happening behind the
scenes, but it's on the fly, form-by-form.

For production use on the browser you do want optimized AOT compilation (or
optimizable, i.e. amenable to tree-shaking). Some of that has been implemented,
but it's not quite there yet. It's not necessarily prohibitive, it matters
mostly for startup time, not so much for runtime performance, but it's one of
the reasons why Piglet makes more sense on Node.js than on the browser right
now.

So, give Piglet a try, why don't you. The repo is
[here](https://github.com/piglet-lang/piglet), and there's a [Quickstart
guide](https://github.com/piglet-lang/piglet/blob/main/doc/quickstart.md). If
you're coming from Clojure, then [here's a short
overview](https://github.com/piglet-lang/piglet/blob/main/doc/porting_clojure_code.md)
of some of the differences.

If you have questions, get stuck, want to chat, etc, then you can find me [on
the fediverse](https://toot.cat/@plexus).
