(ns scad-tarmi.core
  (:require [scad-clj.model :as model]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; CONSTANTS AND SHORTCUTS ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def abs #(max % (- %)))
(def √ #(Math/sqrt %))
(def sin #(Math/sin %))
(def cos #(Math/cos %))

(def π Math/PI)
(def τ (* 2 π))


;;;;;;;;;;;;;;;;;;;;;;;;;
;; INTERFACE FUNCTIONS ;;
;;;;;;;;;;;;;;;;;;;;;;;;;

;; The following functions convert between two ways to measure an angle:
;; - Measured counterclockwise from the x axis, as is typical in trigonometry
;;   and represents the behaviour of OpenSCAD’s rotate function.
;; - Measured clockwise from the y axis.
(defn counterclockwise-from-x-axis [θ] (mod (+ (- τ θ) (/ π 2)) τ))
(defn clockwise-from-y-axis        [θ] (mod (- (- τ θ) (/ π 2)) τ))

(defn long-hex-diagonal
  "The “long diagonal” of a hexagon, computed from the “short” or flat-to-flat
  diagonal."
  [short-diagonal]
  (* 2 (/ short-diagonal (Math/sqrt 3))))
