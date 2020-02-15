(ns scad-tarmi.dfm-test
  (:require [clojure.test :refer [deftest testing is]]
            [scad-tarmi.dfm :refer [error-fn none]]))

(deftest error-functions
  (testing "scalar/arithmetic use of error-fn."
    (is (= ((error-fn -1) 10)
           11)))
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
