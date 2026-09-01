---
title: "Advent 2019 part 19, Advent of Random Hacks"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2019-12-19T20:19:04.000Z"
updated_at: "2026-05-16T13:41:51.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-18 14:51"]
---

_This post is part of[Advent of Parens 2019](https://lambdaisland.com/blog/2019-11-25-advent-of-parens), my attempt to publish one blog post a day during the 24 days of the advent._

Something I’m pretty good at is coming up with random hacks. The thing where you’re like “hey how about we plug this thing into that thing” and everyone says “why would you do that that’s a terrible idea” and I’m like (_mario voice_) “let’s a go”.

And sometimes the result is not entirely useless. Like [this little oneliner](https://github.com/borkdude/babashka/blob/5e10c913e0dc706c4b4de85e508779a67c6e51c0/README.md#convert-projectclj-to-depsedn)I came up with yesterday, using Babashka to “convert” a `project.clj` into a `deps.edn`.
    
    
    cat project.clj | sed -e 's/#=//g' -e 's/~@//g' -e 's/~//g' | bb '(let [{:keys [dependencies source-paths resource-paths]} (apply hash-map (drop 3 *in*))] {:paths (into source-paths resource-paths) :deps (into {} (for [[d v] dependencies] [d {:mvn/version v}]))} ) ' | jet --pretty > deps.edn
    

You could do this properly, write a hundred lines or more to deal with profiles, aliases, etc, or you can cook this up in ten minutes, and do the rest by hand.

Reminds me of that time I wrote a [chat server that fits in a tweet](https://twitter.com/plexus/status/308362006711394304), (the old 140 character ones), on the bus on the way back from a conference. Code golf was a lot easier when I still did Ruby :D

 _Hi, my name is Arne (aka @plexus) and I consult companies and teams about application architecture, development process, tooling and testing. I collaborate with other talented people under the banner[Gaiwan](__GHOST_URL__/). If you like to have a chat about how we could help you with your project then please [get in touch!](__GHOST_URL__/p/contact)_

**[Comment on ClojureVerse](https://clojureverse.org/t/advent-of-parens-2019/5159)**
