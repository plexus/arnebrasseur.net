---
title: Pinyin and unicode in ruby
date: '2007-02-23T15:54:27+00:00'
author: Arne
permalink: /2007/02/23/pinyin-and-unicode-in-ruby
language: en
tags:
- Informational
- Tech
- Ruby
---

_This was published on a personal blog I ran during the nillies, it's included here as a historical archive. Links and images may be broken, it's been a while._

I'm seriously looking into unicode stuff in ruby. Ruby strings are essentially just arrays of bytes, so they're not very encoding aware. Many have been asking for proper unicode support in ruby, but....

  * It's hard to make the string class internally based on an array of code points, for instance, without breaking existing code
  * ruby comes from Japan, where unicode isn't that popular (as far as I can see this is mainly because of controversy surrounding the unicode 'Han unification' )
  * Matz has stated in interviews that he doesn't see the problem, since it's perfectly possible to use unicode in Ruby, it just requires a bit of work

This last point basically means that it's possible to convert a utf-8 encoded string into an array of codepoints, like so:

`
    
    
    "Some string in utf-8".unpack('U*')
    

`

Later on you can pack it in a similar way, and in the meantime do whatever unicode stuff you like. 

The ActiveSupport::Multibyte module in rails adds a 'chars' method to the ruby string. This returns a Multibyte::Chars object, which is basically a proxy that forwards string method calls to a handler for a certain encoding. Currently there's a handler for utf-8, and a dummy handler that behaves like the basic ruby string. 

During the summer there was a [post on ruby-talk](http://blade.nagaokaut.ac.jp/cgi-bin/scat.rb/ruby/ruby-talk/197946) about a similar project by Rob Leslie. It's a one-man project which is unmaintained by now, but it has some interesting stuff. Most importantly it contains a part of the unicode character database (UCD), with classes to access the files. When a part of the database is first used, the text files are parsed and stored as pstore. 

What interests me about this is that there is a wealth of information about Chinese characters in the unihan database. Unihan support wasn't included but was trivially to add. Now I can retreive the pinyin, definition, radical and stroke count based of any chinese character. Cool! 

Hopefully all this will emerge into something useful. Here's a little snippet with the code points of the various pinyin vowels with tone marks. 

`
    
    
    PY_CODEPOINTS={
      :a=>[97, 257, 225, 462, 224],
      :e=>[101, 275, 233, 283, 232],
      :i=>[105, 299, 237, 464, 236],
      :o=>[111, 333, 243, 466, 242],
      :u=>[117, 363, 250, 468, 249],
      :U=>[252, 470, 472, 474, 476]
    }
    

`

PY_CODEPOINTS[:a][3] returns the code point number of an 'a' with a third tone. the uppercase U is the u with umlaut (u). 

My first mini experiment will be to make a pinyin with tone numbers to (unicode) pinyin with tone markers converter, like this site : [pinyin to unicode conversion](http://www.foolsworkshop.com/ptou/) but then in ruby of course.

Thanks go to this site : [Reading and writing Chinese characters and pinyin as unicode on the web](http://www.math.nus.edu.sg/aslaksen/read.shtml) for giving a handy overview of the various code points used for pinyin.
