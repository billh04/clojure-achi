(ns whale.achi.model.world
    (:refer-clojure :exclude nil)
    (:require clojure.set)
    (:use whale.achi.model.boardStatus
		  whale.achi.model.gameStatus
		  whale.achi.model.piece
		  whale.achi.model.player
		  whale.achi.model.gameMove
		  whale.achi.model.place
		  whale.achi.model.block)
    (:import)
    (:load)
	(:gen-class))

;;; Public constant for initial places of pieces

(def -startPlop- (zipmap -pieces- -homePlaces-))

;;; Protected functions

(defn- getOccupiedPlaces [world] (for [ [piece place] (world :placeOfPiece) :when (= (place :block) -board-)] place))

(defn- getEmptyPlaces [world]
  (let [occupiedPlaces (getOccupiedPlaces world)]
    (filter (fn [boardPlace] (not-any? (fn [occupiedPlace] (= boardPlace occupiedPlace)) occupiedPlaces)) -boardPlaces-)))

(defn- getBoardPlacesForPlayer [player world]
  (for [ [piece place] (world :placeOfPiece) :when (and (= (place :block) -board-) (= player (piece :player)))] place))

(defn- isMill? [world]
  (let [lastPlayer (opponentOf (world :currentPlayer))]
	(isMillForPlaces? (getBoardPlacesForPlayer lastPlayer world))))

(defn- stalemate? [world]
  (let [emptyPlaces (getEmptyPlaces world) emptyPlacesCount (count emptyPlaces)]
	(cond
	  (= 0 emptyPlacesCount) true
	  (= 1 emptyPlacesCount)
	    (let
			[emptySquare ((first emptyPlaces) :square)
			 adjacentSquares (set (-adjacentSquaresList- emptySquare))
			 player (world :currentPlayer)
			 playerBoardPlaces (getBoardPlacesForPlayer player world)
			 playerBoardSquares (set (map :square playerBoardPlaces))]
		  (empty? (clojure.set/intersection playerBoardSquares adjacentSquares)))
	  :else false)))

(defn- getDropGameMoves [world]
  (let
	  [player (world :currentPlayer)
	   plop (world :placeOfPiece)
	   fromPlaces (for [[piece place] plop :when (and (= player (piece :player)) (homePlace? place))] place)
	   fromPlace (nth fromPlaces (rand-int (count fromPlaces)))
	   toPlaces (getEmptyPlaces world)]
	(vec (for [toPlace toPlaces] (createGameMove fromPlace toPlace)))))

(defn- getDragGameMoves [world]
  (let
	  [player (world :currentPlayer)
	   plop (world :placeOfPiece)
	   toPlace (first (getEmptyPlaces world))
	   adjacentSquares (-adjacentSquaresList- (toPlace :square))
	   adjacentPlace? (fn [place] (some #(= %1 (place :square)) adjacentSquares))
	   adjacentPlayerPiece? (fn [piece place] (and (= player (piece :player)) (adjacentPlace? place)))
	   fromPlaces (for [[piece place] plop :when (adjacentPlayerPiece? piece place)] place)]
	(vec (for [fromPlace fromPlaces] (createGameMove fromPlace toPlace)))))

(defn- makePlaceOfPiece [places] (zipmap -pieces- places))

;;; Public functions

(defn createWorld [placeOfPiece currentPlayer] {
  :tag ::world,
  :placeOfPiece placeOfPiece,
  :currentPlayer currentPlayer})

(defn createNewWorld [firstPlayer] (createWorld -startPlop- firstPlayer))

(defn getPieceAtPlace [world place]
  (first (for [ [piece itsPlace] (world :placeOfPiece) :when (= itsPlace place)] piece)))

(defn getNumberOfPiecesOnBoard [world] (count (getOccupiedPlaces world)))

(defn dropPhase? [world] (< (getNumberOfPiecesOnBoard world) 8))

(defn gameOver? [world] (or (isMill? world) (stalemate? world)))

(defn getBoardStatus [world] (cond (isMill? world) -mill-, (stalemate? world) -stalemate-, true nil))

(defn getGameStatus [world] (let [boardStatus (getBoardStatus world)] (if (= boardStatus nil) nil -lost-)))

(defn getGameMoves [world] (if (dropPhase? world) (getDropGameMoves world) (getDragGameMoves world)))

(defn validateGameMove [world gameMove]
  (assert (isGameMove gameMove))
  (let
	  [currentPlayer (world :currentPlayer)
	   fromPlace (gameMove :fromPlace)
	   toPlace (gameMove :toPlace)]
	(and (= (:player (getPieceAtPlace world fromPlace)) currentPlayer) ;;; currentPlayer must own piece being moved
		 (= (toPlace :block) -board-) ;;; toPlace must be on the board
		 (nil? (getPieceAtPlace world toPlace)) ;;; toPlace must be vacant
		 (if (dropPhase? world)
		   (homePlace? fromPlace) ;;; fromPlace must be on homePlace during dropPhase
		   (adjacentPlaces? toPlace fromPlace))))) ;;; toPlace must be adjacent to fromPlace during dragPhase

(defn playGameMove [world gameMove]
  (assert (isGameMove gameMove))
  (let [piece (getPieceAtPlace world (gameMove :fromPlace))]
	(assert (not (nil? piece)))
	(let
		[plop (world :placeOfPiece)
		 toPlace (gameMove :toPlace)
		 nextPlop (assoc plop piece toPlace)
		 nextPlayer (opponentOf (world :currentPlayer))]
	  (assoc world :placeOfPiece nextPlop :currentPlayer nextPlayer))))