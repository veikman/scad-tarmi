;;; Utilites for reasoning about a model, reckoning where things will end up.

;;; All functions in this module take and return 3-tuples of numbers
;;; representing coordinates, rather than OpenSCAD objects, and they expect
;;; only one item rather than a variable-length block. They otherwise do
;;; the work of the scad-clj transformations they are named after.

(ns scad-tarmi.reckon
  (:require [clojure.core.matrix :refer [mmul]]
            [scad-tarmi.core :refer [sin cos]]))


;;;;;;;;;;;;;;;;;;;;;;;;;
;; INTERFACE FUNCTIONS ;;
;;;;;;;;;;;;;;;;;;;;;;;;;

(def translate (partial mapv +))

(defn rotate
  "Transform a set of coordinates as in rotation.
  The call signature imitates one form of scad-clj’s rotate. The matrices used
  here are selected to imitate the compound effect of OpenSCAD’s rotate, which
  uses the Eigen library’s Euler-style rotation under the hood. A unified
  matrix would save a couple of CPU cycles but would not affect SCAD or STL
  files."
  [[α β γ] position]
  (->> position
       (mmul [[1           0           0]
              [0           (cos α)     (- (sin α))]
              [0           (sin α)     (cos α)]])
       (mmul [[(cos β)     0           (sin β)]
              [0           1           0]
              [(- (sin β)) 0           (cos β)]])
       (mmul [[(cos γ)     (- (sin γ)) 0]
              [(sin γ)     (cos γ)     0]
              [0           0           1]])))
