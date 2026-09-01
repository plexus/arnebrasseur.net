---
title: Multilingual blogging with Gengo!
date: '2006-07-28T14:20:34+00:00'
author: Arne
permalink: /2006/07/28/multilingual-blogging-with-gengo
language: en
tags:
- Informational
- Tech
---

_This was published on a personal blog I ran during the nillies, it's included here as a historical archive. Links and images may be broken, it's been a while._

A few months ago I wrote a [post](http://www.arnebrasseur.net/index.php/2006/05/30/multilingual-blogging/) about my desire for a Wordpress plugin that would allow me to make my blog multilingual in an elegant way. I was under the impresission there was no plugin that was able to help me. Fortunatly Moshu, author of the ['Cross cultural blog'](http://crosscultural.transycan.net/blog/archives/2006/07/19/multilingual-blogging-revisited/) pointed me to the [Gengo](http://jamietalbot.com/wp-hacks/gengo/) plugin. It took a while because of work on my thesis, but now I've finally gotten around installing it.

It's on their site in great bold letters : first take a backup of your database. So I log into PhpMyAdmin and click export. This v

ery handy tool can export in several ways, however only the first, SQL, can later be imported! I downloaded Gengo and added it to my Wordpress plugins. After activating I promptly get a screen where I can add languages I want to use on the blog. Enthousiasticly I add Dutch, only to get a message that all my existing posts have been marked as Dutch. That certainly wasn't my intention!

I try clicking 'Uninstall Gengo', hoping to start the procedure from scratch, but this feature doesn't seem to be working. Gengo is still beta software so these things can happen. I guess I'll just recover my database then. Backups can be handy indeed. This time around I know better. First I add English, afterwards Dutch.

Browsing through the WP Codex I find a file with the Dutch translation of Wordpress. From the Gengo site I get a similar file specifically for Gengo. A translation of my theme, 'Connections', doesn't seem to exist. Therefore when you look at a Dutch page you get a mix of Dutch and English page elements. I'll have to address that later on.

And so now I'm writing my first English translation of a Dutch post. Great! :) Gengo has a number of extra php methods to add to your theme, like showing a list of available languages, or showing a list with translations for a post. So I'll be tweaking the following days. I'm very curious for the result. Big Up for Gengo!

_Update :_ Apparently this version doesn't allow viewing all posts. The plugin will try to find out your preferred language and only show posts in that language, or otherwise English by default. I would have preferred that at a first visit one would get to see all, and then be able to make a selection. Both have pros and cons however so it's not too bad. Unfortunatly this is also true for the feeds, so you will only get posts in de language of your newsreader. To get all messages, you will have to subscribe to two feeds. [NL feed](http://www.arnebrasseur.net/index.php/feed/nl), [EN feed](http://www.arnebrasseur.net/index.php/feed/en)
