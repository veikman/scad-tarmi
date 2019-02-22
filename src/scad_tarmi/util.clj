;;; Slightly more advanced utilities.

(ns scad-tarmi.core
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
   (apply maybe/union
     (map (partial apply model/hull) (partition chunk-size step block)))))
