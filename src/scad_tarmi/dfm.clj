;;; Utilities for design for manufacturability (DFM).
;;; In this case, it’s mainly design for additive manufacturing (DFAM),
;;; pre-empting 3D printer inaccuracies in the xy plane.

;;; error-fn in this module takes an anticipated error size and returns a
;;; closure to pre-empt errors in printing. The closure, called a compensator,
;;; takes at least a nominal size. The two measurements (error and nominal)
;;; should be of a kind, whether it’s radius or diameter etc. The convention is
;;; to use full size, which for a circle is diameter.

;;; Called with one or two arguments, a compensator supports the arithmetic
;;; adjustment of an individual dimensional input, and returns a number.  The
;;; mandatory first argument must be a number, the optional second argument an
;;; option map. Called with more arguments, which must be intermediate
;;; scad-clj code for 3D shapes, a compensator returns more such code.

;;; Using the function entirely in the numeric manner is typically difficult
;;; but saves on transformations in the OpenSCAD output, thus improving
;;; rendering performance.

;;; An option map may include:
;;; - :negative   true if the measurement pertains to negative space inside a
;;;               shape, where errors in DFM printing tend be largest and most
;;;               important to functionality.
;;; - :x, :y, :z  true if the corresponding dimension is to be included in the
;;;               scaling of a scad-clj shape passed to the compensator.
;;;               The default assumption is that the shape is to be printed
;;;               in its default orientation with high intrinsic z-axis accuracy,
;;;               so x and y are true and z is false.
;;; - :factor     An error coefficient adjusting for fit. A tight fit would use
;;;               a factor between 0 and 1, while a loose fit would use > 1.

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
  "Take an error: A measurement in mm of material lost by a printer in the xy
  plane. Return a compensator function that, when called with an intended
  measurement, neutralizes the error for that measurement."
  ([] (error-fn -0.5))
  ([error] (error-fn (/ error -2) error))
  ([positive-error negative-error]
   (fn compensator
     ([nominal] (compensator nominal {}))
     ([nominal options & block]
      {:pre [(number? nominal)
             (map? options)]}
      (if (zero? nominal)
        0
        (let [{:keys [negative x y z factor]
               :or {negative true, x true, y true, z false, factor 1}} options
              deviation (* factor (if negative negative-error positive-error))]
          (if (empty? block)
            ;; No shape was passed. Return a non-negative number.
            (max 0 (- nominal deviation))
            ;; Return a shape.
            (let [n (max 0 (ratio nominal deviation))
                  factors (vec (map #(if % n 1) [x y z]))]
              (apply (partial maybe/scale factors) block)))))))))

(def none
  "An error function that leaves no trace in OpenSCAD code."
  (error-fn 0))
