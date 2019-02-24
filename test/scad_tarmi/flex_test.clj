(ns scad-tarmi.flex-test
  (:require [clojure.test :refer :all]
            [scad-clj.model :as reference]
            [scad-tarmi.flex :as flex]))

(deftest flex-translate-fn
  (testing "flex/translate with Cartesian inputs."
    (is (= (flex/translate [0 0 0] [0 0 0])
           [0 0 0]))
    (is (= (flex/translate [1 0 -1] [0 0 0])
           [1 0 -1]))
    (is (= (flex/translate [0 0 0] [1 0 -1])
           [1 0 -1]))
    (is (= (flex/translate [1 0 -1] [1 0 -1])
           [2 0 -2])))
  (testing "flex/translate with neutral parameters and scad-clj input."
    (is (= (flex/translate [0 0 0] ::a)
           `(::a))))
  (testing "flex/translate with non-neutral parameters and scad-clj input."
    (is (= (flex/translate [1 0 -1] ::a)
           (reference/translate [1 0 -1] ::a)))))
