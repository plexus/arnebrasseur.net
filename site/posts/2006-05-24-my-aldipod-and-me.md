---
title: My AldiPod and me
date: '2006-05-24T15:38:56+00:00'
author: Arne
permalink: /2006/05/24/my-aldipod-and-me
language: en
tags:
- Personal
---

_This was published on a personal blog I ran during the nillies, it's included here as a historical archive. Links and images may be broken, it's been a while._

About a year and a half ago I bought myself a HD based mp3 player at ALDI. For those of you not familiar with ALDI, it's the kind of supermarket where every managment decision is intended to save money. Their suppliers have to pack their goods in such a way that they can just put the whole box on the shelf, instead of having someone to unpack it. Don't expect service or superior quality, but most of the things they sell are OK. For the money, that is. From afar it looks like a black iPod, gaining itself the ungainly nickname 'AldiPod'.

We have become rather close since. It works well on my Ubuntu Linux box. If it didn't I don't think we ever could have gotten along. The supplied windows software is total crap anyway, so the best way to go is put the songs in the right directory, and then start it up and say 'refresh tracks'. Then you wait for half an hour so it can build its indexes and tadaaa, ready to go. It does these kind of things on its own pace, I can really associate with that.  
When I only had it for a short while I made the mistake of attaching it to the charger of my mobile phone. Bad Move! Luckily it was still under garantee, so I sent it in and got it back (a few months later, but hey it was my own mistake) even with a newer firmware, and with the menus in dutch in stead of english. Now my friend speaks my native language, yay!

In the meantime I've lost the lid that covers the battery, but with a piece of paper jammed in between it stays. But lately I was getting more serious problems. I wanted to wipe it clean to put different music on it. (Yes, I do have more than 20GiB of mp3's, don't ask). However, on certain directories when I did the tried and trusted 'rm -rf *', it suddenly said 'Can't delete some damn directory, filesystem is mounted read-only'. Of course it wasn't mounted read-only, but from that moment on it became read-only. Even though issuing 'mount' still gave me the (rw) indication.

I couldn't figure out what was happening, so It just ended up in the corner of my student flat for a few weeks, so it could reconsider its deeds, and I could get my toughts together.

Today I needed something to do after finishing my first exam. I seldom study after doing an exam, so I started toying around with it, and suddenly it hit me. A flash of bright Command Line Interface light hit my brain, I would give it the 'fsck.vfat -r -a' treatment, then see if it would come to senses. And it did :). So now I'm filling it up with lovely tunes wich makes it oh so happy.

I also found out today my AldiPod sports a PortalPlayer chipset, the same manufacturer the iPod is based on. Now my AldiPod no longer has to be shy when it spots some shiny white earphones. Apple is going to use Samsung chips in the future, but we don't care.
