(set! *warn-on-reflection* true)

(ns whale.achi.main 
    (:refer-clojure :exclude nil) 
    (:require) 
    (:use whale.achi.achiTask.achiTask)
	(:import (javax.swing JFrame JButton AbstractAction)
			 (java.awt.event WindowAdapter)) 
    (:load)
	(:gen-class :name "whale.achi.main"))

(def achiTasksHandle (atom (hash-map)))

(defn newAchiTask [ ]
  (let [achiTask (createAchiTask)
		achiFrameAdapter (proxy [WindowAdapter] [ ]
								(windowClosed [windowEvent]
											  (swap! achiTasksHandle (fn [achiTasks] (dissoc achiTasks achiTask)))
											  (when (zero? (count @achiTasksHandle)) (. System exit 0))))
		newWindowAction (proxy [AbstractAction] ["New Window"]
							   (actionPerformed [actionEvent] (newAchiTask)))
		achiFrame #^JFrame (achiTask :achiFrame)
		newWindowButton #^JButton (achiTask :newWindowButton)]
	(swap! achiTasksHandle (fn [achiTasks] (assoc achiTasks achiTask nil)))
	(.addActionListener newWindowButton newWindowAction)
	(.addWindowListener achiFrame achiFrameAdapter)
	(.show achiFrame)
	(startAchiTaskAgents achiTask)))

(defn -main [ ] (newAchiTask))
