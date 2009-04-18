(ns whale.achi.model.player 
    (:refer-clojure :exclude nil) 
    (:require) 
    (:use) 
    (:import) 
    (:load)
	(:gen-class))

;;; Public constants

(def -northPlayer- :northPlayer)
(def -southPlayer- :southPlayer)

;;; Public functions

(defn opponentOf [player] (if (= player -northPlayer-) -southPlayer- -northPlayer-))