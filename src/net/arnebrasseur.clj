(ns net.arnebrasseur
  (:require
   [clojure.java.io :as io]
   [lambdaisland.hiccup :as hiccup]
   [lambdaisland.kramdown :as kramdown]
   [lambdaisland.ornament :as o]
   [net.cgrand.enlive-html :as enlive])
  (:import
   (java.io StringReader)))

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

(o/defprop --red-violet "#b9077e")
(o/defprop --pink-lace "#fddef4")
(o/defprop --chateau-green "#1ea34c")
(o/defprop --mirage "#0c182e")
(o/defprop --arapawa "#01095E")
(o/defprop --emerald "#38D07B")

(o/defprop --primary --arapawa)
(o/defprop --secondary --emerald)

(o/defrules styles
  [:html
   {:font-size "22pt"}]
  [:body
   {:background-color --secondary
    :color --primary
    :max-width "48em"
    :margin "0 auto"}]
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
    content]])

(defn render []
  (io/make-parents "out/index.html")
  (spit "out/index.html" (hiccup/render [layout (parse-markdown (slurp "index.md"))])))

(render)
