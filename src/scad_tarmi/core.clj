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
(spec/def ::point-2d-3d (spec/or :two ::point-2d :three ::point-3d))


;;;;;;;;;;;;;;;;;;;;;;;;;
;; INTERFACE FUNCTIONS ;;
;;;;;;;;;;;;;;;;;;;;;;;;;

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
