---
title: Making vector images from musical scores
date: '2006-08-02T12:37:55+00:00'
author: Arne
permalink: /2006/08/02/making-vector-images-from-musical-scores
language: en
tags:
- Educational
- Tech
- vector images
- musical scores
- svg
- denemo
- noteedit
- eps
- OOo
- OpenOffice
- OpenOffice.org
- Open source
- FOSS
---

_This was published on a personal blog I ran during the nillies, it's included here as a historical archive. Links and images may be broken, it's been a while._

_Summary of the original Dutch article._

This article describes how to edit a musical score, export it, convert it to a vector based image format, edit it and import it in an openoffice writer document, all using open source progams that are available for Ubuntu. I wrote it because I spent half a day figuring out how to achieve this. Here's a short outline of the process.

  * Create a musical document with either Noteedit or Denemo. Export as an ABC file.
  * Convert the ABC to a Postscript (ps) file with abcm2ps
  * Convert the Postscipt file to an SVG (Scalabel vector graphics) with pstoedit
  * Either edit with Inkscape and save as EPS or
  * Install the OpenOffice import filter for SVG and edit with OOo Draw
  * Either import the EPS in your writer document, or just copy/paste from OOo Draw

If you don't speak Dutch and want to read the whole article, let me know (click on 'Contact' at the top of the page) and I will translate it.
