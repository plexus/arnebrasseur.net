---
title: Internet Explorer on Linux
date: '2006-09-12T09:36:07+00:00'
author: Arne
permalink: /2006/09/12/internet-explorer-on-linux
language: en
tags:
- Educational
- Tech
- IEs4Linux
- IE5
- IE5.5
- IE6
- linux
- wine
- webdesign
- browsers
---

_This was published on a personal blog I ran during the nillies, it's included here as a historical archive. Links and images may be broken, it's been a while._

It's possible to run Internet Explorer on Linux (you know, that unsafe inferior webbrowser from Microsoft that the majority of internet users choose to use). In fact it's very easy thanks to the wonderful [IEs4Linux](http://www.tatanka.com.br/ies4linux/index-en.html) script.

The script will download and install IE5.01, IE5.5 and IE6. I tried it on my Ubuntu machine and it works flawless. Here's what you need to do:

First you need to make sure wine and cabextract are installed. Wine is a program that emulates the windows API so programs written for windows can run on Linux. Cabextract is necessary to unpack the files dowloaded from Microsoft.

Launch a terminal (gnome-terminal if you're on Gnome, konsole if you're on KDE) and type 

sudo apt-get install wine cabextract 

This will ask for your password. 

Now go the [IEs4Linux](http://www.tatanka.com.br/ies4linux/index-en.html) website and download the archive to your home directory. Now go back to the terminal:

tar xvzf ies4linux-2.0.3.tar.gz cd ies4linux-2.0.3/ ./ies4linux 

The script will ask which browsers you want to install, for the rest you're probably fine with the defaults. Now you're all set. The executables are placed in 'bin' inside your home directory. Making system wide symlinks is left as an exercise. (hint: man ln)

Having these quirky browsers at hand is a must for all web designers. If you use this solution for professional purposes please consider donating something to the project.
