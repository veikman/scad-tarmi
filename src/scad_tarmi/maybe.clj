;;; Version of scad-clj functions that drop out of OpenSCAD output when they
;;; would do nothing to a model.

(ns scad-tarmi.maybe
  (:require [scad-clj.model :as model]))

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

(defn- all-zero? [candidate] (every? zero? candidate))

(defn- transformer
  "Take a scad-clj function and an argument for which it does nothing.
  Return a corresponding function that will take a wider range of inputs and
  will return nil when such an input matches the neutral input given here."
  [model-function neutral-predicate]
  (fn [arg & block]
    (apply (clean model-function (when-not (neutral-predicate arg) arg)) block)))

(defn- shape
  "Like transformer but for scad-clj functions that produce shapes.
  Where the neutral predicate is met, the closure will return nil."
  [model-function neutral-predicate]
  (fn [& args]
    (when-not (neutral-predicate args) (apply model-function args))))

(defn- geometric-boolean
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

(def rotate (transformer model/rotate all-zero?))

(def scale (transformer model/scale #(= % [1 1 1])))

(def translate (transformer model/translate all-zero?))

(def union (geometric-boolean model/union))

(def intersection (geometric-boolean model/intersection))

(def difference (geometric-boolean model/difference))

(def polygon (transformer model/polygon #(empty? (first %))))
