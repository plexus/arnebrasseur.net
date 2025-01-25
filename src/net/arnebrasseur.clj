(ns net.arnebrasseur
  (:require
   [clj-yaml.core :as yaml]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [lambdaisland.hiccup :as hiccup]
   [lambdaisland.kramdown :as kramdown]
   [lambdaisland.ornament :as o]
   [net.cgrand.enlive-html :as enlive])
  (:import
   (java.io StringReader)))

(def site-origin "https://arnebrasseur.net")

(defn iso-date [jud]
  (subs (str (.toInstant jud)) 0 10))

(defn xml->hiccup [node]
  (cond
    (string? node)
    node

    (seq? node)
    (into [:<>] (map xml->hiccup node))

    (map? node)
    (into (cond-> [(:tag node)]
            (:attrs node)
            (conj (:attrs node)))
          (map xml->hiccup (:content node)))))

(defn parse-markdown [contents]
  (-> contents
      kramdown/parse-gfm
      StringReader.
      enlive/html-resource
      (enlive/select [:body])
      first
      :content
      xml->hiccup))

(defn slurp-md-with-preamble [file]
  (let [text (slurp file)
        preamble (re-find #"^---\n[\s\S]*?\n---\n" text)]
    (merge
     {:content (parse-markdown (subs text (count preamble)))
      :slug (str/replace (.getName (io/file file)) #".md$" "")}
     (yaml/parse-string (str/replace preamble "---\n" ""))
     )))

(o/defprop --red-violet "#b9077e")
(o/defprop --pink-lace "#fddef4")
(o/defprop --chateau-green "#1ea34c")
(o/defprop --mirage "#0c182e")
(o/defprop --arapawa "#01095E")
(o/defprop --emerald "#38D07B")
(o/defprop --toast "#8B6253")
(o/defprop --laser-lemon "#F3F976")
(o/defprop --green-onion "#78ff94")

(o/defprop --primary --arapawa)
(o/defprop --secondary --green-onion)

(o/defrules styles
  [:html
   {:font-size "18pt"}
   [:at-media {:min-width "60rem"}
    {:font-size "22pt"}]]
  [:body
   {:background-color --secondary
    :color --primary
    :max-width "48em"
    :margin "0 auto"}]
  [:main {:padding "0 1em"}]
  [#{:h1 :h2 :h3 :h4 :h5}
   {:font-family "'Ostrich Sans'"
    :font-weight "400"}]
  [:h1 {:font-size "3rem"}])

(defn layout [content]
  [:html
   [:head
    [:title "Arne Brasseur . net"]
    [:meta {:charset "UTF-8"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
    [:style (o/defined-styles)]
    [:link {:rel "stylesheet" :href "assets/fonts/ostrich-sans/ostrich-sans.css"}]]
   [:body
    [:main
     content]]])

(defn blog-post [{:keys [title date slug content]}]
  [:<>
   [:article
    [:h1 title]
    [:aside "Published " (iso-date date) " by " [:a {:href "/"} "Arne Brasseur"] ". " [:a {:href (str site-origin "/" slug ".html")} "(permalink)"]]
    [:main content]]])

(defn read-posts []
  (->> "site/posts"
       io/file
       file-seq
       (filter #(.isFile (io/file %)))
       (map slurp-md-with-preamble)
       (sort-by :date)
       reverse))

(defn index [posts]
  [:<>
   (parse-markdown (slurp "site/index.md"))
   [:h2 "Posts"]
   [:ul
    (for [{:keys [title slug date]} posts]
      [:li [:a {:href (str "/" slug ".html")} (iso-date date) " — " title ]])]
   ])

(defn render []
  (let [posts (read-posts)]
    (io/make-parents "out/index.html")
    (spit "out/index.html" (hiccup/render [layout [index posts]]))
    (doseq [post posts]
      (spit (str "out/" (:slug post) ".html") (hiccup/render [layout [blog-post post]]))
      ))
  )

(render)
