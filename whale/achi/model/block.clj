(ns whale.achi.model.block
    (:refer-clojure :exclude nil) 
    (:require) 
    (:use) 
    (:import) 
    (:load)
	(:gen-class))

;;; Public constants

(def -northHome- :northHome)
(def -southHome- :southHome)
(def -board- :board)

;;; Public functions

(defn homeBlock? [block] (or (= block -northHome-) (= block -southHome-)))
