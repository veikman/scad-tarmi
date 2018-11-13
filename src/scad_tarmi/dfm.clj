;;; Utilites for design for manufacturability (DFM).

;;; This module produces functions used elsewhere in scad-tarmi for
;;; pre-empting 3D printer inaccuracies, particularly in the xy plane.

(ns scad-tarmi.dfm
  (:require [clojure.spec.alpha :as spec]
            [scad-tarmi.core :refer [maybe-scale]]))


(spec/def ::accordion (spec/coll-of number? :min-count 1 :max-count 3))
(spec/def ::tuplable (spec/or :num number? :tuple ::accordion))

(defn expand-xy-tuple
  "Expand a number or short tuple of numbers to a 3-tuple of numbers,
  emphasizing the first two (x and y) and letting z default to 1 when
  a single number is provided."
  [arg]
  {:pre [(spec/valid? ::tuplable arg)]}
  (let [[input-type _] (spec/conform ::tuplable arg)]
    (case input-type
      :num [arg arg 1]
      :tuple
        (case (count arg)
          1 (expand-xy-tuple (first arg))
          2 (let [[xy z] arg] [xy xy z])
          arg))))

(defn maybe-xy-scale
  "A wrapper for maybe-scale. The first argument is expanded for the xy plane."
  [factor & block]
  (apply (partial maybe-scale (expand-xy-tuple factor)) block))

(defn error-fn
  "Take an error: A measurement in mm of material added by a printer in the xy
  plane. Return an error function that, when called with an intended
  measurement (nominal), produces a third function that returns a scale
  transformation for adjusting a model so as to neutralize the error.

  Error and nominal should refer to the same kind of measurement, whether it’s
  radius or diameter. The convention is to use full size (diameter).

  This function is useful if the size of errors in manufacturing is
  absolute, such that scaling should be applied to each sensitive part of a
  model based on its particular size, not to the model as a whole. Where
  extreme DFM is required, you’re better off adjusting every dimensional input
  directly, thus saving on scale transformations."
  [error]
  (fn [nominal]
    (let [target (+ nominal error)
          ratio (/ target nominal)]
      (partial maybe-xy-scale ratio))))

(defn symmetry
  "Prepare a set of error functions for different parts of a model:
  Positive as well as negative space.

  Functions elsewhere, e.g. the threading module, take such pairs and apply
  the positive function to pre-shrink shapes and the negative function to
  expand negative space for the same reason.

  The default error used here represents a common DFM printer with a 0.5 mm
  nozzle and common PLA filament. The assumption is that such a printer, and
  its material, take about 0.5 mm from the diameter of an aperture in the
  printed model. This would be the correct size for the error parameter if,
  for example, a 3 mm hole in a model prints as 2.5 mm and you want it at 3.

  For an especially accurate printer, less-expanding material, reduced flow,
  elastic materials, a tight fit etc., use a smaller number."
  [& {:keys [error] :or {error 0.5}}]
  {:negative (error-fn error)        ; Enlarge negatives.
   :positive (error-fn (- error))})  ; Shrink positives.

(def none
  "A pair of scaling functions that leave no trace in OpenSCAD code."
  (symmetry :error 0))
