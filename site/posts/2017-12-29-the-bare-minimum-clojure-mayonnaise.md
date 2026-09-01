---
title: "The Bare Minimum, or Making Mayonnaise with Clojure"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2017-12-29T15:38:24.000Z"
updated_at: "2026-05-16T13:42:07.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-09 12:23"]
---

## Making Mayonnaise

Imagine you have a grandfather who’s great at making mayonnaise. He’s been making mayonnaise since before the war, and the result is truly excellent. What’s more, he does this with a good old fashioned whisk. He’s kept his right arm in shape throughout decades just by beating those eggs and oil and vinegar.

Now he’s bought himself a handheld electric mixer after hearing his friend rave about hers, but after a few tries he gives up and goes back to his whisk. He says he just can’t get the same result. This seems slightly odd, so the next time you go over you ask him to show you how he uses the mixer.

Imagine your face when he takes one of the mixer’s beaters and starts beating the stuff by hand. Turns out he actually had no idea how to use the mixer, so he just took the bit that seemed familiar, and started using it the way he was used to.

## What does this have to do with Clojure?

A far fetched story perhaps, but it’s the best metaphor I could come up with to illustrate the difference between “writing Clojure”, and “doing Clojure”.

Sometimes people approach Clojure in a similar way. They’ve heard good things about it, and decide to try it out, while sticking to their familiar tools and workflow. They hear Clojurists talk about their Emacs and other exotica, and write it off as a strange fetish for obscure technology. They know coding. They already have an editor they know and love, thank you very much. All they want to know is how this code in `$LANGUAGE` translates to Clojure.

I made this same mistake some years ago when I decided to give SmallTalk a try. I looked at a couple of implementations, but they all seemed to come with this massive environment, including a UI, editor, and something called “images” that just did not make sense. All I wanted was to learn a language, all I needed (I thought), was a way to stick some code into a source file, compile and run it.

But SmallTalk, like Clojure, isn’t just a language. It’s a complete philosophy about creating software, with specific ideas about how programmers will interact with their environment. This makes it strange and unfamiliar, but if you really want to learn one of these languages you need to dive in wholesale. You will never be able to assess and appreciate what makes them stand apart by skirting around the edges of your comfort zone.

The sad part is that this stuff isn’t made very explicit. It’s the kind of knowledge that’s passed down from mentor to mentee, taught by example. If you’re learning Clojure all on your own, you may never realize what you’re missing.

So I’m here to tell you what the bare minimum setup is you need to not just be “writing Clojure”, but to actually be “doing Clojure”.

In short, there are two things that you simply can not do without. I’ll call these “managed parentheses”, and “in-place evaluation”. Take note that I did not say “use Emacs” (or Vim or ed(1) or whatever). Many editors and IDE’s can provide these features, but you do have to make sure your environment has these features, and you have to learn how to use them.

## Managed Parentheses

Managed parentheses means that your editor makes it near impossible to have unbalanced parentheses in your code. You should never ever have to manually insert a closing parenthesis.

There are several ways to achieve this. The most well known approach is called “paredit”, originally built for Emacs, but with equivalent packages for most major editors. A different approach that’s been gaining popularity, and with less of a learning curve is Parinfer. The general term for this is “structural editing”.

You can find eplanations of how these work in detail elsewhere. Just take it from me that they manage your parentheses, so you don’t have to. What they really do is cause a mental shift, where you _no longer think about parentheses_ , but instead think about blocks of code (also called forms, expressions, or sexps).

Clojure is a LISP. This in itself is a superpower. For decades people have been saying that treating programs as text is backwards, that we need higher level tools that allow us to manipulate the structure of the program directly. LISP drags you halfway there. It takes getting used to, but it pays off massively.

Don’t just take it from me, this is what user [bbrinck had to say on ClojureVerse](https://clojureverse.org/t/share-the-nitty-gritty-details-of-your-clojure-workflow/1208/17?u=plexus):

> I have found that many beginners don’t realize that they will be much, much more productive once they learn paredit or parinfer. I worked in Clojure for a few years in my spare time without structural editing. I knew paredit existed, but I didn’t have a good sense of the time investment necessary vs the productivity difference.  
>   
> It was only when I pair programmed with some experienced Clojurists that I realized how much time I was wasting on bugs related to parentheses that they never experienced.

## In place evaluation

Most languages have REPLs, command line tools that let you enter and evaluate program code one line at a time. They’re great for exploration, for quickly trying things out, or for running commands on the fly.

In the LISP family of languages (as well as SmallTalk) this concept is taken one step further by integrating this REPL deeply into the editor experience. This allows you to put your cursor next to a piece of code in a source file, and evaluate it right there.

Because of LISP’s uniform structure you can unambigously single out any single sub-expression, and send that to the editor-connected-REPL, immediately seeing the result. This brings coding to a new level of interactivity. You are interacting with a full, running system, which you can inspect, update, and alter. You can even connect to your production system, to look at its state, or to quickly redefine a function to patch a bug.

You can also evaluate a piece of code, and _insert the result back into your source file_. People often say that in Clojure code is data, focusing on the manipulation of code structure with things like macros, but the equivalence goes both ways. Data is code, so any Clojure data structure, when printed, is a valid Clojure expression. Depending on where you’re coming from this will be either a no-brainer or a mind bender. In any case it’s extremely powerful. I said it before, LISP is a superpower.

## What else?

These two are the absolute minimum. If you honestly want to give Clojure a try, then make sure you’re using an environment that has these features, and learn how to use them. Some popular options include

  * CIDER (Emacs)
  * Cursive (IntelliJ)
  * ProtoREPL (Atom)
  * fireplace.vim



There are others. The point is to not blindly adopt what someone tells you, but to be able to assess yourself whether the tool you’re using clears this bar.

These two things are only the absolute bare minimum. For a real world setup I would recommend a few more things. For all of these

  * check that your environment can do it
  * learn how to use it
  * make it part of your workflow



### Automatic indentation

LISP, when properly indented, looks a lot like Python. There’s general concensus about how to indent Clojure, and so it’s something that easily can, and should be automated.

Either have a way to automatically correct the indentation of a line (e.g. by pressing TAB), or use something that automatically keeps the indentation of the whole file up to date (I recommend the latter, for Emacs this is called “agressive-indent-mode”).

### Jump to definition

Before coming to Clojure I did a lot of Ruby. In Ruby when you’re looking at a method invocation it can be hard to figure out where that implementation lives, because it depends on runtime types. Not so in Clojure, the system always knows exactly where a given function is defined. Your editor can ask its integrated REPL where that is, so you can jump to the right file and line.

This is great for navigating your own project, as well for jumping into library code to better understand how it works. And while you’re there you could put a couple `println` statements in that library code, and re-evaluate it. Never second guess again what values are flowing through your system.

### Show function signature

When your cursor is on a function name, your editor should show somewhere unintrusively what the signature of that function looks like. This little bit of information, the arity and argument names of a function, are 80% of the documentation you’ll need. Having it right there will help you stay in a state of flow.

### Look up documentation

Functions in Clojure come with built-in documentation. All of core has these docs, and most libraries do as well. You really want to be able to pull these up with the touch of a button.

### Rainbow parentheses

More of a nice to have, but worth a mention. This feature gives matching parentheses matching colors. I really like this, but it’s probably not essential.

## Staying in the flow

If you have all of the above you’re already ahead of most programmers in the industry when it comes to tooling and workflow. Consider yourself lucky. The next step is to make sure you can stay in the flow as much as possible, to eradicate events that upset and interrupt your REPL based workflow.

### Reloadable system state

A downside of dynamically working with a long running process is that eventually state will accrue, so that the in-memory state of your Clojure process no longer reflects the source files on disks. There could be functions that you’ve deleted or renamed, but that still exist in memory. You could have application state that no longer reflects a valid state of the program. There are a bunch of gotchas in Clojure that can cause these errors, for instance redefining a `defrecord`, or a `defmulti`.

This can cause code to work fine, but fail as soon as you restart the process, or it can cause things to fail even though they really should work. In both cases what you need is a clean slate.

Initially you’ll just take this in your stride and restart the REPL process. Once you have felt enough of this pain though you’ll look for a solution. What you’ll end up with will likely be a component-style library to manage your application state, something like Component, Mount, or Integrant, combined with clojure.tools.namespace for intelligent unloading and reloading of all your namespaces.

### Hot loading of new dependencies

When developing applications, especially greenfield apps, you’ll regularly be adding more third party libraries as you go. Each time you add one of these to `project.clj`, `deps.edn`, or `build.boot`, you need to restart your Clojure process.

This can be solved through libraries like Pomegranate, which will install and hot-load new dependencies. Configure them in `~/.lein/profiles.clj` or `~/.clojure/deps.edn` so you have them available on every project.

## Further reading

  * [Share the nitty gritty details of your Clojure workflow (ClojureVerse, December 2017)](https://clojureverse.org/t/share-the-nitty-gritty-details-of-your-clojure-workflow/1208)
  * [My Clojure Workflow, reloaded (Stuart Sierra, June 2013)](http://thinkrelevance.com/blog/2013/06/04/clojure-workflow-reloaded)
  * [(Video) REPL Drive Development (Stuart Haloway, June 2017)](https://vimeo.com/223309989)
