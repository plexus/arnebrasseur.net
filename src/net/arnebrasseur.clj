(ns net.arnebrasseur
  (:require
   [babashka.http-server :as bb-server]
   [clj-yaml.core :as yaml]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [clojure.walk :as walk]
   [lambdaisland.hiccup :as hiccup]
   [lambdaisland.kramdown :as kramdown]
   [lambdaisland.ornament :as o]
   [net.cgrand.enlive-html :as enlive]
   [org.httpkit.server :as server])
  (:import
   (java.io StringReader)))

(require 'net.arnebrasseur.styles)

(def site-origin "https://arnebrasseur.net")

(defn iso-date [^java.util.Date jud]
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
  (let [text     (slurp file)
        preamble (re-find #"^---\n[\s\S]*?\n---\n" text)]
    (if-not preamble
      (println "WARN: no valid preamble in" (str file))
      (let [parsed (yaml/parse-string (str/replace preamble "---\n" ""))]
        (update
         (merge
          {:md      (subs text (count preamble))
           :content (parse-markdown (subs text (count preamble)))
           :slug    (or (:slug parsed)
                        (str/replace (.getName (io/file file)) #".md$" ""))}
          parsed)
         :date
         (fn [d]
           (if (string? d)
             (clojure.instant/read-instant-date d)
             d)))))))

(def language-filters
  {"inline-html"
   (fn [[_ o]]
     (into [::hiccup/unsafe-html] (drop 2 o)))
   "piglet-interactive"
   (fn [[_ o]]
     (into [:piglet-interactive-snippet] (drop 2 o)))
   "python"
   (fn [[_ o]]
     [:pre
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
            (drop 2 o))])})

(defn handle-language-filters [hiccup]
  (walk/postwalk
   (fn [o]
     (if (and (vector? o)
              (= :pre (first o))
              (vector? (second o))
              (= :code (first (second o)))
              (:class (second (second o))))
       (if-let [[_ lang] (re-find #"language-(.*)" (:class (second (second o))))]
         (if-let [f (get language-filters lang)]
           (f o)
           o)
         o)
       o))
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

(defn layout [{:keys [title]} content & [footer]]
  [:html
   [:head
    [:title
     (when title (str title " | "))
     "Arne Brasseur . net"]
    [:meta {:charset "UTF-8"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
    [:link {:rel "stylesheet" :href "/styles.css"}]
    [:link {:rel "stylesheet" :href "assets/fonts/playfair/playfair.css"}]
    ;; [:link {:rel "stylesheet" :href "assets/fonts/sourceserif4/sourceserif4.css"}]
    [:link {:rel "stylesheet" :href "assets/fonts/atkinson/atkinson.css"}]]
   [:body
    [:main
     content]
    (when footer
      footer)]])

(defn blog-post [{:keys [title date slug content]}]
  [:<>
   [:article
    [:header
     [:h1 title]
     [:div.byline "Published " (iso-date date) " by " [:a {:href "/"} "Arne Brasseur"] ". " [:a {:href (str site-origin "/" slug ".html")} "(permalink)"]]]
    [:<> content]]])

(defn hiccup-heading? [e]
  (and (vector? e) (#{:h1 :h2 :h3 :h4 :h5} (first e))))

(defn heading-level [e]
  (parse-long (str (second (name (first e))))))

(defn normalise-heading-levels
  "Kramdown seems to have a weird issue where some h2 are being parsed as h4, and
  our markdown (especially old imports) can be inconsistent. Make sure the
  smallest heading level is h2, with h1 reserved for the title."
  [content]
  (let [min-heading
        (min 
         (keep (fn [e] (when (hiccup-heading? e)
                         (heading-level e)))
               content))
        delta (- (or (first min-heading) 2) 2)]
    (mapv (fn [e]
            (if (hiccup-heading? e)
              (assoc e 0 (keyword (str "h" (- (heading-level e) delta))))
              e))
          content)))

(defn read-posts []
  (->> "site/posts"
       io/file
       file-seq
       (filter #(.isFile (io/file %)))
       (keep slurp-md-with-preamble)
       (map handle-language-filters)
       (map #(update % :content normalise-heading-levels))
       (sort-by :date)
       reverse))

(defn read-pages []
  (->> "site"
       io/file
       .listFiles
       (filter #(.isFile (io/file %)))
       (filter #(.endsWith (str %) ".md"))
       (remove #(= "index.md" (.getName %)))
       (keep slurp-md-with-preamble)
       (map handle-language-filters)
       (map inline-content)
       reverse))

(defn index [posts]
  [:<>
   (parse-markdown (slurp "site/index.md"))
   [:h2 "Posts"]
   [:ul
    (for [{:keys [title draft slug date]} posts
          :when (not draft)]
      [:li [:a {:href (str "/" slug ".html")} (iso-date date) " — " title ]])]])

(defn page [{:keys [title date slug content]}]
  [:<>
   [:article
    [:header
     [:h1 title]]
    [:<> content]]])

(def footer
  [:footer
   [:section
    [:h2 [:a {:href "/"} "ArneBrasseur.net"]]
    [:p
     "Arne is a software developer and technical leader, public speaker, author, conference organiser, and digital creative. "
     "You can connect with him " [:a {:href "https://toot.cat/@plexus"} "on the fediverse"]
     ", on " [:a {:href "https://bsky.app/profile/plexus.eurosky.social"} "Bluesky"]
     ", or on " [:a {:href "https://www.linkedin.com/in/arnebrasseur/"} "LinkedIn"] "."]]])

(defn paths []
  (let [posts (read-posts)
        pages (read-pages)]
    (-> {"/index.html" [layout {} [index posts] footer]
         "/styles.css" {:status 200
                        :headers {"content-type" "text/css"}
                        :body (o/defined-styles {:compress? false})}}
        (into (map (fn [{:keys [slug] :as post}]
                     [(str "/" (:slug post) ".html")
                      [layout post
                       [blog-post post]
                       footer]]))
              posts)
        (into (map (fn [{:keys [slug] :as p}]
                     [(str "/" (:slug p) ".html")
                      [layout p
                       [page p]
                       footer]]))
              pages))))

(defn response [o]
  (cond
    (map? o) o
    (string? o) {:status 200, :body o}
    (vector? o) {:status 200, :headers {"content-type" "text/html"} :body (hiccup/render o)}))

(defn render []
  (io/make-parents "out/index.html")
  (doseq [[path res] (paths)]
    (spit (str "out" path ) (:body (response res)))))

(defn handler [opts]
  (let [dirs (map #(#'bb-server/file-router % nil)
                  ["./public"])]
    (fn [{:keys [uri request-method] :as req}]
      (println " " (str/upper-case (name request-method)) uri)
      (let [paths (paths)
            uri (if (= "/" uri) "/index.html" uri)]
        (if-let [res (get paths uri)]
          (response res)
          (let [req (update req :headers dissoc "range")]
            (some (fn [d]
                    (let [res (d req)]
                      (when (not= 404 (:status res))
                        #_(let [uri (if (= "/" uri) "/index.html" uri)
                                target  (io/file "out" (subs uri 1))]
                            (.mkdirs (io/file (.getParent target)))
                            (spit target (slurp (:body res))))
                        res)))
                  dirs)))))))

(defn run-server [opts]
  (let [port (:port opts 3800)]
    (println "Starting server on port" port)
    (server/run-server (fn [req] ((handler opts) req)) {:port port})))

(render)

(comment
  (run-server {})

  (let [p (inline-content (slurp-md-with-preamble "/home/arne/repos/arnebrasseur.net/site/coderdojo_pingpong.md"))]
    (hiccup/render     (layout p   (page p))))

  )
