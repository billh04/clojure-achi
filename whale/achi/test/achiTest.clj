(ns whale.achi.test.achiTest
    (:refer-clojure :exclude nil)
    (:require)
    (:use)
    (:import)
    (:load)
	(:gen-class))

;;; Testing Piece

(in-ns 'whale.achi.model.piece)

(println (for [i (range 8) :when (= ((-pieces- i) :player) :north)] (-pieces- i)))
(println (for [i (range 8) piece [(-pieces- i)] :when (= (piece :player) -northPlayer-)] piece))

;;; Testing World

(in-ns 'whale.achi.model.world)
(refer 'whale.achi.botThinker.positionEvaluation)

(defn home [player square] ((if (= player 0) -northHomePlaces- -southHomePlaces-) square))
(defn board [square] (-boardPlaces- square))

(def placeOfPiece {(-pieces- 0) (-worldPlaces- 7), (-pieces- 1) (-worldPlaces- 10)})

(def plop2 (makePlaceOfPiece [(home 0 0) (home 0 1) (home 0 2) (home 0 3) (home 1 0) (home 1 1) (home 1 2) (home 1 3)]))
(def world2 (createWorld plop2 -northPlayer-))

(def plop3 (makePlaceOfPiece [(board 1) (board 3) (board 4) (board 5) (board 0) (board 2) (board 6) (home 1 3)]))
(def world3 (createWorld plop3 -southPlayer-))
(println (isMill? world3))

(def plop4 (makePlaceOfPiece [(board 1) (board 3) (board 4) (home 0 3) (board 0) (board 2) (board 6) (home 1 3)]))
(def world4 (createWorld plop4 -northPlayer-))

(def worldz (createWorld
			 (zipmap -pieces- (list (-boardPlaces- 1) (-boardPlaces- 4) (-homePlaces- 2) (-homePlaces- 3)
									(-boardPlaces- 3) (-homePlaces- 5) (-homePlaces- 6) (-homePlaces- 7)))
			 -southPlayer-))
(getGameMoveValue worldz (createGameMove (-homePlaces- 5) (-boardPlaces- 0)) 4)

;;; Miscellaneous testing

(in-ns 'whale.achi.main)

(def m {:fred 37 :ethel 29 :lucy 5})
(def mm (let [{:keys [fred ethel lucy]} m] (list fred lucy ethel)))

(def f (interleave (for [i (range 4)] (-pieces- i)) (for [i (range 4)] (-worldPlaces- i))))
(def g (apply assoc {} f))
(println (g (-pieces- 2)))
(def map1 (apply assoc {} (list 1 10 2 20 3 30 4 40 5 50)))
(def map2 (apply assoc {} [1 10 2 20 3 30 4 40 5 50]))

(defn getCwd "get current working directory" [] (. System getProperty "user.dir"))
(println (getCwd))