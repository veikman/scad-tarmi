(ns scad-tarmi.dfm-test
  (:require [clojure.test :refer :all]
            [scad-tarmi.dfm :refer :all]))

(deftest argument-expansion
  (testing "expand-xy-tuple for a default number argument."
    (is (= (expand-xy-tuple 1) [1 1 1])))
  (testing "expand-xy-tuple for a default 1-tuple argument."
    (is (= (expand-xy-tuple [1]) [1 1 1])))
  (testing "expand-xy-tuple for a default 2-tuple argument."
    (is (= (expand-xy-tuple [1 1]) [1 1 1])))
  (testing "expand-xy-tuple for a default 3-tuple argument."
    (is (= (expand-xy-tuple [1 1 1]) [1 1 1])))
  (testing "expand-xy-tuple for a non-default number argument."
    (is (= (expand-xy-tuple 2) [2 2 1])))
  (testing "expand-xy-tuple for a non-default 1-tuple argument."
    (is (= (expand-xy-tuple [2]) [2 2 1])))
  (testing "expand-xy-tuple for a non-default 2-tuple argument."
    (is (= (expand-xy-tuple [2 1]) [2 2 1])))
  (testing "expand-xy-tuple for a non-default 3-tuple argument."
    (is (= (expand-xy-tuple [1 2 2]) [1 2 2]))))

(deftest error-functions
  (testing "scalar/arithmetic use of error-fn."
    (is (= ((error-fn 1) 10)
           11)))
  (testing "vector/multiplicative use of error-fn with default options."
    (is (= ((error-fn 1) 10 {} ::a)
           `(:scale [11/10 11/10 1] ::a))))
  (testing "vector/multiplicative use of error-fn for a positive shape."
    (is (= ((error-fn 1) 10 {:negative false} ::a)
           `(:scale [19/20 19/20 1] ::a)))))

(deftest no-error
  (testing "the none function with default options."
    (is (= (none 10 {} ::a)
           `(::a))))
  (testing "the none function for a positive shape."
    (is (= (none 10 {:negative false} ::a)
           `(::a)))))
