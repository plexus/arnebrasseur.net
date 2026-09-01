---
title: "Advent 2019 part 21, Project level Emacs config with .dir-locals.el"
slug: "advent-of-parens-21-project-config-dir-locals"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2019-12-21T15:04:39.000Z"
updated_at: "2026-05-16T13:41:50.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-18 14:51"]
---

_This post is part of[Advent of Parens 2019](https://lambdaisland.com/blog/2019-11-25-advent-of-parens), my attempt to publish one blog post a day during the 24 days of the advent._

An extremely useful Emacs feature which I learned about much too late is the `.dir-locals.el`. It allows you to define variables which will then be set whenever you open a file in the directory where `.dir-locals.el` is located (or any subdirectory thereof).

Here’s an example of a `.dir-locals.el` file of a project I was poking at today.
    
    
    ((nil . ((cider-clojure-cli-global-options    . "-A:fig:dev")
             (cider-preferred-build-tool          . clojure-cli)
             (cider-default-cljs-repl             . figwheel-main)
             (cider-figwheel-main-default-options . "dev")
             (cider-repl-display-help-banner      . nil))))
    

What this essentially does is set a bunch of CIDER variables for this specific project. Mainly it stops CIDER asking annoying questions. It really bugs me when it asks me five times a day what ClojureScript REPL I’m using. This is project level stuff, so with `.dir-locals.el` I configure it at the project level and CIDER never has to ask me again.

The syntax can throw people off though, so here’s a really quick primer on association lists.

Emacs Lisp doesn’t have the handy hash-map syntax that we Clojurists enjoy. It does have a kind of hash table but there’s no syntax literal for it, and in practice it seems it’s rarely used. Instead, like most traditional Lisps, Elisp primarily makes use of two-element tuples called cons cells.

You can create them with the `cons` function, or with a ‘dotted pair’ notation. So these two expressions are identical.
    
    
    (cons "val1" "val2")
    '("val1" . "val2")
    

Need to have a hash-map like associative mapping from key to value? Well then just stick a bunch of these cons cells in a list.
    
    
    ((:name . "Arne")
     (:favorite-lisp . "not Elisp"))
    

Back to `.dir-locals.el`. It contains an association list mapping major modes to variable mappings.
    
    
    ((clojure-mode . ((var-1 . "val 1")
                      (var-2 . "val 2")))
     (ruby-mode . ((var-3 . "val 3"))))
    

This will set `var-1` and `var-2` as buffer local variables in Clojure buffers, and it will set `var-3` in Ruby buffers.

Or you can not really care about the major mode and instead just put `nil`, that way it will be used in all buffers regardless of their major mode. The per-major mode stuff is mainly useful for general Emacs variables that you want to change based on the language.
    
    
    ((js-mode . ((js-indent-level . 2)
                 (indent-tabs-mode . nil))))
    

And once you set up these variables for your project by all means do check the `.dir-locals.el` into source control so others may benefit from it as well.

One caveat: when you update your `.dir-locals.el` the changes will only come into effect when you (re-)open a file in that directory. This gets me all the time. To quickly reload a file you already have open use `C-x C-f RET` in Emacs or `SPC f A RET` in Spacemacs.

A final trick to be aware of is that you can use the special `eval` variable to evaluate code when a file gets opened. This can be very useful for instance when you are using a type of ClojureScript REPL that CIDER does not (yet) know about.
    
    
    ((nil . ((cider-default-cljs-repl          . my-cljs-repl)
             (eval . (cider-register-cljs-repl-type 'my-cljs-repl "(code-that-switches-to-the-cljs-repl)")))))
    

For the full lowdown you naturally only need to check the info pages, `(info "(emacs) Directory Variables")`.

_Hi, my name is Arne (aka @plexus) and I consult companies and teams about application architecture, development process, tooling and testing. I collaborate with other talented people under the banner[Gaiwan](__GHOST_URL__/). If you like to have a chat about how we could help you with your project then please [get in touch!](__GHOST_URL__/p/contact)_
