(ns scad-tarmi.dfm-test
  (:require [clojure.test :refer :all]
            [scad-tarmi.dfm :refer :all]))

(deftest argument-expansion
  (testing "expand-xy-tuple for a default number argument"
    (is (= (expand-xy-tuple 1) [1 1 1])))
  (testing "expand-xy-tuple for a default 1-tuple argument"
    (is (= (expand-xy-tuple [1]) [1 1 1])))
  (testing "expand-xy-tuple for a default 2-tuple argument"
    (is (= (expand-xy-tuple [1 1]) [1 1 1])))
  (testing "expand-xy-tuple for a default 3-tuple argument"
    (is (= (expand-xy-tuple [1 1 1]) [1 1 1])))
  (testing "expand-xy-tuple for a non-default number argument"
    (is (= (expand-xy-tuple 2) [2 2 1])))
  (testing "expand-xy-tuple for a non-default 1-tuple argument"
    (is (= (expand-xy-tuple [2]) [2 2 1])))
  (testing "expand-xy-tuple for a non-default 2-tuple argument"
    (is (= (expand-xy-tuple [2 1]) [2 2 1])))
  (testing "expand-xy-tuple for a non-default 3-tuple argument"
    (is (= (expand-xy-tuple [1 2 2]) [1 2 2]))))
