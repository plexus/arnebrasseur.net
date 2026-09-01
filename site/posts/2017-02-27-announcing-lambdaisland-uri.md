---
title: "Announcing lambdaisland/uri 1.0.0"
slug: "announcing-lambdaisland-uri"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2017-02-27T09:35:05.000Z"
updated_at: "2026-05-16T13:42:11.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-09 12:20"]
---

I just released `lambdaisland/uri`, a pure Clojure/ClojureScript URI library. It is available [on Github](https://github.com/lambdaisland/uri) and [Clojars](http://clojars.org/lambdaisland/uri).

This is a small piece of the code base that powers [lambdaisland.com](https://lambdaisland.com). It’s inspired by Ruby’s [Addressable::URI](https://github.com/sporkmonger/addressable), the most solid URI implementation I’ve seen to date, although it only offers a small part of the functionality that library offers.

It’s written in pure Clojure/ClojureScript, with only minimal use of `.cljc` reader conditionals to smooth over differences in regular expression syntax, and differences in core protocols. It does not rely on any URI functionality offered by the host, such as `java.net.URL`, so it’s usable across all current and future Clojure implementations (Clojure, ClojureScript, ClojureCLR).

Currently all this library offers is parsing a URI to its components (scheme, host, path, etc.), manipulating those components individually, and performing RFC compliant joining. `lambdaisland.uri.URI` instances print with a `#lambdaisland/uri "foo://bar"` reader tag, and a reader function is provided for use with `clojure.edn/read`.

The main missing parts are URI normalization and validation. These will be added in later versions. The intention is for this to become a modern Clojure URI library that can serve us for the time to come. Contributions are more than welcome. The code is released under the [Mozilla Public License 2.0](https://www.mozilla.org/media/MPL/2.0/index.txt), a [non-viral copyleft license (link to ‘tl;dr legal’)](https://tldrlegal.com/license/mozilla-public-license-2.0-\(mpl-2\)).

The code has extensive test coverage, and has CI set up for Clojure and ClojureScript (doo+phantomjs) to make sure it stays cross-platform compatible.

Example code:
    
    
    (require '[lambdaisland.uri :refer [uri join]])
    
    
    ;; uri :: String -> lambdaisland.uri.URI
    (uri "//example.com/foo/bar")
    ;;=> #lambdaisland/uri "//example.com/foo/bar"
    
    
    ;; A URI is a record, use assoc to update specific parts
    ;; Use `str` if you want the URI back as a string
    (str
     (assoc (uri "//example.com/foo/bar")
            :scheme "https"
            :user "arne"
            :password "supersecret"
            :host "lambdaisland.com"
            :port "3333"
            :path "/hello/world"
            :query "q=5"
            :fragment "section1"))
    ;;=> "https://arne:supersecret@lambdaisland.com:3333/hello/world?q=5#section1"
    
    
    ;; RFC compliant joining of relative URIs
    (join "//example.com/foo/bar" "./~arne/site/" "../foo.png")
    ;;=> #lambdaisland/uri "//example.com/foo/~arne/foo.png"
    
    
    ;; Arguments to `join` are coerced, you can pass strings, java.net.URI, or any x
    ;; for which `(str x)` returns a URI string.
    (join (java.net.URI. "http://example.com/foo/bar")  (uri "./~arne/site/") "../foo.png")
    ;;=> #lambdaisland/uri "http://example.com/foo/~arne/foo.png"
    
    
    ;; URI implements IFn for keyword based lookup, so it's fully
    ;; interface-compatible with Clojure maps.
    (:path (uri "http://example.com/foo/bar"))
    
    
    ;; Instances of URI are printed with a #lambdaisland/uri reader tag. To read
    ;; them back from EDN, use the provided readers.
    (require '[clojure.edn :as edn])
    
    (edn/read-string
     {:readers lambdaisland.uri/edn-readers}
     "#lambdaisland/uri \"http://example.com/foo/~arne/foo.png\"")
    

### About the author

 _Arne divides his time between making Clojure tutorial videos for[Lambda Island](https://lambdaisland.com), and working on open-source projects like [Chestnut](https://github.com/plexus/chestnut). He is also available for in-person training and mentoring. You can support Arne through [his Patreon page](https://www.patreon.com/plexus), which also gives you access to exclusive content._
