;;; Samples of what scad-tarmi can do.

;;; This module is intended to be run as a script using lein-exec,
;;; https://github.com/kumarshantanu/lein-exec.
;;;
;;; lein-exec suggests adding a try clause to the ns declaration to restrict
;;; execution such that the -main function is only run under lein-exec.
;;; However, as of lein-exec 0.3.7, the suggested trick does not work in
;;; Clojure 1.9.0.
;;;
;;; Simply loading this module will call -main and write several files.

(ns showcase.core
  (:require [clojure.java.io :refer [file make-parents]]
            [clojure.java.shell :refer [sh]]
            [scad-tarmi.threaded :as threaded]
            [scad-clj.scad :refer [write-scad]]))

(defn -main
  [& _]
  (let [write (fn [[filename model]]
                (let [scad (file "showcase" "scad" (str filename ".scad"))
                      stl (file "showcase" "stl" (str filename ".stl"))]
                  (make-parents scad)
                  (spit scad (write-scad model))
                  (make-parents stl)
                  (if-not (zero? (:exit (sh "openscad" "-o" (str stl) (str scad))))
                    (do
                      (println "Rendering" stl "failed")
                      (System/exit 1)))))
        files [["nut-m6"
                (threaded/nut :iso-size 6)]
               ["nut-m4"
                (threaded/nut :iso-size 4)]
               ["nut-m3"
                (threaded/nut :iso-size 3)]]]
     (doall (pmap write files))
     (System/exit 0)))

(apply -main (rest *command-line-args*))
