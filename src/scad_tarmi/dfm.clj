;;; Utilites for design for manufacturability (DFM).
;;; In this case, it’s mainly design for additive manufacturing (DFAM).

;;; This module produces functions used elsewhere in scad-tarmi for
;;; pre-empting 3D printer inaccuracies, particularly in the xy plane.

(ns scad-tarmi.dfm
  (:require [clojure.spec.alpha :as spec]
            [scad-tarmi.core :refer [maybe-scale]]))

;;;;;;;;;;;;;;
;; INTERNAL ;;
;;;;;;;;;;;;;;

(spec/def ::accordion (spec/coll-of number? :min-count 1 :max-count 3))
(spec/def ::tuplable (spec/or :num number? :tuple ::accordion))

(defn- ratio [nominal error] (/ (+ nominal error) nominal))


;;;;;;;;;;;;;;;;;;;;;;;;;
;; INTERFACE FUNCTIONS ;;
;;;;;;;;;;;;;;;;;;;;;;;;;

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
  plane. Return a compensator function that, when called with an intended
  measurement, neutralizes the error for that measurement.

  The two measurements (error and nominal) should be of a kind, whether it’s
  radius or diameter. The convention is to use full size (diameter).

  This function represents a compromise between several concerns in DFM. It is
  based on these assumptions:

  * The printer is correctly calibrated, but the combination of printer
    firmware and slicer software causes printed models to be larger than
    indicated by their blueprint.

  * z-level accuracy is beyond software control, as in FDM.

  * The size of the error is absolute for a given combination of printer
    nozzle, flow rate, material properties, temperature, cooling method etc.
    Thus, unlike errors due to a process of annealing, the size of the error
    predicted by this function does not itself vary with the size of the
    printed part, except at very small multiples of the nozzle diameter.

  * The size of the error is about twice as big on the inside of a gap in the
    model as it is on the outside. In other words, if the outside diameter of
    a model grows by 0.1 mm in printing, a hole (i.e. negative space) in that
    model, caused by a difference() operation in OpenSCAD, will shrink by
    0.2 mm.

  These assumptions, and default values applied in this function, are based
  on tests of a LulzBot TAZ 6, an FDM printer with a 0.5 mm nozzle, running
  its default Marlin firmware (version current as of 2018-11), slicing in Cura
  (LulzBot edition, v3.2) and printing PLA at 100% flow. Results will vary
  with other printers, slicers and materials.

  Furthermore, the function assumes that negative space will be used to fit
  other parts, such as threaded fasteners. Therefore, negative space is the
  primary use case. error-fn expects a negative value as a measurement of error
  and will assume that any passed nominal measurement passed to its closure
  should be enlarged.

  Called with one argument, a number, the closure supports the arithmetic
  adjustment of an individual dimensional input. Using the function entirely
  in this manner is typically difficult but saves on transformations in the
  OpenSCAD output, thus improving rendering performance.

  Called with more arguments, the closure implements maybe-scale.

  Refer to the threaded module for example usage."
  ([] (error-fn (- 0.5)))
  ([error] (error-fn (/ error -2) error))
  ([positive-error negative-error]
   (fn
     ([nominal] (+ nominal negative-error))
     ([nominal options & block]
      {:pre [(number? nominal)
             (map? options)]}
      (let [{:keys [negative x y z]
             :or {negative true, x true, y true, z false}} options
            n (ratio nominal (if negative negative-error positive-error))
            factors (vec (map #(if % n 1) [x y z]))]
        (apply (partial maybe-scale factors) block))))))

(def none
  "An error function that leaves no trace in OpenSCAD code."
  (error-fn 0))
