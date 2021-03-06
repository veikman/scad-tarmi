(ns scad-tarmi.dfm-test
  (:require [clojure.test :refer [deftest testing is]]
            [scad-tarmi.dfm :refer [error-fn none]]))

(deftest error-functions
  (testing "scalar/arithmetic use of error-fn."
    (let [c (error-fn)]
      (is (= (c 10) 10.5))
      (is (= (c 10 {:negative true}) 10.5))
      (is (= (c 10 {:negative false}) 9.75))
      (is (= (c 10 {:negative false, :factor 3}) 9.25)))
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
           `(:scale [19/20 19/20 1] ::a)))))

(deftest no-error
  (testing "the none function with default options."
    (is (= (none 10 {} ::a)
           `(::a))))
  (testing "the none function for a positive shape."
    (is (= (none 10 {:negative false} ::a)
           `(::a)))))
