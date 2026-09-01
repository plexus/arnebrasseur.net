---
title: "Advent 2019 part 15, jcmd and jstack"
slug: "advent-of-parens-15-jcmd-and-jstack"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2019-12-15T20:12:50.000Z"
updated_at: "2026-05-16T13:41:53.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-15 08:10"]
---

_This post is part of[Advent of Parens 2019](https://lambdaisland.com/blog/2019-11-25-advent-of-parens), my attempt to publish one blog post a day during the 24 days of the advent._

Two shell commands anyone using JVM languages should be familiar with are `jcmd`and `jstack`. They are probably already available on your system, as they come bundled with the JDK. Try it out, run `jcmd` in a terminal.

This is what the result might look like
    
    
    $ jcmd
    4508 sun.tools.jcmd.JCmd
    23330 clojure.main -m nrepl.cmdline --middleware ["refactor-nrepl.middleware/wrap-refactor", "cider.nrepl/cider-middleware", "cider.piggieback/wrap-cljs-repl"]
    23053 clojure.main -m nrepl.cmdline --middleware ["refactor-nrepl.middleware/wrap-refactor", "cider.nrepl/cider-middleware", "cider.piggieback/wrap-cljs-repl"]
    

What it does is list all running JVM processes, with their process id (pid), the class whose `main` function is being executed, and the arguments to said `main`entrypoint. In this case I have two REPLs started from CIDER, and you also see the jcmd process itself.

This output tends to be a but more useful than `ps ax | grep clojure`, as it hides the often noisy JVM options like classpath, properties, and heap size.

Once you have found the pid with `jcmd`, you can invoke `jstack`.
    
    
    $ jstack 2330
    Full thread dump OpenJDK 64-Bit GraalVM CE 19.1.0 (25.212-b03-jvmci-20-b04 mixed mode):                                          
    
    "main" #1 prio=5 os_prio=0 tid=0x00007f7828010800 nid=0x5b2f waiting on condition [0x00007f782d8ec000]                                                                      
       java.lang.Thread.State: TIMED_WAITING (sleeping)                
            at java.lang.Thread.sleep(Native Method)             
            at nrepl.cmdline$dispatch_commands.invokeStatic(cmdline.clj:441)    
            at nrepl.cmdline$dispatch_commands.invoke(cmdline.clj:425)  
            at nrepl.cmdline$_main.invokeStatic(cmdline.clj:448)            
            at nrepl.cmdline$_main.doInvoke(cmdline.clj:443)                  
            at clojure.lang.RestFn.applyTo(RestFn.java:137)
            at clojure.lang.Var.applyTo(Var.java:705)                  
            at clojure.core$apply.invokeStatic(core.clj:665)                                  
            at clojure.main$main_opt.invokeStatic(main.clj:514)                               
            at clojure.main$main_opt.invoke(main.clj:510)
            at clojure.main$main.invokeStatic(main.clj:664)
            at clojure.main$main.doInvoke(main.clj:616)                                                        
            at clojure.lang.RestFn.applyTo(RestFn.java:137)
            at clojure.lang.Var.applyTo(Var.java:705)
            at clojure.main.main(main.java:40)             
    
    ...
    

What you get is the full stack trace of every thread of the given process. This can be a lot of output so you might want to pipe it to `less`.

This can be extremely useful when a process seems to be stuck and you want to know what on earth it’s actually doing. For each thread it also tells you if its active (`RUNNABLE`), or sleeping/blocked (`TIMED_WAITING`/`WAITING`).

Since it’s a lot of output you might want to filter it a bit. Most of the output will not relate to your program, it’s either Java/JVM stuff like the GC thread, or parts of the stack that are inside Clojure or in third party libraries, whereas you usually want to know where in your code the program is executing.

Simply grepping can already be quite helpful here. Presumably you have a common namespace prefix for your project, so you can grep for that.
    
    
    jcmd 2330 | grep com.nextjournal
    

But that will mash all threads together. To still keep each thread name and state visible you could do something like this (this is copied verbatim from my shell history).
    
    
    jstack 25235 | egrep '(^"|nextjournal|State)' | less
    

`egrep` is a shorthand for `grep -E` (TIL `egrep` is considered deprecated, so try `grep -E` if `egrep` doesn’t work). It switches `grep` from “basic” to “extended” regular expressions, which only means that things like `(foo|bar|baz)` groups work as you would expect. In basic grep you need to add backslaches to a bunch of the special characaters, and I can never forget which ones do need it and which ones don’t. (Is it `\(foo\|bar\|baz\)` or `\(foo|bar|baz\)`?)

The thread name starts with a double quote as the first character on a line (`^"`), my own namespaces contain in this case `nextjournal`, and the state of the thread contains the word `State`.

You can now combine this with `watch` to keep an eye on your process as its running. (Example again taken from my shell history directly).
    
    
    watch -n 0.2 'jstack 31758 | egrep "(environm|migrate|graph)"'
    

These are pretty basic tools, so YMMV, but I like how simple and lightweight they are. When a process is already pinning all of my CPU cores I also don’t have the luxury to spin up `visualvm` to capture profiling data, it just needs too many resources. In this case `jcmd` and `jstack` can provide some early insights cheaply.

(BTW `visualvm` is another great tool for another. Props to Alexander Yakushev for introducing me to all three of these and several more in [his Dutch Clojure Days talk](https://www.youtube.com/watch?v=HNp5EaRs3KI) from a few years back.)

_Hi, my name is Arne (aka @plexus) and I consult companies and teams about application architecture, development process, tooling and testing. I collaborate with other talented people under the banner[Gaiwan](__GHOST_URL__/). If you like to have a chat about how we could help you with your project then please [get in touch!](__GHOST_URL__/p/contact)_

**[Comment on ClojureVerse](https://clojureverse.org/t/advent-of-parens-2019/5159)**
