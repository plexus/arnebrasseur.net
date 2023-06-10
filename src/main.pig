(module main
  (:import
    [process :from "node:process"]
    [path :from "node:path"]
    [fs :from "node:fs"]
    [md :from "markdown-it"]
    [yaml :from "js-yaml"]
    [jsdom :from "jsdom"]
    [dom :from piglet:dom]))

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

(defn new-document []
  (.-window.document (html->dom "")))

(def posts
  (let [dir "posts"]
    (for [path (fs:readdirSync dir)]
      (let [text (read-file dir path)
            preamble (re-find text "^---\n[\\s\\S]*?\n---\n" "m")]
        [(parse-yaml (.replaceAll preamble "---\n" ""))
         (parse-md (.substring text (count preamble)))]))))


(defn template []
  [:html
   [:head ..]])
