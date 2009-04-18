(ns whale.achi.botThinker.botThinker
    (:refer-clojure :exclude nil) 
    (:require) 
    (:use
        whale.achi.botThinker.positionEvaluation
        whale.achi.model.world
        whale.achi.model.gameMove) 
    (:import) 
    (:load)
	(:gen-class))

(defn decideMove [world]
  (let
	  [numberOfPiecesOnBoard (getNumberOfPiecesOnBoard world)
	   depth (cond (< numberOfPiecesOnBoard 3) 2
				   (< numberOfPiecesOnBoard 4) 8
				   :else (max (- 15 numberOfPiecesOnBoard) 7)) ;; wch: is 7 right??
	   bestMoves (getBestMoves world depth)
	   bestMovesCount (count bestMoves)
	   randomIndex (rand-int bestMovesCount)]
	(nth bestMoves randomIndex)))