---
title: Multilingual blogging
date: '2006-05-30T21:56:25+00:00'
author: Arne
permalink: /2006/05/30/multilingual-blogging
language: en
tags:
- Insightful
- Tech
---

_This was published on a personal blog I ran during the nillies, it's included here as a historical archive. Links and images may be broken, it's been a while._

I've been looking into wordpress plugins to write posts in multiple languages. Until now I've sticked with English for a number of reasons. First of all it's more or less the lingua franca of the internet, second of all most Flemish people have no difficulties with English, so it only broadens the audience. Nevertheless I'd like to blog in Dutch from time to time, and have the option to translate a post so it's available in both languages. As [Molly E. Holzschlag](http://www.molly.com "Molly her blog") recently pointed out in her [excellent article](http://alistapart.com/articles/worldgrowssmall "World Grows Small: Open Standards for the Global Web") on [A List Apart](http://alistapart.com "For people who make website"), issues of internationalization (i18n), and the corresponding localization (i10n) are things everyone who makes web sites should consider. In an ideal world, I'd install a wordpress plugin that 

  * Allows me to write any post and pages in any number of arbitrary languages
  * Allows me to translate any static phrases on the site such as titles in the sidebar, the tag line, etc.
  * Allows for transparent translation of category names
  * Does intelligent i10n such as date and currency formatting, direction of text (e.g. Arabic)
  * Detects the locale of the user, and displays everything that is available in his or her language in that language. A default language could be defined to fall back to, for instance the native language of the writer.
  * Lets the user easily choose a different language, possibly storing their preference in a cookie
  * Add to every post on a page a number of links to the alternate language versions. With a bit of javascript the reader could easily change the language of one post while keeping the rest of the page in the same state, without reloading

Obviously it's not the least I'm asking for. On the other hand is this the least one would expect from a multilingual/multiregional CMS. If anyone knows of a CMS or blogging platform with a reasonable subset of these features, I'd love to hear about it in the comments. Currently I'm sticking to wordpress, so I set out looking for a wordpress based solution. Googling did provide me with one ambitious project, the [Multilingual plugin for Wordpress](http://doocy.net/multilingual/) by [Morgan Doocy](http://doocy.net) and [Chris Waigl](http://serendipity.lascribe.net/ "Chris Waigl's blog"). Unfortunately 

  * It's still in alpha state
  * It's for wordpress 1.5, while I'm using 2.0
  * It doesn't seem to be actively maintained. The last update is a year old, and the article has a bunch of people asking if there will be any more updates.

At least it's something. The page also mentions a plugin named LanguagePicker, but I couldn't find any other reference to it. If I had more time on my hands, I'd dive in the code and see what could be made of it, but unfortunately I haven't even found the time to personalize my blog design. I'm not even sure if it would be possible to write a plugin that does all (or most) of the above. Is the wordpress API powerful enough to provide for such functionality? So I'm a bit stuck for the moment. There's always the approach [Floris](http://www.florisla.be/blog/ "Floris his blog") uses, just use a category for each language. The positive thing with this method is that it works, you don't even need anything extra. Personally I think of this as an ugly hack. As Molly points out in her article, opening up web sites for an international audience does amount to more than just translating some of it. It's also not very elegant when you want to provide a post in more than one language. Do you post twice? Put both versions in the same post? What to do with categories/tags? It seems multilingual blogging isn't that popular for the moment, judging from the solutions people have come up with. So for the sake of coherence I'm sticking to English, but don't be surprised if sporadicly posts in another language do show up. _Update : I found a plugin that satisfies my needs. Read about it[here](http://www.arnebrasseur.net/index.php/2006/07/28/multilingual-blogging-with-gengo/en/)._
