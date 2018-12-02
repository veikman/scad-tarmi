(ns scad-tarmi.core-test
  (:require [clojure.test :refer :all]
            [scad-tarmi.core :refer :all]))

(deftest trig
  (testing "conversions."
    (is (= (counterclockwise-from-x-axis 0) (* 1/2 π)))
    (is (= (counterclockwise-from-x-axis π) (* 3/2 π)))
    (is (= (counterclockwise-from-x-axis (* 3/2 π)) π))
    (is (= (counterclockwise-from-x-axis (* 1/2 π)) 0.0))
    (is (= (clockwise-from-y-axis 0) (* 3/2 π)))
    (is (= (clockwise-from-y-axis π) (* 1/2 π)))
    (is (= (clockwise-from-y-axis (* 1/2 π)) π))
    (is (= (clockwise-from-y-axis (* 3/2 π)) 0.0))))
