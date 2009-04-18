(ns whale.achi.botThinker.positionEvaluation 
    (:refer-clojure :exclude nil) 
    (:require) 
    (:use  whale.achi.model.world
		   whale.achi.model.gameStatus
		   whale.achi.model.gameMove) 
    (:import) 
    (:load)
	(:gen-class))

;;; Protected functions

(defn- getGameMoveValueUsingLevel [world gameMove depth level]
  (let
	  [nextWorld (playGameMove world gameMove)
	   nextGameStatus (getGameStatus nextWorld)]
	(cond
	 (= nextGameStatus -won-) (- level 100)
	 (= nextGameStatus -lost-) (- 100 level)
	 (= level depth) level
	 :else (let
			  [nextGameMoves (getGameMoves nextWorld)
			   nextLevel (+ level 1)
			   getNextGameMoveValue (fn [nextGameMove] (getGameMoveValueUsingLevel nextWorld nextGameMove depth nextLevel))
			   nextValues (map getNextGameMoveValue nextGameMoves)]
			(- (apply max nextValues))))))

;;; Public functions

(defn getGameMoveValue [world gameMove depth] (getGameMoveValueUsingLevel world gameMove depth 1))

(defn getBestMoves [world depth]
  (let
	  [gameStatus (getGameStatus world)]
	(cond
	 (= gameStatus -won-) nil
	 (= gameStatus -lost-) nil
	 (= gameStatus -drawn-) nil
	 :else (let
			   [gameMoves (getGameMoves world)
				valuesOfGameMoves (map (fn [gameMove] (getGameMoveValue world gameMove depth)) gameMoves)
				bestValue (apply max valuesOfGameMoves)
				gameMoveValuePairs (partition 2 (interleave gameMoves valuesOfGameMoves))]
			 (for [[gameMove value] gameMoveValuePairs :when (= value bestValue)] gameMove)))))