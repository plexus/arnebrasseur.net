---
title: Installing OpenOffice 2.0.3 on Ubuntu Dapper Drake
date: '2006-08-18T10:50:08+00:00'
author: Arne
permalink: /2006/08/18/installing-openoffice-2-0-3-on-ubuntu-dapper-drake
language: en
tags:
- Educational
- Tech
- OpenOffice
- OOo
- OpenOffice 2.0.3
- Ubuntu
- Dapper
- dapper-proposed
- upgrade
- adept
- apt
- repositories
---

_This was published on a personal blog I ran during the nillies, it's included here as a historical archive. Links and images may be broken, it's been a while._

More than a month ago OpenOffice 2.0.3 was released, with a bunch of [notable improvements](http://development.openoffice.org/releases/2.0.3.html "OOo 2.0.3 Release Notes"). Dapper Drake, the latest release from the [Ubuntu](http://www.ubuntu.com "Linux for humans") Linux distro still contains version 2.0.2. Since it contains new features it will probably have to wait for the next release to be included. Updated packages can be added to a distribution through the updates repositories, but only if they address bugfixes only.

A bug in the outline numbering was giving me headaches so I looked around for an update. Ubuntu set up a special repository called dapper-proposed that contains the new version. All packages in this repository seem to be OpenOffice related. If you prefer the vi /etc/apt-sources.list; apt-get update; apt-get install routine I don't have to explain anything to you. For all the rest here's a step by step guide on how to install it.

  * Go to the System menu and launch adept (packetmanager), enter your password
  * From the View menu choose Manage repositories

There should be a line somewhere at the top that looks like this :

deb http://be.archive.ubuntu.com/ubuntu/ dapper main restricted

It might look a bit different, especially the country code (be) might be different or absent. Make sure it contains 'dapper' and 'main'.

  * Right click on it, choose 'Clone'
  * On the newly created line, double click on 'dapper' and change it to 'dapper-proposed'
  * At the bottom on the screen click on 'Apply'
  * At the top click on 'Fetch updates'
  * You return to the main Adept screen, type 'openoffice' in the search field
  * If all went right you should now see the package 'OpenOffice.org' in the list, with 'upgradable' next to it
  * Hit the little blue arrow next to it
  * Click on the 'Request update' button
  * Hit 'Apply changes' at the top of the screen

That's all it takes! OOo 2.0.3 ready to serve you.
