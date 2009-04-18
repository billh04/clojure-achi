(ns whale.achi.achiTask.achiTask 
    (:refer-clojure :exclude nil) 
    (:require) 
    (:use whale.achi.achiTask.achiTaskAgents
		  whale.achi.model.achiGame
		  whale.achi.model.world
		  whale.achi.model.director
		  whale.achi.model.directorType
		  whale.achi.model.player
		  whale.achi.model.place
		  whale.achi.gui.achiGamePanel) 
    (:import (javax.swing Box JFrame JLabel JPanel JButton AbstractAction BoxLayout Renderer)
			 (javax.swing.border MatteBorder)
			 (java.awt.event ItemEvent ItemListener WindowAdapter)
			 (java.awt Choice Color Component Dimension Point))
    (:load)
	(:gen-class))

;; Constants for directorsTypesChoice

(def -directorsTypesChoices-
	 {"Computer vs Human" [[-northPlayer- -computer-] [-southPlayer- -human-]],
	  "Human vs Human" [[-northPlayer- -human-] [-southPlayer- -human-]],
	  "Computer vs Computer" [[-northPlayer- -computer-] [-southPlayer- -computer-]],
	  "Human vs Computer" [[-northPlayer- -human-] [-southPlayer- -computer-]]})

;;; Public functions

(defn createAchiTask [ ]
  (let
	  [firstPlayer -northPlayer-
	   world (createNewWorld firstPlayer)
	   gameMoveHistory (vector)
	   finished false
	   northDirector (createDirector -northPlayer- -computer-)
	   southDirector (createDirector -southPlayer- -computer-)
	   directors {-northPlayer- northDirector, -southPlayer- southDirector}
	   achiGame (createAchiGame 0 firstPlayer world gameMoveHistory finished directors)
	   achiGameHandle (atom achiGame)
	   achiTaskAgents (hash-map :bossAgent (agent nil), :northPlayerAgent (agent nil), :southPlayerAgent (agent nil))
	   achiFrame (doto (new JFrame) (.setDefaultCloseOperation JFrame/DISPOSE_ON_CLOSE) (.setResizable false))
	   achiGamePanel #^JPanel (createAchiGamePanel achiGameHandle)
	   newWindowButton (new JButton "New Window")
	   achiTask (hash-map :tag ::achiTask,
						  :achiGameHandle achiGameHandle,
						  :achiTaskAgents achiTaskAgents,
						  :achiFrame achiFrame,
						  :achiGamePanel achiGamePanel
						  :newWindowButton newWindowButton)
	   newGameAction (proxy [AbstractAction] ["New Game"]
					   (actionPerformed [actionEvent] (swap! achiGameHandle startNewAchiGame -northPlayer-)))
	   directorsTypesChoice (doto (new Choice)
							 (.addItem "Computer vs Human") (.addItem "Human vs Human")
							 (.addItem "Computer vs Computer") (.addItem "Human vs Computer"))
	   statusLabel (new JLabel)
	   newGameButton (new JButton newGameAction)
	   buttonBarPanel (new JPanel)
	   menuBarPanel (doto (new JPanel) (.add directorsTypesChoice) (.add newWindowButton))
	   contentPane (.getContentPane achiFrame)
	   achiGamePanelWatcher (fn [key achiGameHandle oldAchiGame newAchiGame]
								(.setValue #^Renderer achiGamePanel newAchiGame false)
								(.setText statusLabel (if (newAchiGame :finished) "Game over" ""))
								(.repaint achiGamePanel))]
	(.setValue  #^Renderer achiGamePanel achiGame false)
	(.select directorsTypesChoice 2)
	(.addItemListener directorsTypesChoice
					  (proxy [ItemListener] [ ]
						(itemStateChanged [#^ItemEvent itemEvent]
										  (let [directorsTypesChoiceAt (.getItem itemEvent)
												directorsTypes (-directorsTypesChoices- directorsTypesChoiceAt)]
											(swap! achiGameHandle updateAchiGameDirectorsTypes directorsTypes)))))
	(.setBorder buttonBarPanel (new MatteBorder 1 0 1 0 Color/black))
	(.setLayout buttonBarPanel (new BoxLayout buttonBarPanel BoxLayout/LINE_AXIS))
	(doto buttonBarPanel
	  (.add (Box/createRigidArea (Dimension. 5 0))) (.add statusLabel) (.add (Box/createHorizontalGlue)) (.add newGameButton))
	(doto contentPane
	  (.setLayout (new BoxLayout contentPane BoxLayout/Y_AXIS))
	  (.add achiGamePanel)
	  (.add buttonBarPanel)
	  (.add menuBarPanel))
	(doto achiFrame (.setTitle "Achi Game") .pack)
	(add-watch achiGameHandle :achiGamePanel achiGamePanelWatcher)
	achiTask))

(defn startAchiTaskAgents [achiTask]
  (let
	  [achiTaskAgents (achiTask :achiTaskAgents)
	   achiGameHandle (achiTask :achiGameHandle)
	   achiFrame #^JFrame (achiTask :achiFrame)]
	(send-off (achiTaskAgents :bossAgent) bossAgentAction achiGameHandle achiFrame)
	(send-off (achiTaskAgents :northPlayerAgent) playerAgentAction achiGameHandle achiFrame -northPlayer-)
	(send-off (achiTaskAgents :southPlayerAgent) playerAgentAction achiGameHandle achiFrame -southPlayer-)))