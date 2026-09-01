---
title: Java debugging tip
date: '2006-09-01T09:19:42+00:00'
author: Arne
permalink: /2006/09/01/java-debugging-tip
language: en
tags:
- Educational
- Tech
- Java
- debug
- exception
- observable
- listener
- pattern
- swing
---

_This was published on a personal blog I ran during the nillies, it's included here as a historical archive. Links and images may be broken, it's been a while._

Here's a little trick to help you debug your Java programs. When an Exception occurs and it's not caught somewhere, you get a stacktrace to help you locate the problem. When you do catch an Exception you can still call printStackTrace() on it. But what if you just like to see the call stack? Just make an Exception yourself! 

(new Exception()).printStackTrace(); 

Well actually the method is defined in Throwable, so you might as well write 

(new Throwable()).printStackTrace(); 

I find this very usefull when using the Observable pattern, as in the Java Swing API (there it's called listeners). One message can start a cascade of other messages throughout the system. This way you can see where it comes from. Use sparingly!
