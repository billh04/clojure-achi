(ns whale.achi.achiDebug 
    (:refer-clojure :exclude nil) 
    (:require) 
    (:use whale.achi.model.world
		  whale.achi.model.player
		  whale.achi.model.place) 
    (:import)
    (:load)
	(:gen-class))

(defn displayWorld [world] ;; wch: used for debugging
  (dorun (for [boardPlace -boardPlaces-]
		   (let [piece (getPieceAtPlace world boardPlace)
				 player (:player piece)]
			 (print (cond (= player -northPlayer-) "X" (= player -southPlayer-) "O" :else "."))))))
