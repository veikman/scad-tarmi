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
            [scad-tarmi.threaded :as threaded]
            [scad-clj.scad :refer [write-scad]]))

(defn -main
  [& _]
  (let [write (fn [filename model]
                (let [filepath (file "showcase" (str filename ".scad"))]
                  (make-parents filepath)
                  (spit filepath (write-scad model))))]
    (write "nut-m6" (threaded/nut :iso-size 6))
    (write "nut-m6-101-percent" (threaded/nut :iso-size 6 :scale [1.01 1]))
    (write "nut-m4" (threaded/nut :iso-size 4))
    (write "nut-m4-101-percent" (threaded/nut :iso-size 4 :scale [1.01 1]))))

(apply -main (rest *command-line-args*))
