---
draft: true
date: 2025-06-04
title: The Piglet Story - Part 1
---

```inline-html
<script type="importmap">
{"imports":{"astring":"https://cdn.jsdelivr.net/npm/astring@1.9.0/dist/astring.mjs"}}
</script>
<script type="module" src="https://cdn.jsdelivr.net/npm/piglet-lang@0.1.42/lib/piglet/browser/main.mjs"></script>
<script type="piglet">
(await (load-package "https://cdn.jsdelivr.net/npm/piglet-interactive-snippet@0.3.0"))
(await (import 'https://arnebrasseur.net/piglet-interactive-snippet:main))
</script>
<style>
.techy {
  font-weight: 300;
  border-left: 4px solid #390;
  padding-left: 1em;
  line-height: 1.5em;
}
</style>
```

# Piglet

A few years ago I started a big and ambitious project, to create a Clojure-like
LISP, written in modern JavaScript. It would lean into the capabilities of the
web platform, while providing the rich mix of features that have made LISP stand
out for decades: interactive (REPL based) programming, a dynamic environment
with rich introspection, and metaprogramming at multiple levels.

```piglet-interactive
(println "hello world")
```

{:.techy} 
The code snippets in this article are Piglet code that is executed in your
browser. You can edit them and press the "Run" button to play around with
Piglet. To get a Piglet REPL in your terminal, you can:

{:.techy} 
```
npx -p piglet-lang@latest pig repl
```

I wanted the language to be cute and approachable, and first called it Bunny.
But after noticing someone name-squatting "bunny-lang" the bunny became a
Piglet. Less fuzzy and cuddly, but still cute, and a _much_ smarter animal.

This post is the story of Piglet. It's long, winding, and personal. I don't know
what the future holds for Piglet. I stopped working on it for over a year,
unsure if it even had a future. But earlier this year I picked the project up
again, and I'm very happy with the progress the last few months, and the
potential for the project. Above all I want to start being much more public and
transparent about Piglet, to find out if others can see its promise.

There wasn't one particular thing that made me decide to create a new LISP in
plain JavaScript, rather it was a combination of frustrations with existing
offerings, some language design ideas I wanted to explore, and ideas around
educational or creative coding tools that would really benefit from a dynamic
runtime.

I also had (and still have) the sense that a language occupying this particular
space ought to exist, that if I didn't create it it was only a matter of time
before someone else did. It's like there's a Piglet-shaped hole in the existing
language landscape. JavaScript has become what C once was, the baseline
language, the lingua franca. Can we do what Clojure did for Java, pave over the
baroque syntax with something more flexible and consistent, provide first class
functional programming features, and above all provide a dynamic environment
with built-in help, and excellent support for interactive programming?

I did develop Piglet in the open from the start, pushing the code to a public
repository on github, but I didn't announce it yet, and left the README
intentionally blank. And the longer I put off showing Piglet to the world, the
more I wanted it be perfect before I showed it. There was just always one more
thing that should really work first...

I was wary of prematurely announcing the project. I wanted to first demonstrate
that it could be more than a toy, to have it in a state where someone could
actually pick it up and build something. I think I was also a bit wary of the
unforgiving internet crowd. Creating a new programming language always smacks of
hubris, and it's so easy to imagine the Reddit or HN critics having a glance at
a half finished thing and boring it into ground... No thanks. So instead I
plugged away, with a little help from some colleagues.

At some point life and work forced me to reconsider priorities, and Piglet
started gathering dust. It was a working language at that point. There was a
REPL, Emacs integration, it ran in the browser and on Node.js. The beginnings of
a built-in standard library were there, and I had dogfooded it for some internal
Gaiwan tooling. But being the main developer of a programming language
implementation, while running a consultancy, as well as consulting as a tech
lead and architect, turned out to be a bit too much, and Piglet started
gathering dust.

{:.techy}
So what makes Piglet different from for instance ClojureScript? There are a
number of differences, but lets start with the high level architecture.
ClojureScript, like Piglet, compiles to JavaScript. But the ClojureScript
compiler and tooling are written in Clojure, which means they execute on the
Java Virtual Machine (JVM). This means there are two separate runtimes involved,
the JS runtime you are targeting, and the JVM runtime where your compiler and
language tooling live. This means ClojureScript is not a true LISP, it's a fancy
transpiler. By setting up a communication channel between the compiler and
runtime environment, and keeping a separate compiler state that tracks the state
of the code loaded in the JS runtime, it can offer interactive programming
features, but the additional complexity needed to make this work is significant,
and this is the reason ClojureScript tooling is often frustrating to work with.

```piglet-interactive
(await (eval (read-string "(* 7 3 2)")))
```

Piglet, like Clojure, only has a single runtime environment, containing both
your program, and the compiler and other language tooling. Code gets loaded from
source, and compiled and executed on the fly. It's how LISP and Smalltalk
systems generally work, and it's what makes the dynamic, interactive, rapidly
iterating development style possible that these systems are known for.

Most programming languages attract some kind of following, no matter how small.
A community, a tribe. Part of the reason why I wanted to make sure we controlled
the messaging around Piglet's announcement was that I wanted to be intentional
about the kind of community I wanted to steward. Friendly, welcoming,
approachable, curious, diverse, creative. Great communities don't simply sprout
up by chance. I've been around long enough to know that much. They need role
models, and active governance, moderation, and stewardship.

I think part of the reason that work on Piglet stalled is that it became a bit
overwhelming. Thinking about all the work ahead. Improving the compiler, the
language, creating good accessible tooling, working out packaging and
distribution of libraries, writing documentation, from tutorials to reference
material. Creating an online presence, setting up community spaces, doing
advocacy... I've done some version of all of these things for various projects
in the past. I knew we could get there by doing one thing at a time, by sticking
to the values of simplicity and a practical minimalism. But what started as a
project that was a joy to hack on, one that inspired and motivated me, was
starting to feel like a snowball of obligations. Given how busy we were with
client work at Gaiwan, the sensible thing was to put the project on ice for a
while.

The last commits before the hiatus are from October 2023. Some of the last
things I worked on were a built-in library providing reactive primitives,
intended as a basis for declarative UIs, a "dev server" which made it easier to
develop Piglet programs intended for the browser, by providing resolution and
files from npm and piglet packages, and auto-generating the index.html that ties
it all together. I also added a build setup for compiling and bundling Piglet
itself using Rollup.

I had initially shunned build tools a Rollup, Esbuild, or Vite. For the longest
time JavaScript did not have a "native" module format, making these bundlers
essentials. In the process they could provide additional optimizations, and even
support upcoming language features by desugaring them more basic code understood
by all browsers. But it was 2023, ESM had been supported by evergreen browsers
for some time, and JavaScript itself had significantly matured over the years. I
wanted to see how far I could writing code that browsers could readily
understand. Not necessating a specific build tool was compelling because it
meant a significant piece of complexity and potential friction was eliminated.

Still, I realized that for Piglet to be taken seriously we had to eventually be
able to generate compact, optimized, single-file builds. Both of the Piglet
runtime itself, and of the code the Piglet compiler generates. Adding Rollup
took care of the runtime. While full Ahead-of-Time compilation of `.pig` sources
is still a work in progress, earlier this year the groundwork for AOT was laid,
ensuring that Piglet can spit out ESM modules that are amenable to tree-shaking
(also known as dead code eliminiation).
