;;; Functions that inspect their arguments and apply either OpenSCAD
;;; functions or their mathematical equivalents from the reckon module.

(ns scad-tarmi.flex
  (:require [clojure.spec.alpha :as spec]
            [scad-tarmi.core :as core]
            [scad-tarmi.maybe :as maybe]
            [scad-tarmi.reckon :as reckon]))

;;;;;;;;;;;;;;
;; INTERNAL ;;
;;;;;;;;;;;;;;

(defn- flex-fn
  "Produce a function that applies one of two subject functions."
  [reckon-fn else-fn]
  (fn [parameters item]
    (if (spec/valid? ::core/point-2d-3d item)
      (reckon-fn parameters item)
      (else-fn parameters item))))


;;;;;;;;;;;;;;;;;;;;;;;;;
;; INTERFACE FUNCTIONS ;;
;;;;;;;;;;;;;;;;;;;;;;;;;

(def translate (flex-fn reckon/translate maybe/translate))

(def rotate (flex-fn reckon/rotate maybe/rotate))
