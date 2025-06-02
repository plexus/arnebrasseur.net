(ns net.arnebrasseur
  (:require
   [clj-yaml.core :as yaml]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [clojure.walk :as walk]
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
    :margin "0 auto"
    :font-family "sans-serif"}]
  [:aside {:font-size "80%"}]
  [:article {:padding "0 0.5em"}]
  [#{:h1 :h2 :h3 :h4 :h5}
   {:font-family "'Ostrich Sans'"
    :font-weight "400"}]
  [:h1 {:font-size "3rem"}]

  [:pre
   {#_#_:border "2px solid #ccc"
    :color "#222"
    :background-color "#eee"
    :padding "1rem"
    :margin "1rem 0"
    :border-radius "0.1rem"
    :box-shadow "rgba(0, 0, 0, 0.16) 0px 1px 4px"}]

  [:blockquote
   {:border "1px solid black"
    :border-left "3px solid black"
    :border-top "3px solid black"
    :padding "0 1rem"
    :margin "1rem 0"
    :border-radius "0.2rem"}
   [:h2 {:margin-bottom 0}]
   ]

  [:.invisible-whitespace
   {:display "inline-block"
    :width 0}]

  [:.visible-whitespace
   {:opacity "0.2"
    ;;:user-select "none"
    }]
  [".visible-whitespace::before"
   {:content "attr(data-content)"}]
  )

(defn python-visible-whitespace [hiccup]
  (walk/postwalk
   (fn [o]
     (if (and (vector? o)
              (= :code (first o))
              (= "language-python" (:class (second o))))
       (into (vec (take 2 o))
             (comp
              (mapcat #(str/split % #"\R"))
              (map (fn [line]
                     (if-let [ws (re-find #"^ +" line)]
                       [:<>
                        [:span.visible-whitespace {:data-content
                                                   (apply str (repeat (Math/floor (/ (count ws) 4)) "»   "))}]
                        [:span.invisible-whitespace ws]
                        (str/replace line #"^ +" "") "\n"]
                       (str line "\n")))))
             (drop 2 o))
       o
       ))
   hiccup))

(defn inline-content [hiccup]
  (walk/postwalk
   (fn [o]
     (if-let [f (and (vector? o)
                     (= :div (first o))
                     (:inline (second o)))]
       [::hiccup/unsafe-html (slurp (io/file "site" f))]
       o))
   hiccup))

(defn layout [{:keys [title]} content]
  [:html
   [:head
    [:title
     (when title (str title " | "))
     "Arne Brasseur . net"]
    [:meta {:charset "UTF-8"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
    [:style (o/defined-styles)]
    [:style {:media "print"}
     ]
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

(defn read-pages []
  (->> "site"
       io/file
       .listFiles
       (filter #(.isFile (io/file %)))
       (filter #(.endsWith (str %) ".md"))
       (remove #(= "index.md" (.getName %)))
       (map slurp-md-with-preamble)
       (map python-visible-whitespace)
       (map inline-content)
       reverse))

(defn index [posts]
  [:<>
   (parse-markdown (slurp "site/index.md"))
   [:h2 "Posts"]
   [:ul
    (for [{:keys [title draft slug date]} posts
          :when (not draft)]
      [:li [:a {:href (str "/" slug ".html")} (iso-date date) " — " title ]])]
   ])

(defn page [{:keys [title date slug content]}]
  [:<>
   [:article
    [:h1 title]
    [:main content]]])

(defn render []
  (let [posts (read-posts)
        pages (read-pages)]
    (io/make-parents "out/index.html")
    (spit "out/index.html" (hiccup/render [layout {} [index posts]]))
    (doseq [post posts]
      (spit (str "out/" (:slug post) ".html") (hiccup/render [layout post [blog-post post]])))
    (doseq [p pages]
      (spit (str "out/" (:slug p) ".html") (hiccup/render [layout p [page p]])))))

(render)

(comment
  (let [p (inline-content (slurp-md-with-preamble "/home/arne/repos/arnebrasseur.net/site/coderdojo_pingpong.md"))]
    (hiccup/render     (layout p   (page p))))

  )
