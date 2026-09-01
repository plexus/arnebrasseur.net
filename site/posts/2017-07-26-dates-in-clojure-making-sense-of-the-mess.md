---
title: "Dates in Clojure: Making Sense of the Mess"
slug: "dates-in-clojure-making-sense-of-the-mess"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2017-07-26T12:53:12.000Z"
updated_at: "2026-05-16T13:42:08.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-09 11:48"]
---

_Update 2018-11-27: while most of this article is still relevant, I no longer recommend using JodaTime as the main date/time representation for new projects. Even existing projects that aren’t too invested in JodaTime/clj-time should consider migrating to`java.time` and `clojure.java-time` across the board._

_Update 2 2019-05-29: Also check out the talk[Cross Platform DateTime Awesomeness](https://www.youtube.com/watch?v=UFuL-ZDoB2U) by Henry Widd, given at Clojure/north 2019_

You can always count on human culture to make programming messy. To find out if a person is a programmer just have them say “encodings” or “timezones” and watch their face.

In Java, and hence Clojure, there are half a dozen different ways to represent dates and times, which can lead to confusion, needless type coercion, and inconsistent code. In this post I’ll give you a quick lay of the land, and some tips to make it all a bit easier to deal with.

## Representing Time

### Unix Time

The traditional way to represent time, the system that has been handed down to us by The Elders, is known as [Unix Time](https://en.wikipedia.org/wiki/Unix_time). A Unix timestamp is a number representing the amount of seconds passed since the [start of time](https://en.wikipedia.org/wiki/Epoch_\(reference_date\)), which is conveniently defined as midnight, January 1, 1970.

![1970's family pic](https://farm1.staticflickr.com/36/75450245_ddc16cbb18_z_d.jpg)

_A happy family shortly after the start of time[(credits)](https://www.flickr.com/photos/gregor_y/75450245)_

### java.util.Date

Some 822 million seconds later Java 1.0 came out. It was the year of the Spice Girls, the Macarena, and mutable date classes.
    
    
    user> (def date (java.util.Date.))
    #'user/date
    user> date
    #inst "2017-06-09T06:59:40.829-00:00"
    user> (.setTime date 822391200000)
    nil
    user> date
    #inst "1996-01-23T10:00:00.000-00:00"
    user>
    

A `Date` wraps a milli-second based Unix timestamp, meaning it’s not time zone aware.
    
    
    user>  (java.util.TimeZone/setDefault (java.util.TimeZone/getTimeZone "UTC"))
    user> (.toString date)
    "Tue Jan 23 10:00:00 UTC 1996"
    user> (java.util.TimeZone/setDefault (java.util.TimeZone/getTimeZone "Europe/Berlin"))
    user> (.toString date)
    "Tue Jan 23 11:00:00 CET 1996"
    

That’s not all that’s wrong with `java.util.Date`, so in Java 1.1 they tried to rectify things by deprecating half of the `Date` API and instead introducing a (obviously mutable) `GregorianCalendar`.

## JodaTime

It’s 2002, Brazil has beaten Germany in the FIFA World Cup, and someone decides the time is ripe for a proper date/time library. In no time at all [JodaTime](http://www.joda.org/joda-time/) is born!

It doesn’t take long or Joda becomes the de facto time library on the JVM, and in 2010 it becomes the basis for [clj-time](https://github.com/clj-time/clj-time).

Joda contains two principles classes for representing time, `org.joda.time.Instant`, which is essentially a Unix timestamp, and `org.joda.time.DateTime`, which explicitly stores year/month/day/etc.

Having both these classes makes the distinction clear between a “machine time” and “human time”. For machines things are relatively straightforward: time is an ever increasing number. The same instant in time corresponds with the same number, no matter where you are or how you look at it. There are no jumps or gaps, time is strictly monotonous.

The human view of time is a lot more complex. There are time zones, daylight saving time, not to mention different calendar systems and historical time adjustments. For adding timestamps to your logs machine time will work fine, but when your system communicates dates and times with end users you need a richer model.

## JSR-310

For years this was the state of things: you used the built-in `java.util.Date`despite its shortcomings, or you relied on JodaTime as an external dependency. That you needed a library to do something as essential as working with dates and times seemed a bit odd, and so Oracle involved the JodaTime author, Stephen Colebourne, to work on a new Date/Time library for the JDK, affectionately known as [JSR-310](https://jcp.org/en/jsr/detail?id=310).

JSR-310 introduces a new `java.time` package, which includes `java.time.Instant`and `java.time.ZonedDateTime`. While clearly inspired by JodaTime, there are also some differences. The author [explains in this article](http://blog.joda.org/2009/11/why-jsr-310-isn-joda-time_4941.html)that there are some shortcomings in JodaTime that they sought to address.

Development was completed in 2014, and `java.time` shipped with Java 8. That’s basically last week in Java years, it will be a while before support for this has become widespread.

On the clj-time issue tracker there’s an [ongoing discussion](https://github.com/clj-time/clj-time/issues/196) about what this will mean for clj-time. While there’s a lot of interest for clj-time to adopt JSR-310, it seems the current concensus is that if they do this will be with a new artifact id (a new library name). The differences between JodaTime and JSR-310 are big enough to make breaking API changes inevitable. By using a different name it will be possible to switch to the new version, while libraries you depend on can keep using the old version.

## clojure.java-time

Meanwhile someone has gone ahead and created an idiomatic Clojure library for the `java.time.*` API, aptly named [clojure.java-time](https://github.com/dm3/clojure.java-time), so perhaps this will be what clj-time 2.0 could have been. If you are starting a new project today you might skip clj-time altogether, and instead use clojure.java-time for your date and time needs, assuming you’re at least on Java 8.

If you do go that route you should do your homework though. `java.time` contains a handful of different date/timestamp classes, and you should know which is which. The [clojure.java.time README](https://github.com/dm3/clojure.java-time/blob/master/README.md)has several links for further reading.

## clojure.instant

It’s also worth pointing out that since Clojure 1.8 there’s a built-in namespace called [clojure.instant](https://clojure.github.io/clojure/clojure.instant-api.html), which contains a few utility methods for parsing dates.

## java.sql.Timestamp

You thought we were done? Guess again! Besides `java.util.Date`, Java has always included yet another timestamp class: `java.sql.Timestamp`. The reason is that timestamps in the SQL standard have a nanosecond precision, while `java.util.Date` only stores milliseconds. So a `java.sql.Timestamp` inherits from `java.util.Date`, but adds an extra nanosecond field.

If you’re using JDBC to read or store timestamps, then you’ll run into this class.

## Making things bearable

Having all these different types for representing essentially the same thing is a recipe for massive frustration, but with a few small tweaks we can make it all a lot less painful.

The trick is to pick one of the above, and use it across the board. For now `org.joda.time.Instant` is still a likely candidate, it has widespread library support, and you can combine it with `clj-time` for writing idiomatic Clojure code. If instead you want to be future proof you can opt for `java.time.Instant`, in combination with `clojure.java-time`. Several people have reported they’re already getting good use out of `java.time`. The snippets below assume you’re sticking with JodaTime.

Whenever time values enter your program, convert them to JodaTime. Maybe you’re reading JSON, EDN, Transit, you’re pulling values off an API or out of a database. Don’t let them propagate through your code as `java.util.Date`s (or worse, `long`s representing Unix timestamps), but convert them to `org.joda.time.DateTime` as soon as possible.

The same goes for outputting values. You don’t want to see an exception any time you encode some JSON with a timestamp, or save a date to the database.

Many libraries can be configured to do this automatically, so you only have to worry about it once.

### JDBC

JDBC is Java’s standard for communicating with relational databases, and the `clojure.java.jdbc` library provides a convenient Clojure wrapper. By default JDBC expects, and outputs, `java.sql.Timestamp`. But by loading the `clj-time.jdbc` namespace it switched to JodaTime instead. Nice, that was easy!
    
    
    ;; load/save DB times as joda DateTime
    (require 'clj-time.jdbc)
    

### Cheshire

The go-to library for reading and writing JSON from Clojure is called Cheshire. JSON doesn’t have a standard way of encoding time values. If you do need to encode timestamps the common thing to do is to either use a string containing an ISO-8601 formatted timestamp, or to use a number representing a UNIX timestamp.

This snippet configures Cheshire to output `org.joda.time.DateTime` as a string.
    
    
    (ns my.ns
      (:require [cheshire.factory]
                [cheshire.generate :as cheshire]
                [clj-time.coerce :as tc]
                [clj-time.core :as t])
      (:import [org.joda.time.DateTime]
               [com.fasterxml.jackson.core.JsonGenerator]
               [java.text.SimpleDateFormat]
               [java.util SimpleTimeZone]))
    
    (defn cheshire-add-jodatime-encoder! []
      (cheshire/add-encoder
       org.joda.time.DateTime
       (fn [^org.joda.time.DateTime d ^JsonGenerator jg]
         (let [sdf (SimpleDateFormat. cheshire.factory/default-date-format)]
           (.setTimeZone sdf (SimpleTimeZone. 0 "UTC"))
           (.writeString jg (.format sdf (tc/to-date d)))))))
    
    (cheshire-add-jodatime-encoder!)
    

## EDN/Clojure

In EDN and Clojure source code you can use an `#inst` tagged literal to represent timestamps. These by default will be read as `java.util.Date`.

Having a literal syntax for timestamps can be very handy, one example would be to write out test data containing timestamps.

In my apps I tend to configure Clojure’s reader and printer to represent `org.joda.time.DateTime` as `#joda/inst "...timestamp..."`, providing me the same convenience. When doing REPL-driven development (directly or inside a source buffer) this is particularly useful, since results that are printed out can be read again, they are proper time literals.

Clojure’s printer can be extended through the `print-method` and `print-dup`multimethods.
    
    
    ;; Configure the printer
    (defmethod print-method org.joda.time.DateTime
      [dt out]
      (.write out (str "#joda/inst \"" (.toString dt) "\"") ))
    
    (defmethod print-dup org.joda.time.DateTime
      [dt out]
      (.write out (str "#joda/inst \"" (.toString dt) "\"") ))
    
    ;; Utility function for the reader
    (defn ->joda-inst [time-str]
      (org.joda.time.DateTime/parse time-str))
    
    

The reader can be taught about new tagged literals through a `data_readers.clj` file placed on the classpath. I found it works best to put it under `resources`, that way Leiningen is smart enough to merge it with other `data_readers.clj` files provided by libraries when packing up an uberjar.
    
    
    ;; resources/data_readers.clj
    {joda/inst lambdaisland.util.time/->joda-inst}
    

## Tu quoque, ClojureScript?

This article has focused on JVM based Clojure, but what about ClojureScript? Things aren’t quite as chaotic on the JavaScript side. It seems in general people are content using `js/Date`. There is a separate [goog.date.DateTime](https://google.github.io/closure-library/api/goog.date.DateTime.html) included with the Google Closure library, which in turn is used by [cljs-time](https://github.com/andrewmcveigh/cljs-time), so you might run into it.

## For a good time

Java’s time/date landscape can be difficult to navigate, but a map of the territory can help you along the way. And with the right tools in your belt it can be a pleasant journey after all.

_Do you have your own snippets for making libraries or subsystems play well with either JodaTime or java.time?[Send them in](__GHOST_URL__/contact) and I will add them to the article_

 _A special thanks to Ben Lovell, Tim Gilbert, and Sean Corfield for pointing me to clojure.java-time._
