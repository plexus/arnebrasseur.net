(ns user)

(defn go []
  ((requiring-resolve 'clooj.main/startup))
  #_((requiring-resolve 'clooj.project/add-project) @@(requiring-resolve 'clooj.main/current-app) (.getAbsolutePath (java.io.File. ".")))
  #_((requiring-resolve 'clooj.project/update-project-tree) (:docs-tree @@(requiring-resolve 'clooj.main/current-app)))
  )
