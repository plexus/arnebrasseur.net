---
title: "Advent 2019 part 20, Life Hacks aka Emacs Ginger Tea"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2019-12-20T15:28:35.000Z"
updated_at: "2026-05-16T13:41:51.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-18 14:51"]
---

_This post is part of[Advent of Parens 2019](https://lambdaisland.com/blog/2019-11-25-advent-of-parens), my attempt to publish one blog post a day during the 24 days of the advent._

Here’s a great life hack, to peal ginger don’t use a knife, use a spoon. I’m not kidding. Just scrape off the peal with the tip of the spoon, it’s almost too easy.

Here’s a Clojure + Emacs life hack:
    
    
    (defun plexus-clojure-extras/cider-pprint-register (register)
      (interactive (list (register-read-with-preview "Eval register: ")))
      (cider--pprint-eval-form (get-register register)))
    

So what does this do? It takes the value of a specific register and evaluates it as Clojure code with CIDER, showing the pretty-printed result in a separate buffer.

Registers are these places in Emacs where you can store a snippet of text (among other things) for later use. They have one letter names, so for instance you select some text, then press `C-x r s a` to put that text into register `a`.

Command| Keybinding| Description  
---|---|---  
copy-to-register| C-x r s| Copy to region to a register.  
view-register|  | See what’s in a register.  
insert-register| C-x r i| Insert a given register into the buffer.  
set-register|  | Set a register to a given value.  
  
Try `(info "(Emacs)Registers")` for more info on registers.

I have a number of these registers prepopulated when my Emacs starts, for instance:
    
    
    (set-register ?k "(do (require 'kaocha.repl) (kaocha.repl/run))")
    (set-register ?K "(do (require 'kaocha.repl) (kaocha.repl/run-all))")
    (set-register ?r "(user/refresh)")
    

But often I’ll also bind them on the fly. Say I’m working on a complicated function, and below the function in the buffer I have some code that calls said function. I can jump back and forth between the two, each time updating the function, then moving to the invocation and checking the result. Or I can copy the invocation to a register, and invoke it from anywhere.

The last piece of the puzzle is binding this to some handy key combination. I’m a Spacemacs user, and I’ve bound this to leader-key + `,`. My leader key is also `,`, so I can now run the tests for a namespace with `,,k`. Pretty handy.
    
    
    (dolist (m '(clojure-mode
                   clojurec-mode
                   clojurescript-mode
                   clojurex-mode
                   cider-repl-mode
                   cider-clojure-interaction-mode))
        (spacemacs/set-leader-keys-for-major-mode m
          ","  'plexus-clojure-extras/cider-pprint-register))
    

_Hi, my name is Arne (aka @plexus) and I consult companies and teams about application architecture, development process, tooling and testing. I collaborate with other talented people under the banner[Gaiwan](__GHOST_URL__/). If you like to have a chat about how we could help you with your project then please [get in touch!](__GHOST_URL__/p/contact)_

**[Comment on ClojureVerse](https://clojureverse.org/t/advent-of-parens-2019/5159)**
