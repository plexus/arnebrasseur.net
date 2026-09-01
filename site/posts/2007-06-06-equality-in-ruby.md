---
title: Equality in Ruby
date: '2007-06-06T07:42:28+00:00'
author: Arne
permalink: /2007/06/06/equality-in-ruby
language: en
tags:
- Educational
- Tech
- Ruby
---

_This was published on a personal blog I ran during the nillies, it's included here as a historical archive. Links and images may be broken, it's been a while._

Just so you know:

  * == a.k.a. value equality
  * eql? a.k.a. value and type equality
  * equal? a.k.a. object identity
  * === a.k.a. case equality

Object identity should never be overridden. It compares object ids, always. 

When overriding, start with value equality. If you're not doing type conversions then you can simply add "alias :eql? :==". I was surprised to learn that eql? doesn't call == or vice versa (by default). 

Case equality defaults to calling value equality, but you can do handy stuff by providing a custom interpretation. This is what makes ruby's case statement so powerful Notable examples are Class, where it tests if the object is an instance of the class, and Regexp, where it tests if the string matches. Because of this your can write 
    
    
      case p
      when Symbol;...;end
      when Class;....;end
      end
    

or 
    
    
      case str
      when /\\A\\d+\\z/
        ...
      end
      end
