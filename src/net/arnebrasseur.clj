(ns net.arnebrasseur
  (:require
   [lambdaisland.ornament :as o]
   [lambdaisland.hiccup :as hiccup]
   [clojure.string :as str]))

(o/defprop --red-violet "#b9077e")
(o/defprop --pink-lace "#fddef4")
(o/defprop --chateau-green "#1ea34c")
(o/defprop --mirage "#0c182e")
(o/defprop --arapawa "#01095E")
(o/defprop --emerald "#38D07B")

(o/defprop --primary --emerald)
(o/defprop --secondary --arapawa)

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


(spit "out/index.html"
      (hiccup/render
[:html
 [:head
  [:title "AB after hours"]
  [:meta {:charset "UTF-8"}]
  [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
  [:style (o/defined-styles)]
  [:link {:rel "stylesheet" :href "assets/fonts/ostrich-sans/ostrich-sans.css"}]]
 [:body
  [:div.net_arnebrasseur__front_page
   [:h1 "AB After Hours"]
   [:p "Welcome to my corner of the web."]]]]
))