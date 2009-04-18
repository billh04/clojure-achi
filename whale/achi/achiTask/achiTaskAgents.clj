(ns whale.achi.achiTask.achiTaskAgents 
    (:refer-clojure :exclude nil) 
    (:require) 
    (:use whale.achi.botThinker.botThinker ;; needed for decideMove
		  whale.achi.model.achiGame
		  whale.achi.model.world
		  whale.achi.model.gameMove
		  whale.achi.model.director
		  whale.achi.model.directorType
		  whale.achi.model.player
		  whale.achi.achiDebug) 
    (:import (javax.swing JFrame))
    (:load)
	(:gen-class))

(defn playerAgentAction [_ achiGameHandle #^JFrame achiFrame player]
  (when (.isDisplayable achiFrame)
	(let
		[achiGame @achiGameHandle
		 world (achiGame :world)
		 currentPlayer (world :currentPlayer)
		 directorType (((achiGame :directors) player) :directorType)]
	  (when (and (= directorType -computer-) (= player currentPlayer) (not (achiGame :finished)) (not (gameOver? world)))
		(let
			[gameMove (decideMove world)
			 nextAchiGame (playGameMoveForAchiGame achiGame gameMove currentPlayer)
			 swapped? (compare-and-set! achiGameHandle achiGame nextAchiGame)]
		  (when swapped? nil)))
	  (. Thread (sleep 300))
	  (send-off *agent* #'playerAgentAction achiGameHandle achiFrame player)))
  nil)

(defn bossAgentAction [_ achiGameHandle #^JFrame achiFrame]
  (when (.isDisplayable achiFrame)
	(let
		[achiGame @achiGameHandle
		 world (achiGame :world)]
	  (cond (not (achiGame :finished))
			(when (gameOver? world)
			  (let
				  [finishedAchiGame (finishAchiGame achiGame true)
				   swapped? (compare-and-set! achiGameHandle achiGame finishedAchiGame)]
				(when swapped? nil)))
			:else (let
					  [directors (achiGame :directors)
					   northDirectorType ((directors -northPlayer-) :directorType)
					   southDirectorType ((directors -southPlayer-) :directorType)
					   isContinuousPlay false ;; default is false. To enable continuous play set true.
					   isComputerVsComputer (and (= northDirectorType -computer-) (= southDirectorType -computer-))]
					(if (and isComputerVsComputer isContinuousPlay) (swap! achiGameHandle startNewAchiGame -northPlayer-))))
	  (. Thread (sleep 300))
	  (send-off *agent* #'bossAgentAction achiGameHandle achiFrame)))
  nil)