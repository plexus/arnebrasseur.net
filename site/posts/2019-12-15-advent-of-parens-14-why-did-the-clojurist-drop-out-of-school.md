---
title: "Advent 2019 part 14, Why did the Clojurist drop out of school?"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2019-12-15T00:01:53.000Z"
updated_at: "2026-05-16T13:41:54.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-15 08:10"]
---

_This post is part of[Advent of Parens 2019](https://lambdaisland.com/blog/2019-11-25-advent-of-parens), my attempt to publish one blog post a day during the 24 days of the advent._

Because they didn’t like classes.

Almost missed today’s post, so I’ll keep it short.

It’s fairly well known that you can put `set -e` at the top of a shell script to exit the script when a command returns a non-zero exit status. What isn’t as widely known is that `set -x` will cause every command to be printed out, so you get a trace of what the script is doing.

And what is not nearly as widely known as it should be is that you can also execute `bash -x some_script` to run that script with tracing on. Curious what the `clj` / `clojure` scripts do exactly? Then try this:
    
    
    bash -x `which clojure`
    

What’s great about this is that any environment variables get expanded, so you really get the exact commands that are executed, and you can copy and tweak and re-run them as you like.

That’s it, see you tomorrow!

_Hi, my name is Arne (aka @plexus) and I consult companies and teams about application architecture, development process, tooling and testing. I collaborate with other talented people under the banner[Gaiwan](__GHOST_URL__/). If you like to have a chat about how we could help you with your project then please [get in touch!](__GHOST_URL__/p/contact)_
