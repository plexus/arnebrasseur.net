---
title: Open Source Diary C.X.5
date: 2025-03-14
---

You might have noticed from these updates that my open source work isn't very
focused. There's no single big project that I am committing most of my time to.
Instead I jump around between the dozens of lambdaisland projects that have been
released, and making contributions to third party libraries along the way as
well.

There are two reasons for that, which really point at the two things that
motivate me to do open source in the first place. The first is that I address
the needs of my dayjob. We constantly dogfood these projects, either on client
projects, or for Gaiwan internal projects, and when I run into an issue I will
go fix it, release it, and move on. Our tooling is optimized so it doesn't take
more than a few minutes to make a change and get a release out. We rarely make
public announcements about these things, which is part of the reason I decided
to start keeping these journals, so there's at least some visibility in the work
we're doing.

The second reason that I do open source is that I find it relaxing. Early in the
morning or during the weekend instead of bingeing youtube I like to zone out by
poking at an interesting problem. This kind of coding is deliberately unfocused
and exploratory, with many loose ends and abandoned pathways. It's often driven
by being unsatisfied with the available options. If there's an area of the
ecosystem that isn't well addressed, or where I find the existing solutions
lacking in elegance or design, then I will pick small pieces of the problem and
try to solve them in a clean and elegant way. You'll notice that many of the
libraries we've put out in recent years are quite narrow in focus. It can be
very liberating to pick a very specific well delineated problem, and try to
solve it in the best possible way you know how, giving you a building block that
you can leverage in the future to solve bigger problems.

There's sort of a third category of FOSS work, which has to do with creating a
delightful dev experience. Projects like launchpad and Kaocha fit into this
category.

Anyway, up to the updates.

## lambdaisland/cli - initial context vs flag defaults

The mental model of [lambdaisland/cli](https://github.com/lambdaisland/cli) is
that of gradually constructing a map. You start with an intial map you pass into
`lambdaisland.cli/dispatch`, we call this the initial context. Command line
flags can assoc values onto this map, or trigger functions that further
transform this map. Command handlers add further key-values for parsed
arguments, before passing this map into the command handler function itself.

Flags can also be given a default, for example

```clj
{:flags ["--env=<env>" {:doc "Environment to execute against"
                        :default "dev"
                        :parse keyword}]}
```

In this example, if you don't specify an `--env` flag on the command line, then
the context map will get `:env :dev` assoc-ed onto it. But what if the initial
context already contains an `:env` key? If we blindly assoc the default value,
then this initial value will always get overwritten. So now we check if there's
already a value present, before applying the default. The check uses `some?`, so
both an absent value, or a `nil` value will be replaced with the default.

## Launchpad - back aliases from CLI

I recently switched [launchpad](https://github.com/lambdaisland/launchpad) over
from its own ad-hoc CLI arg handling, to using `lambdaisland/cli`. It seems
somewhere in that migration we lost the ability to provide Clojure CLI aliases
on the command line.

For example:

```
bin/launchpad dev test --go
```

Will lauch Clojure with `-A:dev:test`. You could still get these aliases by
providing them in `deps.local.edn` instead.

```clj
{:launchpad/aliases [:dev :test]}
```

But not being able to specify them on the command line directly was a major and
unfortunate regression which has since been fixed.

## Launchpad -- better handling of boolean options

Launchpad has a number of boolean flags that determine how it behaves, for instance 

- `--go` - invoke `(user/go)` on startup
- `--cider-nrepl` - inject the CIDER nREPL middleware
- `--cider-connect` - instruct Emacs/CIDER to connect to nREPL once it's booted
- `--emacs` - same as `--cider-nrepl --nrepl-refactor --cider-connect`
- `--portal` - inject Portal as a dependency, and inject a `(user/portal)` function for starting it

These are really user preferences. It's likely you want launchpad to run with
the same set of options in each project. That's why we also let you set these in
`~/.clojure/deps.edn`. For instance, mine looks something like this.

```clj
{:launchpad/options {:emacs true :go true :portal true}}
```

But what if on some occasions you want to turn these back off. For instance,
while playing around with some nREPL middleware I didn't want launchpad to
automatically hook up Emacs, since I was using
[lambdaisland/nrepl-proxy](https://github.com/lambdaisland/nrepl-proxy) to
inspect the nREPL traffic, and I wanted to manually connect CIDER to that. In
that case you should be able to say `bin/launchpad --no-cider-connect`, and have
it temporarily turn off that specific option. This is how it was supposed to
work the whole time, but for reasons it wasn't working. That's been fixed.

## Thonny

You probably didn't expect to see any Python stuff in here, but here we are. For
over a year I've been volunteering at my local CoderDojo group, helping kids
once a month to get creative, either with scratch or Python. We usually set them
up with Thonny, a minimal Python IDE that works well for this use case.

Many of the projects we do use Pygame or Pygame Zero. The latter is a bit more
frameworky, eliminating as much boilerplate as possible by automatically
importing certain packages, and implicitly creating the main game loop. For that
to work, you don't start the program with the `python` executable, but with a
program called `pgzrun`.

Thonny has a special mode you can enable for Pygame Zero projects, so you can
easily run them inside Thonny as well, but seems this had been broken for newer
versions of Python for some time (ah that famous Python stability...).

There was an open issue, that [documented exactly the one-line
fix](https://github.com/thonny/thonny/issues/3158) that had to happen, but
almost a year later no one had actually bothered to fix it. I can't blame the
Thonny author. With 686 open issues at the time of writing there's clearly too
much for one person to keep up with. So I made [a small
PR](https://github.com/thonny/thonny/pull/3552), simply applying the fix as
documented.

As the maintainer of dozens of projects I know exactly how it is. Even
meticulously documented issues just go onto the pile. Maybe I'll get to them,
maybe not. It's even less likely if the issue doesn't really affect me. But give
me a reasonable PR so I can simply hit the merge button, and I'll probably be
quick to accept it.

And so it went here. Less than a day later my change was merged, and will be
part of the next Thonny release.

While on the topic of CoderDojo, I've also been working on a [Pygame Zero
tutorial for a ping-pong
game](https://arnebrasseur.net/coderdojo_pingpong.html). It's in Dutch, since
the audience is the Flemish teens who come to our CoderDojo workshops. It's
still a work in progress, by the next event I want to add a bunch of graphics
and illustrations to make it a bit nicer and more clear, but I'm happy with how
it's coming together. It'll be good to have a clear project that newcomers can
immediately start on.

## Ornament-next

I'm a big fan of the web platform and its ever improving open standards, and am
always keeping an eye on how browsers are evolving, and how it lets us create
better applications.

One of the things that have evolved tremendously over the years is CSS, and so
I've been looking at ways to incorporate some of that into Ornament, our
CSS-in-CLJ(s) component system. You can find this work in the
[ornament-next](https://github.com/lambdaisland/ornament/tree/ornament-next). It
hasn't been merged or released, but we've used it on several projects. This work
really started while working on
[Compass](https://github.com/GaiwanTeam/compass), the app we made for [Heart of
Clojure](https://heartofclojure.eu) attendees.

Ever since working with Felipe Barros on the [Lambda Island
redesign](https://lambdaisland.com/blog/2021-07-23-launching-lambda-island-redesign)
I've been interested in the concept of design tokens. Since then a JSON format
for design tokens has become standardized, and CSS variables have become more
widespread, paving the way for projects like [Open
Props](https://open-props.style/), which is a kind of design system, packaged as
a set of design tokens.

Ornament-next adds a few extra functions and macros to Ornament. `defprop`
defines a CSS property (what colloquially is called a CSS variable). `defrules`
lets you define CSS rules that are not tied to a specific component, but that
still benefit from Ornament's Garden/CSS pre-processing, in particular the use
of [Girouette](https://github.com/green-coder/girouette) to provide
Tailwind-compatible CSS shorthands.

Finally `import-tokens!` lets you import a JSON design tokens file directly,
generating vars for all the properties within it.

Here's a snippet from the compass code base.

```clj
(ns co.gaiwan.compass.css.tokens
  "Design tokens, partly imported from Open Props, partly defined here"
  {:ornament/prefix ""}
  (:require
   [charred.api :as charred]
   [clojure.java.io :as io]
   [garden.stylesheet :as gs]
   [lambdaisland.ornament :as o]))

(o/import-tokens! (charred/read-json (io/resource "open-props.tokens.json")) {:include-values? false})

(o/defprop --hoc-pink "#e25f7d")
(o/defprop --hoc-pink-1 "#e7879d")
(o/defprop --hoc-pink-2 "#cd4e6a")
(o/defprop --hoc-pink-3 --hoc-pink)
(o/defprop --hoc-pink-4 "#c0415b")

(o/defprop --talk-color)
(o/defprop --workshop-color)

(o/defprop --highlight)

(o/defrules session-colors
  [":where(html)"
   {--talk-color         --blue-2
    --workshop-color     --teal-2
    --highlight          --hoc-pink-1}]

  (gs/at-media
   {:prefers-color-scheme 'dark}
   [":where(html)"
    {--talk-color         --blue-9
     --workshop-color     --teal-8
     --highlight          --hoc-pink-4}]))
```

As you can see you can use these variable names like `--highlight` or `--blue-9`
directly in your Garden/Ornament CSS, and it'll be smart enough to know if it
should get rendered as `var(--highlight)`, or just as `--highlight`.

Another small but nice thing is that all Ornament created vars now get
docstrings. You can add docstrings to your components, and the generated
docstring will include the compiled CSS, so you can quickly inspect the result.

