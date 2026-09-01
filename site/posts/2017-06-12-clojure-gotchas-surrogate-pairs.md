---
title: "Clojure Gotchas: Surrogate Pairs"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2017-06-12T21:12:17.000Z"
updated_at: "2026-05-16T13:42:09.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-09 11:48"]
---

_tl;dr: both Java and JavaScript have trouble dealing with unicode characters from[Supplementary Planes](https://en.wikipedia.org/wiki/Plane_\(Unicode\)#Supplementary_Multilingual_Plane), like emoji 😱💣._

Today I started working on the next feature of [lambdaisland/uri](https://github.com/lambdaisland/uri), URI normalization. I worked test-first, you’ll get to see how that went in the next Lambda Island episode.

One of the design goals for this library is to have 100% parity between Clojure and ClojureScript. Learn once, use anywhere. The code is all written in `.cljc` files, so it can be treated as either Clojure or ClojureScript. Only where necessary am I using a small amount of reader conditionals.
    
    
    #?(:clj
       (defmethod print-method URI [this writer]
         (.write writer "#")
         (.write writer (str edn-tag))
         (.write writer " ")
         (.write writer (prn-str (.toString this))))
    
       :cljs
       (extend-type URI
         IPrintWithWriter
         (-pr-writer [this writer _opts]
           (write-all writer "#" (str edn-tag) " " (prn-str (.toString this))))))
    

_Example of a reader conditional_

For this feature however I’m digging quite deeply into the innards of strings, in order to do [percent-encoding](https://en.wikipedia.org/wiki/Percent-encoding) and decoding. Once you get into hairy stuff like text encodings the platform differences become quite apparent. Instead of trying to smooth over the differences with reader conditionals, I decided to create two files `platform.clj` and `platform.cljs`. They define the exact same functions, but one does it for Clojure, the other for ClojureScript. Now from my main namespace I require `lambdaisland.uri.platform`, and it will pull in the right one depending on the target that is being compiled for.
    
    
    (ns lambdaisland.uri.normalize
      (:require [clojure.string :as str]
                ;; this loads either platform.clj, or platform.cljs
                [lambdaisland.uri.platform :refer [string->byte-seq
                                                   byte-seq->string
                                                   hex->byte 
                                                   byte->hex
                                                   char-code-at]]))
    

The first challenge I ran into was that I needed to turn a string into a UTF-8 byte array, so that those bytes can be percent encoded. In Clojure that’s relatively easy. In ClojureScript the Google Closure library came to the rescue.
    
    
    ;; Clojure
    (defn string->byte-seq [s]
      (.getBytes s "UTF8"))
    
    (defn byte-seq->string [arr]
      (String. (byte-array arr) "UTF8"))
    
    
    ;; ClojureScript
    (require '[goog.crypt :as c])
    
    (defn string->byte-seq [s]
      (c/stringToUtf8ByteArray s))
    
    (defn byte-seq->string [arr]
      (c/utf8ByteArrayToString (apply array arr)))
    

To detect which characters need to be percent-encoded I’m using some regular expressions. Things seemed to be going well, but when re-running my tests on ClojureScript I started getting some weird results.
    
    
    ;; Clojure
    (re-seq #"." "🥀")
    ;;=> ("🥀")
    
    ;; ClojureScript
    (re-seq #"." "🥀")
    ;;=> ("�" "�")
    

**Update: Ben Lovell ([@socksy](https://twitter.com/socksy/)) pointed out that modern JavaScript has a flag you can add to regular expressions to make them unicode aware, like so: `/some-regex/u`. In ClojureScript you can use this syntax to achieve the same effect: `(re-seq #"?(u)." "🥀")`**

This, gentle folks, is the wonder of surrogate pairs. So how does this happen?

Sadly I don’t have time to give you a complete primer on Unicode and its historical mistakes, but to give you the short version…

JavaScript was created at a time when people still assumed Unicode would never have more than 65536 characters, and so its strings use two bytes to represent one character, always. This is known as the UCS-2 encoding.

Unicode has grown a lot since then, and now also has a lot of codepoints with numbers greater than 65536. These include many old scripts, less common CJK characters (aka Hanzi or Kanji), many special symbols, and last but not least, emoji!

So they needed a way to represent these extra characters, but they also didn’t want to change all those systems using UCS-2 too much, so UTF-16 was born. In UTF-16 the first 65536 codepoints are still encoded the same as in UCS-2, with two bytes, but the ones higher up are encoded with 4 bytes using some special tricks involving some gaps in the Unicode space. In other words, these characters take up the width of two characters in a JavaScript string. These two characters are known as a “surrogate pair”, the first one being the “high surrogate”, and the other one the “low surrogate”.

So this is what JavaScript strings do now, but the rest of the language never got the memo. Regular expressions, string operations like `.substr` and `.slice` all still happily assume it’s 1995, and so they’ll cut surrogate pairs in half without blinking.

ClojureScript builds on those semantics, so you are liable to all the same mess.
    
    
    (seq "🚩 ")
    ;;=> ("�" "�")
    

I managed to work around this by first implementing `char-seq`, a way of looping over the _actual_ characters of a string.
    
    
    (defn char-code-at [str pos]
      #?(:clj (.charAt str pos)
         :cljs (.charCodeAt str pos)))
    
    (defn char-seq
      "Return a seq of the characters in a string, making sure not to split up
      UCS-2 (or is it UTF-16?) surrogate pairs. Because JavaScript. And Java."
      ([str]
       (char-seq str 0))
      ([str offset]
       (if (>= offset (count str))
         ()
         (let [code (char-code-at str offset)
               width (if (<= 0xD800 (int code) 0xDBFF) 2 1)] ; detect "high surrogate"
           (cons (subs str offset (+ offset width))
                 (char-seq str (+ offset width)))))))
    

I imagine this snippet might come in handy for some. Notice how it’s basically identical for Clojure and ClojureScript. This is because Java suffers from the same problem. The only difference is that there _some_ of the language got the memo. So for instance regular expressions correctly work on characters, but things like substring or `.charAt` are essentialy broken.

Hopefully ClojureScript will eventually fix some of this mess, for instance by having a `seq` over a string return the real characters, but for performance reasons it’s likely they will want to stick closely to JavaScript semantics, so I wouldn’t count too much on this happening.

In the meanwhile what we can do is document the things you need to watch out for, and write cross-platform libraries like [lambdaisland/uri](https://github.com/lambdaisland/uri) that smooth over the differences. 👍
