;;; Utilities for design for manufacturability (DFM).
;;; In this case, it’s mainly design for additive manufacturing (DFAM),
;;; pre-empting 3D printer inaccuracies in the xy plane.

(ns scad-tarmi.dfm
  (:require [scad-tarmi.maybe :as maybe]))

;;;;;;;;;;;;;;
;; INTERNAL ;;
;;;;;;;;;;;;;;

(defn- ratio [nominal error] (/ (- nominal error) nominal))


;;;;;;;;;;;;;;;;;;;;;;;;;
;; INTERFACE FUNCTIONS ;;
;;;;;;;;;;;;;;;;;;;;;;;;;

(defn error-fn
  "Take an error: A measurement in mm of material added by a printer in the xy
  plane. Return a compensator function that, when called with an intended
  measurement, neutralizes the error for that measurement.

  The two measurements (error and nominal) should be of a kind, whether it’s
  radius or diameter. The convention is to use full size (diameter).

  Called with one argument, a number, the closure supports the arithmetic
  adjustment of an individual dimensional input. Using the function entirely
  in this manner is typically difficult but saves on transformations in the
  OpenSCAD output, thus improving rendering performance.

  Called with two arguments, a number and an option map, the closure again
  adjusts the stated dimensional input, and can do so even for a positive
  error, when non-default {:negative false} is part of the option map.

  Called with more arguments, the closure implements scad-tarmi’s maybe/scale,
  returning scad-clj code, not a number."
  ([] (error-fn (- 0.5)))
  ([error] (error-fn (/ error -2) error))
  ([positive-error negative-error]
   (fn compensator
     ([nominal] (- nominal negative-error))
     ([nominal options & block]
      {:pre [(number? nominal)
             (map? options)]}
      (let [{:keys [negative x y z]
             :or {negative true, x true, y true, z false}} options
            n (ratio nominal (if negative negative-error positive-error))
            factors (vec (map #(if % n 1) [x y z]))]
        (if (nil? block)
          ;; No shape was passed. Return a number.
          (if negative
            (compensator nominal)  ; Recurse to the simpler form.
            (- nominal positive-error))
          (apply (partial maybe/scale factors) block)))))))

(def none
  "An error function that leaves no trace in OpenSCAD code."
  (error-fn 0))
