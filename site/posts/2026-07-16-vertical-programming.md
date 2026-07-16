---
title: Vertical Programming
date: 2026-07-16
---

Here's a neat little macro

```clojure
(defmacro nest
  "Nesting macro, puts every form as the last of the previous one.

  This has the effect of flattening if, when, let, etc. by putting the else 
  (for if) or the last (and often single) expression of the body (for when, 
  let, loop etc.) out and after the form.

  This keeps the main code path vertical, while special cases branch out 
  of the main path."
  [& forms]
  `(->> ~@(reverse forms)))
```

This one comes straight out of the bright mind of [Christophe
Grand](https://tensegritics.com/). I have worked with Christophe on a few
projects now, and without fail he manages to sneak this into the code base. I
have come to appreciate the simple utility of this humble little macro.

While documenting our latest project, I realised that this macro does not yet
have a forever home on the web, a place that people can easily point their
friends (or foes) and coworkers at, that explains the what and why in clear
terms. So here it is, I hope it makes itself at home.

To be fair ([♫](https://www.youtube.com/watch?v=G19B7lTgwCE)), Christophe did write about it
in [Extensible Macros](https://buttondown.com/tensegritics-curiosities/archive/extensible-macros/),
but the exposition is somewhat tangential to the main point of the post. There was also an OG post on the
platform formerly known as Twitter, but I'm not going to wade into that cesspool to dredge it up.

Before we dive in, let's talk about the name. The earliest known version of this
macro was called `<<-`, which captures nicely its implementation (it's a
reversed threading macro), but does not elucidate its use. Since then it's been
called a number of things, including `else->>` (worse) and `warp->>` (meh at
best). My proposed colour for this particular bike shed would actually be `<|`.
I like how the pipe character lines up with the opening parens of the forms
beneath it, emphasising that vertical programming flair. But to keep it vanilla
and inoffensive for the rest of this post I'll use `nest`. Cause that's what it
does.

Ok so it nests, what does that mean? Let's start with a simple example. Used
with `if` it practically becomes a `cond`.

```clj
(def x (rand-int 3)) 

(nest
  (if (= 0 x) :zero)
  (if (= 1 x) :one)
  (if (= 2 x) :two)
  :other)
```

Which expands to:

```clj
(if (= 0 x)
  :zero
  (if (= 1 x)
    :one
    (if (= 2 x)
      :two
      :other)))
```

Now let's throw in a `let`:

```clj
(def x (rand-nth (range -3 4)))

(nest
  (if (<= -1 x 1) :close-to-zero)
  (let [a (abs x)])                 ; binding available hereafter
  (if (= 2 a) :pos-or-neg-two)
  (if (= 3 a) :pos-or-neg-three))
```

Who hasn't felt the need for a `:let` inside a `cond`? This is like a `cond`,
but better! We are now in
[better-cond](https://github.com/Engelberg/better-cond) territory, but in 3
lines instead of 171.

`better-cond` also offer `:when-let`, `:when-some`, etc., while we can just use
Clojure's built-in `when`, `when-let`, `if-let`, and more.

In this example the code immediately short-circuits and returns `nil` if the
wrong type of value is supplied.

```clj
(nest
  (when (integer? x))
  (if (<= -1 x 1) :close-to-zero)
  (let [a (abs x)])
  (if (= 2 a) :pos-or-neg-two)
  (if (= 3 a) :pos-or-neg-three))
```

While `if`/`when`/`let` are the most immediate candidates for nesting, it also
can make sense for instance for `loop`

```clj
(nest
  (loop [i 0]
    (println i))
  (if (< 5 i) (println "DONE"))
  (if (= 2 i) (recur 4))
  (recur (inc i)))
```

Here we've separated the code inside the loop block that runs for side-effects,
from the form in tail position that handles the recursion. And that form in turn
consists of a few special case checks, and the default recursion clause.

Finally, for something entirely different, let's look at generating some HTML
with a hypothetical HTML generation library (note: the first version of this
blog post used Hiccup, i.e. vectors, but that doesn't work with `nest` or `->>`,
since they are treated as if wrapped with an implicit `()`. You'd need a custom
nesting macro if you want it to work with Hiccup.)

```clj
(nest
  (h/html {:lang "en"}
   (h/head (h/title "My cool page")))
  (h/body
   (h/header "header stuff"))
  (h/main)
  (h/article
   (h/header "article header")
   (h/section "...")
   (h/section "...")))
```

This pattern also applies to a lot of UI toolkits, where various frames and
utility components wrap the actual content.

As with Clojure's threading macros, one should take some care to use the nesting
macro where appropriate. You use thread first (`->`) when passing a value
through a bunch of functions, and thread last (`->>`) when transforming
sequences or collections in successive steps, not to flatten arbitrary syntax.
While to the compiler it's all the same, the person reading your code has
certain expectations when seeing `->` or `->>`. 

```clj
;; Don't do this
(->> arg
     (println "Got arg!")
     (defn my-fn [arg]))
```

Similary, `nest` (or `<|` if you will) comes with certain expectations. Don't be
too clever!

What do you think, would you use it? Or maybe you do already. What name do you
prefer? Come yell at me [on the fediverse](https://toot.cat/@plexus) or [the
atmosphere](https://bsky.app/profile/plexus.eurosky.social).

_Update:_ Ben Lieberman pointed out that there is a Scheme "Request for Implementation", [SRFI 197: Pipeline Operators](https://srfi.schemers.org/srfi-197/srfi-197.html), which among other proposes a `nest` macro. Very similar in intent, but with an explicit placeholder to indicate where the nested form gets inserted in the previous form. Seems this is available either built-in or as a package in several (most?) Scheme implementations.

To match Clojure's existing macro conventions, perhaps this could be adopted as `nest-as`.
