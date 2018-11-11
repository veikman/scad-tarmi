(ns scad-tarmi.core-test
  (:require [clojure.test :refer :all]
            [scad-clj.model :as reference]
            [scad-tarmi.core :refer :all]))

(deftest upstream-reference
  (testing "rotate’s original behaviour for one shape."
    (is (= (reference/rotate [0 0 0] ::a)
           `(:rotatec [0 0 0] (::a)))))
  (testing "rotate’s original behaviour for two shapes."
    (is (= (reference/rotate [0 0 0] ::a ::b)
           `(:rotatec [0 0 0] (::a ::b)))))
  (testing "scale’s original behaviour for one shape."
    (is (= (reference/scale [1 1 1] ::a)
           `(:scale [1 1 1] ::a))))
  (testing "scale’s original behaviour for two shapes."
    (is (= (reference/scale [1 1 1] ::a ::b)
           `(:scale [1 1 1] ::a ::b)))))

(deftest maybes
  (testing "maybe-rotate with its full neutral argument and one shape."
    (is (= (maybe-rotate [0 0 0] ::a)
           `(::a))))
  (testing "maybe-rotate with its neutral argument and two shapes."
    (is (= (maybe-rotate [0 0 0] ::a ::b)
           `(::a ::b))))
  (testing "maybe-rotate for a non-neutral argument and one shape."
    (is (= (maybe-rotate [0 0 1] ::a)
           `(:rotatec [0 0 1] (::a)))))
  (testing "maybe-rotate for a non-neutral argument and two shapes."
    (is (= (maybe-rotate [0 1 0] ::a ::b)
           `(:rotatec [0 1 0] (::a ::b))))))

(deftest argument-expansion
  (testing "maybe-scale for a neutral number argument"
    (is (= (maybe-scale 1 ::a)
           `(::a))))
  (testing "maybe-scale for a neutral 1-tuple argument"
    (is (= (maybe-scale [1] ::a)
           `(::a))))
  (testing "maybe-scale for a neutral 2-tuple argument"
    (is (= (maybe-scale [1 1] ::a)
           `(::a))))
  (testing "maybe-scale for a neutral 3-tuple argument"
    (is (= (maybe-scale [1 1 1] ::a)
           `(::a))))
  (testing "maybe-scale for a non-neutral number argument"
    (is (= (maybe-scale 2 ::a)
           `(:scale [2 2 2] ::a))))
  (testing "maybe-scale for a non-neutral 1-tuple argument"
    (is (= (maybe-scale [2] ::a)
           `(:scale [2 2 2] ::a))))
  (testing "maybe-scale for a non-neutral 2-tuple argument"
    (is (= (maybe-scale [2 1] ::a)
           `(:scale [2 2 1] ::a))))
  (testing "maybe-scale for a non-neutral 3-tuple argument"
    (is (= (maybe-scale [1 2 2] ::a)
           `(:scale [1 2 2] ::a)))))
