---
title: Getting subtitles in sync
date: '2007-12-23T08:40:58+00:00'
author: Arne
permalink: /2007/12/23/getting-subtitles-in-sync
language: en
tags:
- Tech
- Ruby
---

_This was published on a personal blog I ran during the nillies, it's included here as a historical archive. Links and images may be broken, it's been a while._

As an early new years resolution I'll start with sharing more of the snippets I code up to make my life easier. Here's one : the [SRT subtitle scaler](http://snipplr.com/view/4411/resync-srt-subtitles/). It's a little ruby script to sync subtitles with a movie. I don't know how often one needs this, but I got some English subtitles on-line for a Chinese movie and I just couldn't get them to properly line up with the movie. 

Mplayer has an option to set a delay (possibly negative), but that didn't help in my case. You can also set the subtitle fps (frames per second), but that only works when the subtitle format uses frame numbers, while SRT files use timestamps. 

If you have this problem this script can help. There are usage instructions in the comments. 

Enjoy!
