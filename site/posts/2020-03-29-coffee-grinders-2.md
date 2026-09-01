---
title: "Coffee Grinders, part 2"
slug: "coffee-grinders-2"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2020-03-29T11:57:06.000Z"
updated_at: "2026-05-16T13:41:48.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-18 14:51"]
---

Back in December I wrote about a coding pattern that I’ve been using more and more often in my work, which I dubbed “[Coffee Grinders](https://lambdaisland.com/blog/2019-12-16-advent-of-parens-16-coffee-grinders)”. My thoughts around this were still gelling at the time, and so the result was a meandering semi-philosophical post that didn’t really get to the point, and that didn’t seem to resonate that much with people.

Some of the responses I got were “just use functions” or “sounds like a finite-state machine”, which makes it clear that I was not in fact making myself clear.

Having continued to encounter and apply this pattern I’d like to present a more concise, semi-formal definition of coffee grinders.

At this point I should note that interceptors as pioneered by Pedestal, and since implemented in several other places like Sieppari or re-frame are a specific type of Coffee Grinder. In other words, coffee grinders are a generalization of interceptors. This post will make a lot more sense if you are familiar with Interceptors, at least in passing.

I’m making the [Lambda Island episode about Interceptors](https://lambdaisland.com/episodes/interceptors-concepts) free to watch for a limited time in case you need to brush up on that. There’s also a [toy Interceptor implementation](https://github.com/lambdaisland/ep47-interceptors/tree/master/src/lambdaisland) available, which is written in a way that encourages getting familiar with the concept from a REPL.

So without further ado, a Coffee Grinder is a way to model and execute a step-by-step process. It has the following elements:

  * A persistent (functional, immutable) data structure known as the `context`, which contains the complete current state of the process: inputs, outputs, intermediate state, and `work queue`.
  * `Step functions`, which accept a `context`, and yield an updated `context`, possibly asynchronously
  * A `runner` component which takes a `context`, and repeatedly runs it through the next `step function` in the `work queue`, until the queue is empty or some other terminating condition is reached



Some corollaries:

Step functions have access to the full `context`, including the `work queue`, which they are able to manipulate (by returning a `context` with an updated `work queue`). This allows queuing additional steps, but it also allows other things like prepending steps to the beginning of the queue, interleaving the steps in the queue with extra intermediate steps, or returning an empty queue, to terminate the process.

The complete state of the process is [reified](https://lambdaisland.com/blog/2019-12-01-advent-of-parens-1-clojure-vocab-reify) in the `context`. You can pause, restart, or inspect the process at any time, including after an error occurs, as long as you have the right `context` value available.

Coffee Grinders distinguish themselves in the “rules” of how the runner picks up and executes work, how asynchrony is handled, and what extra features it supports, like error handling or terminating conditions. Let’s look at the most basic implementation of a coffee grinder.
    
    
    (defn run [{:keys [queue] :as ctx}]
      (if-let [[step & next-queue] (seq queue)]
        (recur (step (assoc ctx :queue next-queue)))
        ctx))
    
    
    
    (run {:counter 0
          :queue [#(update % :counter inc)
                  #(update % :counter inc)]})
    ;;=> {:counter 2 :queue nil}
    

Note again that we have the full context available, including the queue, so a single step can dynamically enqueue ten more steps.
    
    
    (run {:counter 0
          :queue [(fn [ctx]
                    (update ctx
                            :queue
                            concat
                            (for [i (range 10)]
                              #(update % :counter + i))))]})
    ;;=> {:counter 45 :queue nil}
    

A typical use case is HTTP routing. Remember that HTTP request handling was the inspiration for Interceptors in the first place. They replace the opaque “wrapping function” based middleware concept with a reified queue and stack.

Here a single `route` step is responsible for enqueuing the actual HTTP handler.
    
    
    (defn handle-hello-world [req]
      {:status 200
       :body "Hiiii"})
    
    (def routes
      [[:get "/hello/world" handle-hello-world]])
    
    (defn router [{:keys [request] :as ctx}]
      (if-let [handler (some (fn [[m p h]]
                               (when (= [m p]
                                        [(:request-method request)
                                         (:path request)])
                                 h))
                             routes)]
        (update ctx
                :queue
                concat
                [(fn [ctx]
                   (assoc ctx :response
                          (handler (:request ctx))))])
        ctx))
    
    (run {:request {:request-method :get
                    :path "/hello/world"}
          :queue [router]})
    ;; => {:request {:request-method :get, :path "/hello/world"}
    ;;     :queue nil
    ;;     :response {:status 200, :body "Hiiii"}}
    

Another use case which I described in my original coffee grinders post is resource loading. If a step notices that a resource it needs to operate is not locally available, then it can prepend a resource-fetching step to the queue, and then re-enqueue itself.

Coffee grinders can implement asynchrony by allowing step functions to return not a new context, but a future value of a context, for instance a promise, core.async channel, or future.
    
    
    (defn run [{:keys [queue] :as ctx}]
      (if-let [[step & next-queue] (seq queue)]
        (let [result (step (assoc ctx :queue next-queue))]
          (recur (cond-> result
                   (instance? IDeref result)
                   deref)))
        ctx))
    

The above code works fine in Clojure because the threaded nature of the JVM means you can always block until a result is available, no matter which flavor of asynchrony you are using. In evented contexts like JavaScript this is not possible, and you may need to fully embrace the asynchrony. In this code snippet we allow step functions to return JS promises. An extra `done` callback is used to deliver the final context back to the caller.
    
    
    (defn run [{:keys [queue] :as ctx} done]
      (if-let [[step & next-queue] (seq queue)]
        (let [result (step (assoc ctx :queue next-queue))]
          (if (promise? result)
            (.then result #(run % done))
            (recur result)))
        (done ctx)))
    

So far we’ve reified process state, making what is otherwise spread across the stack and heap — in locals and instance fields — a single concrete value that we can pass around, inspect, manipulate, or serialize.

However we still have opaque function values in there, making the last option not viable. Instead we can further move towards pure data by enqueuing operations rather than functions, similar to how events and subscriptions are represented in re-frame.
    
    
    (defmulti handle (fn [ctx [op _]] op))
    
    (defmethod handle :add-to-counter [ctx [_ i]]
      (update ctx :counter + i))
    
    (defn run [{:keys [queue] :as ctx}]
      (if-let [[op & next-queue] (seq queue)]
        (recur (handle (assoc ctx :queue next-queue) op))
        ctx))
    
    (run {:counter 0
          :queue [[:add-to-counter 31]
                  [:add-to-counter 11]]})
    ;;=> {:counter 42, :queue nil}
    

Now your context is pure data that can be sent over the wire or stored to disk. Maybe you want to snapshot intermediate states to pick them up later, capture the context in case of an error to accompany bug reports, or have multiple worker nodes in a network which pick up work in the form of in-progress contexts off a database or queue, execute the next step, and write back the result.

You can take this one step further, by also reifying your side effects, and letting the runner handle them. This has great benefits for testing, since all your step functions are now pure.
    
    
    (defmulti handle-effect key)
    
    (defmethod handle-effect :send-email [[_ {:keys [to subject body]}]]
      ,,,)
    
    (defmethod handle :signup-user [ctx _]
      (assoc ctx :fx {:create-user [,,,]
                      :send-email {:to ,,, :subject ,,,}}))
    
    (defn run [{:keys [queue] :as ctx}]
      (if-let [[op & next-queue] (seq queue)
               next-ctx (handle (assoc ctx :queue next-queue) op)]
        (do
          (run! handle-effect (:fx next-ctx))
          (recur (dissoc next-ctx :fx)))
        ctx))
    

There are many more directions you can take this pattern. Just look at interceptors, which besides a `queue` also add a `stack` to the `context`, thus emulating HTTP middleware with its “on-the-way-in” and “on-the-way-out” operations.

I hope at this point I’ve at least piqued some people’s curiosity. Do let me know if you encounter a problem in your work that is elegantly solved with a coffee grinder.
