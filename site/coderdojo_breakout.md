---
title: Breakout spel in Python
---

In deze tutorial gaan we een klassiek Breakout spel maken met [Pygame Zero](https://pygame-zero.readthedocs.io/en/stable/). Werk stap voor stap, en test je code na iedere wijziging.

## Wat is Breakout?

Breakout is een klassiek computerspelletje waarbij je met een paddle een bal moet raken om bakstenen te breken. Het doel is om alle bakstenen kapot te maken 

## Stap 0: De basisstructuur en achtergrond

Zorg ervoor dat je de juiste assets hebt, je kan deze downloaden van itch.io: [https://moxica.itch.io/block-breaker-arkanoid-breakout](Breakout asset pack).

Plaats ze in een map `images`, en verander de hoofdletters in de bestandsnamen naar kleine letters. Alleen zo kan Pygame Zero er gebruik van maken. Hernoem `Brick1_4.png` naar `brick1.png`, enz.

- `images/player.png`
- `images/ball-small.png`
- `images/brick1.png` t/m `images/brick9.png`

Maak een bestandje `breakout.py`:

```python
import pgzrun

TITLE = "My Breakout Game"
WIDTH = 640
HEIGHT = 480

def draw():
    screen.fill((30, 30, 80))

pgzrun.go()
```

**Wat gebeurt er?**
- `TITLE = "My Breakout Game"`: titel van het venster
- `WIDTH, HEIGHT`: formaat van het speelveld
- `draw()`: speciale functie die Pygame Zero automatisch aanroept, alles wat we zichtbaar op het scherm willen zien moeten we in deze functie "tekenen"

> **Test:** Run `python breakout.py`. Je ziet een donkerblauw venster van 640x480 pixels. Het is nog niet veel, maar het is een goede test dat python en Pygame Zero geinstalleerd zijn en functioneren. 

## Stap 1: De paddle toevoegen

Met deze stap voegen we de beweegbare paddle toe die later de bal zal raken. We gebruiken hiervoor een pygame `Actor`. Dit is vergelijkbaar met een Sprite in Scratch.

Let op, deze code bouwt verder op de vorige. Met `# NIEUW` geven we aan welke code je in deze stap moet toevoegen.

**breakout1.py:**

```python
from pgzero.actor import Actor # NIEUW
import pgzrun

TITLE = "My Breakout Game"
WIDTH = 640
HEIGHT = 480

paddle = None # NIEUW

# NIEUW
def start_game():
    global paddle
    paddle = Actor("player")
    paddle.center = (WIDTH / 2, HEIGHT - 30)

def draw():
    screen.fill((30, 30, 80))
    paddle.draw() # NIEUW

start_game() # NIEUW
pgzrun.go()
```

**Nieuwe elementen:**
- `paddle = Actor("player")`: maak een nieuwe "Actor", `paddle`, en gebruik de afbeelding "player.png" uit de images folder
- `start_game()`: start functie die één keer wordt aangeroepen - uiteindelijk gaan we deze opnieuw aanroepen als je Game Over bent
- we plaatsen het middelpunt (center) van de paddle horizontaal in het midden, en verticaal 30 pixels verwijdert van de onderkant
- om de paddle zichtbaar te maken voegen we `paddle.draw()` toe aan onze `draw()` functie

> Een _lokale_ variabele bestaat enkel binnen een functie. Elke keer dat de functie wordt opgeroepen krijgt deze opnieuw een waarde, en wanneer de functie ten einde is verdwijnt deze weer. Als je een variabele binnen een functie een waarde geeft, (bv. `x = 1`, of `paddle = Actor("paddle")`, dan gaat Python er van uit dat dit een lokale variabele is. De functie is als het ware een krachtschild, de variabele komt daar niet buiten.
>
> Een _globale_ variabele is niet gebonden aan één functie, maar kan je van eender waar in je programma gebruiken, maar je moet dan wel in iedere functie waar je ze een waarde geeft (met `=`) aangeven dat je de globale variabele bedoelt. Dat doe je met `global`.
>
> In ons geval is `paddle` een globale variabele, en in de start functie geven we deze een waarde (`paddle = Actor("player"`), dus schrijven we `global paddle` aan het begin van de functie. Zo weet Python dat we de globale variable wil wijzigen. Doe je dat niet, dan gaat Python er van uit dat het een _lokale_ variabele is, en blijft de globale variabele ongewijzigd.

## Stap 2: De bal toevoegen

Nu voegen we de stuiterende bal toe die voor het breken zorgt. We herhalen niet
de hele code deze keer, maar tonen alleen wat nieuw of gewijzigd is. We voegen
code toe bovenaan, in de `start_game` functie, en in de `draw` functie.

```python
ball = None # NIEUW
ball_vx = 0 # NIEUW
ball_vy = 0 # NIEUW

def start_game():
    global paddle, ball, ball_vx, ball_vy # GEWIJZIGD
    # ...player...
    
    # NIEUW
    ball = Actor("ball-small")
    ball.center = (WIDTH / 2, HEIGHT / 2)
    ball_vx = 3
    ball_vy = -3

def draw():
    # ...
    ball.draw() # NIEUW
```

**Nieuwe variabelen:**
- `ball_vx`: horizontale snelheid (3 pixels per frame)
- `ball_vy`: verticale snelheid (-3 pixels per frame, naar boven)

## Stap 3: De paddle laten bewegen met de muis

De paddles moeten beweeglijk zijn! We voegen muisbesturing toe.

```python
def on_mouse_move(pos):
    paddle.x = max(20, min(WIDTH - 20, pos[0]))
```

**Nieuwe functie:**
- `on_mouse_move(pos)`: Pygame Zero roept deze automatisch aan wanneer je muis beweegt
- `max(20, min(...))`: beperkt de paddle beweging binnen het scherm (met 20 pixels marge)

Deze combinatie van max + min is een veel voorkomend patroon, om te zorgen dat een waarde binnen bepaalde grenzen blijft. Bijvoorbeeld, 

> **Check voor jezelf:**
> Neem deze code: `max(0, min(x, 10))`.
> Wat is het resultaat, bij `x=5`, `x=-2`, `x=2`, `x=12`?

> **Test:** Als je nu speelt, zie je de paddle bewegen met je muis!

## Stap 4: De bal laten bewegen

Nu gaan we de bal echt laten stuiteren met een update functie. Hier is wel wat werk aan. We moeten afzonderlijke tests doen voor de zijkanten, de onderkant (= Game Over), en de paddle.

`update()` is net als `draw()` een speciale functie die door Pygame Zero wordt opgeroepen, wel 60 keer per seconde. Dit is de uitgelezen plaats om dingen te laten bewegen.

```python
def update():
    # Geef aan dat we de globale variabelen ball_vx en ball_vy gaan wijzigen
    global ball_vx, ball_vy
    
    # Ball bewegen op basis van de snelheid
    ball.x += ball_vx
    ball.y += ball_vy
    
    # Botsen met de zijkanten
    if ball.left <= 0 or ball.right >= WIDTH:
        ball_vx *= -1

    # Botsen met de bovenkant
    if ball.top <= 0:
        ball_vy *= -1
        
    # Ball raakt de onderkant: GAME OVER
    if ball.bottom >= HEIGHT:
        start_game()
        return
        
    # Botsen met de paddle
    if paddle.colliderect(ball):
        ball_vy *= -1
```

Je ziet op verschillende plaatsen `ball_vx *= -1`, of `ball_vy *= -1`. Dit is hetzelfde als `ball_vy = ball_vy * -1`. Met andere woorden, de nieuwe verticale (of horizontale) snelheid van de bal is de oorspronkele snelheid, maal `-1`. Vermenigvuldigen met `-1` verandert het teken. Was de waarde positief, dan is ze nu negatief. Was ze negatief, dan is ze nu positief.

Op deze manier doen we de bal "botsen". Gaat de bal naar links en raakt de kant, dan gaat deze nu naar rechts. De verticale snelheid (naar boven of beneden) blijft ongewijzigd.

Om te testen of de bal de paddle raakt gebruiken we `paddle.colliderect(ball)`. `colliderect` is deel van de `Actor` klasse die we gebruikt hebben voor de paddle. "`collide`" wil zegen "in aanraking komen met", "`rect`" staat hier voor rechthoek. We testen dus of the bal de rechthoek van de paddle raakt.

## Stap 5: De bakstenen toevoegen

Tijd voor visuele verrijking met kleurtjes! We gaan een rooster van bakstenen toevoegen.

```python
bricks = []  # NIEUW

def start_game():
    # ...
    
    # NIEUW
    bricks.clear()
    for row in range(8):
        for col in range(16):
            x = 20 + col * 40
            y = 40 + row * 18
            color_index = 1 + (row % 8)
            brick = Actor(f"brick{color_index}")
            brick.center = (x, y)
            bricks.append(brick)

def draw():
    # NIEUW
    for brick in bricks:
        brick.draw()
```

We houden de bakstenen bij in een lijst, `bricks`. Aan het begin van een nieuw spel maken we de lijst eerst leeg met `clear`, en dan voegen we al de bakstenen toe. In de `draw` functie overlopen we dan de lijst om alle stenen te tekenen.

We gebruiken opnieuw Actors voor de bakstenen. We hebben 8 verschillende afbeeldingen, `brick1.png`, ..., `brick8.png`. We hebben echter meer dan 8 rijen. Met de _modulo_ operator `% 8` (rest bij deling door 8) zorgen we dat we een waarde tussen `0` en `7` krijgen. Voegen we er nog 1 aan toe, dan wordt dat tussen `1` en `8`. Met een Python "format string" (`f"...{...}..."`) maken we de juiste bestandsnaam.

```
0 % 8 #=> 0
1 % 8 #=> 1
...
7 % 8 #=> 7
8 % 8 #=> 0
9 % 8 #=> 1
10 % 8 #=> 2

f"abc{1+1}" #=> "abc2"
```

**De bakstenen:**
- 8 rijen × 16 kolommen = 128 bakstenen
- 40 pixels breedte tussen bakstenen, 18 pixels hoogte
- Kleurvariatie met `color_index = 1 + (row % 8)` en `f"brick{color_index}"`

> **Een leuke variant:**
> Je kan zoal rij als kolomnummer gebruiken om de kleur van de baksteen te bepalen, dat ziet er visueel nog iets interessanter uit: `color_index = 1 + ((row+col) % 8)

## Stap 6: De bakstenen laten breken

Als laatste stap maken we de bakstenen kapot wanneer de bal ze raakt! We overlopen de lijst met stenen, en testen of de bal de steen raakt. Als dat zo is, dan gebruiken we de `remove` functie om de baksteen uit de lijst te verwijderen. Dan zal ze niet meer worden getekend.

Een lege lijst is "falsey" in Python, we kunnen dus testen met `not bricks` of de lijst leeg is.

```python
def update():
    # NIEUW
    for brick in bricks:
        if brick.colliderect(ball):
            bricks.remove(brick)
            ball_vy *= -1
            break
            
    # NIEUW
    if not bricks:
        start_game()
```

**Nieuwe functionaliteit:**

1. **Brick collision detection**: test elke baksteen of deze geraakt wordt door de bal
2. **Verwijder brick**: `bricks.remove(brick)` haalt de brick uit de lijst
3. **Bal botst**: `ball_vy *= -1` laat de bal weer naar beneden stuiteren
4. **Game win**: `if not bricks:` controleert of alle bakstenen kapot zijn

## Het volledige spel:

```python
from pgzero.actor import Actor
import pgzrun

TITLE = "My Breakout Game"
WIDTH = 640
HEIGHT = 480

paddle = None
ball = None
ball_vx = 0
ball_vy = 0
bricks = []

def start_game():
    global paddle, ball, ball_vx, ball_vy
    paddle = Actor("player")
    paddle.center = (WIDTH / 2, HEIGHT - 30)
    ball = Actor("ball-small")
    ball.center = (WIDTH / 2, HEIGHT / 2)
    ball_vx = 3
    ball_vy = -3
    bricks.clear()
    for row in range(8):
        for col in range(16):
            x = 20 + col * 40
            y = 40 + row * 18
            color_index = 1 + (row % 8)
            brick = Actor(f"brick{color_index}")
            brick.center = (x, y)
            bricks.append(brick)

def draw():
    screen.fill((30, 30, 80))
    paddle.draw()
    ball.draw()
    for brick in bricks:
        brick.draw()

def on_mouse_move(pos):
    paddle.x = max(20, min(WIDTH - 20, pos[0]))

def update():
    global ball_vx, ball_vy

    # Move ball
    ball.x += ball_vx
    ball.y += ball_vy

    # Wall collisions
    if ball.left <= 0 or ball.right >= WIDTH:
        ball_vx *= -1
    if ball.top <= 0:
        ball_vy *= -1

    # Game over if ball hits bottom
    if ball.bottom >= HEIGHT:
        start_game()
        return

    # Paddle collision
    if paddle.colliderect(ball):
        ball_vy *= -1
        # Add angle based on where ball hits paddle
        offset = (ball.x - paddle.x) / 50
        ball_vx += offset

    # Brick collisions
    for brick in bricks:
        if brick.colliderect(ball):
            bricks.remove(brick)
            ball_vy *= -1
            break

    # Check win
    if not bricks:
        start_game()

start_game()
pgzrun.go()
```

## Uitbreidingen voor gevorderden

Ons Breakout spel is nu compleet! Enkele uitbreidingsideeën:

**Punten systeem:**
```python
score = 0  # toevoegen bovenaan

# in draw functie toevoegen:
screen.draw.text(f"Score: {score}", topleft=(10, 10))
```

**Levens systeem:**
```python
lives = 3  # toevoegen bovenaan
```

**Power-ups:** zorg voor speciale bakstenen die 
- de paddle groter maken
- meerdere ballen geven
- superball die door meerdere bakstenen gaat

**Levels:** maak verschillende start posities en layouts per level

