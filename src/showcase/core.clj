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
            [scad-clj.scad :refer [write-scad]]
            [scad-clj.model :as model]
            [scad-tarmi.threaded :as threaded]
            [scad-tarmi.dfm :as dfm]))

(defn- array
  [& pairs]
  (let [n-steps (dec (/ (count pairs) 2))]
    (model/union
      (model/difference
        (model/translate [(* 5 n-steps) 5 -7.5]
          (model/cube (+ 15 (* 10 n-steps)) 10 15))
        (apply model/union
          (map-indexed (fn [index [_ item]]
                         (model/translate [(* 10 index) 0 0] item))
                       (partition 2 pairs))))
      (apply model/union
        (map-indexed (fn [index [label _]]
                       (model/translate [(* 10 index) -10 -15]
                         (model/rotate [0 0 1]
                          (model/text label :size 2))))
                     (partition 2 pairs))))))

(defn -main
  [& _]
  (let [write
          (fn [[filename item]]
            (let [scad (file "showcase" "scad" (str filename ".scad"))
                  stl (file "showcase" "stl" (str filename ".stl"))]
              (make-parents scad)
              (spit scad (write-scad (model/fs! 1) item))
              (make-parents stl)
              (if-not (zero? (:exit (sh "openscad" "-o" (str stl) (str scad))))
                (do
                  (println "Rendering" stl "failed")
                  (System/exit 1)))))
        files [["nut-m3"
                (threaded/nut :iso-size 3)]
               ["nut-m3-dfm"
                (threaded/nut :iso-size 3 :compensator (dfm/error-fn))]
               ["nut-m4"
                (threaded/nut :iso-size 4)]
               ["nut-m4-dfm"
                (threaded/nut :iso-size 4 :compensator (dfm/error-fn))]
               ["bolt-m4-socket"
                (threaded/bolt :iso-size 4, :head-type :socket,
                               :drive-type :hex, :threaded-length 10,
                               :unthreaded-length 5)]
               ["bolt-m4-countersunk-dfm"
                (threaded/bolt :iso-size 4, :head-type :countersunk,
                               :drive-type :hex, :total-length 10,
                               :compensator (dfm/error-fn))]
               ["holes-m4"
                (array
                  "plain"
                  (threaded/bolt :iso-size 4, :head-type :countersunk,
                                 :total-length 10, :negative true)
                  "mixed"
                  (threaded/bolt :iso-size 4, :head-type :countersunk,
                                 :total-length 10, :threaded-length 5,
                                 :negative true)
                  "socket"
                  (threaded/bolt :iso-size 4, :head-type :socket,
                                 :total-length 10, :negative true)
                  "DFM"
                  (threaded/bolt :iso-size 4, :head-type :countersunk,
                                 :total-length 10, :negative true,
                                 :compensator (dfm/error-fn))
                  "for tap"
                  (threaded/bolt :iso-size 4, :head-type :countersunk,
                                 :threaded false, :total-length 10,
                                 :negative true)
                  "oddball"
                  (threaded/bolt :iso-size 4, :head-type :countersunk,
                                 :threaded false, :total-length 10,
                                 :threaded-length 5, :negative true,
                                 :compensator (dfm/error-fn)))]]]
     (doall (pmap write files))
     (System/exit 0)))

(apply -main (rest *command-line-args*))
