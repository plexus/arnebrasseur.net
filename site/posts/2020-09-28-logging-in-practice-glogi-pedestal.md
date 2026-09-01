---
title: "Logging in Practice with Glögi and Pedestal.log"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2020-09-28T11:23:21.000Z"
updated_at: "2026-05-16T13:41:45.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-18 14:59"]
---

In an [earlier post](https://lambdaisland.com/blog/2020-06-12-logging-in-clojure-making-sense-of-the-mess)I tried to map out the Clojure/Java logging landscape a bit, so people can make an informed choice about how to set up logging for their application. There are several solutions out there with different trade-offs, and recently both [Timbre](https://github.com/ptaoussanis/timbre)and [mulog](https://github.com/BrunoBonacci/mulog) have had some new releases. However until further notice the default logging setup in the “Lambda Island Stack” remains pedestal.log on the backend, and Glögi at the front.

This blog post is a bit of practical advice on how to make good use of logging, with the focus on these two libraries, which both provide the same API, just for a different platform.

This API consists of a number of macros corresponding with the different log levels, which all take key-value pairs as arguments.
    
    
    (ns foo.bar
      (:require [#?(:clj io.pedestal.log :cljs lambdaisland.glogi) :as log])
    
    (log/info :hello "world" :good "morning")
    

So it looks a lot like a call to `hash-map`, and generally that’s what it will look like in your logs
    
    
    17:51:17.743 [Thread-3] INFO foo.bar {:hello "world", :good "morning"}
    

What follows are a few dos and don’ts that I’ve figured out over time, they work well for me, maybe there’s something in here that will work for you too. Feel free to try them on for size.

## Start with a single namespaced keyword

While you can stick any number of keys at the top level, I often have only one, with a map as value.
    
    
    (log/info :run-loop/starting {...})
    

Note that I don’t use the current namespace as the namespace for the keyword (`::run-loop-starting`), since the namespace name already shows up in the logs separately. Instead I use the namespace part of the keyword to point at some context, and the name part to point at some event.

This “context” is often just the name of the function, but it could also be some other name from your business domain. It needs to provide a person reading the logs enough info to know what it is we’re talking about. It helps to group related log messages, and when well chosen it gives you something to grep for.

The name part (the part after the slash) will usually be a verb. Note that logging usually happens before or after performing side effects. Sending an email, doing an API call, adding an event to a queue… Logging that you are doing some pure computation usually doesn’t add much value.

That means that you are typically adding a log entry right before something happens, or after something has finished. In the first case I will use a present continuous tense (`:run-loop/starting`), in the latter a past tense (`:run-loop/started`).

It’s fine to have both, in that case you probably want to decide which of the two is more important to know about, and assign that one a higher log level.

This is usually the only top-level key I have in there, that way it also functions as a kind of event identifier to identify specific log entries, making it easy to filter them after the fact (logs are just data after all).

The only other top level keys I may add are `:exception`, since Pedestal.log and Glögi both look for this key to log the exception, and `:message`, to include a human readable message (a string).

## Use the map for inputs, outputs, locals

So what goes into that single map? That’s up to you of course, you want to put enough relevant data in there so your logs tell you something, but you don’t want to put huge data structures in there that will result in pages of output.

When logging before a side effect, try to include any arguments/parameters that determine what happens. When logging after the effect, also include any return or success/fail values.

Try to use the same names in your logging statement that you use in your code. This makes it much easier to correlate the two later on, and it makes it easier to copy some values from a log message into a REPL to reproduce some behavior.
    
    
    (defn perform-action [{:keys [color size id]}]
      (log/info :action/performing {:color color :size size :id id})
      (let [result (do-action color size id)]
        (log/trace :action/performed {:id id :result result})
        result))
    

## Layer your logging

When coaching people I often see them use logging the way you would use println debugging. They add `log/error` statements anywhere where they want to inspect a value, since that way they don’t need to think about log levels, and when they’re done they take the logging out again.

Instead try to place meaningful logging statements that you can leave in. Here log levels come into play. You want to think of logging of a certain function, namespace, or subsystem as layers of information. Like layers in photoshop you can show or hide them to see more or fewer details.

The bottom layer is stuff that you simply can not ignore, in other words `log/error`. Right on top is things that you probably should pay attention to because it might be a problem, this is `log/warn`.

The next layer is `log/config` (this one is only available in Glögi, not in Pedestal, for pedestal use `log/info`). Here you see information about how things are set up and configured. File system paths, port numbers, environment variables, etc. Next up is `log/info`, informational messages about what the system is doing under the hood. Major subsystems starting or stopping, messages about current workload. This is usually a good default level.

Finally we get to the detailed layers, `log/debug` and `log/trace`. These you don’t generally enable for the whole system because that would get too noisy, but you turn them on for certain namespaces when you are working on them, or figuring out an issue.

With `log/debug` you already want to give a fairly good idea of what’s actually happening, logging individual requests, events, or whatever the unit of processing in your system is.

Finally `log/trace` opens the floodgates, showing in fine details what is happening. Here you no longer care much about parsimony, you care about providing all the details that could be relevant to see exactly what’s going on. When you do enable this log level it’s probably only for a short amount of time for a very limited scope, so that you can capture all that information and then analyze it.

I hope this advice resonates with people, if you have your own suggestions of how to approach logging in practice then come find us in the [#observability](https://clojurians.slack.com/archives/C010TGGL02X) channel on Clojurians Slack.
