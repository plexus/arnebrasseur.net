---
title: Ping-pong spel in Python
---

Voor dit projectje gaan we [Pygame
Zero](https://pygame-zero.readthedocs.io/en/stable/) gebruiken. Werk stap voor
stap, en lees goed wat er staat. Typ code niet blind over zoder te begrijpen wat
ze doet!

Maak een nieuw bestand, `pingpong.py`

```python
WIDTH = 800
HEIGHT = 450

def draw():
    screen.fill("blue")

import pgzrun
pgzrun.go()
```

Je kan dit programma uitvoeren in je terminal met `python pingpong.py`, of
vanuit Thonny door op de groene knop met witte driehoek te klikken. Het
resultaat is een venster van 800 pixels breed, 450 pixels hoog, en met een
blauwe achtergrond.

Hier vind je een lijst met kleuren die je kan gebruiken: [Pygame Named Colors](https://www.pygame.org/docs/ref/color_list.html)

> ## Nederlands of Engels
> 
> In onze voorbeelden gebruiken we Nederlands voor namen van variabelen of
> functies die we zelf kiezen. Namen van functies of variabelen die deel
> uitmaken van Python, Pygame, of Pygame Zero zijn uiteraard in het Engels.


> ## Opdrachten
>
> 1. Verander de cijfers voor `WIDTH` en `HEIGHT`, wat gebeurt er?
> 2. Probeer een paar andere kleuren tot je een achtergrond vind die je leuk vind.
> 2. [extra] Zoek online naar een "color picker", je kan de hexadecimale waarde ook als kleur gebruiken, bv. `"#e0da63"`

Laten we lijn per lijn even kijken wat dit programma doet.

```python
WIDTH = 800
HEIGHT = 450
```

Hier definiëren we twee variabelen, `WIDTH` (breedte) en `HEIGHT` (hoogte). Deze
hebben een speciale betekenis in Pygame Zero, ze bepalen de grootte van het
venster.

```python
def draw():
```

Defineer de functie ("methode") `draw`. Pygame Zero zal deze functie
herhaaldelijk oproepen om ons spel te tekenen.

```python
    screen.fill("blue")
```

"Vul" het venster met een achtergrondkleur, in dit geval "blauw".

```
import pgzrun
pgzrun.go()
```

Laad en start Pygame Zero. Je kan deze twee lijnen weglaten als je het programma
dan start met `pgzrun` in plaats van met `python`. In Thonny gebruik je dan de
optie "Pygam Zero modus"

## De Bal Tekenen

Ons spel bestaat uit vier element: één bal, twee paddles, en een middelijn. De
bal is een cirkel, de paddles en middelijn zijn rechthoeken.

Een cirkel tekenen doe je met `screen.draw.filled_circle`. Zo ziet deze functie
er uit:

- `screen.draw.filled_circle((x, y), straal, kleur)`

Waarbij `x` en `y` de positie van de cirkel aangeven, `straal` de straal is van
de cirkel, en `kleur` de kleur aangeeft van de cirkel.

Om de bal te tekenen voegen we dit toe aan onze `draw` functie:

Bijvoorbeeld:

```
def draw():
    screen.fill("blue")
    screen.draw.filled_circle((200, 100), 50, "beige")
```

> ## Opdrachten
> 
> - Verander 200 (de x-waarde) in hogere of lagere waarden (bv. 100, 300, 150),
>   en kijk wat er gebeurt.
> - Verander 100 in hogere of lagere waarden (bv. 50, 200), en kijk wat er gebeurt.
> - Verander de waarde van de straal, kijk wat er gebeurd. Kies een goede grootte voor de bal.
> - Zoek een kleur die je leuk vindt.

Deze cirkel wordt onze bal. En aangezien de bal kan bewegen kunnen we niet
gewoon een vaste waarde zoals `100` of `200` gebruiken. In plaats daarvan maken
we twee variabelen, `bal_x` bevat de horizontale positie van de bal, `bal_y`
bevat de verticale positie van de bal. Hierdoor kunnen we nadien de bal doen
bewegen door deze variabelen te veranderen. De grootte van de bal noemen we
`bal_straal`.

> ## X en Y as 
> 
> Het assenstelsel in Pygame begint in de linker bovenhoek. De X-as loopt van
> links naar rechts, de Y-as van boven naar onder. 

```python
bal_x = 200
bal_y = 100
bal_straal = 25

def draw():
    screen.fill("blue")
    screen.draw.filled_circle((bal_x, bal_y), bal_straal, "beige")
```

## De Bal Doen Bewegen

We hebben reeds de functie `draw`, die Pygame Zero zal oproepen om ons spel te
tekenen. Er is nog een andere speciale function, `update`. Deze zal tientallen
keren per seconde opgeroepen worden. Hier kunnen we dingen laten veranderen,
zoals de positie van de bal.

Hiervoor introduceren we twee nieuwe variabelen, `bal_vx` oftewel de
horizontale snelheid van de bal, en `bal_vy`, de verticale snelheid van de bal.
In iedere update verplaatsen we de bal een klein beetje, afhankelijk van de
snelheid.

> `v` komt van het Engels, "velocity". Dit is ook het symbool dat we in de
> physica gebruiken voor snelheid.

```python
bal_vx = 1
bal_vy = 0
```

Voeg deze variabelen toe bovenaan je programma, onder `bal_straal = ...`.

Vervolgens voegen we een `update` functie toe. Dit is een speciale functie zoals
`draw` die door Pygame Zero opgeroepen zal worden, zo'n 60 keer per seconde.
Hier kunnen we de variabelen `bal_x` en `bal_y` wijzigen, zodat deze op een
andere plaats getekend wordt.

> ## Global
>
> `bal_x` en `bal_y` zijn "globale" variabelen, ze staan aan het begin van een
> lijn zonder spaties ervoor, en behoren dus niet to één enkele functie, maar
> wel tot het gehele programma. Zulke globale variabelen kunnen we zonder meer
> in elke functie raadplegen, maar als we ze willen wijzigen, dan moeten we dat
> eerst duidelijk maken aan python. Dat doen we met `global`.

```python
def update():
    global bal_x, bal_y
    bal_x = bal_x + bal_vx
    bal_y = bal_y + bal_vy
```

> ## Opdracht 
> 
> Verander de waarden van `ball_vx` en `ball_vy`, wat is het effect? Probeer ook
> negatieve getallen, zoals `-1` of `-2. Wanneer gaat de bal naar links of
> rechts, boven of onder? Wat gebeurt er als vx 0 is? Wat als vy 0 is? Wat als
> ze allebei nul zijn?

## De Middelijn tekenen

Een rechthoek tekenen doe je met `screen.draw.filled_rect`. Deze functie neemt
twee parameters, een "Rect" object dat de plaats en grootte van de rechthoek
bevat, en een kleur.

- `screen.draw.filled_rect(rechthoek, kleur)`

Om deze "rechthoek" te definieren gebruik je de `Rect` functie (opgelet, met een
hoofdletter!)

- `rechthoek = Rect(plaats, afmeting)`

Plaats en afmeting zijn elk een paar van twee getallen. Deze `plaats` komt
overeen met de _linker bovehoek_ van de figuur.

- `plaats = (x, y)`
- `afmeting = (breedte, hoogte)`

We kunnen dit alles te samen combineren in een functie die we `teken_rechthoek`
noemen. Deze kunnen we nadien gebruiken voor zowel de paddles als de middelijn
te tekenen.

```python
def teken_rechthoek(x, y, breedte, hoogte, kleur):
    plaats = (x, y)
    afmeting = (breedte, hoogte)
    rechthoek = Rect(plaats, afmeting)
    screen.draw.filled_rect(rechthoek, kleur)
```


Nu hebben we alle ingredienten om de middelijn te tekenen. We tekenen een smalle
rechthoek over de volledige hoogte van het scherm.

De `x` positie is in het midden van het venster, op de helft van de volledige
breedte: `WIDTH/2`.

De `y` positie is helemaal bovenaan, met andere woorden `0`.

De breedte kunnen we zelf kiezen, we introduceren hiervoor een extra variabele,
`middelijn_breedte`.

De hoogte is de hoogte van het volledige venster, oftewel `HEIGHT`.

```python
middelijn_breedte = 10

def draw():
    # ...
    teken_rechthoek(WIDTH/2, 0, middelijn_breedte, HEIGHT)
```

Als je goed kijkt zie je echter dat er een probleem is, de lijn bevindt zich
niet juist in het midden, maar wel rechts van het midden. Hoe breder je de lijn
maakt, hoe meer dit opvalt.

Om dit op te lossen moeten we een "correctie" toevoegen, door de lijn met de
helft van de breedte van de lijn naar links op te schuiven.

> ## Opdracht
> 
> - Verander de breedte van de lijn naar `20`, `30`, `50`, enz. Kijk wat er
>   gebeurt.
> - Verander `WIDTH/2` in `WIDTH/2 - middelijn_breedte/2`. Lost dit
>   het probleem op? Waarom?

## De paddles tekenen

Dit zijn ook twee rechthoeken, met een vaste grootte, maar een veranderende
positie. We moeten dus voor elke paddle dezelfde vier getallen kennen die we
nodig hebben voor het tekenen van een rechthoek: x, y, breedte, en hoogte.

Maar let op! Breedte en hoogte zijn voor beide paddles gelijk. Ook de y-positie
is twee keer dezelfde, enkel de x-positie is anders (en is ook de enige waarde
die verandert tijdens het spel).

Om het typen iets aanvoudiger te maken geven we deze variabelen namen die
beginnen met `pad`, als afkorting voor `paddle`.

```python
pad_breedte = 200
pad_hoogte = 40
pad_y = 400

pad1_x = 150
pad2_x = 550
```

```python
def draw():
    # ...
    teken_rechthoek(pad1_x, pad_y, pad_breedte, pad_hoogte, "beige")
    teken_rechthoek(pad2_x, pad_y, pad_breedte, pad_hoogte, "beige")
```

> ## Opdracht
> 
> De paddles zijn nu 200 op 40 pixels groot, ze zouden iets kleiner mogen zijn.
> Kies een waarde voor `pad_breedte` en `pad_hoogte` die je goed vindt. Pas
> eventueel `pad_y` aan.

## De paddles doen bewegen

Wanneer een toets ingedrukt wordt dan kan je daar op reageren in je `update`
functie. Dat zier er zo uit:

``` python
def update():
    # ...
    if keyboard[keys.LEFT]:
        print("links wordt ingedrukt")
    if keyboard[keys.RIGHT]:
        print("rechts wordt ingedrukt")
```

We gebruiken de pijltjestoetsten voor de rechtse paddle, en `X` en `C` voor de
linkse. Zo kunnen we makkelijk met twee spelers spelen. Voor de snelheid van de
paddles maken we een nieuwe variabele `pad_v`.

Opgelet! Om `pad1_x` en `pad2_x` te kunnen wijzigen moeten we eerst `global`
gebruiken.

``` python
pad_v = 5

def update():
    global bal_x, bal_y, pad1_x, pad2_x
    # ...
    if keyboard[keys.X]:
        pad1_x = pad1_x - pad_v
    if keyboard[keys.C]:
        pad1_x = pad1_x + pad_v
    if keyboard[keys.LEFT]:
        pad2_x = pad2_x - pad_v
    if keyboard[keys.RIGHT]:
        pad2_x = pad2_x + pad_v
```

## De bal doen botsen


