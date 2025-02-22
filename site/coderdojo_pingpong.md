---
title: Ping-pong spel in Python
---

Voor dit projectje gaan we [Pygame Zero](https://pygame-zero.readthedocs.io/en/stable/) gebruiken.

Maak een nieuw bestand, `pingpong.py`

```python
from pygame import Color

WIDTH = 800
HEIGHT = 450

def draw():
    screen.fill(Color("blue"))


import pgzrun
pgzrun.go()
```

Je kan dit programma uitvoeren in je terminal met `python pingpong.py`, of
vanuit Thonny door op de groene knop met witte driehoek te klikken. Het
resultaat is een venster van 800 pixels breed, 450 pixels hoog, en met een
blauwe achtergrond.

Hier vind je een lijst met kleuren die je kan gebruiken: [Pygame Named Colors](https://www.pygame.org/docs/ref/color_list.html)

> ## Opdrachten
> 1. Verander de cijfers voor `WIDTH` en `HEIGHT`, wat gebeurd er?
> 2. Probeer een paar andere kleuren tot je een achtergrond vind die je leuk vind.
> 2. [extra] Zoek online naar een "color picker", je kan de hexadecimale waarde ook als kleur gebruiken, bv. `Color("#e0da63")`

Laten we lijn per lijn even kijken wat dit programma doet.

```python
from pygame import Color
```

Hier zeggen we dat we de module `pygame` willen gebruiken. Deze module bevat een
"klasse" `Color`, die we in ons programma willen kunnen gebruiken. Dit laat ons
toe bijvoorbeeld `Color("lightblue")` of `Color("tomato")` te schrijven.

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
    screen.fill(Color("blue"))
```

"Vul" het venster met een achtergrondkleur, in dit geval "blauw".

```
import pgzrun
pgzrun.go()
```

Laad en start Pygame Zero.

## De Bal Tekenen

Ons spel bestaat uit vier element: één bal, twee paddles, en een middelijn. De
bal is een cirkel, de paddles en middelijn zijn rechthoeken.

Een cirkel tekenen doe je met `screen.draw.filled_circle` 

```python
screen.draw.filled_circle((x, y), grootte, kleur)
```

Deze functies kan je gebruiken in je eigen `draw` functie om dingen te tekenen.
Je tekent dan eerst de achtergrond, en nadien de dingen die bovenop de
achtergrond komen.

Bijvoorbeeld:

```
def draw():
    screen.fill(Color("blue"))
    screen.draw.filled_circle((200, 100), 50, Color("seagreen1"))
```

> ## Opdracht 
> Kies andere waarden in plaats van `200`, `100`, `50`. Snap je wat
> elk getal doet?

Deze cirkel wordt onze bal. En aangezien de bal kan bewegen kunnen we niet
gewoon een vaste waarde zoals `100` of `200` gebruiken. In plaats daarvan maken
we twee variabelen, `ball_x` bevat de horizontale positie van de bal, `ball_y`
bevat de verticale positie van de bal. Hierdoor kunnen we nadien de bal doen
bewegen door deze variabelen te veranderen.

```python
ball_x = 200
ball_y = 100

def draw():
    screen.fill(Color("blue"))
    screen.draw.filled_circle((ball_x, ball_y), 20, Color("seagreen1"))
```

Denk er aan na elke verandering te testen dat je spel nog werkt!

Ten slotte maken we ook een variabele voor de grootte van de bal. Aangezien de
grootte niet verandert tijdens het spel schrijven we de naam van deze variabele
in hoofdletters. Dat moet niet per se, maar het maakt ons programma wel
duidelijker. We gaan deze grootte nadien nog nodig hebben om te bepalen of de
bal tegen de paddle botst

```python
BALL_SIZE = 20

ball_x = 200
ball_y = 100

def draw():
    screen.fill(Color("blue"))
    screen.draw.filled_circle((ball_x, ball_y), BALL_SIZE, Color("seagreen1"))
```

## De Bal Doen Bewegen

We hebben reeds de functie `draw`, die Pygame Zero zal oproepen om ons spel te
tekenen. Er is nog een andere speciale function, `update`. Deze zal tientallen
keren per seconde opgeroepen worden. Hier kunnen we dingen laten veranderen,
zoals de positie van de bal.

Hiervoor introduceren we twee nieuwe variabelen, `ball_vx` oftewel de
horizontale snelheid van de bal, en `ball_vy`, de verticale snelheid van de bal.
In iedere update verplaatsen we de bal een klein beetje, afhankelijk van de
snelheid.

Wanneer we in Python een globale variabele willen wijzigen moeten we dat eerst
aangeven met `global`.

```python
ball_vx = 1
ball_vy = 0

def update():
    global ball_x, ball_y
    ball_x = ball_x + ball_vx
    ball_y = ball_y + ball_vy
```

> ## Opdracht 
> Verander de waarden van `ball_vx` en `ball_vy`, wat is het effect?
> Wat gebeurd er als vx 0 is? Wat als vy 0 is? Wat als ze allebei nul zijn?

## De Middelijn tekenen

Een rechthoek tekenen doe je met `screen.draw.filled_rect`. In dit geval bepaal
je eerst waar de rechthoek moet komen.

```python
rechthoek = Rect((x, y), (breedte, hoogte))
screen.draw.filled_rect(rechthoek, kleur)
```

Je kan dit ook op één lijn schrijven, maar let dan goed op dat je je haakjes
juist plaatst!

```python
screen.draw.filled_rect(Rect((x, y), (breedte, hoogte)), kleur)
```

Nu hebben we alle ingredienten om de middelijn te tekenen. We maken eerst een
variabele voor de breedte, zodat we deze makkelijk kunnen aanpassen.

```python
MIDDELIJN_BREEDTE = 6

def draw():
    # ... voeg dit toe aan je draw functie: ...
    middelijn = Rect((WIDTH/2 - MIDDELIJN_BREEDTE/2, 0), (LINE_WIDTH, HEIGHT))
    screen.draw.filled_rect(middelijn, Color("seagreen1"))
```

> ## Opdracht
> Begrijp je de formule `WIDTH/2 - MIDDELIJN_BREEDTE/2`? 

## Keyboard input

In de `update` functie kan je met `keyboard[keys....]` testen of een toets
momenteel ingedrukt is:

``` python
paddle1_x = 100
PADDLE_SNELHEID = 3

def update():
    if keyboard[keys.RIGHT]:
        paddle1_x += PADDLE_SNELHEID
```
