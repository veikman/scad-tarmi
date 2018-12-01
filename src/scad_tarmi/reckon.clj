;;; Utilites for reasoning about a model, reckoning where things will end up.

;;; Functions in this module take and return 3-tuples of numbers representing
;;; coordinates, rather than OpenSCAD objects, but otherwise do the work of
;;; OpenSCAD transformations.

(ns scad-tarmi.reckon
  (:require [clojure.core.matrix :refer [mmul]]))


;;;;;;;;;;;;;;;;;;;;;;;;;
;; INTERFACE FUNCTIONS ;;
;;;;;;;;;;;;;;;;;;;;;;;;;

(def translate (partial map +))

(defn rotate
  "Transform a set of coordinates as in rotation.
  The call signature imitates one form of scad-clj’s rotate. The matrices used
  here are selected to imitate the compound effect of OpenSCAD’s rotate, which
  uses the Eigen library’s Euler-style rotation under the hood. A unified
  matrix would save a couple of CPU cycles but would not affect SCAD or STL
  files."
  [[α β γ] position]
  (->> position
       (mmul [[1                0                0]
              [0                (Math/cos α)     (- (Math/sin α))]
              [0                (Math/sin α)     (Math/cos α)]])
       (mmul [[(Math/cos β)     0                (Math/sin β)]
              [0                1                0]
              [(- (Math/sin β)) 0                (Math/cos β)]])
       (mmul [[(Math/cos γ)     (- (Math/sin γ)) 0]
              [(Math/sin γ)     (Math/cos γ)     0]
              [0                0                1]])))
