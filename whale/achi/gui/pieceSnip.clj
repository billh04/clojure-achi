(ns whale.achi.gui.pieceSnip
    (:refer-clojure :exclude nil) 
    (:require) 
    (:use whale.achi.gui.achiGamePanelSettings
		  whale.achi.model.piece) 
    (:import (java.awt Color Graphics Graphics2D)
			 (java.awt.image BufferedImage))
    (:load)
	(:gen-class))

;;; PieceSnip

(defn createPieceSnip [piece image] {:tag ::pieceSnip, :piece piece, :image image})

(defn createPieceImage [pieceColor]
  (let
	  [pieceImage (new BufferedImage -pieceSnipWidth- -pieceSnipHeight- BufferedImage/TYPE_INT_ARGB)
	   pieceGraphics (.createGraphics pieceImage)]
	(doto pieceGraphics
	  (.setColor pieceColor)
	  (.fillOval 0 0 -pieceSnipWidth- -pieceSnipHeight-)
	  (.setColor (new Color 0 0 0))
	  (.drawOval 0 0 -pieceSnipWidth- -pieceSnipHeight-))
	pieceImage))