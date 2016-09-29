Last weekend I was at ClojuTRE, where David Nolen gave a great talk titled "The
Next 5 Years of ClojureScript". The title is somewhat misleading, since it was
really a historical overview of the milestones achieved in the last 5 years of
ClojureScript. One of those milestones was JS module support, the ability to use
any third party CommonJS, AMD, or ES6 module directly from ClojureScript.

This work was done by Maria Geller as part of Google Summer of Code 2015. My
impression so far had been that this wasn't ready for prime time yet, but David's
talk encouraged me to play around with it to see where things stand.

To understand why this matters you need to understand the role of the Google
Closure Compiler ("GCC" henceforth).

GCC is a powerful tool, developed and used extensively by Google, that analyzes,
optimizes, and compacts JavaScript code. To be able to do these optimizations,
GCC demands that you organize your code in modules following certain
conventions. This is what a typical JS file might look like

``` javascript
goog.provide('myproject.start');

goog.require('goog.dom');

myproject.start = function() {
  var newDiv = goog.dom.createDom('h1', {'style': 'background-color:#EEE'},
    'Hello world!');
  goog.dom.appendChild(document.body, newDiv);
};

// Ensures the symbol will be visible after compiler renaming.
goog.exportSymbol('myproject.start', myproject.start);
```

With `goog.provide` you declare which identifiers in this script can be required
by required and used by other scripts, with `goog.require` you declare which
identifiers declared elsewhere you need. This information all goes into a
dependency graph, to figure out what needs to be loaded first, what is used, and
what isn't. That last part is important, anything that isn't actually used can
be removed (dead-code eliminated), reducing the final file size.

ClojureScript builds upon GCC directly by compiling ClojureScript namespaces to
GCC modules, for example,

``` clojure
(ns food.core
  (:require [om.core :as om]))

(enable-console-print!)
```

turns into this

``` javascript
goog.provide('food.core');
goog.require('cljs.core');
goog.require('om.core');

cljs.core.enable_console_print_BANG_();
```

From there GCC takes over, and depending on the compilation settings does all
kinds of clever code rewriting.

The thing is that the non-Google rest of the world does not use the GCC
conventions, so if you add a 3rd party JS file to a GCC build with advanced
compilation, then most of that code gets removed, or functions get renamed, and
everything ends up broken. Ugh.

There are several ways to get around this. The simplest is to simply keep your
3rd party lib out of GCC territory. Say you want to use D3, just do what you
would do in JavaScript, add a `<script>` tag, and reference the `d3` global,
using the `js/` pseudo-namespace.

``` javascript
<script src="https://d3js.org/d3.v4.js"></script>
```

``` clojurescript
(ns food3.core)

(def data #js [4 8 15 16 23 42])

(.. (js/d3.select "#app")
    (selectAll "div")
    (data data)
    enter
    (append "div")
    (style "width" (fn [d] (str (* d 10) "px")))
    (text identity))
```

This works, but it means doing an extra HTTP request to load the full D3
library. We're only using a tiny fraction of it, it would be nice if we could
get GCC to also take D3 into its build, so it can do its magic.

But D3 does not contain the necessary calls to `goog.provide`, or
`goog.exportSymbol`, and without these the optimizations it does will result in
a big mess.

The answer are "externs", you provide GCC with a file containing JavaScript
declarations, and it will leave these alone so you can be sure they'll still be
around in the final build. An extern file for D3 could look like this. All that
matters are the identifier names, that's why just using empty functions or
objects is fine. Here's a
[full externs file for D3](https://github.com/cljsjs/packages/blob/master/d3/resources/cljsjs/d3/common/d3.ext.js)

``` javascript
var d3 = {
  "version": {},
  "selectAll": function() {},
  ...
}
```


The friendly people behind [CLJJS](http://cljsjs.github.io/) have bundled up
many popular JS libs including the correct externs, so at least you no longer
have to do that yourself. Say you want to use D3 with CLJSJS, on their site you
find two pieces of info: the dependency vector so lein/boot/maven can pull in
the right Jar, and some options for the ClojureScript compiler, telling it where
to find this "external lib", and which externs file to use. Here's a complete
`project.clj` with both a dev and a production build.

``` clojure
(defproject food "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.89" :scope "provided"]
                 [cljsjs/d3 "4.2.2-0"]]

  :plugins [[lein-cljsbuild "1.1.3"]]

  :min-lein-version "2.6.1"

  :source-paths ["src/clj" "src/cljs"]

  :cljsbuild {:builds
              [{:id "app"
                :source-paths ["src/cljs" "src/cljc"]

                :compiler {:main food.core
                           :asset-path "js/compiled/out"
                           :output-to "resources/public/js/compiled/food.js"
                           :output-dir "resources/public/js/compiled/out"
                           :source-map-timestamp true
                           :foreign-libs [{:file "cljsjs/d3/development/d3.inc.js",
                                           :provides ["cljsjs.d3"],
                                           :file-min "cljsjs/d3/production/d3.min.inc.js"}],
                           :externs ["cljsjs/d3/common/d3.ext.js"]}}


               {:id "min"
                :source-paths ["src/cljs" "src/cljc"]
                :jar true
                :compiler {:main food.core
                           :output-to "resources/public/js/compiled/food.js"
                           :output-dir "target"
                           :source-map-timestamp true
                           :optimizations :advanced
                           :pretty-print false
                           :foreign-libs [{:file "cljsjs/d3/development/d3.inc.js",
                                           :provides ["cljsjs.d3"],
                                           :file-min "cljsjs/d3/production/d3.min.inc.js"}],
                           :externs ["cljsjs/d3/common/d3.ext.js"]}}]})
```

These are the interesting bits:

``` clojure
:foreign-libs [{:file "cljsjs/d3/development/d3.inc.js",
                :provides ["cljsjs.d3"],
                :file-min "cljsjs/d3/production/d3.min.inc.js"}],
:externs ["cljsjs/d3/common/d3.ext.js"]
```

`:foreign-libs` means "I have these libraries that do not conform to the GCC
module conventions, but compile them with GCC anyways". When using `:advanced`
compilation GCC does a lot of renaming of identifiers (function names), and this
is going to cause trouble, so with `:externs` you tell it "here's a JS file with
declarations of identifiers that you should not rename". This one here contains
the signatures of all public functions in D3, so GCC will leave them alone, and
you can call them from your code.

GCC still needs to know which parts of your code depend on this 3rd party code,
so it can load things in the right order. For this you provide a "synthetic
namespace" with `:provide ["cljsjs.d3"]`. This basically tells GCC "You and I
both know there's not *actually* a namespace/module called `cljsjs.d3`, but when
I require `cljsjs.d3`, then make sure this foreign lib is loaded. I'll take care
of the rest, wink wink."

So now the code from before gets an extra `:require`, but we're still treating
`d3` as an external `js/` global.

``` clojure
(ns food3.core
  (:require [cljsjs.d3]))

(def data #js [4 8 15 16 23 42])

(.. (js/d3.select "#app")
    (selectAll "div")
    (data data)
    enter
    (append "div")
    (style "width" (fn [d] (str (* d 10) "px")))
    (text identity))
```

Now an advanced compilation *will* include D3 into the single compacted JS file
that it produces. Here's a small snippet of it

``` javascript
t.version=Ad,t.bisect=Cd,t.bisectRight=Cd,t.bisectLeft=zd
```

This corresponds with this code from D3

``` javascript
exports.version = version;
exports.bisect = bisectRight;
exports.bisectRight = bisectRight;
exports.bisectLeft = bisectLeft;
```

The thing to note here is that some identifiers have been renamed (`exports`,
`bisectRight`, etc), but thanks to the exports file included with the CLJSJS
package, the ones that matter to the outside world are left intact.

The final file size of our code + cljs.core + d3 is 208k.

Up to here things are relatively well known and documented. What isn't as
commonly known, is that GCC can convert JavaScript modules like CommonJS to its
own `goog.provide`/`goog.require` format, and for a year now ClojureScript has
exposed that functionality for us to use.

The D3 README says that it supports both CommonJS and AMD, both of which are
supported, so let's try that. The key now is to use `:module-type` in
`:foreign-libs`.

According to the examples in the
[ClojureScript Compiler Docs](https://github.com/clojure/clojurescript/wiki/Compiler-Options#foreign-libs)
it should be possible to put a URL for a `:file`, so this was my first attempt.

``` clojurescript
:foreign-libs [{:file "https://d3js.org/d3.v4.js"
                :provides ["d3"]
                :module-type :commonjs}]
```

The error message made it clear that it was looking for a file relative from the
project root instead. Maybe I'm just holding it wrong but whatever, I just
downloaded the current
[D3 release](https://github.com/d3/d3/releases/tag/v4.2.2), unzipped it, and
copied `d3.js` to `resources`. Now I have this in my compiler options.

``` clojurescript
:foreign-libs [{:file "resources/d3.js"
                :provides ["d3"]
                :module-type :commonjs}]
```

Now the code looks like this

``` clojure
(ns food3.core
  (:require [d3]))

(def data #js [4 8 15 16 23 42])

(.. (d3/select "#app")
    ,,,)
```

Notice that `js/d3.select` has become `d3/select`. It's like a real namespace now. Cool!

Except... it doesn't work :(

I start poking around and looking at the generated output. This is what the `core.js` looks like

``` javascript
// Compiled by ClojureScript 1.9.89 {}
goog.provide('food3.core');
goog.require('cljs.core');
goog.require('module$resources$d3');
food3.core.data = [(4),(8),(15),(16),(23),(42)];
d3.select("#app").selectAll("div").data(food3.core.data).enter().append("div").style("width",(function (d){
return [cljs.core.str((d * (10))),cljs.core.str("px")].join('');
})).text(cljs.core.identity);

//# sourceMappingURL=core.js.map?rel=1473680382555
```

Notice this bit,

``` javascript
goog.require('module$resources$d3');
```

Seems D3 has been given its own generated module name. Let's see how that works.

This is what `d3.js` looks like before compilation

```
(function (global, factory) {
  typeof exports === 'object' && typeof module !== 'undefined' ? factory(exports) :
  typeof define === 'function' && define.amd ? define(['exports'], factory) :
  (factory((global.d3 = global.d3 || {})));
}(this, (function (exports) {
  ...
});
```

Here's what these first few lines look like after compilation

``` javascript
goog.provide("module$resources$d3");
(function(global,factory){
  typeof module$resources$d3 === "object" && typeof module !== 'undefined' ? factory(module$resources$d3) :
  typeof define === "function" && define.amd ? define(["exports"],factory) :
  factory(global.d3 = global.d3 || {})})
(this,function(exports){
  ...
});
```

At first glance not much has changed, the main thing you notice is the
`goog.provide` line at the top, but the important bit is that `exports` has been
renamed to `module$resource$d3`. In CommonJS modules, the exported values need
to be added to either `exports` or `module.exports`. GCC looks for these
identifiers and renames them, so it can "capture" whatever this library is
trying to export.

The problem though lies a bit further. Like many popular libraries nowadays, D3
tries to detect the module type being used. It only uses CommonJS when `exports`
is an `object`, and when `module` is not `undefined`. Because GCC only checks
for `exports` and `module.exports`, but not for `module` by itself, `module`
remaind undefined, and so the check fails, and D3 ends up exporting zip, nada,
nothing.

Since this is a pretty common pattern it seems like a big issue. Ideally GCC
would find a way around this, but perhaps in the meanwhile ClojureScript can
come up with a workaround. Doing something like this before the lib gets loaded
might already be enough.

``` clojurescript
(set! js/module nil)
```

I ended up taking `&& typeof module !== 'undefined'` out of the D3 source file.
After that it worked great.

So what's cool about this? We managed to include and use D3 (mostly) as-is, and
most importantly, without an externs file.

I had expected the final file size to be smaller now, since being a real GCC
module presumably would better allow GCC to do its magic, but that's not the
case. It's 284k now, vs 208k before. Not sure what's up with that.

I did notice some other glitches, from a Figwheel browser repl when trying to do
`d3/version` I got an error message saying `d3.cljs` could not be found, even
though after that I did get the right return value. My guess is (from briefly
poking around some sources, don't quote me on this) that these foreign libs are
tracked separately from "real" namespaces in the compiler env, so code that now
deals with namespaces might have to be made aware of both.

In general this seems like the way forward. I hope this post inspires more
people to try this out, and report their findings. ClojureScript really lives on
community contributions, and if it's more clear how things work, what already
works, and what could be improved, then it will be easier for people (like you?)
to jump in and make ClojureScript even more awesome. Here's to the next 5 years
of ClojureScript!
