---
title: 'Going Gutsy : installing the latest Ubuntu'
date: '2007-11-13T19:24:14+00:00'
author: Arne
permalink: /2007/11/13/going-gutsy-installing-the-latest-ubuntu
language: en
tags:
- Tech
- Informational
---

_This was published on a personal blog I ran during the nillies, it's included here as a historical archive. Links and images may be broken, it's been a while._

A while ago the fabulous folks from Ubuntu released the latest version of their popular Linux distributions. The version number of Ubuntu, Kubuntu and Edubuntu is now up to 7.10 (being the year and month it was released). More often however Ubuntu versions are referred to by their alliterating nicknames. I'll go over my experiences upgrading from Feisty Fawn to Gutsy Gibbon. 

### Upgrading from Feisty

Being a Debian based distribution all software management can be easily performed by the 'apt' family of tools. The first step in upgrading is letting apt know that I would from now on prefer the Gutsy packages. 

`
    
    
    sudo sed s/feisty/gutsy/g -i /etc/apt/sources.list
    sudo apt-get update
    

`

You might notice that there is no gutsy-commercial repository like there used to be a feisty-commercial, with for instance Opera and Realplayer. If you were using it you'll have to comment it out or keep using the one from Feisty. 

When I do these big upgrades I switch to a plain tty console because I've had it happen that one of the install scripts restarted X11. So press SHIFT-F1 and login to the console. To have some record of what happened afterwards I use the great little utility 'tee'. This will record whatever passes stdout to a file. So to start upgrading do this: 

`
    
    
    sudo apt-get dist-upgrade | tee feisty-to-gutsy-`date +%Y%m%d`.log
    

`

This way you'll get a nice date stamped log of the whole install process. If you switch back to X (Shift-F7) to do some work in the meanwhile, you can open a terminal window and do a 'tail -f' on that file so you know what's happening 

### First hickup : device-manager

After some 1300 packages were upgraded I rebooted my computer. A new kernel (2.6.22-14) had been installed, and it seemed to be making some trouble. Instead of booting into X I saw per second several identical error messages flying over my screen, the gist of it being: 
    
    
    device-manager: linear: dm-linear: Device lookup failed
    

Booting with my previous kernel solved the problem, so I got on-line and after some googling found a fix, in /etc/evms.conf find the line that says 'exclude=...' and change it to: 
    
    
    exclude = [ sd* ]
    

I'm not even sure what EVMS is, I'm guessing this is related to HAL (Hardware Abstraction Layer). What this does is exclude your harddrives, from eh.. something. It solved the problem for me. 

I've had kernel upgrades in Ubuntu create problems before, IMHO they could be a little bit more conservative with their kernels. 

### Smaller hickups

After booting again everything was working like a charm. They also did a great job again on the artwork. I had to tweak a few more small things.

Mozilla's Thunderbird email program is now started simply with 'thunderbird' in stead of 'mozilla-thunderbird'. I had to update a shortcut.

I use Emacs for most of my editing tasks, and somehow the version with XFont backend that I previously had installed got replaced by another one. The version with XFont support is not available in the Ubuntu repositories so this is understandable. There's a guy that packages this pretty version, and apparently he has since I last checked made an actual apt repository available. See this article : [Pretty Emacs by Alexandre Vassalotti](http://peadrop.com/blog/2007/01/06/pretty-emacs/). 

Looking closer I found to have three different Emacs flavors on my system. I just removed them all, added the repositories from Alexandre and installed my trusted XFont version. Nice!

There seems to also be something going on with StarDict. The little windows that pop up with translations jump to the upper left corner when you hover over them, and then immediatly disappear. I might have to revert to the old version.

### Conclusion

I've been upgrading my system since Dapper, and although there are often small hickups a little bit of google will solve most of your woes. I've only used my new system for a day now, so there's probably lots of new little features that I still have to discover. All in all this seems to be another rock solid distribution which I'd wholeheartedly recommend to both newbies and seasoned hackers.
