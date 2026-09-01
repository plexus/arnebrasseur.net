---
title: "Advent 2019 part 10, Hillcharts with Firebase and Shadow-cljs"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2019-12-10T11:43:20.000Z"
updated_at: "2026-05-16T13:41:56.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-15 08:10"]
---

_This post is part of[Advent of Parens 2019](https://lambdaisland.com/blog/2019-11-25-advent-of-parens), my attempt to publish one blog post a day during the 24 days of the advent._

Recently I led a workshop for a client to help them improve their development process, and we talked a lot about [Shape Up](https://basecamp.com/shapeup), a book released by Basecamp earlier this year that talks about _their_ process. You can read it for free on-line, and I can very much recommend doing so. It’s not a long read and there are a ton of good ideas in there.

One of these ideas has also become a feature in Basecamp, namely hill charts. These provide a great way to communicate what stage a piece of work is in. Are you still going uphill, figuring things out and discovering new work, or are you going downhill, where it’s mostly clear what things will look like, and you’re just executing what you discovered?

[![Hillchart](https://hillchart-91fcf.firebaseapp.com/a3e77804-4aee-44c2-808c-70ff37d803a6.svg)](https://hillchart-91fcf.firebaseapp.com/#a3e77804-4aee-44c2-808c-70ff37d803a6)

The team really liked this idea, but they’re not planning to switch to Basecamp, since they’re quite heavily invested in another tool. So during breaks and on the way home I wrote up a little app that does just that. Click on the chart above and you can see it in action. Simply remove the uuid from the URL to get a new chart.

The UX could still be vastly improved, but with some clicking around hopefully it’s easy enough to figure out how it works. Click on the big plus to add a dot, click on a dot to start moving it around, click again to put it down. The “M” button on the right gives you a markdown snippet that you can paste for instance in Github or Gitlab, and the button below that lets you edit the captions. The code can be found on Github: [plexus/hillchart](https://github.com/plexus/hillchart).

I used Firebase for this project which was a first for me, and I have to say the experience was pretty great. Firebase basically does three things: it serves the app (HTML/CSS/JS), it acts as a database, and using a Firebase Cloud Function (akin to an AWS Lambda), it can render a chart to SVG, which is then used by the Markdown snippet to embed the chart in other platforms.

It’s really neatly self-contained that way, and by using the Firestore storage you get synchronization basically for free. Try opening the same chart in two tabs and edit it and you’ll see what I mean.

Matt’s article on the Applied Science blog [ClojureScript on Firebase Cloud Functions](http://www.appliedscience.studio/articles/cljs-firebase-functions.html)was very helpful to set things up. I initially started the project with Figwheel, but switched over to Shadow-cljs as it seemed to make using the Firebase npm package a bit easier. That was another first for me and I have to say it was a very positive experience.

Go ahead and browse around the code. It’s really just a Reagent app. There are several bits that would be interesting to write about, I’ll just show how the Firestore integration works. (slightly redacted)
    
    
    (ns hillchart.main
      (:require [reagent.core :as r]))
    
    ;; Reagent app state
    (defonce state (r/atom {:dots []}))
    
    ;; Each chart is identified by a random UUID, if there is no UUID yet in the URL
    ;; then we'll generate one, i.e. create a new chart
    (when (= "" js/document.location.hash)
      (set! js/document.location.hash (random-uuid)))
    
    ;; Grab the current ID, this will stay fixed
    (def chart-id (subs js/document.location.hash 1))
    
    ;; Initialize firestore, grab the charts collection, and grab the document with
    ;; the current chart-id.
    (def ^js db (js/firebase.firestore))
    (def ^js fs-charts (.collection db "charts"))
    (def ^js fs-doc (.doc fs-charts chart-id))
    
    ;; Helper that merges the state coming from the db into the reagent state
    (defn fs->state! [^js doc]
      (swap! state merge (js->clj (.data doc) :keywordize-keys true)))
    
    ;; Grab the data the first time, the `defonce` is just so this doesn't happen
    ;; again when hot reloading.
    (defonce fetch-doc
      (.then (.get fs-doc) fs->state!))
    
    ;; Listen for changes
    (defonce setup-listener
      (.onSnapshot fs-doc fs->state!))
    
    ;; Whenever a change happens I'll call this to push changes to the db. I used a
    ;; watch on the atom first, but since there are things in there like the current
    ;; viewport size that don't need to be stored I figured this worked better.
    (defn save-doc! []
      (.set fs-doc (clj->js (select-keys @state [:dots]))))
    

And this is the code that powers the cloud function, so an SVG version can be rendered on the server-side. Data access is a little different here, but it comes down to the same thing.
    
    
    (ns hillchart.firebase
      (:require ["firebase-functions" :as functions]
                ["firebase-admin" :as admin]
                [hillchart.main :as main]
                [reagent.dom.server]
                [clojure.string :as str]))
    
    (.initializeApp admin)
    
    (defn render-chart-svg [^js req, ^js res]
      (let [doc-id (str/replace (str/replace (.-path req) ".svg" "") "/" "")
            ^js db (.firestore admin)
            ^js fs-charts (.collection db "charts")
            ^js fs-doc (.doc fs-charts doc-id)]
        (.then (.get fs-doc)
               (fn [doc]
                 (.type res ".svg")
                 (.send res
                        (reagent.dom.server/render-to-static-markup
                         [main/Chart (assoc (js->clj (.data doc) :keywordize-keys true)
                                       :screen/width 500
                                       :screen/height 300
                                       :static? true)])))))) ;;<- hide the buttons
    
    (def cloud-functions
      #js {:renderChartSVG (.onRequest functions/https render-chart-svg)})
    

_Hi, my name is Arne and I consult companies and teams about application architecture, development process, tooling and testing. I collaborate with other talented people under the banner[Gaiwan](__GHOST_URL__/). If you like to have a chat about any of these things just [get in touch!](__GHOST_URL__/p/contact)_

**[Comment on ClojureVerse](https://clojureverse.org/t/advent-of-parens-2019/5159)**
