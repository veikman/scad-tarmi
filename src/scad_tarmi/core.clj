;;; Mathematics and schemata. No dependence on SCAD utilities.

(ns scad-tarmi.core
  (:require [clojure.spec.alpha :as spec]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; CONSTANTS AND SHORTCUTS ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def abs #(max % (- %)))
(def √ #(Math/sqrt %))
(def sin #(Math/sin %))
(def cos #(Math/cos %))

(def π Math/PI)
(def τ (* 2 π))


;;;;;;;;;;;;;;;;;;;;
;; SPECIFICATIONS ;;
;;;;;;;;;;;;;;;;;;;;

(spec/def ::point-2d (spec/coll-of number? :count 2))
(spec/def ::point-3d (spec/coll-of number? :count 3))
(spec/def ::point-2-3d (spec/or :two ::point-2d :three ::point-3d))

(spec/def ::point-coll-2d (spec/coll-of ::point-2d))
(spec/def ::point-coll-3d (spec/coll-of ::point-3d))
(spec/def ::point-coll-2-3d (spec/or :two ::point-coll-2d
                                     :three ::point-coll-3d))


;;;;;;;;;;;;;;;;;;;;;;;;;
;; INTERFACE FUNCTIONS ;;
;;;;;;;;;;;;;;;;;;;;;;;;;

(defn mean
  "The arithmetic mean of one or more planar 2D or 3D Cartesian coordinates."
  [& coord]
  {:pre [(spec/valid? ::point-coll-2-3d coord)]
   :post [(spec/valid? ::point-2-3d %)]}
  (let [[type values] (spec/conform ::point-coll-2-3d coord)
        n (count values)
        divisor (case type :two [n n], :three [n n n])]
    (mapv / (apply (partial map +) values) divisor)))

(defn long-hex-diagonal
  "The “long diagonal” of a hexagon, computed from the “short” or flat-to-flat
  diagonal."
  [short-diagonal]
  {:pre [(number? short-diagonal)]}
  (* 2 (/ short-diagonal (Math/sqrt 3))))

(defn unit-circle-point-coord
  "The Cartesian coordinates of a point on a unit circle around [0, 0]."
  [θ]
  {:pre [(number? θ)], :post [(spec/valid? ::point-2d %)]}
  [(sin θ) (cos θ)])
