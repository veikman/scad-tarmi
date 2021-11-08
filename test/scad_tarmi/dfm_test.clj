(ns scad-tarmi.dfm-test
  (:require [clojure.test :refer [deftest testing is]]
            [scad-tarmi.dfm :refer [error-fn none]]))

(deftest error-functions
  (testing "scalar/arithmetic use of error-fn."
    (let [c (error-fn 10)]
      (is (= (c 0) 0))
      (is (= (c 0.0) 0))
      (is (= (c 1) 0))
      (is (= (c 9) 0))
      (is (= (c 10) 0))
      (is (= (c 11) 1))
      (is (= (c 12) 2))
      (is (= (c 100) 90))
      (is (= (c 100 {:negative true}) 90))
      (is (= (c 100 {:negative false}) 105))
      (is (= (c 100 {:negative false, :factor 3}) 115)))
    (let [c (error-fn 1)]
      (is (= (c 10) 9))
      (is (= (c 10 {:negative true}) 9))
      (is (= (c 10 {:negative true, :factor 0.5}) 9.5))
      (is (= (c 10 {:negative false}) 21/2))))
  (testing "vector/multiplicative use of error-fn with default options."
    (is (= ((error-fn -1) 10 {} ::a)
           `(:scale [11/10 11/10 1] ::a))))
  (testing "vector/multiplicative use of error-fn for a positive shape."
    (is (= ((error-fn -1) 10 {:negative false} ::a)
           `(:scale [19/20 19/20 1] ::a))))
  (testing "default error-fn."
    (let [c (error-fn)]
      (is (= (c -0.6) 0))
      (is (= (c -0.5) 0.0))
      (is (= (c -0.4) 0.09999999999999998))
      (is (= (c -0.3) 0.2))
      (is (= (c -0.2) 0.3))
      (is (= (c -0.1) 0.4))
      (is (= (c 0.0) 0))
      (is (= (c 0.1) 0.6))
      (is (= (c 0.2) 0.7))
      (is (= (c 0.3) 0.8))
      (is (= (c 0.4) 0.9))
      (is (= (c 0.5) 1.0))
      (is (= (c 1 {} ::a) `(:scale [1.5 1.5 1] ::a))))))

(deftest no-error
  (testing "the none function with default options."
    (is (= (none 10 {} ::a)
           `(::a))))
  (testing "the none function for a positive shape."
    (is (= (none 10 {:negative false} ::a)
           `(::a)))))
