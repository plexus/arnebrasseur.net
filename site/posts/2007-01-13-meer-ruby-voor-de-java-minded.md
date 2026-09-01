---
title: Meer Ruby voor de Java-minded
date: '2007-01-13T11:27:59+00:00'
author: Arne
permalink: /2007/01/13/meer-ruby-voor-de-java-minded
language: nl
tags:
- Educational
- Tech
- Ruby
---

_This was published on a personal blog I ran during the nillies, it's included here as a historical archive. Links and images may be broken, it's been a while._

In mijn [vorige post](http://www.arnebrasseur.net/index.php/2007/01/12/me-loves-ruby/nl/ "Me loves Ruby") bezong ik reeds het lof van de dynamische programmeertaal Ruby. Tijdens mijn studies tot informaticus heb ik kilometers Java code voortgebracht. Het is dus een beetje mijn digitale moedertaal. In deze post leg ik de twee programmeertalen naast elkaar.

Misschien heb jij wel examens nu en vorm je dus een ideaal doelwit voor willekeurige afleiding. Om het meeste plezier uit dit artikel te halen (geloof me, Ruby _is_ fun) kan je best meevolgen in irb, de 'interactive ruby' shell. Dat kan van in je webbrowser : [Try Ruby!](http://tryruby.hobix.com/).

Je kan het ook installeren, op Debian/Ubuntu/... linux commandeer je gewoon het volgende
    
    
    sudo apt-get install irb ruby
    

Voor Windows is er een one-click installer te vinden op [Ruby-lang.org](http://www.ruby-lang.org/)

### Driewerf dynamisch

Het grootste verschil tussen Ruby en Java is dat Ruby een echte dynamische taal is. Dat uit zich op verschillende vlakken.

#### Duck typing

In Java zijn klassen gelijk aan types. Een variabele is van een bepaald type, en dat type komt overeen met een klasse. Nochtans zijn types iets heel anders dan klassen. In Ruby hebben variabelen geen types. Je kan aan een variabele eender welk soort object toewijzen.

In Java
    
    
    Integer[] a = {10,11,12};
    String b="boeboeketsjoe";
    File c=new File("some.txt");
    

In Ruby
    
    
    a=[10,11,12]              #class Array
    a='boeboeketsjoe'         #class String
    a=File.open('some.txt')   #class File
    

Omdat de interpreter niet weet welk type een variabele heeft weet hij ook niet welke methodes er ter beschikking zijn. Daarom wordt er pas gekeken wat te doen bij het uitvoeren van de eigenlijke instructie. Hierdoor kan je makkelijk objecten van verschillende types uitwisselen.

In Ruby
    
    
    def get_numbers(v)
      (0..9).each do |i|
        v << i
      end
    end
    a=[10,11,12] 
    get_numbers(a)
    #a is nu [10, 11, 12, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
    
    a='boeboeketsjoe'
    get_numbers(a)
    #a is nu "boeboeketsjoe\\000\\001\\002\\003\\004\\005\\006\\a\\010\\t"
    #\\001 is het karakter met als octale ascii waarde 001
    
    a=File.open('some.txt', 'w')    #w van write
    get_numbers(a)
    a.close
    #het bestand some.txt bevat nu 0123456789
    

Dit werkt omdat zowel arrays, strings als files de methode << implementeren. In Java gebruikt men typisch interfaces hiervoor. We zouden bijvoorbeeld de abstracte klasse Writer kunnen gebruiken : 
    
    
    class C {
        void getNumbers(Writer w) {
            for (int i = 0 ; i < 10 ; i++) {
                w.write(i);
            }
        }
    }
    

Voor het bestand gebruiken we dan een OutputStreamWriter, en voor de String een StringWriter. Voor de Array zullen we onze eigen Writer klasse moeten voorzien. Volgens de javadoc van java.io.Writer moeten we daarvoor alleen maar de methodes write(char[], int, int), flush(), en close() implementeren. Misschien toch wat veel overhead wanneer alleen write(int) ons interesseert. Er zijn natuurlijk nog andere mogelijke oplossingen. Met reflectie kan je heel wat dynamische dingen bereiken, inclusief de gigantische hoop Exception handling die daar bij komt kijken.

Een nadeel van variabelen geen type te geven is dat je type-safety verliest. Of dat opweegt tegen de flexibiliteit is een aangaande discussie met hevige voorstanders aan beide kanten. Sommige fouten die bij Java bij het compileren naar boven komen, komen in Ruby pas bij het uitvoeren naar boven. Anderzijds, tot voor Java 5 generics introduceerde stockeerden alle standaard collecties objecten van het type Object, en moest je bij het opvragen expliciet casten. Wat zich in Java uit(te) als een ClassCastException uit zich in Ruby als een NoMethodError. Nochtans leverde dit in de praktijk slechts zelden problemen op. Voorstanders van Ruby argumenteren dat je toch een degelijke set unittests nodig hebt, en dat die mogelijke problemen opvangen.

En dan nog die gekke titel. Het Ruby systeem heet duck typing, want als het kwaakt als een eend, en waggelt als een eend, dan wordt het met plezier als een eend beschouwd.

#### Uitbreidbaar

Klassen en objecten in Ruby zijn eindeloos uitbreidbaar. Je kan altijd methodes toevoegen (tenzij de klasse expliciet bevroren werd.) Ook de klassen die standaard ingebakken zitten kan je uitbreiden. De ActiveSupport module van Ruby On Rails (RoR) voegt bijvoorbeeld een methode pluralize toe aan String :
    
    
    "person".pluralize
    #resultaat : people
    

In java zou je een eigen afgeleide klasse moeten maken. Je kan in Ruby ook methodes herdefinieren. Wat denk je hiervan:
    
    
    class Fixnum
      def +(i)
        self.-(i)
      end
    end
    
    5+2
    # resultaat: 3
    

#### mixins

Ruby kent geen meervoudige overerving, elke klasse heeft (net als in Java) één superklasse. Ruby heeft wel iets waarmee je gelijkaardige dingen kan doen : mixins. Het principe is eenvoudig, bijvoorbeeld :
    
    
    module Logging
      def warn(str)
        puts "WARNING : <#{self.to_s}> #{str}"
      end
    
      def info(str)
        puts "INFO : <#{self.to_s}> #{str}"
      end
    
      def error(str)
        puts "ERROR: <#{self.to_s}> #{str}"
      end
    end
    
    class A < Object       #erf over van Object, de default
      include Logging
    
      def do_it
        info "doing it"
      end
    end
    
    a=A.new
    a.do_it
    #toont INFO : <#<A:0xb7cbcc4c>> doing it
    

Dit wordt pas echt nuttig als ingemixte methodes gaan interageren met bestaande methodes. Als je klasse de methode <=> (de spaceship operator) implementeert, (geeft negatief terug indien kleiner dan, 0 indien gelijk, positief indien groter dan), dan kan je de Comparable module includen en krijg je <, >, ==, <=, >= en between? cadeau.

### Stijl

Mijn professor voor het vak Programmeren in Java hamerde nogal eens op het 'programmeren met stijl'. Een belangrijk aspect daarvan is het afschermen van de interne keuken van een object. Attributen (instantie variabelen) horen privaat/protected te zijn met eventueel getters/setters om ze van buitenaf te bereiken. Dit wordt ook wel het principe van de uniforme interface genoemd, voor het eerst geponeerd door Meyer. Voor de gebruiker van een klasse mag het niet uitmaken of de opgevraagde waarde in een variabele gestockeerd was, ter plaatse berekend, gelezen van een bestand, enz.

In Ruby zijn attributen altijd privaat. Je moet dus getters en setters voorzien.
    
    
    class Kast
      def inhoud
        return @inhoud    # @ betekent attribuut, prefix voor instantievariabelen
      end
    
      def inhoud=(i)
        @inhoud=i
      end
    end
    

Maar gelukkig kan het een heel eind korter :
    
    
    class Kast
      attr_reader :inhoud
      attr_writer :inhoud
    end
    
    #of in één keer
    
    class Kast
      attr_accessor :inhoud     #reader+writer
    end
    
    boekenkast=Kast.new
    boekenkast.inhoud=['The Pragmatic Programmer', 'Programming Ruby']
    puts boekenkast.inhoud
    

### Functioneel programmeren

Verschillende features in Ruby zijn geinspireerd door functionele talen als Haskell, Miranda of OCaml. Om te beginnen is het resultaat van een methode het resultaat van de laatste expressie, tenzij er een expliciete return staat. Erg handig als het resultaat rechtstreeks berekent wordt uit de argumenten:
    
    
    def kwadraat(i)
      i*i
    end
    

Andere invloeden zijn de methodes map en inject uit Array. Map is voor Haskell adepten niets nieuw, en inject is gewoon een foldr
    
    
    [1,2,3].map {|i| i*i}
    #resultaat [1,4,9]
    

Het stuk tussen de haakjes is opnieuw een blok (soort anonieme functie, zie [vorige post](http://www.arnebrasseur.net/index.php/2007/01/12/me-loves-ruby/nl/ "Me loves Ruby")). De map methode zal dit blok uitvoeren voor elk element in de array. Het resultaat is een nieuwe array met de resultaten.
    
    
    [1,2,3].inject(0) {|som, term| som+term}
    #resultaat 6
    

Inject zal ook voor elk element in de array het blok uitvoeren, met de tweede variabele (hier term) gelijk aan de waarde uit de array. De eerste keer dat het blok wordt uitgevoerd heeft som de waarde 0 (argument van inject). De keren daarna is som gelijk aan het resultaat van de vorige uitvoering van het blok.

### Iterators

Java 5 introduceerde een shorthand voor itereren over collecties. De idiomatische manier in Ruby is met een iterator functie. Het volgende voorbeeld behoeft weinig uitleg
    
    
    [5,10,37].each do |i|      #blok, andere syntax
       puts "i==#{i}"
    end
    

Toch iets handiger dan 
    
    
    for(Iterator it = collectie.iterator() ; it.hasNext(); ) {
        Integer i = (Integer)i.next();
        System.out.println("i==" + i);
    }
    

Of pakweg
    
    
    for (int j=0 ; j < ar.lenght; j++) {
        int i=ar[j];
        System.out.println("i==" + i);
    }
    

  

Ik geloof dat Ruby en Java beide hun plaats hebben in het landschap der programmeertalen. Na jaren van Java is Ruby een heerlijk verfrissende afwisseling. Ze werken trouwens prima samen dankzij JRuby. In 'The Pragmatic Progammer' van Andy Hunt en Dave Thomas raadt men programmeurs aan elk jaar een nieuwe taal te leren. Naast een extra gereedschap en een extra aandeel in je kennisportfolio doet elke taal je op een andere manier over problemen denken. Mijn Java en C++ programmatie ondervindt nu al een positieve invloed van mijn avonturen in Ruby.

Welke taal ga jij leren in 2007?
