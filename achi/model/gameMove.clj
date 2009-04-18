(ns whale.achi.model.gameMove 
    (:refer-clojure :exclude nil) 
    (:require) 
    (:use whale.achi.model.place) 
    (:import) 
    (:load)
	(:gen-class))

(defn createGameMove [fromPlace toPlace] {
  :tag ::gameMove
  :fromPlace fromPlace,
  :toPlace toPlace})

(defn isGameMove [gameMove] (= (:tag gameMove) ::gameMove))