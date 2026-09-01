---
title: Me loves Ruby
date: '2007-01-12T16:48:59+00:00'
author: Arne
permalink: /2007/01/12/me-loves-ruby
language: en
tags:
- Educational
- Tech
- Ruby
---

_This was published on a personal blog I ran during the nillies, it's included here as a historical archive. Links and images may be broken, it's been a while._

Mijn meest recente hobby draagt de ronkende naam Ruby. Deze uit Japan afkomstige programmeertaal is snel de wereld aan het veroveren dankzij het populaire (en gi-gan-tisch gehypte) webframework Ruby On Rails. In de hoop een stukje van mijn enthousiasme over te brengen een kort overzicht van enkele typische Ruby features.

### Objecten en boodschappen

Ruby is een object-georienteerde taal, en wat heb je daarvoor nodig? Objecten, uiteraard, en boodschappen die objecten naar elkaar kunnen sturen. Zo was het in den beginne bij Smalltalk, en zo is het in weze nog steeds. Men gebruikt hiervoor het gebruikelijke punt :
    
    
    variabele_die_object_bevat.een_boodschap('deze string is een parameter')
    

Puntkommas op het einde zijn niet nodig, een regeleinde is voldoende voor de parser. Ook de haakjes mogen weg wat soms een elegente syntax geeft: 
    
    
    verkoper.geef_me_een 'appel'
    

Om boodschappen te sturen heb je objecten nodig. Er zijn twee courante manieren om aan die objecten te geraken : klassen wat eigenlijk factory objecten zijn om bepaalde objecten mee te maken, of prototypes die je kan klonen en aanpassen, zoals in Javascript of Self. Ruby heeft een beetje van beide: 
    
    
    class Appel
      def proef
        puts 'lekker'
      end
    end
    
    fruit=Appel.new
    fruit.proef
    

of 
    
    
    appel=Object.new
    def appel.proef
      puts 'lekkere appel'
    end
    
    appel.proef
    
    #klonen
    peer=appel.dup
    
    def peer.proef
      puts 'lekkere peer'
    end
    
    peer.proef
    

### Idiomen en Metaprogrammeren

Een van de sterktes van Ruby is metaprogrammeren. Voor pakweg LISP enthousiasten is het geen nieuw concept, maar voor pakweg Java adepten kan dit toch wel een eye-opener zijn.
    
    
    class Teller
      10.times do |i|
        class_eval("def zeg#{i};puts 'ik zeg het u, #{i}';end")
      end
    end
    
    t=Teller.new
    t.zeg5
    #outputs : ik zeg het u, 5
    

In dit voorbeeld zitten een boel Ruby idiomen, ik zal beginnen bij het begin. We definieren een klasse, zoveel is duidelijk. wat tussen 'class Teller' en 'end' staat is code die uitgevoerd wordt in de context van de klasse. We kunnen daar methode definities zetten, maar ook andere code. In dit geval sturen we een boodschap naar het object 10. Alles in Ruby is een object, ook getallen, true en false en nil (de null van Ruby).

10 krijgt de boodschap 'times', en als argument geven we een block code mee, dat begint met 'do' en eindigt met 'end'. Een blok (block) is een soort anonieme functie. Java mensen kunnen denken aan anonieme binnenklassen (al is dat een veel minder flexibel concept). Wie wel eens een functionele taal gebruikt heeft of iets kent van lambda-calculus weet natuurlijk waar de klepel hangt. Onze anonieme functie heeft een parameter met naam 'i'. Deze staat aangegeven tussen de twee pipe symbolen.

Dit blok code zal tien keer uitgevoerd worden door de times methode van 10 (no kidding?). De parameter zal daarbij telkens een andere waarde krijgen van 0 tot 9. Wat doet het blok dan? Het gebruikt de methode class_eval om code die hij on-the-fly produceert uit te voeren in de context van de klasse. In dit geval genereren we een klasse definitie (een string) en laten die evalueren. Letterlijke strings in Ruby kennen twee varianten, strings met 'enkele quotes' zijn letterlijk zoals ze zijn. Alleen als je er een enkele quote in wil plaatsen moet je die 'escapen' met een backslash. In "dubbele quotes" kan je heel wat meer speciale dingen doen. Een daarvan is het ter plaatse invullen van het resultaat van expressies. Dat doe je door Ruby code tussen '#{' en '}' te plaatsen.

Ik vermoed dat het zo wel stilaan duidelijk is. Dit artikel licht slechts een tipje van de sluier op, ik hoop dat het enige interesse heeft weten te wekken.
