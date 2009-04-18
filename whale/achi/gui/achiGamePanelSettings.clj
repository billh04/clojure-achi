(ns whale.achi.gui.achiGamePanelSettings 
    (:refer-clojure :exclude nil) 
    (:require) 
    (:use) 
    (:import)
    (:load)
	(:gen-class))

(def -pieceSnipRadius- 20)
(def -boardDotSpacing- 90)
(def -gamePanelWidth- 400)
(def -gamePanelHeight- 400)

;;; Geometry of pieceSnip
(def -pieceSnipWidth- (* 2 -pieceSnipRadius-))

(def -pieceSnipHeight- (* 2 -pieceSnipRadius-))

;;; Geometry of board places
(def -boardDotX- (vec (let [originX (/ (- -gamePanelWidth- (* 2 -boardDotSpacing-)) 2)]
						(map (fn [i] (+ originX (* -boardDotSpacing- i))) (range 3)))))

(def -boardDotY- (vec (let [originY (/ (- -gamePanelHeight- (* 2 -boardDotSpacing-)) 2)]
						(map (fn [i] (+ originY (* -boardDotSpacing- i))) (range 3)))))

;;; Geometry of home places
(def -homeDotX- (vec (let [originX (/ (- -gamePanelWidth- (* 6 -pieceSnipWidth-)) 2)
						   spacing (* 2 -pieceSnipWidth-)]
						(map (fn [i] (+ originX (* spacing i))) (range 4)))))

(def -homeDotY- (let [boardHeight (* 2 -boardDotSpacing-)
					  originY (/ (- -gamePanelHeight- (+ boardHeight -pieceSnipHeight-)) 4)]
						[originY (- -gamePanelHeight- originY)]))