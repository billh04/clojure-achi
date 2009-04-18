(ns whale.achi.model.place 
    (:refer-clojure :exclude nil) 
    (:require) 
    (:use whale.achi.model.block) 
    (:import) 
    (:load)
	(:gen-class))

;;; Declare constants

(declare -adjacentSquaresList-)
(declare -northHomePlaces-)
(declare -southHomePlaces-)
(declare -homePlaces-)
(declare -boardPlaces-)
(declare -worldPlaces-)

;;; Public functions

(defn createPlace [block square] {:tag ::place, :block block, :square square})

(defn homePlace? [place] (contains? #{-northHome- -southHome-} (place :block)))

(defn boardPlace? [place] (= -board- (place :block)))

(defn adjacentPlaces? [place1 place2]
  (let
	  [square1 (:square place1)
	   square2 (:square place2)]
	(and (= (place1 :block) -board-)
		 (= (place2 :block) -board-)
		 (some (fn [adjacentSquare] (= adjacentSquare square1)) (-adjacentSquaresList- square2)))))

(defn isMillForPlaces? [places]
  (let
	  [squares (map :square places)
	   countOfSquares (count (distinct squares))
	   isRowMillFor3Squares? (fn [squares] (apply = (map (fn [square] (quot square 3)) squares)))
	   isColMillFor3Squares? (fn [squares] (apply = (map (fn [square] (rem square 3)) squares)))
	   isMillFor3Squares? (fn [squares] (or (isRowMillFor3Squares? squares) (isColMillFor3Squares? squares)))
	   drop-nth (fn [coll n] (concat (take n coll) (nthnext coll (+ 1 n))))]
	  (cond
		  (< countOfSquares 3) false
		  (= countOfSquares 3) (isMillFor3Squares? squares)
		  (= countOfSquares 4) (some true? (map isMillFor3Squares? (for [n (range 4)] (drop-nth squares n))))
		  :else false)))

;;; Public Constants

(def -adjacentSquaresList- (vector [4 1 3] [4 0 2] [4 1 5] [4 0 6] [0 1 2 3 5 6 7 8] [4 2 8] [4 3 7] [4 6 8] [4 7 5]))

(def -northHomePlaces- (vec (for [i (range 4)] (createPlace -northHome- i))))
(def -southHomePlaces- (vec (for [i (range 4)] (createPlace -southHome- i))))
(def -boardPlaces- (vec (for [i (range 9)] (createPlace -board- i))))
(def -homePlaces- (vec (concat -northHomePlaces- -southHomePlaces-)))
(def -worldPlaces- (vec (concat -northHomePlaces- -boardPlaces- -southHomePlaces-)))