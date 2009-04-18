(ns whale.achi.model.achiGame 
    (:refer-clojure :exclude nil) 
    (:require clojure.set) 
    (:use whale.achi.model.world
		  whale.achi.model.player
		  whale.achi.model.gameMove
		  whale.achi.model.director)
    (:import) 
    (:load)
	(:gen-class))

(defn createAchiGame [gameId firstPlayer world gameMoveHistory finished directors] 
  {
   :tag ::achiGame,
   :gameId gameId,
   :firstPlayer firstPlayer, ;; wch: should place this in world
   :world world,
   :gameMoveHistory gameMoveHistory
   :finished finished
   :directors directors})

(defn startNewAchiGame [achiGame firstPlayer]
  (let
	  [gameId (inc (achiGame :gameId))
	   world (createNewWorld firstPlayer)
	   gameMoveHistory (vector)]
	(assoc achiGame :gameId gameId, :firstPlayer firstPlayer, :world world, :gameMoveHistory gameMoveHistory, :finished false)))

(defn playGameMoveForAchiGame [achiGame gameMove player]
  (let
	  [world (achiGame :world)
	   currentPlayer (world :currentPlayer)
	   gameMoveHistory (achiGame :gameMoveHistory)
	   nextWorld (playGameMove world gameMove)
	   nextGameMoveHistory (conj gameMoveHistory gameMove)]
	(assert (not (gameOver? world)))
	(assert (= player currentPlayer))
	(assoc achiGame :world nextWorld, :gameMoveHistory nextGameMoveHistory)))

(defn finishAchiGame [achiGame finished]
  (assoc achiGame :finished finished))

(defn- updateDirectorType [directors [player directorType]] (assoc-in directors [player :directorType] directorType))

(defn- updateDirectorsTypes [directors directorsTypes] (reduce updateDirectorType directors directorsTypes))

(defn updateAchiGameDirectorsTypes [achiGame directorsTypes]
  (let
	  [gameId (inc (achiGame :gameId))
	   directors (achiGame :directors)
	   updatedDirectors (updateDirectorsTypes directors directorsTypes)]
	(assoc achiGame :gameId gameId, :directors updatedDirectors)))