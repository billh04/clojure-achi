(let
	[userdir (. System getProperty "user.dir")
	 rootdir (apply str (doall (drop-last (count "/whale/achi") userdir)))]
  (add-classpath (str "file://" rootdir "/")))

(ns project 
    (:refer-clojure :exclude nil)
    (:require)
    (:use)
    (:import)
    (:load))

(comment ;; run test
 (load-file "main.clj")
 (whale.achi.main/-main)
)