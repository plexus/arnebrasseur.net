---
title: "Well Behaved Command Line Tools"
slug: "well-behaved-command-line-tools"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2020-07-28T13:32:26.000Z"
updated_at: "2026-05-16T13:41:46.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-18 14:54"]
---

Yesterday Vlaaad wrote a pretty cool blog post titled [Alternative to tools.cli in 10 lines of code](https://vlaaad.github.io/tools-cli-in-10-lines-of-code). It’s a great read and a very cool hack. It uses `read-string` to parse command line arguments, resolves symbols, and invokes the first symbol/function, passing in the rest of the parsed arguments. In other words: it’s the most straightforward way to translate a command line invocation into a Clojure function call.

The benefits are illustrated well in the post. It removes all possible friction. Want to add a new subcommand? Just add a function. Adding CLI arguments and options equals adding arguments and options to said function.

It’s actually not too different from what people do in shell scripts sometimes.
    
    
    #!/bin/sh
    
    hello() { echo "Hi, $1"; }
    world() { echo "It's big and it's blue"; }
    
    # Expand all arguments to the script, so the first one is interpreted as a
    # command
    "$@"
    

What I like about Vlaad’s approach is that it establishes clear semantics. If you’re a Clojure programmer you are familiar with how Clojure’s reader works, and so your intuition transfers nicely. If you squint it almost looks like you’re just invoking a function from the REPL.

So for tools that are either personal/internal, or that are really only meant to be used by a Clojure audience I think this makes sense. That said I would like to make a case for [clojure.cli](https://github.com/clojure/tools.cli). Don’t dismiss it too easily.

Perhaps it seems a little large for what it does (how hard can command line argument parsing be, right? … right?), but that’s because it takes care of a lot of things for you. It makes sure your program follows established conventions, minimizing surprises for your users.

With Babashka and GraalVM more and more Clojure friends are venturing into writing command line utilities. `deps.edn` is also helping with this resurgance, since a lot of tools that used to be Leiningen plugins are now simply libraries with a `-main` namespace. They all need some form of command line argument parsing, and many end up rolling their own ad-hoc version, loosely but inconsistently based on existing UNIX conventions.

Now UNIX is famously inconsistent as well (looking at you `dd`). Just like in Clojure there’s a big desire for retaining compatibility, so older tools tend to have their idiosyncracies, but generally speaking things are converging towards common conventions, which are described for instance in the [GNU libc manual](https://www.gnu.org/software/libc/manual/html_node/Argument-Syntax.html).

I should note that these are the GNU conventions, they are not universal, but they are widely used. GNU in particular pioneered the double dash for long option names which has become ubiquitous. POSIX (the standard that most UNIXes adhere to) [only mentions single-letter options](https://pubs.opengroup.org/onlinepubs/9699919799/basedefs/V1_chap12.html). If you want to read more about the overgrowth of command line styles this [archived StackOverflow question](https://web.archive.org/web/20090503075914/https://stackoverflow.com/questions/367309/which-command-line-commands-style-do-you-prefer)has you covered.

So what are those conventions? First of all a distinction is made between “options” and “arguments”. Options start with `-` or `--`, the rest are arguments.
    
    
    ls --color ./*
               ^------ argument
       ^-------------- option
    

Arguments may be positional, that’s up to how the program interprets them, but options can be supplied in any order typically without changing the semantics. They can be written before, after, or in between arguments, or all of the above at the same time. Not dealing with this correctly is probably the most common “violation” I see out there.

A high profile example is [cljs.main](https://clojurescript.org/reference/repl-and-main) I use this example because a lot of people will be familiar with it, and because I think the ClojureScript maintainers can take this bit of constructive criticism. Their design choices are of course valid, and they have good reason to avoid adding more dependencies, I’m merely using this as an illustration of what it looks like when one deviates from these — admittedly fuzzy — “UNIX conventions”, because this is actually a case that has tripped me up more than once.

When invoking `cljs.main` the `--main`, `--compile` or `--repl` options are positional, they have to come last, after any “init options”.

Say you run a command and it’s not clear if it’s succeeded or not, so you want to run it with `--verbose`. You press “up” in your terminal and tack on `--verbose`. Oops, that doesn’t work. `--verbose` is an “init option”, it has to come before the main/repl/compile option… Not very intuitive.

At this point it’s maybe a good time to say a few words about `-` vs `--`. GNU tools tend to have “short options” that start with a single dash followed by a single letter, and “long options” starting with a double dash. For example `ls -a` and `ls --all`. The short ones are always a single letter because this allows combining them in a single chunk, like `ls -adR` (the equivalent of `ls --all --directory --recursive`).

Options can take associated values. The GNU conventions use `=` for this, a lot of tools also just use a space. tools.cli (and a lot of GNU tools it seems) will understand both.
    
    
    ls --format=commas
    ls --format commas
    

tools.cli lets you pick both short and long options, but my advise is to start with only long options. It’s more readable and explicit. If certain options are used very commonly then look into providing some short versions. By that time you should have an overview of the different options you’ll be offering, and can make reasoned decisions about which letter to allocate to which option.

The only short options I tend to always add are `-h` / `--help` and `-v`/`--verbose`. While you’re at it maybe also add a `--version`.

`-` and `--` also have a specific meaning when used by themselves. `--` acts as a terminator, anything after it is treated as arguments instead of options, whether it starts with a dash or not. This is especially useful if your tool ends up invoking some other tool, and you want to allow passing options through verbatim. This is another thing that tools.cli gives you for free.

`-` by itself can often be used instead of a file name, in which case STDIN will be used as input file. It is also common for tools to read from STDIN if no input file is specified, which makes it convenient to combine them with UNIX pipes. But this convention you’ll have to implement yourself, tools.cli can’t help you with that.

I hope this post will convince people to not dismiss tools.cli too quickly. For general purpose tools, especially those with reach beyond the Clojure community sticking to existing conventions is a Good Thing To Do.

To finish up here’s a little template to get you started. Don’t be daunted by the hefty README, you don’t need that much to get started.
    
    
       (ns org.example.my.tool
        (:gen-class)
        (:require [clojure.tools.cli :as cli]))
    
      (def VERSION "0.0.1")
    
      (def cli-opts
        [["-v" "--verbose" "Increase verbosity" :default 0 :update-fn inc]
         [nil  "--version" "Print version and exit"]
         ["-h" "--help" "Print this help information"]])
    
      (defn main [args {:keys [] :as opts}]
        ;; main functionality goes here, you get a seq of positional arguments, and a
        ;; map of options.
        )
    
      (defn -main [& args]
        (let [{:keys [errors options arguments summary]} (cli/parse-opts args cli-opts)]
          (cond
            (seq errors)
            (do
              (run! println errors)
              (println summary)
              (System/exit -1)) ;; non-zero exit code if something goes wrong
    
            (:help options)
            (do
              (println summary)
              (System/exit 0))
    
            (:version options)
            (do
              (println VERSION)
              (System/exit 0))
    
            :else
            (main arguments options))))
    

[Discuss on Reddit](https://www.reddit.com/r/Clojure/comments/hzd3t6/well_behaved_command_line_tools/)
