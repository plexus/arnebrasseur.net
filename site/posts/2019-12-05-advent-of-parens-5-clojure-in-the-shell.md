---
title: "Advent 2019 part 5, Clojure in the shell"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2019-12-05T17:57:16.000Z"
updated_at: "2026-05-16T13:41:59.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-15 08:10"]
---

_This post is part of[Advent of Parens 2019](https://lambdaisland.com/blog/2019-11-25-advent-of-parens), my attempt to publish one blog post a day during the 24 days of the advent._

I already showed you netcat, and how it combines perfectly with socket REPLs. But what if all you have is an nREPL connection? Then you use [rep](https://github.com/eraserhd/rep)
    
    
    $ rep '(clojure.tools.namespace.repl/refresh)'
    :reloading ()
    :ok
    

`rep` will find a `.nrepl-port` file, or you can specify the port directly.
    
    
    rep -p 4829 "(require 'foo.bar :reload)"
    

Unfortunately `rep` is not able to read code from STDIN, making it the odd one out in my exposition of command line tools, but there’s a pull request open for that.

Rep is part of a new breed of Clojure tools that use GraalVM to compile to native binaries, giving them super fast start-up times.

Finally using Clojure from the shell has become tractable, which must have been what Michiel Borkent (a.k.a. borkdude) was thinking when he created [Babashka](https://github.com/borkdude/babashka), or `bb` for friends.
    
    
    $ bb -e '(+ 1 1)'
    2
    

My favorite part of `bb` are the `-I` and `-O` options. The first will read and parse consecutive edn forms from STDIN, and make them available as a lazy sequence bound to `*in*`. The second will print the return value of your program as newline-delimited EDN forms.

Here’s a little loop to get the download numbers for Kaocha for the past ten days
    
    
    for i in {1..10} ; do curl -s https://clojars.org/stats/downloads-`date -d "now - $i days" "+%Y%m%d"`.edn; done |
      bb -I -O '(->> *in* (map #(get % ["lambdaisland" "kaocha"])) (map vals) (map (partial apply +)))'
    

But what if instead of EDN you have some other format, like [newline-delimited JSON](http://jsonlines.org/)? Then you use another one of Michiel’s projects, and a perfect complement to Babashka: [Jet](https://github.com/borkdude/jet)!
    
    
    cat log.json | jet --from json --to edn | bb -I '(map ,,, *in*)'
    

It can convert back and forth between JSON, EDN, and Transit, and it can pretty print. So say you have an big old ugly Transit blob and you want to actually look at what’s in there, you could do this.
    
    
    jet --from transit --pretty < blob.transit | less
    

Pretty neat. It also has some querying capabilities similar to `jq`, and an interactive mode, although for complex querying I would probably just use Babashka, since that way I can just write Clojure.

**[Comment on ClojureVerse](https://clojureverse.org/t/advent-of-parens-2019/5159)**
