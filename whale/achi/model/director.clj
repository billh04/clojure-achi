(ns whale.achi.model.director
    (:refer-clojure :exclude nil) 
    (:require) 
    (:use whale.achi.model.player
		  whale.achi.model.directorType) 
    (:import) 
    (:load)
	(:gen-class))

;;; Public functions

(defn createDirector [player directorType] {:tag ::director, :player player, :directorType directorType})