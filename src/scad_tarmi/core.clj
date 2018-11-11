(ns scad-tarmi.core
  (:require [clojure.spec.alpha :as spec]
            [scad-clj.model :as model]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; CONSTANTS AND SHORTCUTS ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def sin #(Math/sin %))
(def cos #(Math/cos %))
(def τ (* 2 Math/PI))


;;;;;;;;;;;;;;
;; INTERNAL ;;
;;;;;;;;;;;;;;

(defn- clean
  "Return a closure over passed function if its argument is truthy.
  Else return a variary identity function.
  The purpose of this function is to filter out, in the Clojure layer, those
  elements of OpenSCAD output that would be no-ops."
  [function arg]
  (if arg (partial function arg) (fn [& args] args)))

(spec/def ::contracting-tuple (spec/coll-of number? :min-count 1 :max-count 3))
(spec/def ::tuplable (spec/or :num number? :tuple ::contracting-tuple))

(defn- expand-tuple
  "Expand a number or short tuple of numbers to a 3-tuple of numbers."
  [arg]
  {:pre [(spec/valid? ::tuplable arg)]}
  (let [[input-type _] (spec/conform ::tuplable arg)]
    (case input-type
      :num [arg arg arg]
      :tuple
        (case (count arg)
          1 (apply concat (repeat 3 arg))
          2 (let [[xy z] arg] [xy xy z])
          arg))))

(defn- cleaning-tuple-fn
  "Take a scad-clj function and a 3-tuple argument for which it does nothing.
  Return a corresponding function that will take a wider range of inputs and
  will return nil when such an input matches the neutral input given here."
  [model-function neutral-arg]
  (fn [arg & block]
    (let [expanded (expand-tuple arg)
          maybe-arg (when (not= expanded neutral-arg) expanded)]
      (apply (clean model-function maybe-arg) block))))


;;;;;;;;;;;;;;;;;;;;;;;;;
;; INTERFACE FUNCTIONS ;;
;;;;;;;;;;;;;;;;;;;;;;;;;

(defn long-hex-diagonal
  "The “long diagonal” of a hexagon, computed from the “short” or flat-to-flat
  diagonal."
  [short-diagonal]
  (* 2 (/ short-diagonal (Math/sqrt 3))))

(def maybe-rotate (cleaning-tuple-fn model/rotate [0 0 0]))
(def maybe-scale (cleaning-tuple-fn model/scale [1 1 1]))
(def maybe-translate (cleaning-tuple-fn model/translate [0 0 0]))
