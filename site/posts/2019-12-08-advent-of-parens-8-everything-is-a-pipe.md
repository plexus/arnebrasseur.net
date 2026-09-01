---
title: "Advent 2019 part 8, Everything is (not) a pipe"
slug: "advent-of-parens-8-everything-is-a-pipe"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2019-12-08T14:42:16.000Z"
updated_at: "2026-05-16T13:41:57.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-15 08:10"]
---

_This post is part of[Advent of Parens 2019](https://lambdaisland.com/blog/2019-11-25-advent-of-parens), my attempt to publish one blog post a day during the 24 days of the advent._

I’ve always been a big UNIX fan. I can hold my own in a shell script, and I really like the philosophy of simple tools working on a uniform IO abstraction. Uniform abstractions are a huge enabler in heterogenous systems. Just think of Uniform Resource Locators and Identifier (URLs/URIs), one of the [cornerstones](https://www.ics.uci.edu/~fielding/pubs/dissertation/top.htm) of the web as we know it.

Unfortunately since coming to Clojure I feel like I’ve lost of some of that power. I’m usually developing against a Clojure process running inside (or at least connected to) my trusty editor, and the terminal plays second fiddle. How do I pipe things into or out of that?

On top of that consider that most UNIX tools are line based (grep, awk, sed, cut, …), how do I marry that with Clojure’s EDN view of the world (another fabulous uniform abstraction).

I already covered `nc`, `rep`, and `jet` in a previous post. Each of these is just a single focused tool, but together they start to form a kind of Swiss army knife. Today I’ll look at a few more of these: HTTPie, Pup, kafkacat and websocat.

[HTTPie](https://httpie.org/) is a tasty alternative to `curl`. It’s a bit more intuitive when it comes to passing along custom headers or parameters, and it can pretty print and format JSON. I have to thank Tommi from Metosin for indirectly introducing me to this one, I came across it in one of the Metosin READMEs (can’t remember which one).

After adoping HTTPie (the command is simply `http`) life was great, until I tried to use it to connect to a websocket. Websockets are kind of HTTP so I thought it might work, but they’re also kind of their own thing so I don’t blame the devs for leaving this out. Focused tools, right? Fortunately there’s [websocat](https://github.com/vi/websocat) to do just that.

See how there’s a common theme here? We have various mechanisms for shuttling data from one place to another, either as a stream, or a single shot transfer, and we’re simply hooking these to UNIX pipes. (Macbook owners can think of dongles as an apt metaphor).

The final tool I want to mention is `kafkacat`, you can probably guess by now what it does. I’ve been poking around a bit with Kafka lately, and having a simple command line client for both sending and receiving was super helpful to debug and better understand things.

So this is your set of dongles:

  * TCP streams: `nc`
  * Websocket streams: `websocat`
  * HTTP requests: `http`
  * kafka topics: `kafkacat`
  * nREPL connections: `rep`



But just plugging things together isn’t always enough, they also need to speak the same format. In the Clojure world we like things like Transit and EDN, but the rest of the world speaks JSON, or XML/HTML, or CSV, and UNIX tools like to have simple lines of text, so we need to convert between these formats.

I already mentioned `jet`, and `jq` is helpful for extracting things from JSON. The final one I want to introduce in this series is `pup`, which is like `jq`for HTML.

For instance:
    
    
    get-latest-datomic-version () {                   
        http https://my.datomic.com/downloads/free | pup .latest attr{href} | sed 's^/downloads/free/^^'
    }
    

Or say so you are putting Transit on a Kafka topic, but you want to see that as pretty printed EDN to inspect what’s going over the wire? easy peasy.
    
    
    kafkacat -b 127.0.0.1:9092 -t topic-1 | jet --from transit --pretty
    

  * `jet` convert/query EDN/Transit/JSON ; pretty-print EDN
  * `jq` convert/query/pretty-print JSON
  * `pup` extract from HTML/XML



And of course all the classics are still there, `sed`, `grep`, `awk`, `cut`, `head`, `tail`, `tee` and many more.

**[Comment on ClojureVerse](https://clojureverse.org/t/advent-of-parens-2019/5159)**
