---
title: "Union Types with Clojure.Spec"
slug: "union-types"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2016-09-25T19:04:50.000Z"
updated_at: "2026-05-16T13:42:14.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-09 12:20"]
---

Elm and other statically typed languages have a great feature called Union Types (also called Sum Types or Algebraic Data Types).

Here’s an example taken from Elm. Suppose your system used to represent users as integers, maybe just an auto-incrementing primary key, but then switched to UUIDs represented as strings.

To correctly model this situation, you need a way to create a type that can be either an integer or a string, that’s what union types give you.
    
    
    type UserID = OldID Int | NewID String
    

Now each time you want to do something with a user id, you’ll have to add code to cover all these cases. This you can do with Elm’s `case ... of`. The interesting thing is that if you miss one of the cases, then the compiler will complain. Say in the future you decide to use a reified UUID type, now the code won’t compile until you’ve handled that case everywhere you’re dealing with user ids.
    
    
    toNewID : UserID -> String
    toNewID userID =
        case userID of
          OldID number -> toString number
          NewID string -> string
    

With clojure.spec we get the same power of expression, here’s how a `::user-id` spec would look like.
    
    
    (require [clojure.spec :as s])
    
    (s/def ::user-id (s/or :old-id int?
                           :new-id string?))
    

Consuming this is straightforward, especially with [core.match](https://github.com/clojure/core.match).
    
    
    (require [core.match :refer [match]])
    
    (defn format-id [user-id]
      (match [(s/conform ::user-id user-id)]
        [:old-id id] (str "Old id: " id)
        [:new-id id] (str "New id: " id)
        :else "Invalid id"))
    

So we get pretty much the same functionality, even the syntax is still pretty clean, but there’s one crucial difference: we don’t get compile time, or even runtime checks to make sure we’re covering all cases.

I thought it would be a fun excercise to implement a `case-of` macro, that during macro-expansion checks to see if all cases are covered. This is what it looks like in action.
    
    
    (require '[lambdaisland.uniontypes :refer [case-of])
    
    (s/def ::availability (s/or :sold-out  #{:sold-out}
                                :in-stock  pos-int?
                                :reordered (s/tuple pos-int? pos-int?)
                                :announced string?))
    
    (defn format-availability [a]
      (case-of ::availability a
        :sold-out _
        "Sold out."
    
        :in-stock amount
        (str "In stock: " amount " items left.")
    
        :reordered [min max]
        (str "Available again in " min " to " max "days" )
    
        :announced date
        (str "Will be available on: " date)))
    

The first argument is the name of a spec, the second is the value to check. After that you give the name of a case in the `s/or` spec, a binding form that will receive the conformed value, you can also use destructuring, as in the `:reordered` case, and finally the code that handles the given case.

If you have too many or too few cases, your code will not compile, and you’ll get an error message that looks like this.
    
    
    The cases in this `case-of` are different from the ones in the spec:
    
        (s/def :lambdaisland.uniontypes-test/availability
          (s/or :sold-out #{:sold-out}
                :in-stock pos-int?
                :reordered (tuple pos-int? pos-int?)
                :announced string?))
    
    Add a case for :announced.
    Remove the case :foo.
    

If the given value does not conform to the spec, it’ll throw an exception. You can override that behavior by adding a special `:spec/invalid` case. (Note: that’s _not_`:clojure.spec/invalid` for… reasons).

The given variable will in this case be bound to the result of `clojure.spec/explain-data`.

The code is [on Github](https://github.com/lambdaisland/uniontypes) as well as [Clojars](https://clojars.org/lambdaisland/uniontypes).
    
    
    [lambdaisland/uniontypes "0.1.0"]
    

## Use cases

It might not be immediately obvious why this compile time check is so important, so let me give another example.

You have a chat application, each channel contains messages, and notifications like people coming and going. In your application state this is modeled as a single heterogeneous sequence. While rendering you need to loop over this sequence, and render the right component depending on the type of each entry.

The same kind of data is used to display notifications, to update a search index, and to aggregate statistics.

The new product manager decides you really need picture uploads, so that becomes a new type of entry. Now you need to update all of the places in your code where you deal with entries. Normally you would try to `grep`, or just do it from memory, and hope for the best. If you were using `uniontypes`, you could be sure you weren’t forgetting any of these cases.

## ClojureScript support

So does it work on ClojureScript you ask… good question! Yes and no. I tried hard to make it compatible with ClojureScript, and that threw more than one spanner in the works. I got it working though, in the sense that `lein doo phantom` tells me all the tests pass on ClojureScript (there aren’t many, but enough to test at least a happy path and some error conditions).

Any attempt to actually use it on a ClojureScript project went awry, unfortunately.

One of the reasons it’s hard to do this on ClojureScript is that the spec is needed both at Compile time, to check that you have all the branches, and at runtime, to validate and conform the value. That means the spec provided by the end user needs to be available both on Clojure and ClojureScript, in other words, it’s necessary to put your specs in `cljc` files.

That alone isn’t always enough to also make sure the spec is loaded during macroexpansion, so the library will try to load the namespace based on the name of the spec. So if you do `(case-of :foo.bar/baz)`, then during macroexpansion this will try to load `foo.bar`. This worked to get the tests going, but might also be what’s causing issues. It’s not the greatest hack as far as hacks go.

Another “interesting” issue I ran into was the difference between `clojure.spec`and `cljs.spec`. ClojureScript has gotten smarter about this, in the sense that when you `(require '[clojure.spec :as s])` it will actually require `cljs.spec`, but when writing macros you need to make sure manually that the symbols you’re generating are in the right namespace.

What I ended up doing was detecting if the macro was being expanded for use in ClojureScript or not using the `(:ns &env)`[trick](https://groups.google.com/forum/#!topic/clojurescript/iBY5HaQda4A), and then doing a `clojure.walk/prewalk` over the generated code, to replace all symbols and keywords in the `clojure.spec` namespace with their cousins in `cljs.spec`.

## Reflections

I thought this would be one evening hack, it turned out to be a bit more involved than that. I’m a bit bummed that ClojureScript support isn’t fully there yet, as I think this could be especially useful in UI programming. The fact that it’s working for the restricted case of running tests on Phantom.js does mean that it’s compatible in principle, if anyone cares to do the final stretch of troubleshooting.

I need to dogfood this some more to see how useful it will be in practice, but I think it does show that there are still a lot of imaginative uses of clojure.spec left to be uncovered.
