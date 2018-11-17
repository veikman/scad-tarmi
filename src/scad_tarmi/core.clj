(ns scad-tarmi.core
  (:require [clojure.spec.alpha :as spec]
            [scad-clj.model :as model]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; CONSTANTS AND SHORTCUTS ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def √ #(Math/sqrt %))
(def sin #(Math/sin %))
(def cos #(Math/cos %))

(def π Math/PI)
(def τ (* 2 π))


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

(defn- maybe-transform
  "Take a scad-clj function and an argument for which it does nothing.
  Return a corresponding function that will take a wider range of inputs and
  will return nil when such an input matches the neutral input given here."
  [model-function neutral-arg]
  (fn [arg & block]
    (apply (clean model-function (when (not= arg neutral-arg) arg)) block)))

(defn- maybe-boolean
  "Take a Boolean scad-clj function.
  Return a corresponding function that will omit the scad-clj function where
  it would do nothing."
  [model-function]
  (fn [& block]
    (let [non-nil (remove nil? block)]
      (if (empty? (rest non-nil))
        (first non-nil)
        (apply model-function non-nil)))))


;;;;;;;;;;;;;;;;;;;;;;;;;
;; INTERFACE FUNCTIONS ;;
;;;;;;;;;;;;;;;;;;;;;;;;;

(defn long-hex-diagonal
  "The “long diagonal” of a hexagon, computed from the “short” or flat-to-flat
  diagonal."
  [short-diagonal]
  (* 2 (/ short-diagonal (Math/sqrt 3))))

(def maybe-rotate
  "A rotate element that drops out of OpenSCAD code when it would do nothing."
  (maybe-transform model/rotate [0 0 0]))

(def maybe-scale
  "A scale element that drops out when it would do nothing."
  (maybe-transform model/scale [1 1 1]))

(def maybe-translate
  "A translate element that drops out when it would do nothing."
  (maybe-transform model/translate [0 0 0]))

(def maybe-union
  "A union element that drops out when it would do nothing."
  (maybe-boolean model/union))

(def maybe-intersection
  "An intersection element that drops out when it would do nothing."
  (maybe-boolean model/intersection))

(def maybe-difference
  "A difference element that drops out when it would do nothing."
  (maybe-boolean model/difference))
