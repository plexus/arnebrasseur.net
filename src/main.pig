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

;; markdown

(defn read-file [& paths]
  (.toString (fs:readFileSync (apply path:join paths))))

(defn parse-yaml [yaml]
  (.load yaml:default yaml))

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
        [(oset (parse-yaml (.replaceAll preamble "---\n" ""))
           :slug (.replace path ".md" ""))
         (html->fragment (parse-md (.substring text (count preamble))))]))))

(defn template [opts]
  (let [title (.-title opts)
        date (.toTemporalInstant (.-date opts))]
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

(doseq [[opts fragment] posts]
  (def xxx opts)
  (let [tmpl-dom (dom:dom (new-document) (template opts))]
    (dom:append-child (dom:query tmpl-dom "article")
      (.cloneNode fragment true))
    (spit (str "out/" (.-slug opts) ".html")
      (str "<!DOCTYPE>\n"
        (dom:outer-html tmpl-dom)))))
