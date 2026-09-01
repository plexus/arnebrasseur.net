---
title: Pinyin for Ruby 0.0.1 has been released
date: '2007-07-26T08:47:14+00:00'
author: Arne
permalink: /2007/07/26/pinyin-for-ruby-0-0-1-has-been-released
language: en
tags:
- Informational
- Tech
- Ruby
---

_This was published on a personal blog I ran during the nillies, it's included here as a historical archive. Links and images may be broken, it's been a while._

Today I released [my very first public Ruby project](http://rubyforge.org/projects/pinyin/)!

> Pinyin is a pure Ruby library with a specific focus : converting between various transliteration systems for Mandarin Chinese.
> 
> Over time various systems have been devised to capture the phonetics of the Chinese language, often as a teaching aid.
> 
> This is the first public release. Pinyin for Ruby can currently convert between five different systems, with more to come.
> 
> \- Hanyu Pinyin \- Wade-Giles \- Tongyong Pinyin \- Zhuyin Fuhao (BoPoMoFo) \- IPA (International Phonetic Alphabet)
> 
> It does this by decomposing each syllable into its three components (initial, final and tone) and then recomposing the syllable in a different system. This approach was chosen over a table based approach because it better captures the way each system works.
> 
> Conversion tables are included to test the package. 

There's still plenty to be done, but since it already has some nice capabilities I'm putting this out in the open, to see if there is any interest. Next step is to move from my private SVN repository to the one on Rubyforge. The project also needs a homepage.

Give it a whirl! Simply type
    
    
    gem install pinyin
    

(Adding 'sudo' where necessary.)
