---
title: The Nitro documentation buzz
date: '2007-06-13T07:53:25+00:00'
author: Arne
permalink: /2007/06/13/the-nitro-documentation-buzz
language: en
tags:
- Nitro
- Informational
- Tech
- Ruby
---

_This was published on a personal blog I ran during the nillies, it's included here as a historical archive. Links and images may be broken, it's been a while._

I'm finding myself slowly emerged in the Nitro community. Nitro is a ruby framework for the rapid development of web applications. Another such framework, Ruby on Rails, has gotten a lot of press. The alternatives are less known : 

  * Camping
  * IOWA
  * Ramaze
  * Nitro

Nitro has a clean, flexible and extensible design. But there are as I see it two big obstacles in the way of mass adoption. Nitro is essentially developed by a single person, and so development has a rather slow pace. The other and bigger problem is the lack of documentation, and the amount of outdated documentation. 

The Nitro API has significantly changed over the versions (now at 0.4.2 with 0.5.0 on it's way). Personally I think this is a good thing. Too much clarity and coherence has been sacrificed for backward compatibility. But the result is that most examples found on the web no longer work, which is simply depressing for a noob trying 'Hello World'. 

There's a discussion going on on the Nitro mailing list about this, and it seems others agree this should be addressed. I'm willing to coordinate a decent documentation effort. In my opinion we need 

  * Step-by-step starter material
  * In depth reference guide
  * Cookbook style recipes for common tasks

I've been looking at how other FOSS projects do their docs. I've looked at three projects with excellent docs : Django, PHP and PostgreSQL. All use version controlled text files with some sort of markup language like DocBook. I think this is a smart way of doing things, since from there you could generate web pages, pdf files, whatever... all from the same source files. 

George Moschovitis, the lead developer has responded that he will bring back the Nitro wiki which is certainly a good start. I'm very curious how this will evolve.
