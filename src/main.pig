(module main
  (:import
    [process :from "node:process"]
    [path :from "node:path"]
    [fs :from "node:fs"]
    [md :from "markdown-it"]
    [yaml :from "js-yaml"]
    [jsdom :from "jsdom"]
    [temporal :from "@js-temporal/polyfill"]
    [dom :from piglet:dom]))

(set! js:Date.prototype.toTemporalInstant temporal:toTemporalInstant)

(defn ->pig [o]
  (cond
    ;; Convert plain JSON-like objects, leave constructed objects alone
    (and (object? o)
      (or
        (nil? (.-constructor o))
        (= js:Object (.-constructor o))))
    (reify
      DictLike
      (-keys [_] (js:Object.keys o))
      (-vals [_] (js:Object.values o))
      Associative
      (-assoc [_ k v]
        (if (or (string? k) (keyword? k))
          (->pig (ossoc o k v))
          (assoc
            (into {} (map (fn [[k v]] [k (->pig v)]))
              (js:Object.entries o))
            k v)))
      Lookup
      (-get [_ k] (->pig (oget o k)))
      (-get [_ k v] (->pig (oget o k v)))
      Conjable
      (-conj [this [k v]] (assoc this k v))
      Counted
      (-count [_] (.-length o))
      ;; FIXME: a call like `(-repr ...)` here would call this specific
      ;; implementation function, instead of the protocol method, thus causing
      ;; infinite recursion.
      ;; FIXME: we have a built-in, non-overridable package alias for piglet, we
      ;; should probably also add a default but overridable module alias to
      ;; `lang`
      Repr
      (-repr [this] (piglet:lang:-repr (into {} this)))
      Seqable
      (-seq [_]
        (seq (iterator (js:Object.entries o)))))

    (array? o)
    (lazy-seq (map ->pig o))

    :else
    o))

;; markdown

(defn read-file [& paths]
  (.toString (fs:readFileSync (apply path:join paths))))

(defn parse-yaml [yaml]
  (->pig (.load yaml:default yaml)))

(defn parse-md [md]
  (.render (md:default.) md))

;; dom

(defn html->dom [html]
  (jsdom:JSDOM. html))

(defn html->fragment [html]
  (.fragment jsdom:JSDOM html))

(defn new-document []
  (.-window.document (html->dom "")))

(def posts
  (let [dir "posts"]
    (for [path (fs:readdirSync dir)]
      (let [text (read-file dir path)
            preamble (re-find (js:RegExp. "^---\n[\\s\\S]*?\n---\n" "m") text)]
        [(assoc
           (parse-yaml (.replaceAll preamble "---\n" ""))
           :slug (.replace path ".md" ""))
         (html->fragment (parse-md (.substring text (count preamble))))]))))

(defn template [opts]
  (let [title (:title opts)
        date (.toTemporalInstant (:date opts))]
    [:html
     [:head
      [:meta {:charset "UTF-8"}]
      [:meta {:content "width=device-width, initial-scale=1" :name "viewport"}]
      [:link {:href " https://cdn.jsdelivr.net/npm/@picocss/pico@1.5.10/css/pico.min.css " :rel "stylesheet"}]
      #_[:link {:rel "stylesheet" :href "https://cdn.simplecss.org/simple.min.css"}]
      #_[:link {:rel "stylesheet" :href "https://unpkg.com/mvp.css@1.12/mvp.css"}]
      [:link {:href "http://devblog.arnebrasseur.net/feed.xml" :rel "alternate" :title "Plexus Devblog" :type "application/atom+xml"}]
      [:title title]]
     [:body
      [:header
       [:nav
        [:a {:href "/"} [:strong "Arne's Personal Blog"]]
        ]]
      [:main
       [:article
        [:header
         [:h1 title]
         "Posted by Arne on " (.toLocaleString date "en-GB")]]]
      [:footer]]]))

(defn atom-feed [posts]
  (let [date (map :date posts)]
    (str
      "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
      (dom:outer-html
        (dom:dom
          (new-document)
          [:feed {:xmlns "http://www.w3.org/2005/Atom"}
           [:title "Arne Brassseur"]
           [:link {:href "https://arnebrasseur.net/blog"}]
           [:updated (str date)]
           [:author [:name "Arne Brasseur"]]
           [:id "https://arnebrasseur.net/blog"]
           (for [[opts html] posts]
             [:entry
              [:title (:title opts)]
              [:link {:href (str "https://arnebrasseur.net/blog/" (:slug opts))}]
              [:id "urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a"]
              [:updated "2003-12-13T18:30:02Z"]
              [:updated "2003-12-13T18:30:02Z"]
              [:summary "Some text."]])]))))
  )

(atom-feed posts)
(dom:dom
  (new-document)
  (for [[opts html] posts]
    [:entry
     [:title (:title opts)]
     [:link {:href (str "https://arnebrasseur.net/blog/" (:slug posts))}]
     [:id "urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a"]
     [:updated "2003-12-13T18:30:02Z"]
     [:updated "2003-12-13T18:30:02Z"]
     [:summary "Some text."]]))

(vector? (for [x (range 2)]
           x))

(doseq [[opts fragment] posts]
  (let [tmpl-dom (dom:dom (new-document) (template opts))]
    (dom:append-child (dom:query tmpl-dom "article")
      (.cloneNode fragment true))
    (spit (str "out/" (.-slug opts) ".html")
      (str "<!DOCTYPE>\n"
        (dom:outer-html tmpl-dom)))))
