(ns user)

(defmacro jit
  [sym]
  `(requiring-resolve '~sym))

(defn go []
  )

(defn browse []
  ((jit clojure.java.browse/browse-url) "http://localhost:3800"))
