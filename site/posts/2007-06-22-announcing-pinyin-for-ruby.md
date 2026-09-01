---
title: Announcing Pinyin for Ruby
date: '2007-06-22T11:57:31+00:00'
author: Arne
permalink: /2007/06/22/announcing-pinyin-for-ruby
language: en
tags:
- Informational
- Tech
- Ruby
---

_This was published on a personal blog I ran during the nillies, it's included here as a historical archive. Links and images may be broken, it's been a while._

This is a little library I wrote to convert between various romanization systems for Chinese. It currently supports Hanyu pinyin, Wade Giles, Zhuyin Fuhao (BoPoMoFo) and IPA (international phonetic alphabet).

A number of different systems to represent tones are supported : numbers, stand alone tone marks, accents or no tones. Take note that not all combinations make sense. E.g. ipa + accents will not yield useful results. However, numbers and standalone marks can be used with any system.

To get it do this
    
    
    $ gem instal pinyin --source http://gems.arnebrasseur.net
    

Alternatively download the tarbal [_here_](http://gems.arnebrasseur.net/pinyin-0.0.1.tgz).
