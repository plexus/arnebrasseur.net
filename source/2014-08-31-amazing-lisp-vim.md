---
title: The Amazing LISP Vim
date: 2014-08-31
---

Why Vim users will switch to Emacs in the coming years.

I've recently stated that within the next five years half of Vim users will switch to Emacs. And while the exact time span and percentage are debatable, I stand by that statement. I think a number of factors are coming together that will make switching very appealing.

Editor flame wars have been around for decades. Let me do my share to fan the flames by saying this: of all modern editors, Vim has the superior editing model. The modal text-object style of editing, where few if any modifier keys are used, but instead commands are composed of keys typed in sequence is better for reasons of economy, ergonomics, and sheer expressive power.

Compare this with Emacs's keybindings that will never improve, and its exclusionary culture. Let's pick on the culture first.

Emacs is rooted in old-school hacker narrative. Some of the developers have been around for over three decades, and they like things just the way they are, making Emacs possibly the most closed open source project.

The community is filled with the type of people who yell heresy when the `Reply-to` of a mailing list goes back to the list. The type of people who moan about Eternal September ad nauseam. This feeds into the other sticking point: the keybindings will never, ever change. Out of respect for many man centuries of muscle memory, the defauts are set in stone.

Luckily both of these issues can be ignored. While Emacs is still being developed and evolving in interesting ways, the really exciting stuff is happening on top of Emacs. By now we have a very solid platform, and a new generation of developers is taking it to the next level.

Keep in mind that it's only a configuration detail that Emacs functions as an editor. In essence it's a VM that runs LISP. As far as UI goes you're stuck with rectangles representing buffers, but at least they're capable of displaying graphics and proportional text. It's not iOS, but for building the workbench of a software developer it's a solid base.

## The Perfect Storm

All of that isn't new, what's different this time around?

### Evil is Growing Up

If Emacs's editing model and keybindings are so atrocious, then why not reprogram it? Evil is an Emacs extension that implements Vim in Emacs. It's been around for a while, and from what I can gather, it's quite complete. You have all the basic editing commands, can define leader key combinations, the ex commands (the `:` prompt) are implemented. An advanced Vim user will probably find some things missing. If that's you please do let me know what it is! I've been asking my Vimmy friends and so far haven't gotten much concrete feedback.

For an upper intermediate user it seems Evil is more than good enough, and it's only getting better.

Apart from the usual Vim modes (insert, visual, etc.) Evil also has an Emacs mode (toggled with Ctrl-Z), for using existing functionality. Although you can also just create custom bindings or leader key commands.

Evil has been developed since 2011, and is currently at version 1.0.9. It weighs about 30K SLOC, has over 1400 commits, and is actively maintained.

### Elpa/Melpa

Only recently has Emacs spawned a package manager. Before people would manually copy source files into their `~/.emacs.d/`. MELPA is a community-managed, curated repository that grows by the day. This makes it much more appealing for people to create packages, and easier for others to try them out. I feel it's a major catalyst for innovation.

### Clojure

With Clojure, for the first time in ages we have a Lisp that is actually gaining in popularity, and for good reason: it sits on top of the most advanced VM around, it has a compelling story around concurrency and parallelism, it benefits from the huge Java/JVM ecosystem. Long story short: we are gaining Lisp developers again.

This is great news for Emacs. Instead of having people that know just enough Lisp to solve their immediate problems, we have a new generation of developers that is capable of developing high-quality Emacs extensions. They are challenging old ways of doing things.

### Emacs Revival

Watch this space, Emacs is more alive than ever. Sign of the times: only in the past year Emacs user groups were founded in Berlin, London, Paris, Madrid, Bangalore, and St. Louis.

## The Perfect Setup

So it's no secret by now: I strongly believe that where we are now, Emacs+Evil is the holy grail of editing. But if it's just a Vim clone, why bother switching?

This section should really be a post in itself, but I'll give a few reasons in brief.

### A Sensible Process Model

While Emacs is single-threaded like Vim, you can spawn processes asynchronously and communicate with them from Elisp. No awkward hybrid scripting of Vim+Tmux; integrate your tests, REPL, compiler, linter, right into your Emacs workflow.

### org-mode

One of the coolest "apps" to be built on top of Emacs. Org-mode is plain text that fell in the cauldron of magic potion as a child. Keep todos and calendar items, do reproducible research and literate programming, manipulate tables with spreadsheet-like operations, capture links, snippets, todos, without disrupting your workflow, and have all of it in simple plain text, exportable to HTML, LaTeX, PDF, iCalendar and more.

### Paredit / Smartparens

Don't edit text, edit syntax trees. Paredit has been around for a long time and has always been popular with Lisp programmers. Smartparens is usable for other languages as well, and once you get used to it you'll never go back.

You can watch [this short demo](https://www.youtube.com/watch?v=D6h5dFyyUX0) by [Emacs Rocks!](https://twitter.com/emacsrocks) to get an idea.

### All the rest

Just some of the things that amaze the first time people see them:

editable-dired, expand region, multiple cursors, REPL driven development, artist-mode, tramp, magit, flycheck, the list goes on. Even Tetris.

## Getting started

There doesn't seem to be a go-to Emacs+Evil starter kit available yet, but you don't need much to get started.

You'll need Emacs, preferably the "real" GNU Emacs, so no Aquamacs, XEmacs or NTEmacs. Try `emacs --version` to see if it's already available on your system. You want to have at least version 24.

Note that pre-installed versions might not have GUI extensions compiled. This is fine for running from a terminal, but does limit things somewhat, so you might want to get a fuller version later on.

For Mac users, `brew install emacs` should do the trick. Debian-based systems such as Ubuntu can do `apt-get install emacs`.

Put this in `~/.emacs.d/init.el` (you're allowed to use Vim for this).

~~~ emacs-lisp
(require 'package)

(setq package-archives
  '(("gnu"         . "http://elpa.gnu.org/packages/")
    ("melpa"       . "http://melpa.milkbox.net/packages/")))

(package-initialize)

(when (not (package-installed-p 'evil))
  (package-refresh-contents)
  (package-install 'evil))

(evil-mode)
~~~

Now simply launch `emacs`. It will install Evil on its first run, and enable it in all buffers. Use Ctrl-Z to switch back to Emacs mode if necessary.

Some things to try:

Follow the Vim tutor: `:edit /usr/share/vim/vim74/tutor/tutor`, or read the Evil manual: `C-h i m Evil <RET>`.

Enjoy your Amazing LISP Vim.
