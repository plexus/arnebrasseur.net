---
title: Open Source Diary C.V.4
date: 2025-02-06
---

As reported [on fedi](https://toot.cat/@plexus/113934085882016240) I've started
on an Integrant-alike, which we're calling Makina. I've worked with a bunch of
reloadable component/system libraries, including [Alessandra Sierra's original
`component`](https://github.com/stuartsierra/component),
[mount](https://github.com/tolitius/mount) (not my choice, but already decided
by a client), [kepler16/gx.cljc](https://github.com/kepler16/gx.cljc) developed
by a former client of ours. Another notable mention in this space is
[juxt/clip](https://github.com/juxt/clip).

We've written about this category of libraries on our Pattern Wiki, Tea Garden:
[Tea Garden: Reloadable Component System](https://gaiwan.co/wiki/ReloadableComponentSystem.md).

For many years now our go-to library in this category has been James Reeves
(weavejester)'s excellent [Integrant](https://github.com/weavejester/integrant)
and the accompanying
[integrant-repl](https://github.com/weavejester/integrant-repl), and I think
it's fair to say that Integrant currently is the de facto standard library for
this use case in the broader Clojure community. I say all that because I want to
make it clear that I am grateful for Integrant and James's work on it. Makina
borrows extensively from Integrant, and couldn't exist without the learnings and
observations that we got from years of using it effectively.

So why write Makina at all? There are a few reasons:

- Integrant makes heavy use of multimethods, meaning there is a single global
  registry for handlers. This is quite limiting, in particular it makes it hard
  to say "I want to stub out this component during testing, here are some
  functions to use for handlers instead."
- Integrant-repl provides a decent policy level over the basic mechanisms
  provided by Integrant, but it still requires a good amount of boilerplate, and
  only really handles the dev-time system use case (although it's also commonly
  used for prod). I wanted something that provides a more complete package,
  while retaining a good mechanism (purely functional base API) and policy
  (integrated application harness) separation (read about [policy vs mechanism
  here](https://lambdaisland.com/blog/2022-03-10-mechanism-vs-policy).
- Better support for partially starting/stopping systems, especially allowing to
  manually recover after an error in a component handler leads to a partially
  started system.
- Finally there's a bunch of integrant functionality that we simply don't use,
  and that in my opinion is rarely used in general, and tends to confuse people
  if you do use it in a project. I'm mainly thinking of composite keys here. Not
  really a problem that that's there, but I'm not going to try to mimic that, so
  that simplifies my design a bit.
  
More news on Makina later, as it's still very alpha as I sort out what the ideal
API should look like, but in the meanwhile this has led to some productive yak
shaving.

[lambdaisland/deep-diff2](https://github.com/lambdaisland/deep-diff2) is now
smarter about diffing records. Before records were just treated like maps, and
if you compared a record to a map with the same keys and values, it would
consider them equal. This is now different, we only diff records when comparing
them against records of the same type. If you compare them to anything else, the
entire value will be marked as a mismatch.

When we do diff records, it will now preserve the record type in the output, so
if you have data printers set up for those types, you'll see what the type
originally was. This is breaking change, but hopefully people find it makes
sense. If anyone comes forward with a good reason to prefer the old behavior,
then we can add a way to opt in to that.

[lambdaisland/data-printers](https://github.com/lambdaisland/data-printers) has
seen its first releaes since 2021. data-printers makes it easy to register print
handlers for tagged literals, kind of the inverse of
[`data_readers.cljc`](https://clojure.org/reference/reader#tagged_literals). It
provides a uniform API for adding print handlers for Clojure's built in
print/pprint functions, as well as Puget, Transit, deep-diff, and deep-diff2. It
now has a `lambdaisland.data-printers.auto` namespace (clj-only), so you don't
need to worry about which of these are present on your projects. It will try to
load the ones it can find, and register handlers for them.

I went through and closed the remaining open issues for
[lambdaisland/cli](https://github.com/lambdaisland/cli/). Small stuff, but it's
nice to further polish that up and get a release out.

[lambdaisland/config](https://github.com/lambdaisland/config) has seen a new
release, to make use of `data-printers.auto`.

Some other small stuff that I did recently:

Pull out [lambdaisland/kramdown](https://github.com/lambdaisland/kramdown) out
of the original Lambda Island web app. This uses JRuby to expose the Ruby
markdown library Kramdown as a Clojure library. I really like some of the
markdown extensions that Kramdown has, especially the shorthands to add HTML
classes or other attributes to arbitrary markdown elements, and I'm using that
to render this blog. I also use it even more extensively when generating slides
from markdown. You decide if this is worth pulling all of JRuby in for, but I
can certainly recommend it.

[lambdaisland/hiccup](https://github.com/lambdaisland/hiccup) has seen a new
release. When rendering HTML attributes, it needs to figure out if dashes should
be preserved or not, for instance `:on-click` becomes `onclick`, but
`:accept-charset` stays `accept-charset`. For any standard HTML stuff it's smart
enough to do the right thing, but if you are using custom prefixes that's not
always the case. For example, HTMX uses prefixes like `hx-...` or `ws-...`.
These have been built-in to li/hiccup for a while as well, so HTMX "just works",
but if you need to add additional ones, you have some options. You can `swap!`
`lambdaisland.hiccup/kebab-prefixes`, this option was already there. Now you can
also specify them as a Java system property: `clojure -J-Dlambdaisland.hiccup.kebab-prefixes=foo-,bar- ...`

We switched [lambdaisland/launchpad](https://github.com/lambdaisland/launchpad)
from its own custom CLI handling to `lambdaisland/cli` back in December. This
broke some of the flags, like `--emacs`, this has been fixed last month.


