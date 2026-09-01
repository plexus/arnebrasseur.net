---
title: "Advent 2019 part 23, Full size SVG with Reagent"
slug: "advent-of-parens-23-full-size-svg-with-reagent"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2019-12-23T21:10:09.000Z"
updated_at: "2026-05-16T13:41:49.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-18 14:51"]
---

_This post is part of[Advent of Parens 2019](https://lambdaisland.com/blog/2019-11-25-advent-of-parens), my attempt to publish one blog post a day during the 24 days of the advent._

I’ve been a big fan of SVG since the early 2000’s. To me it’s one of the great victories of web standards. Mind you, browser support has taken a long time to catch up. Back then you had to embed your SVG file with an `<object>` tag, and support for the many cool features and modules was limited and inconsistent.

These days of course you can drop an `<svg>` tag straight into your HTML. What a joy! And since the SVG can now go straight into the DOM, you can draw your SVG with React/Reagent. Now there’s a killer combo.

I already mentioned [Hillcharts](https://lambdaisland.com/blog/2019-12-10-advent-of-parens-10-hillchart-reagent-shadow-firebase)earlier in this series. Another SVG project that I’ve been tinkering on is a game prototype called Hexagons (working title). It came about when I was on vacation the weeks after Heart of Clojure. I had taken some time off to decompress, eat rösti, and spend some time in nature. One evening my idle mind started wandering and came up with an idea for a tabletop game. I built a first prototype of it the next day from cardboard, playing cards, post-its and pebbles.

The prototype left to be desired, and I figured that if I wanted to experiment more with the game mechanics then a browser-based version would actually be pretty convenient. This would also allow me to try it out with some of my old buddies back home whom I don’t get to see often, thanks to the magic of the internet.

The whole game is just a single big SVG, so my idea was to have this one `<svg>`element simply span the full browser viewport. This wasn’t as easy as I thought it would be. CSS was of little help, it was easy enough to have it span the full width, but I couldn’t figure out a good way to have it span the full height.

So I came up with this.
    
    
    (defn viewport-size []
      {:width js/document.documentElement.clientWidth
       :height js/document.documentElement.clientHeight})
    

This app is just a simple Reagent app, no re-frame or anything like that. Since it’s pretty limited in scope I figured it would be fun to go back to basics, and just use ratoms and reactions directly.

In typical reagent/re-frame style a single ratom contains the application state, including the size of the browser viewport.
    
    
    (defonce state (reagent/atom (viewport-size)))
    

And this is what the root of the render tree looks like. I’m using the SVG `viewBox` property to shift the coordinate system so that `[0, 0]` is right in the middle of the browser.

The game pieces are hexagonal tiles. You start with one tile, and then players add more tiles one by one, so the game kind of grows out of the center where that first tile is. That’s why I decided to lay the coordinates like this.

Other UI elements are drawn relative to the viewport edge. This turned out to work well with the zoom functionality built into browsers. When you zoom out all tiles and other elements get smaller, but they keep their position either relative to the center of the viewport, or relative to the edge of the viewport.
    
    
    (defn app-root []
      (let [{:keys [width height]} @state
            top-left-x (/ width -2)
            top-left-y (/ height -2)]
        [:svg {:width width
               :height height
               :viewBox (str/join " " [top-left-x top-left-y width height])}
         ;; UI goes in here
         ]))
    

You still need to handle browser resizing, that’s what this final snippet is for.
    
    
    (set! js/document.body.onresize (fn [_] (swap! state merge (viewport-size))))
    

Playing around with SVG, Reagent’s hiccup, and Figwheel or shadow-cljs for reloading is a lot of fun. I can highly recommend it. If you’re not sure how to start then just draw some things in Inkscape, convert the result to Hiccup, and paste it into your code.

_Hi, my name is Arne (aka @plexus) and I consult companies and teams about application architecture, development process, tooling and testing. I collaborate with other talented people under the banner[Gaiwan](__GHOST_URL__/). If you like to have a chat about how we could help you with your project then please [get in touch!](__GHOST_URL__/p/contact)_

**[Comment on ClojureVerse](https://clojureverse.org/t/advent-of-parens-2019/5159)**
