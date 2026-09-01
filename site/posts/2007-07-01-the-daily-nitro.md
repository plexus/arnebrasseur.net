---
title: The daily Nitro
date: '2007-07-01T09:13:41+00:00'
author: Arne
permalink: /2007/07/01/the-daily-nitro
language: en
tags:
- Nitro
- Informational
- Tech
- Ruby
---

_This was published on a personal blog I ran during the nillies, it's included here as a historical archive. Links and images may be broken, it's been a while._

**Important 2007-12-01 :** This article is deprecated. The location of the repository changed and I haven't had the time to update my scripts to reflect this. With version 0.50 approaching an easier way of trying out Nitro is on the horizon. I'll write a new article soon that will supersede this one. If you need help getting Nitro up and running please ask advice on the Nitro mailing list.

_Update:_ Changed the RUBYOPT line to load rubygems and to be compatible with the RSpec specs.

As I've mentioned I'm becoming involved in the Nitro community, a web framework for Ruby.

The latest release is 0.42, but it's an old, outdated release and is no longer recommended. In stead we urge everyone to use the repository version, wich in time will become 0.50. The version control system used is Darcs, a powerful distributed VCS written in Haskell. However since darcs is less popular than say Subversion or CVS, I decided to make a daily tarball. This way you can try the repo version without having darcs installed.

It can be found at <http://arnebrasseur.net/glycerin/nitro-latest.tar.gz>. This is a symlink to a timestamped file in the same [directory](http://arnebrasseur.net/glycerin). A new one should be created daily around 9.30 CET (8.30 GMT).

To get started with it you need to set up your ruby load path. The easiest way to do this is to load the glycerin script, which will set up the load path to include the various Nitro components (Nitro, Raw, Og and Glue).

I have this in my ~/.bashrc to do this behind the scenes.
    
    
    export NITRO_REPO=~/work/nitro
    export RUBYOPT="-r$NITRO_REPO/script/lib/glycerin -rubygems"
    

You will also need Facets, Xmlsimple and RedCloth. These can be installed as gems. To run the blog example you also need the uuidtools gem.

There's also a nitro command to start a Nitro app with various options. It can be found under nitro/nitro/bin, so I added this to .bashrc:
    
    
    export PATH=~/work/nitro/nitro/bin:"${PATH}"
    

An overview of the command line options (and other assorted Nitro articles) are available [on my wiki.](http://wiki.arnebrasseur.net/NitroOg/CommandLineOptions)

To get a feel for a typical nitro application, take a look at examples/blog.

Please report any difficulties you had with getting the repo version up and running.
