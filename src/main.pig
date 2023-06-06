(module main
  (:import
    [process :from "node:process"]
    [path :from "node:path"]
    [fs :from "node:fs"]
    [md :from "markdown-it"]
    [yaml :from "js-yaml"]
    [jsdom :from "jsdom"]
    [dom :from piglet:dom]
    ))

(defn read-file [& paths]
  (.toString (fs:readFileSync (apply path:join paths))))

(defn html->dom [html]
  (jsdom:JSDOM. html))

(defn parse-yaml [yaml]
  (.load yaml:default yaml))

(defn parse-md [md]
  (.render (md:default.) md))

(def posts
  (let [dir "posts"]
    (for [path (fs:readdirSync dir)]
      (let [text (read-file dir path)
            preamble (first (.match text (js:RegExp. "^---\n[\\s\\S]*?\n---\n" "m")))]
        [(parse-yaml (.replaceAll preamble "---\n" ""))
         (parse-md (.substring text (count preamble)))]))))


(.match "---\ntitle: Emergence for Developers\ndate: 2013-03-17\n---\n" (js:RegExp. "m"))


(read-string "(def posts\n  (let [dir \"posts\"]\n    (for [path (fs:readdirSync dir)]\n      (let [text (read-file dir path)\n            preamble (.match text (js:RegExp. \"^---\\n.*\\n---\\n\" \"m\"))\n            ]\n        text #_(when preamble [(parse-yaml preamble)]))\n      )))")



(await (reduce (fn ^:async foo [acc x] (+ x (await acc))) 0 (range 10)))

(typeof x)

(meta (fn* ^:async foo []))

(await (slurp "posts/2013-03-17-emergence.md"))

*current-location*
(set! (.-imports *current-module*) [])
(.-vars (find-module 'md))

(jsdom:JSDOM. "<a>test</a>")


(do
  (set! dom:*window* (.-window (jsdom:JSDOM. "<a href=\"/foo/bar\">test</a>")))
  nil)

(defn foo ([] 1) ([x] 2))

(dom:query (dom:doc) "a")

(set! js:global.document (.-document (.-window (jsdom:JSDOM. ))))

(
  .-prototype
  (.-constructor (.querySelector js:document "a")))

(.-Node
  (.-window (jsdom:JSDOM. "<a>test</a>")))

(path:join "/foo/bar" ".." ".." "..")

(process:cwd)
