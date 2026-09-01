---
title: Vectorafbeeldingen van notenbalken maken
date: '2006-08-02T12:22:47+00:00'
author: Arne
permalink: /2006/08/02/vectorafbeeldingen-van-notenbalken-maken
language: nl
tags:
- Educational
- Tech
- vectorafbeeldingen
- notenbalken
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

Wat wil ik? Een muziekstukje bewerken in een daarvoor gemaakte editor, de boel in een vectorformaat exporteren, vervolgens met een vector gebaseerd tekenprogramma dingen toevoegen en het resultaat importeren in OpenOffice. Ondertussen heb ik een dozijn programmaatjes uitgeprobeerd voor verschillende stadia in het proces. Bij momenten begon de frustratie hoog op te lopen. Ik hoop dat dit relaas ooit iemand deze lijdensweg zal besparen. De laatste stappen kunnen bovendien ook nuttig zijn voor andere toepassingen dan muziekschriftuur.

Er bestaan twee verschillende soorten formaten om afbeeldingen op te slagen : bitmap formaten en vector formaten. Bitmap formaten slagen een raster van puntjes op. Ze hebben dus een hoogte en breedte in pixels. Voorbeelden zijn Jpeg PNG, GIF, BMP en nog zo'n 1024 anderen. Een vector gebaseerd formaat slaat informatie op over uit welke elementen je afbeelding bestaat, lijnen, krommen, tekst, cirkels en zo voort. Omdat het al deze informatie behoudt kunnen afbeeldingen op gelijk welke resolutie getoond worden, ze zullen altijd even scherp zijn.

Het spreekt voor zich dat ik de beste kwaliteit wil voor m'n thesis, het is dus een logische keuze van een vector formaat te gebruiken. Voorbeelden zijn PS (postscript) en PDF (portable document format) die beide vooral voor volledige documenten gebruikt worden, en EPS (encapsulated postscript) en SVG (scalable vector graphics) die meer voor afzonderlijke afbeeldingen gebruikt worden.

Ik gebruik Ubuntu linux, dus bij voorkeur gebruik ik paketten die deel uitmaken van de Ubuntu distributie. Voor het editeren van muziek vond ik Noteedit, gebaseerd op QT zodat het goed integreert met KDE, en Denemo, gebaseerd op GTK+ zodat het zich eerder thuis voelt op een Gnome desktop. Beide gebruiken 'lilypond' als backend. Lilypond is een erg krachtig, maar ook ingewikkeld programma voor het maken van professionele partituren. Mijn voornaamste indruk hier is dat beide programma's nog niet zo volwassen zijn. In Noteedit lukte het me niet een geplaatste noot weer te verwijderen, hoewel de handleiding volhield dat zowel backspace als delete daarvoor zouden dienen. Noteedit exporteert naar geen enkel grafisch formaat, wel naar een handvol beschrijvende muziekformaten waaronden MusixTex (een uitbreiding van LaTex), MIDI, Lilypond en ABC. Denemo heeft een aanlokkelijke 'export naar PDF' feature. Ik doorloop de procedure, klik ok en... noppes. Geen PDF, geen foutboodschap. Ge-wel-dig zou Eddy Wally haast zeggen.

Nog niet veel soeps dus, maar deze programma's laten me toch al toe wat noten en een sleutel op een notenbalk te plaatsen. Eens kijken waar we van daar heen kunnen. Bijden kunnen ABC bestanden schrijven, en in de Ubuntu repositories vind ik een pakketje abcm2ps, dat zo'n bestandje omzet naar Postscript. Nu is Postscript eigenlijk ontstaan als een formaat om printers mee aan te sturen. Op sommige plaatsen wordt het nog uitvoerig gebruikt, maar als grafisch formaat is het niet zo courant.

Ik zou het resultaat graag nog wat bewerken, kwestie van echte instructieve grafiekjes te krijgen om mijn tekst mee op te fleuren. Voorheen heb ik vaak 'The Gimp' gebruikt om grafische bestanden te bewerken, maar Gimp doet niet aan vector graphics, dan maar iets anders proberen. Ik probeer Sodipodi maar besluit uiteindelijk Inkscape te gebruiken. Inkscape leest weliswaar geen Postscript, maar een pakketje dat 'pstoedit' heet zet mijn postscript mooi om naar SVG.

Dat begint al wat op te schieten. OpenOffice leest dan weer geen SVG... nu begint het wel echt te fraai te worden. Ik kan in Inkscape opslaan als EPS, maar even googlen levert me een ['import filter' op voor OpenOffice](http://www.ipd.uka.de/~hauma/svg-import/) (hier vind je ook de [instructies voor Ubuntu](http://www.ubuntuforums.org/showthread.php?t=92616)), zodat ik rechtstreeks de SVG bestanden kan lezen. De bestanden worden geladen in OpenOffice Draw, een tekenprogramma dat van nature vector-based is. Misschien besluit ik zelfs nog alle nodige aanpassingen rechtstreeks daar te doen, al lijkt Inkscape me wel krachtiger. Copy, paste, en daar staat de afbeelding die ik nodig had, klaar voor de drukker. Weliswaar een halve dag gespendeerd, maar op termijn zal me dat zeker opleveren.
