---
title: "Advent 2019 part 2, Piping hot network sockets with Netcat"
slug: "advent-of-parens-2-netcat"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2019-12-02T16:09:05.000Z"
updated_at: "2026-05-16T13:42:00.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-15 08:10"]
---

_This post is part of[Advent of Parens 2019](https://lambdaisland.com/blog/2019-11-25-advent-of-parens), my attempt to publish one blog post a day during the 24 days of the advent._

Part of what I want to do in this series is simply point at some of the useful tools and libraries I discovered in the past year. I’ve adopted a few tools for doing network stuff on the command line which I’ll show you in another post. First though we’ll look at a classic: netcat!

I’ve been using netcat for years, it’s such a great tool. It simply sets up a TCP connection and connects it to STDIN/STDOUT. Pretty straightforward. I’ve been using it more and more though because of Clojure’s socket REPL.

You might have to poke around a bit to find out how to install it on your system or distribution. Debian/Ubuntu packages it as `netcat-openbsd`, RHEL has it as `nmap-ncat`. Mac users can `brew install netcat`. The executable is sometimes called `ncat`, but usually you find it as just `nc`.

First start a socket REPL by setting the `clojure.server.repl` Java property. (`-J` passes an option straight through to `java`, and `-D` is the java flag to set a property).
    
    
    clj -J-Dclojure.server.repl='{:port,5555,:accept,clojure.core.server/repl}'
    

(Note the “commas are whitespace” trick, to prevent the shell from splitting this into multiple arguments. Not really necessary here, but it has served me well for instance in Dockerfiles. Life is too short to mentally process shell quoting rules.)

Now connect to it with `nc` and you got yourself a REPL!
    
    
    nc localhost 5555
    

Add `rlwrap` for some readline-based command line editing:
    
    
    rlwrap nc localhost 5555
    

I now by default install `rlwrap` and `nc` on servers that I administer, and always start my Clojure processes with a socket REPL on, just in case I need to log in and debug stuff. It’s also neat for sending Clojure commands to a process from scripts, or from cron. In these cases you should add `-N` so it closes the connection upon EOF.
    
    
    echo '(foo.bar/baz)' | nc -N localhost 5555
    

Other stuff you can do with netcat: wait until a socket is ready to accept connections. Great in shell scripts if you first start a service, and then want to use that service later on.
    
    
    while ! nc -z localhost 1234; do sleep 0.5; done
    

I’ve even used `nc` with a NULL modem (an ethernet cable with the send/receive wires twisted, so you can connect two network interfaces directly), as a cheapo way of transferring files. This also works when you’re on the same local network.
    
    
    # on the receiving end, -l for "listen"
    # ifconfig eth0 10.0.0.1
    nc -l 10.0.0.1 9999 > file.txt
    
    # on the sending end
    # ifconfig eth0 10.0.0.2
    nc 10.0.0.1 9999 < file.txt
    

**[Comment on ClojureVerse](https://clojureverse.org/t/advent-of-parens-2019/5159)**
