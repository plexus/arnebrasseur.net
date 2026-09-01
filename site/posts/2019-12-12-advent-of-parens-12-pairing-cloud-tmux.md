---
title: "Advent 2019 part 12, Pairing in the Cloud with Tmux"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2019-12-12T12:40:51.000Z"
updated_at: "2026-05-16T13:41:55.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-15 08:10"]
---

_This post is part of[Advent of Parens 2019](https://lambdaisland.com/blog/2019-11-25-advent-of-parens), my attempt to publish one blog post a day during the 24 days of the advent._

I’m a strong believer in pair programming. It can be intense and exhausting, and its a skill you need to learn and get good at, but it’s extremely valuable. It improves knowledge sharing, prevents mistakes, and helps people to stay on track to make sure they are building the right thing, which is arguably one of the hardest aspects of our job.

But [Gaiwan](__GHOST_URL__/) is a remote-first company. We are spread out across Germany, Brazil, Italy, and work with clients as far away as Singapore and Hong Kong, so we need good ways to pair remotely. For this we need a tool that is

  * cross platform, at least across Linux and mac, this rules out most of the newer tools that try to solve this problem
  * symetrical, the experience should be the same for both participants, this rules out simple screen sharing, as well as most desktop sharing or VNC style tools
  * responsive, nothing is worse than a laggy editor



This may seem like an impossible set of requirements, but sometimes all you need to do is change your perspective, and instead of looking at the shiniest new developments, look at the classics. Unix once again comes to our rescue!

What we do nowadays is spin up a linux machine in the cloud, both participants ssh into that, and then we use `tmux` to share the screen. This means you have to use a terminal based editor. For this we use Spacemacs in hybrid mode. This allows quickly switching between Emacs-style and vim-style keybindings, which is a good compromise.

We have most of the setup automated, including pulling SSH keys off of Github so people can immediately log in, and setting up the tools and dependencies we use. Finally we use `ngrok` to set up a HTTP tunnel so you can still access the app you’re working on in your own browser.

This you then combine with whatever audio/video solution you prefer. We tend to use [Whereby](https://whereby.com) (previously Appear.in).

This works really well because it’s literally just a bunch of characters going over the wire, rather than complete video frames. It also means you can still copy/paste into and out of your terminal/editor, which is a nice plus.

At Clojure/Conj this year I talked to some folks from [Viasat](https://www.viasat.com/) about this approach, and turns out they do almost the exact same thing, which was very validating. One nice touch is that they equip everyone with a hardware SIP speaker phone, to get the lowest latency and highest fidelity audio. That’s definitely something we will consider as well.

_Hi, my name is Arne (aka @plexus) and I consult companies and teams about application architecture, development process, tooling and testing. I collaborate with other talented people under the banner[Gaiwan](__GHOST_URL__/). If you like to have a chat about how we could help you with your project then please [get in touch!](__GHOST_URL__/p/contact)_

**[Comment on ClojureVerse](https://clojureverse.org/t/advent-of-parens-2019/5159)**
