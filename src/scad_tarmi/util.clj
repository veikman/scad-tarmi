;;; Slightly more advanced utilities.

(ns scad-tarmi.util
  (:require [scad-clj.model :as model]
            [scad-tarmi.maybe :as maybe]))

;;;;;;;;;;;;;;;;;;;;;;;;;
;; INTERFACE FUNCTIONS ;;
;;;;;;;;;;;;;;;;;;;;;;;;;

(defn loft
  "Link passed shapes by a series of convex hulls. By default, link by pairs.
  While this function shares its etymological root with lofting as the term is
  used in WYSIWYG 3D modeling software, it does not generate any new primitives
  or intermediate planar sections to fill out a shape along a given path. The
  path must be created first and all objects placed along it before they are
  passed to this function. The function merely wraps each pair/trio etc. of the
  sequence in hulls."
  ([block]
   (loft 2 1 block))
  ([chunk-size block]
   (loft chunk-size 1 block))
  ([chunk-size step block]
   {:pre [(integer? chunk-size)
          (not (zero? chunk-size))
          (integer? step)
          (not (zero? step))]}
   (cond
     ;; If no shapes are passed return nil.
     (empty? block) nil
     ;; If one shape is passed, return it.
     (= 1 (count block)) (first block)
     ;; Hull other short input (partitioning would return an empty list).
     (> chunk-size (count block)) (apply model/hull block)
     :else
       (apply maybe/union
         (map (partial apply model/hull) (partition chunk-size step block))))))

(defn radiate
  "Link a series of shapes through a single, central shape."
  [hub spokes]
  (loft 2 2 (concat [hub] (interpose hub spokes))))
