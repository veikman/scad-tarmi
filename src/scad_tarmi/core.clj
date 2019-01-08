(ns scad-tarmi.core)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; CONSTANTS AND SHORTCUTS ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def abs #(max % (- %)))
(def √ #(Math/sqrt %))
(def sin #(Math/sin %))
(def cos #(Math/cos %))

(def π Math/PI)
(def τ (* 2 π))


;;;;;;;;;;;;;;;;;;;;;;;;;
;; INTERFACE FUNCTIONS ;;
;;;;;;;;;;;;;;;;;;;;;;;;;

(defn long-hex-diagonal
  "The “long diagonal” of a hexagon, computed from the “short” or flat-to-flat
  diagonal."
  [short-diagonal]
  (* 2 (/ short-diagonal (Math/sqrt 3))))
