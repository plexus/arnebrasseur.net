---
title: Goed en wel gearriveerd
date: '2007-01-19T15:39:36+00:00'
author: Arne
permalink: /2007/01/19/goed-en-wel-gearriveerd
language: nl
tags:
- Travels
- Informational
---

_This was published on a personal blog I ran during the nillies, it's included here as a historical archive. Links and images may be broken, it's been a while._

De verhuis is vlot verlopen, en deze site draait nu vanop de Dreamhost servers. Yaheej! Er is wel nog iets niet in orde met de quota instellingen, waardoor enkele emails niet zijn aangekomen. Wie mij mail stuurde en een 'bounce' terug kreeg : probeer aub nog eens.

Een paar tips voor wie zijn wordpress site wil migreren:

  * Zet je plugins af voor de verhuis
  * Zet je permalink instellingen op default

Bij mij stond Gengo nog aan, en die voegde telkens /en/ of /nl/ toe aan de URL, waardoor Apache dan weer niet vond wat er gevraagd werd. Om dezelfde reden geraakte ik niet meer in mijn admin panel (al zou dat achteraf bekeken misschien wel gelukt zijn). Ik heb dan maar met de hand mijn plugins afgezet. Dat kan in pakweg PhpMyAdmin met de volgende SQL: 
    
    
    UPDATE wp_options SET option_value='a:0:{}' WHERE option_name='active_plugins'
    

Het kan zijn dat wp_ moet vervangen worden door een ander prefix, dat kan je zien in wp-config.php. 

Als je command-line toegang hebt kan je het ook van daar doen: 
    
    
    echo "UPDATE wp_options SET option_value='a:0:{}' WHERE option_name='active_plugins';
        " | mysql -u <user> -p<paswoord> --host=<server>
    

Let op: geen spatie tussen -p en <paswoord>! 

Nog een tip voor wie domeinnamen verhuist : schrijf het IP adres van je mailserver op voor je je DNS settings aanpast, dan kan je eens de verhuis rond is, kijken of er geen mails nog in je oude inbox terecht zijn gekomen. Ik had dat niet gedaan en heb een support ticket moeten openen om er achter te komen.
