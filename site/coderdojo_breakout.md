---
title: Breakout spel in Python
---

In deze tutorial gaan we een klassiek Breakout spel maken met [Pygame Zero](https://pygame-zero.readthedocs.io/en/stable/). Werk stap voor stap, en test je code na iedere wijziging.

## Wat is Breakout?

Breakout is een klassiek computerspelletje waarbij je met een paddle een bal moet raken om bakstenen te breken. Het doel is om alle bakstenen kapot te maken 

## Stap 0: De basisstructuur en achtergrond

Zorg ervoor dat je de juiste assets hebt, je kan deze downloaden van itch.io: https://moxica.itch.io/block-breaker-arkanoid-breakout

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
- `draw()`: speciale functie die Pygame Zero automatisch aanroept om te tekenen

> **Test:** Run `python breakout.py`. Je ziet een donkerblauw venster van 640x480 pixels. Het is nog niet veel, maar het is een goede test dat python en Pygame Zero geinstalleerd zijn en functioneren. 

## Stap 1: De paddle toevoegen

Met deze stap voegen we de beweegbare paddle toe die later de bal zal raken.

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
- `paddle = Actor("player")`: laadt de afbeelding "player.png" uit de images folder
- `start_game()`: start functie die één keer wordt aangeroepen - uiteindelijk gaan we deze opnieuw aanroepen als je Game Over bent
- paddle wordt gecenterd in het midden, 30 pixels verwijdert van de onderkant

> **Opdrachten:**
> 1. Verander de positie: wat gebeurt er als je HEIGHT - 100 gebruikt?
> 2. Probeer andere startposities

## Stap 2: De bal toevoegen

Nu voegen we de stuiterende bal toe die voor het breken zorgt. We herhalen niet
de hele code deze keer, maar tonen alleen wat nieuw of gewijzigd is.

```python
ball = None # NIEUW

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

Deze combinatie van max + min is een veel voorkomend patroon, om te zorgen dat een waarde binnen bepaalde grenzen blijft.

> **Test:** Als je nu speelt, zie je de paddle bewegen met je muis!

## Stap 4: De bal laten bewegen

Nu gaan we de bal echt laten stuiteren met een update functie. Hier is wel wat werk aan. We moeten afzonderlijke tests doen voor de zijkanten, de onderkant (= Game Over), en de paddle.

```python
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
```

**Belangrijke nieuwe elementen:**
- `update()`: Pygame Zero roept deze 60 keer per seconde aan
- Bots detectie met `colliderect()`
- Logica voor muur botsingen en spel reset

## Stap 5: De bakstenen toevoegen

Tijd voor visuele verrijking met kleurtjes! We gaan een grid van bakstenen toevoegen.

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
            color_index = row % 8
            brick = Actor(f"brick{color_index+1}")
            brick.center = (x, y)
            bricks.append(brick)

def draw():
    # NIEUW
    for brick in bricks:
        brick.draw()
```

**De bakstenen grid:**
- 8 rijen × 16 kolommen = 128 bakstenen
- 40 pixels breedte tussen bakstenen, 18 pixels hoogte
- Kleurvariatie met `color_index = row % 8` en `f"brick{color_index+1}"`

## Stap 6: De bakstenen laten breken

Als laatste stap maken we de bakstenen kapot wanneer de bal ze raakt!

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

1. **Brick collision detection**: test elke brick of deze geraakt wordt door de bal
2. **Verwijder brick**: `bricks.remove(brick)` haalt de brick uit de lijst
3. **Ball reflection**: `ball_vy *= -1` laat de bal weer naar beneden stuiteren
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
            color_index = row % 8
            brick = Actor(f"brick{color_index+1}")
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
- groter paddle maken
- meerdere ballen geven
- superball die door meerdere bakstenen gaat

**Levels:** maak verschillende start posititons en layouts per level

## Samenvatting

Van een lege blauwe achtergrond naar een compleet spel in zes stappen:
0. Basisstructuur met venster
1. Paddle toevoegen
2. Bal toevoegen
3. Beweging toevoegen
4. Ball physics
5. Bakstenen grid
6. Win logica met brick breaking

Veel plezier met het uittesten en uitbreiden van je Breakout spel!
