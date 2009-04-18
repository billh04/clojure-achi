(ns whale.achi.gui.achiGamePanel
    (:refer-clojure :exclude nil) 
    (:require) 
    (:use whale.achi.gui.pieceSnip
		  whale.achi.gui.achiGamePanelSettings
		  whale.achi.model.achiGame
		  whale.achi.model.director
		  whale.achi.model.directorType
		  whale.achi.model.world
		  whale.achi.model.piece
		  whale.achi.model.player
		  whale.achi.model.gameMove
		  whale.achi.model.place
		  whale.achi.model.block) 
    (:import (java.awt Color Dimension Graphics Graphics2D Point BasicStroke)
			 (javax.swing JPanel JFrame Renderer)
			 (java.awt.event MouseEvent)
			 (javax.swing.event MouseInputAdapter)) 
    (:load)
	(:gen-class))

(declare pointOfPlace)
(declare onMousePressed)
(declare onMouseDragged)
(declare onMouseReleased)
(declare onMouseExited)

(defn createAchiGamePanel [achiGameHandle]
  (let
	  [;;; drag stuff
	   dragPieceSnipHandle (atom nil)
	   dragPointHandle (atom nil)

	   ;;; PieceSnip
	   northPieceSnips (vec
						(map (fn [piece pieceColor] (createPieceSnip piece (createPieceImage pieceColor)))
							 -northPieces- (repeat 4 (new Color 255 255 255))))
	   southPieceSnips (vec
						(map (fn [piece pieceColor] (createPieceSnip piece (createPieceImage pieceColor)))
							 -southPieces- (repeat 4 (new Color 0 0 0))))
	   pieceSnips (zipmap (concat -northPieces- -southPieces-) (concat northPieceSnips southPieceSnips))

       ;;; Render
	   renderBackground
	   (fn [#^Graphics2D g]
		 (doto g
		   (.setColor (new Color 0 140 0)) ;; color is dark green
		   (.fillRect 0 0 -gamePanelWidth- -gamePanelHeight-)))

	   renderBoard
	   (fn [#^Graphics2D g]
		   (let
			   [strokeWidth 3]
			 (doto g
			   (.setColor (new Color 0 0 0))
			   (.setStroke (new BasicStroke strokeWidth BasicStroke/CAP_SQUARE BasicStroke/JOIN_BEVEL)))
			 (dorun (map (fn [row] (.drawLine g (-boardDotX- row) (-boardDotY- 0) (-boardDotX- row) (-boardDotY- 2))) (range 3)))
			 (dorun (map (fn [col] (.drawLine g (-boardDotX- 0) (-boardDotY- col) (-boardDotX- 2) (-boardDotY- col))) (range 3)))
			 (doto g
			   (.setStroke (new BasicStroke strokeWidth BasicStroke/CAP_BUTT BasicStroke/JOIN_BEVEL))
			   (.drawLine (-boardDotX- 0) (-boardDotY- 0) (-boardDotX- 2) (-boardDotY- 2))
			   (.drawLine (-boardDotX- 2) (-boardDotY- 0) (-boardDotX- 0) (-boardDotY- 2)))))

	   renderPieceSnip
	   (fn [piece place #^Graphics2D g]
		   (let
			   [pieceSnip (pieceSnips piece)
				[x y] (pointOfPlace place)]
			 (when (not= pieceSnip @dragPieceSnipHandle)
			   (.drawImage g (pieceSnip :image) (- x -pieceSnipRadius-) (- y -pieceSnipRadius-)
						   -pieceSnipWidth- -pieceSnipHeight- nil))))

	   renderPieceSnips
	   (fn [#^Graphics2D g]
		   (let
			   [achiGame @achiGameHandle]
			 (when (not (nil? achiGame))
			   (let
				   [world (achiGame :world)
					plop (world :placeOfPiece)]
				 (doseq [[piece place] plop] (renderPieceSnip piece place g))
				 (when  @dragPieceSnipHandle
				   (let
					   [dragPieceSnip @dragPieceSnipHandle
						[x y] @dragPointHandle]
					 (.drawImage g (dragPieceSnip :image)
								 (- x -pieceSnipRadius-) (- y -pieceSnipRadius-) -pieceSnipWidth- -pieceSnipHeight- nil)))))))

	   renderGamePanel
	   (fn [#^Graphics2D g] (doto g (renderBackground) (renderBoard) (renderPieceSnips)))

	   ;;; AchiGamePanel
	   #^JPanel achiGamePanel
	   (proxy [JPanel Renderer] [ ]
			  (paint [g] (renderGamePanel g))
			  (getComponent [ ] this) ;; needed for Renderer interface
			  (setValue [aValue #^boolean isSelected] (comment (reset! achiGameHandle aValue)))) ;; needed for Renderer interface

	   ;;; MouseInputAdapter
	   mouseInputAdapter (proxy [MouseInputAdapter] [ ]
						   (mousePressed [mouseEvent] (onMousePressed mouseEvent dragPieceSnipHandle dragPointHandle
																	  achiGameHandle achiGamePanel pieceSnips))
						   (mouseDragged [mouseEvent] (onMouseDragged mouseEvent dragPieceSnipHandle dragPointHandle
																	  achiGameHandle achiGamePanel))
						   (mouseReleased [mouseEvent] (onMouseReleased mouseEvent dragPieceSnipHandle dragPointHandle
																		achiGameHandle achiGamePanel))
						   (mouseExited [mouseEvent] (onMouseExited mouseEvent dragPieceSnipHandle dragPointHandle
																	achiGameHandle achiGamePanel)))]
	(doto achiGamePanel (.setPreferredSize (new Dimension -gamePanelWidth- -gamePanelHeight-)))
	(doto achiGamePanel (.addMouseListener mouseInputAdapter) (.addMouseMotionListener mouseInputAdapter))
	achiGamePanel))

(defn- pointOfPlace [place]
	 (let [block (place :block)
		   square (place :square)]
	   (cond
		(= block -northHome-) (vector (-homeDotX- square) (-homeDotY- 0))
		(= block -southHome-) (vector (-homeDotX- square) (-homeDotY- 1))
		(= block -board-) (vector (-boardDotX- (rem square 3)) (-boardDotY- (quot square 3))))))

(defn- pointInPlace? [point place]
  (let
	  [[x1 y1] (pointOfPlace place)
	   [x2 y2] point
	   dx (- x2 x1)
	   dy (- y2 y1)]
  (< (+ (* dx dx) (* dy dy)) (* -pieceSnipRadius- -pieceSnipRadius-))))

(defn- getPlaceWithPoint [point] (some (fn [place] (if (pointInPlace? point place) place)) -worldPlaces-))

(defn- snapBack [dragPieceSnipHandle dragPointHandle #^JPanel achiGamePanel]
  (reset! dragPieceSnipHandle nil)
  (reset! dragPointHandle nil)
  (.repaint achiGamePanel))

(defn- dropOnPlace [toPlace dragPieceSnipHandle dragPointHandle achiGameHandle #^JPanel achiGamePanel]
  (let
	  [achiGame @achiGameHandle
	   world (achiGame :world)
	   currentPlayer (world :currentPlayer)
	   plop (world :placeOfPiece)
	   isGameOver (gameOver? world)
	   isDropPhase (dropPhase? world)
	   dragPieceSnip @dragPieceSnipHandle
	   dragPiece (dragPieceSnip :piece)
	   dragPlayer (dragPiece :player)
	   fromPlace (plop dragPiece)
	   pieceAtPlace (getPieceAtPlace world toPlace)]
	(cond
	 isGameOver (snapBack dragPieceSnipHandle dragPointHandle achiGamePanel)
	 (not= dragPlayer currentPlayer) (snapBack dragPieceSnipHandle dragPointHandle achiGamePanel)
	 pieceAtPlace (snapBack dragPieceSnipHandle dragPointHandle achiGamePanel)
	 (and isDropPhase (not (homeBlock? (fromPlace :block)))) (snapBack dragPieceSnipHandle dragPointHandle achiGamePanel)
	 (and (not isDropPhase) (not (adjacentPlaces? fromPlace toPlace))) (snapBack dragPieceSnipHandle dragPointHandle achiGamePanel)
	 :else (do
			 (reset! dragPieceSnipHandle nil)
			 (reset! dragPointHandle nil)
			 (let
				 [gameMove (createGameMove fromPlace toPlace)
				  nextAchiGame (playGameMoveForAchiGame achiGame gameMove currentPlayer)
				  swapped? (compare-and-set! achiGameHandle achiGame nextAchiGame)]
			   (cond swapped? nil :else (.repaint achiGamePanel)))))))

(defn- onMousePressed [#^MouseEvent mouseEvent dragPieceSnipHandle dragPointHandle
					  achiGameHandle #^JPanel achiGamePanel pieceSnips]
  (let [#^Point javaPoint (.getPoint mouseEvent)
		point [(.x javaPoint) (.y javaPoint)]
		place (getPlaceWithPoint point)]
	(when place
	  (let
		  [achiGame @achiGameHandle
		   world (achiGame :world)
		   piece (getPieceAtPlace world place)]
		(when piece
		  (let
			  [player (piece :player)
			   directors (achiGame :directors)
			   playerDirector (directors player)
			   playerDirectorType (playerDirector :directorType)]
			(when (= playerDirectorType -human-)
			  (let
				  [pieceSnip (pieceSnips piece)]
				(reset! dragPieceSnipHandle pieceSnip)
				(onMouseDragged mouseEvent dragPieceSnipHandle dragPointHandle achiGameHandle achiGamePanel)))))))))

(defn- onMouseDragged [#^MouseEvent mouseEvent dragPieceSnipHandle dragPointHandle achiGameHandle #^JPanel achiGamePanel]
  (when @dragPieceSnipHandle
	(let [#^Point javaPoint (.getPoint mouseEvent)
		  dragPoint [(.x javaPoint) (.y javaPoint)]]
	  (reset! dragPointHandle dragPoint)
	  (.repaint achiGamePanel))))

(defn- onMouseReleased [#^MouseEvent mouseEvent dragPieceSnipHandle dragPointHandle achiGameHandle #^JPanel achiGamePanel]
  (when @dragPieceSnipHandle
	(onMouseDragged mouseEvent dragPieceSnipHandle dragPointHandle achiGameHandle achiGamePanel)
	(let [#^Point javaPoint (.getPoint mouseEvent)
		  point [(.x javaPoint) (.y javaPoint)]
		  toPlace (getPlaceWithPoint point)]
	  (if toPlace
		(dropOnPlace toPlace dragPieceSnipHandle dragPointHandle achiGameHandle achiGamePanel)
		(snapBack dragPieceSnipHandle dragPointHandle achiGamePanel)))))

(defn- onMouseExited [#^MouseEvent mouseEvent dragPieceSnipHandle dragPointHandle achiGameHandle #^JPanel achiGamePanel]
  (when @dragPieceSnipHandle
	(snapBack dragPieceSnipHandle dragPointHandle achiGamePanel)))