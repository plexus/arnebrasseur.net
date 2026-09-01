---
title: "Advent 2019 part 13, Datomic Test Factories"
slug: "advent-of-parens-13-datomic-test-factories"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2019-12-13T10:13:23.000Z"
updated_at: "2026-05-16T13:41:54.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-15 08:10"]
---

_This post is part of[Advent of Parens 2019](https://lambdaisland.com/blog/2019-11-25-advent-of-parens), my attempt to publish one blog post a day during the 24 days of the advent._

When I started consulting for [Nextjournal](https://nextjournal.com/) I helped them out a lot with tooling and testing. Their data model is fairly complex, which made it hard to do setup in tests. I created a factory based approach for them, which has served the team well ever since.

First some preliminaries. At Nextjournal we’re big fans of Datomic, and so naturally we have a Datomic connection as part of the Integrant system map.
    
    
    {:com.nextjournal.journal.system/datomic-conn
     #profile
     {:default {:uri #or[#env JOURNAL_DATOMIC_URI "datomic:mem://nextjournal_dev"]}
      :prod    {:uri #or[#env JOURNAL_DATOMIC_URI #secret "journal/datomic/uri"]
                :com.nextjournal.journal.system.vault/service #integrant/ref[:com.nextjournal.journal.system.vault/service]}
      :test    {:uri        "datomic:mem://nextjournal_test"
                :delete-db? true}}}
    

For testing we use a dynamic var `*test-system*`. You could just set up a system and pass it around, but in this case a dynamic var is really convenient, since this allows you to set it up in a fixture, and test helper functions can access the system directly.
    
    
    (ns ns com.nextjournal.journal.test-system)
    
    (def ^:dynamic *test-system*)
    
    ;; REPL use only
    (defn start! [] ,,,)
    (defn stop! [] ,,,)
    
    (defn wrap-system [test]
      (binding [*test-system* (ig/init! config)]
        (try
          (test)
          (finally
            (ig/halt! *test-system*)))))
    
    ;; in a test namespace:
    ;; (use-fixtures :each test-system/wrap-system)
    

With that we can make helpers to quickly get the Datomic connection, get a database value, or do a query. This already takes a lot of clutter out of tests.
    
    
    (defn conn []
      (:com.nextjournal.journal.system/datomic-conn *test-system*))
    
    (defn db []
      (datomic/db (conn)))
    
    (defn q [query & args]
      (apply datomic/q query (db) args))
    

To get test data into the database we have this handy `transact!` utility. It transacts whatever transaction data you give it, and returns a map with resolved temp-ids. If it finds any temp-ids that end in `"-id"`, then it will also return a `(datomic/entity ,,,)` for that id.
    
    
    (defn transact*!
      "Like `transact!` but takes the connection explicitly"
      [conn & tx-datas]
      (let [tx-data  (vec (mapcat #(if (map? %) [%] %) tx-datas))
            report   @(datomic/transact conn tx-data)
            db-after (:db-after report)
            tempids  (:tempids report)
            ids      (into {} (keep (fn [{id :db/id}]
                                      (when (string? id)
                                        [(keyword id)
                                         (datomic/resolve-tempid db-after tempids id)]))
                                    tx-data))]
        (reduce-kv (fn [ids kid id]
                     (let [kidn (name kid)]
                       (if (str/ends-with? kidn "-id")
                         (let [k (keyword (str/replace kidn #"-id$" ""))
                               e (datomic/entity db-after id)]
                           (assoc ids k e))
                         ids)))
                   ids
                   ids)))
    
    (defn transact!
      "Transact data and resolve temp-ids.
    
      Calls datomic/transact on the current connection in *system*, and :db/id in
      the input that are strings are resolved as tempids, keywordified, and returned
      as a map. On top of that any keys ending in \"-id\" are also resolved as
      entities, dropping the -id suffix.
    
      Each argument can be either a map or a vector of maps.
    
      See also: defactory.
      "
      [& tx-datas]
      (apply transact*! (conn) tx-datas))
    

So for example say I’m creating an article and a person entity for use in my test, this will look something like this.
    
    
    (let [{:keys [article article-id
                  person person-id]}
          (transact! {:db/id "article-id" :article/change 1234}
                     {:db/id "person-id" :person/email "foo@bar.com"})]
      article    ;;=> {:db/id 158953 ,,,}
      article-id ;;=> 158953
      person     ;;=> {:db/id 158954 ,,,}
      article-id ;;=> 158954
    
      )
    

I can destructure whatever I need out of that. Most of the time it’s most convenient to work with the entity maps, but if you need the ids instead to pass on to another function then you can access those directly.

With all of that set up it’s time for the grand finale: `defactory`!
    
    
    (defmacro defactory
      "Define a datomic factory helper. This should look like a function which takes a
      single argument (an options map), and returns datomic transaction data, either
      a single map or a vector of maps.
    
      Use strings for temp-ids if possible.
    
      This will create a regular function that just returns that data, but also a
      method ending in a bang which will transact the tx-data against the
      *test-system*, and return a map of resolved temp-ids."
      [name & [a1 a2 & rst :as args]]
      (let [[docstring arglist body]
            (if (string? a1)
              [a1 a2 rst]
              ["Factory function" a1 (next args)])]
        `(do
           (defn ~name ~docstring [& ~arglist]
             (let [res# ~@body]
               (if (map? res#)
                 [(dissoc res# :suffix)]
                 (mapv #(dissoc % :suffix) res#))))
    
           (defn ~(symbol (str name "!")) ~docstring
             [& [opts#]]
             (let [conn# (:datomic/conn opts# (conn))
                   opts# (dissoc opts# :datomic/conn)]
               (transact*! conn# (~name opts#)))))))
    

This is already a hairy macro, I guess you don’t always want to see how the sausage gets made, but in usage it’s really nice.
    
    
    (ns com.nextjournal.journal.test-factories)
    
    (defactory profile
      "Creates a profile entity with random handle. Options are merged in directly.
      Provides a \"profile-id\" tempid."
      [opts]
      (merge #:profile
             {:db/id  "profile-id"
              :handle (rand-username)
              :name   "Jonny Zimmerman"}
             opts))
    
    (defactory person
      "Creates a person entity. Options are merged in directly. The
      `:person/password` key is treated special, it is converted to a password
      digest and stored as such. Expects a \"profile-id\" temp-id in the same
      transaction.
      Provides a \"person-id\" tempid."
      [opts]
      (merge #:person
             {:db/id   "person-id"
              :profile "profile-id"
              :email   (rand-email)}
             (cond-> opts
               (:person/password opts)
               (-> (dissoc :person/password)
                   (assoc :person/password-digest
                     (password/encrypt (:person/password opts)))))))
    

This has created four functions: `profile`, `profile!`, `person`, and `person!`. The versions without a bang just return transaction data.
    
    
    (profile)
    ;;=> {:db/id "person-id", :profile/handle "jonny7", :profile/name "Jonny Zimmerman"}
    
    (profile {:profile/name "Arne Brasseur" :profile/website "https://lambdaisland.com"})
    ;;=> {:db/id "person-id", :profile/handle "jonny7", :profile/name "Arne Brasseur" :profile/website "https://lambdaisland.com"}
    
    (let [{:keys [person]} (transact! (person) (profile))]
      (-> person :person/profile :profile/handle) ;;=> "marcus123"
      )
    

The version with a bang immediately call `transact!`.
    
    
    (let [{:keys [profile]} (profile!)]
      (profile :profile/handle) ;;=> "marcus123"
      )
    

We have a bit more logic in the factories to make it easy to create multiple entities of the same type in a single transaction, in that case they will get temp-ids like `article-1-id`, so you can destructure them as `{:keys [article-1 article-2]}`.

Is this API perfect? Far from it, there are definitely some things I would reconsider if I did a second iteration of this. It’s also not the kind of code I would write for regular, non-test use. It already starts to smell a little of “magic”, the kind that as a recovering Rubyist I have learned to avoid. Pulling up database connections from behind the covers, checking for the type of arguments to have a more flexible interface, transacting things “automatically”… these are things Clojure has taught me to avoid, generally.

But in this case the magic does serve a higher purpose, it is taking the friction out of writing unit tests, and anything that helps and encourages people to test their code is a win in my book. And because it takes a lot of clutter and boilerplate out of the tests they start to better convey intention. Besides being there to test stuff tests also act as a form of documentation. They show examples of how APIs can be used, and in the case of a failure they point at the scenario that is causing trouble. So all in all we think these factories are a win.

_Hi, my name is Arne (aka @plexus) and I consult companies and teams about application architecture, development process, tooling and testing. I collaborate with other talented people under the banner[Gaiwan](__GHOST_URL__/). If you like to have a chat about how we could help you with your project then please [get in touch!](__GHOST_URL__/p/contact)_
