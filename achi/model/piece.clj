(ns whale.achi.model.piece
    (:refer-clojure :exclude nil) 
    (:require) 
    (:use whale.achi.model.player) 
    (:import) 
    (:load)
	(:gen-class))

;;; Public functions

(defn createPiece [id player] {:tag ::piece, :id id, :player player})

;;; Constants

(def -northPieces- (vec (for [i (range 4)] (createPiece i -northPlayer-))))
(def -southPieces- (vec (for [i (range 4)] (createPiece i -southPlayer-))))
(def -pieces- (vec (concat -northPieces- -southPieces-)))