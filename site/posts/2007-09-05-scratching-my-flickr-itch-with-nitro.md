---
title: Scratching my Flickr itch with Nitro
date: '2007-09-05T10:43:57+00:00'
author: Arne
permalink: /2007/09/05/scratching-my-flickr-itch-with-nitro
language: en
tags:
- Nitro
- Educational
- Tech
- Ruby
---

_This was published on a personal blog I ran during the nillies, it's included here as a historical archive. Links and images may be broken, it's been a while._

I have somewhat neglected my English speaking audience, I must apologise. I've recently moved to Taiwan to study Mandarin, and my travellers journal will mostly be published on the Dutch side of this blog. 

Before I left to Taiwan my grandmother gave me a beautiful little camera, and I've been taking pictures like a madman and posting them to [my Flickr account](http://flickr.com/photos/plexus). I would like to include these pictures into my blogposts, but I would like to use a specific HTML template, so I can easily style them with CSS, like so : 

[![](http://farm2.static.flickr.com/1383/1293955511_c30ccce012_m.jpg)](http://www.flickr.com/photos/49646285@N00/1293955511 "Giraf schenkt David en Arne thee")

Giraf schenkt David en Arne thee

Because of this the Flickr generated code and most other solutions out there are no good. Instead I decided to code up my own solution, scratch the old itch so to say. In the meantime you guys can enjoy a little example of a Nitro app in action. It's a wee little thingy, but it shows that with Nitro you don't get much overhead in your code. If you make a small Rails application, the noise to signal ratio is huge with so much pregenerated stuff you're not gonna need. 

So the target is just a single page with my most recent pictures, and next to that a text area that contains copy-paste ready HTML in my desired format. I coded this up in a small hour, with most time spent reading the Flickr API docs. I tried the flickr gem, but it's no longer maintained and I couldn't get it to work quickly, so I started my own wrapper just for the API calls I need. (YAGNI, right.) 

The "app" is called Flikker (which is a not-so-polite word in Dutch BTW, wonder how many search engine hits this will bring me). Download it here [Flikker v0.0](/downloads/flikker0.tar.gz) as a tarball. 

To run: 

  * in settings.rb fill in your Flickr API key and user name
  * adapt 'launch' to point to your Nitro repository
  * change conf/debug.rb if you want to use something other than mongrel
  * ./launch
  * visit localhost:9000 in your browser

Actually this is such an embarassingly small app I wasn't going to put it out there, but it'll be damn handy for me, so maybe somebody else has a use for it also.
