(ns scad-tarmi.maybe-test
  (:require [clojure.test :refer :all]
            [scad-clj.model :as reference]
            [scad-tarmi.maybe :as maybe]))

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

(deftest maybe-rotate-fn
  (testing "maybe/rotate with its full neutral integer argument and one shape."
    (is (= (maybe/rotate [0 0 0] ::a)
           `(::a))))
  (testing "maybe/rotate with its neutral non-integer argument."
    (is (= (maybe/rotate [0 0.0 0] ::a)
           `(::a))))
  (testing "maybe/rotate with its neutral integer argument and two shapes."
    (is (= (maybe/rotate [0 0 0] ::a ::b)
           `(::a ::b))))
  (testing "maybe/rotate for a non-neutral argument and one shape."
    (is (= (maybe/rotate [0 0 1] ::a)
           `(:rotatec [0 0 1] (::a)))))
  (testing "maybe/rotate for a non-neutral argument and two shapes."
    (is (= (maybe/rotate [0 1 0] ::a ::b)
           `(:rotatec [0 1 0] (::a ::b))))))

(deftest maybe-scale-fn
  (testing "maybe/scale with its full neutral argument and one shape."
    (is (= (maybe/scale [1 1 1] ::a)
           `(::a))))
  (testing "maybe/scale with its neutral argument and two shapes."
    (is (= (maybe/scale [1 1 1] ::a ::b)
           `(::a ::b))))
  (testing "maybe/scale for a non-neutral argument and one shape."
    (is (= (maybe/scale [1 1 2] ::a)
           `(:scale [1 1 2] ::a))))
  (testing "maybe/scale for a non-neutral argument and two shapes."
    (is (= (maybe/scale [1 1 2] ::a ::b)
           `(:scale [1 1 2] ::a ::b)))))

(deftest maybe-shape-fn
  (testing "maybe/polygon with its neutral argument only."
    (is (= (maybe/polygon [])
           nil)))
  (testing "maybe/polygon with a single vertex."
    (is (= (maybe/polygon [[1 0]])
           `(:polygon {:points [[1 0]]}))))
  (testing "maybe/polygon with points and paths."
    (is (= (maybe/polygon [[1 0]] [0])
           `(:polygon {:points [[1 0]] :paths [0] :convexity nil})))))
