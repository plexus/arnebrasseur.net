---
title: "Advent 2019 part 16, Coffee Grinders"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2019-12-16T11:35:27.000Z"
updated_at: "2026-05-16T13:41:53.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-15 08:10"]
---

_This post is part of[Advent of Parens 2019](https://lambdaisland.com/blog/2019-11-25-advent-of-parens), my attempt to publish one blog post a day during the 24 days of the advent._

Over the last year or so I’ve found myself using some variations on a certain pattern when modelling processes in Clojure. It’s kind of like a event loop, but adapted to the functional, immutable nature of Clojure. For lack of a better name I’m calling these coffee grinders. (The analogy doesn’t even really work but the kid needs to have a name.)

Since I saw [Avdi Grimm’s OOPS Keynote](https://www.youtube.com/watch?v=UJnsXUVsr7w) at Keep Ruby Weird last year I’ve been thinking a lot about the transaction vs process dichotomy. Avdi talks about the “Transactional Fallacy” from around 15:25. From his slides:

A transaction:

  * Has a goal and is synonymous with its goal.
  * Is synchronous (blocking).
  * Can’t be put aside for later.
  * Completes (or fails) in essentially zero time.
  * Not supposed to be able to hang.
  * Either completes or aborts.
  * Self-destructs on error. (there is no concept of resuming)



The transactional fallacy is when you model something as a transaction while it really is a process. In Clojure we are perhaps even more susceptible to this because we tend to model things simply as functions. A web service call, a database import, a HTTP handler which in turn consults several microservices, they all just become functions. When you call such a function all you can do is wait for it to finish or blow up. What if you want to inspect where in the process it is at, maybe show a progress bar or some stats to the user? What if you want to be able to resume a failed process?

Then you need to model these things _as a process_. Luckily in Clojure we actually have great tools to do this, because in Clojure time is first class. We model time as successive values of immutable data.

A process has a notion of internal state. Inspired by [interceptors](http://pedestal.io/reference/interceptors) I tend to use a map for this called `context` or `ctx`.

A process happens step by step, so it has a notion of what to do next. I tend to model this as some kind of queue of simple EDN events, maps which have an event `:type` property.

I’ll describe two concrete use cases, with some clear differences but also clearly some commonalities. The first is the message queue the [kaocha-cljs](https://github.com/lambdaisland/kaocha-cljs) uses to handle messages coming from the ClojureScript pREPL. The second is code I wrote for [Nextjournal](https://nextjournal.com) to reify part of their data model.

To run ClojureScript tests Kaocha-cljs starts up a ClojureScript pREPL. A pREPL is a Process REPL, it returns data structures rather than just a stream of text, so it is more suitable for tooling.

The first thing Kaocha-cljs will do after starting this pREPL is open a websocket connection back to Kaocha, so it can send testing events back while tests are running, like `:fail`, `:pass`, `:begin-test-var`, etc.

Kaocha sends forms to the pREPL like `(require 'foo.bar-test)` and `(foo.bar-test/some-test)`. It then asynchronously starts receiving messages, either from the pREPL whenever something gets printed, when an exception occurs, when the function returns, or from websocket (test started, test failed, etc).

Based on these messages it needs to keep track of where it is in the process (has the pREPL booted? has the websocket connection been established? has the namespace loaded? has the test finished?), tally up test results, assign them to the right test in the result data, and decide to continue with the process, moving on to the next test or namespace, or time out.

To do this it puts all these messages on a `java.util.concurrent.LinkedBlockingQueue`. Then at each point where we need to wait for the ClojureScript side to do stuff it invokes this `queue-consumer`.
    
    
    (defn queue-consumer [{:keys [queue timeout handlers result]}]
      (let [poll #(.poll queue timeout TimeUnit/MILLISECONDS)
            handlers (merge default-queue-handlers handlers)]
        (loop [message (poll)
               context {}]
    
          (if (nil? message)
            (if-let [timeout-handler (get handlers :timeout)]
              (timeout-handler context)
              :timeout)
    
            (if-let [handler (get handlers (:type message))]
              (let [context (handler message context)]
                (if-let [result (result context)]
                  result
                  (recur (poll) context)))
              (recur (poll) context))))))
    

This takes messages of the queue one by one, and dispatches them to a dispatch function based on the message type. Each dispatch function receives the message and the context map, and returns a potentially updated context map.

A special `result` handler is called after each message is handled, if it returns truthy then the loop finishes and Kaocha can continue.

Here’s an example of how `queue-consumer` is used
    
    
    (queue-consumer {:queue queue
                     :timeout timeout
                     :handlers {:cljs/ret
                                (fn [{:keys [val] :as event} context]
                                  (when (map? val)
                                    (throw (ex-info "ClojureScript Error while loading Kaocha websocket client" event)))
                                  (cond-> context
                                    (= (str done) val)
                                    (assoc :eval-done? true)))
    
                                :cljs/out
                                (fn [{:keys [val]} context]
                                  (if (= "NODE_WS_NOT_FOUND\n" val)
                                    (throw (ex-info "Nodejs: require('ws') failed, make sure to 'npm install ws'."
                                             (merge limited-testable context)))
                                    (println val))
                                  context)
    
                                :timeout
                                (fn [{:keys [eval-done?] :as context}]
                                  (if eval-done?
                                    (throw (ex-info "Failed initializing ClojureScript runtime" (merge limited-testable context)))
                                    (throw (ex-info "Kaocha ClojureScript client failed connecting back." (merge limited-testable context)))))}
    
                     :result (fn [context]
                               (and (:eval-done? context)
                                    (:ws-client/ack? context)))})
    

This has turned out to be a surprisingly stable and reliable approach, that not only does what it says on the tin, but also manages to fail better, since Kaocha has ample context for error reporting.

The second use case was for Nextjournal. Nextjournal uses Datomic, but most of the nodes that make up an article are not directly represented as Datomic entities. Instead we store Transit encoded blobs, either of the full article document state, or of a change event that updates a previous version.

This has worked quite well, since the application usually requires a complete article document to work with, so building it up from individual entity nodes each time would be a lot of extra work, but the downside is that we can’t query for article nodes directly. So we decided to [reify](https://lambdaisland.com/blog/2019-12-01-advent-of-parens-1-clojure-vocab-reify)some of these, starting with “environments” (think: Docker images + a reference to the article that created them).

An article may have exported multiple versions of multiple environments over time, so we need to loop over the complete article’s history. These in turn may be based on environments from other articles, so to correctly link them to their parent we need to reify those environments first. But when running this locally I may not have all the articles that are referenced imported yet, so if any are missing they need to be imported from production.

So I came up with another coffee grinder approach. This time the queue is part of the context, similar to how it is in interceptors. I also split out side effects here, so action handlers return `:tx-data`, which is then transacted outside of the handler.

If I try to reify an environment but its parent is missing, then it will put the reification of the parent on the queue, and then enqueue itself again afterwards.
    
    
    (defn- run-queue!
      "Naive task runner. Takes a context map consisting of a `queue` of actions, and
      a datomic connection and initial db. The `handler` executes one action at a
      time, returning an updated context map.
    
      This action handler can manipulate the queue, i.e. prepend (lifo) or
      append (fifo) actions. If the returned `ctx` contains `:tx-data` then this
      will be transacted, and subsequent actions will receive the new database
      value."
      [ctx handler]
      (loop [{:keys [conn db queue] :as ctx} ctx]
        (let [[action & queue] queue]
          (if action
            (do
              (log/debug (first action) (second action))
              (let [ctx (handler action (assoc ctx :queue queue))
                    db  (if-let [tx-data (:tx-data ctx)]
                          (do
                            (log/trace :tx-data tx-data)
                            (:db-after @(datomic/transact conn tx-data)))
                          db)]
                (recur (dissoc (assoc ctx :db db) :tx-data))))
            ctx))))
    

The handler in this case is not a map of functions but a multimethod.
    
    
    (defmethod handle-action :import-article [[_ article-uuid] ctx]
      (let [import-data (article/article-imports [article-uuid]
                                                 {:host     "https://nextjournal.com"})]
        (-> ctx
            (add-tx-data import-data)
            (queue-prepend [[:reify-article-environments [:nextjournal/id article-uuid]]]))))
    

And invoking the whole thing
    
    
    (defn reify-article-environments
      "Given a sequence of article ids (:db/id), go over the history of each article,
      reifying all environments that are exported by runtimes, imported by
      docker-environment nodes, or that are referenced by transclusion. For
      non-production environments this will also import any article that is
      referenced by transclusion.
    
      See namespace level docs for some overarching considerations."
      [conn db-ids]
      (run-queue! {:conn  conn
                   :db    (datomic/db conn)
                   :queue (for [article-id db-ids]
                            [:reify-article-environments article-id])}
                  handle-action))
    

What I like about these approaches is the intermediate state that is built up progresses in clear increments. In this way it’s very different from keeping state in a mutable object with a bunch of fields. There’s a single point where the state progresses, so it is always consistent, and you have access to the previous and next values, so you can store them, diff them, save them to retry etc.

I also like how in this last example both enqueuing new actions, and transacting data into the database are accomplished by simple adding stuff to the context. When functions need to query Datomic they also find the database value in the context, so even though in a way these implementations do a lot of work, at their core they are pure functions, making them a joy to test. (with the exception of the article import).

Today has been a longer brain dump, but I’m looking forward to hear what people think, and maybe inspire a few of you to try out similar patterns when you find that what you are modeling is really more a process than a transaction. I’m opening [a thread on ClojureVerse](https://clojureverse.org/t/coffee-grinders/5218) for comments and discussion.

_Hi, my name is Arne (aka @plexus) and I consult companies and teams about application architecture, development process, tooling and testing. I collaborate with other talented people under the banner[Gaiwan](__GHOST_URL__/). If you like to have a chat about how we could help you with your project then please [get in touch!](__GHOST_URL__/p/contact)_

**[Comment on ClojureVerse](https://clojureverse.org/t/coffee-grinders/5218)**
