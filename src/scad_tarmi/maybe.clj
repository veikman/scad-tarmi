;;; Versions of scad-clj functions that drop out of OpenSCAD output when they
;;; would do nothing to a model.

(ns scad-tarmi.maybe
  (:require [scad-clj.model :as model]))

;;;;;;;;;;;;;;
;; INTERNAL ;;
;;;;;;;;;;;;;;

(defn- clean
  "Return a closure over passed function if its argument is truthy.
  Else return a variary identity function. Notice this function is not perfect;
  just like scad-clj, it will return a list regardless of whether it’s given
  one or more arguments. If scad-clj’s behaviour changes, scad-tarmi should change
  to match the upstream.
  The purpose of this function is to filter out, in the Clojure layer, those
  elements of OpenSCAD output that would be no-ops."
  [function arg]
  (if arg (partial function arg) (fn [& args] args)))

(defn- all-zero? [candidate] (every? zero? candidate))

(defn- transformer
  "Take a scad-clj function and a predicate function for its neutral argument.
  Return a corresponding function that will return nil when input matches the
  predicate, else behave like the scad-clj function."
  [model-function neutral-predicate]
  (fn [arg & block]
    (apply (clean model-function (when-not (neutral-predicate arg) arg)) block)))

(defn- shape
  "Like transformer but for scad-clj functions that produce shapes.
  The neutral predicate takes all arguments passed to the closure.
  Where the neutral predicate is met, the closure will return nil."
  [model-function neutral-predicate]
  (fn [& args]
    (when-not (apply neutral-predicate args) (apply model-function args))))

(defn- interactor
  "Take a Boolean scad-clj function like union, or a similar form that operates
  on two or more OpenSCAD blocks without other parameters.
  Return a corresponding function that will omit the scad-clj function where
  it would do nothing."
  [model-function]
  (fn [& block]
    (let [non-nil (remove nil? block)]
      (if (empty? (rest non-nil))
        (first non-nil)
        (apply model-function non-nil)))))

(defn- projector
  "Take a scad-clj function like cut that operates on one or more OpenSCAD
  blocks without other parameters.
  Return a corresponding function that will omit the scad-clj function where
  it would do nothing."
  [model-function]
  (fn [& block]
    (let [non-nil (remove nil? block)]
      (when (seq non-nil)
        (apply model-function non-nil)))))


;;;;;;;;;;;;;;;;;;;;;;;;;
;; INTERFACE FUNCTIONS ;;
;;;;;;;;;;;;;;;;;;;;;;;;;

(def rotate (transformer model/rotate all-zero?))

(def scale (transformer model/scale #(= % [1 1 1])))

(def mirror (transformer model/mirror all-zero?))

(let [plain (transformer model/translate all-zero?)]
  (defn translate
    "Fold child translation operations into the present one."
    [arg & block]
    (if (and (= (count block) 1) (seq? (first block)))
      ;; There is one child operation.
      (let [[c-op c-arg & c-rest] (first block)]
        (if (and (= c-op :translate) (= (count c-rest) 1))
          ;; Merge the two translation operations, recursing.
          (apply (partial translate (mapv + arg c-arg)) c-rest)
      ;; In all other cases, apply the ordinary transformer.
          (apply (partial plain arg) block)))
      (apply (partial plain arg) block))))

(def union (interactor model/union))

(def intersection (interactor model/intersection))

(def difference (interactor model/difference))

(def project (projector model/project))

(def cut (projector model/cut))

(defn projection [is-cut & block] (apply (if is-cut cut project) block))

(def hull (interactor model/hull))

(def polygon (shape model/polygon (fn [p & _] (empty? p))))

(def polyhedron (shape model/polyhedron #(some empty? (take 2 %&))))
