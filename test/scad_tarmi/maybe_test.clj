(ns scad-tarmi.maybe-test
  (:require [clojure.test :refer :all]
            [scad-clj.model :as reference]
            [scad-tarmi.maybe :as maybe]))

(deftest upstream-reference
  (testing "rotate’s original behaviour."
    (is (= (reference/rotate [0 0 0] ::a)
           `(:rotatec [0 0 0] (::a))))
    (is (= (reference/rotate [0 0 0] ::a ::b)
           `(:rotatec [0 0 0] (::a ::b)))))
  (testing "scale’s original behaviour."
    (is (= (reference/scale [1 1 1] ::a)
           `(:scale [1 1 1] ::a)))
    (is (= (reference/scale [1 1 1] ::a ::b)
           `(:scale [1 1 1] ::a ::b))))
  (testing "mirror’s original behaviour."
    (is (= (reference/mirror [0 0 0] ::a)
           `(:mirror [0 0 0] ::a)))
    (is (= (reference/mirror [0 0 0] ::a ::b)
           `(:mirror [0 0 0] ::a ::b))))
  (testing "polygon’s original behaviour."  ; With idiosyncrasies.
    (is (= (reference/polygon [[0 0]])
           `(:polygon {:points [[0 0]]})))
    (is (= (reference/polygon [[0 0]] [0])
           `(:polygon {:points [[0 0]], :paths [0], :convexity nil})))
    (is (= (reference/polygon [[0 0]] [0] :convexity 0)
           `(:polygon {:points [[0 0]], :paths [0], :convexity 0})))
    (is (thrown? java.lang.IllegalArgumentException
                 (reference/polygon [[0 0]] :convexity 0))))
  (testing "polyhedron’s original behaviour."
    (is (= (reference/polyhedron [[0 0 0]] [[0]])
           `(:polyhedron {:points [[0 0 0]], :faces [[0]]})))
    (is (= (reference/polyhedron [[0 0 0]] [[0]] :convexity 0)
           `(:polyhedron {:points [[0 0 0]], :faces [[0]], :convexity 0})))))

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

(deftest maybe-mirror-fn
  (testing "maybe/mirror with its neutral argument and one shape."
    (is (= (maybe/mirror [0 0 0] ::a)
           `(::a))))
  (testing "maybe/mirror with its neutral argument and two shapes."
    (is (= (maybe/mirror [0 0 0] ::a ::b)
           `(::a ::b))))
  (testing "maybe/mirror for a non-neutral argument and one shape."
    (is (= (maybe/mirror [-1 0 0] ::a)
           `(:mirror [-1 0 0] ::a))))
  (testing "maybe/mirror for a non-neutral argument and two shapes."
    (is (= (maybe/mirror [-1 0 0] ::a ::b)
           `(:mirror [-1 0 0] ::a ::b)))))

(deftest maybe-polygon-fn
  (testing "maybe/polygon with its neutral argument only."
    (is (nil? (maybe/polygon []))))
  (testing "maybe/polygon with a single vertex."
    (is (= (maybe/polygon [[1 0]])
           `(:polygon {:points [[1 0]]}))))
  (testing "maybe/polygon with points and a non-standard path."
    (is (= (maybe/polygon [[1 0]] [0])
           `(:polygon {:points [[1 0]], :paths [0], :convexity nil}))))
  (testing "maybe/polygon with points, path and a non-standard convexity."
    (is (= (maybe/polygon [[1 0]] [0] :convexity 1)
           `(:polygon {:points [[1 0]], :paths [0], :convexity 1})))))

(deftest maybe-polyhedron-fn
  (testing "maybe/polyhedron with two neutral arguments only."
    (is (nil? (maybe/polyhedron [] []))))
  (testing "maybe/polyhedron with a single vertex or single face."
    (is (nil? (maybe/polyhedron [[0 0 0]] [])))
    (is (nil? (maybe/polyhedron [] [[0]]))))
  (testing "maybe/polyhedron with a single vertex and single face."
    (is (= (maybe/polyhedron [[0 0 0]] [[0]])
           `(:polyhedron {:points [[0 0 0]], :faces [[0]]}))))
  (testing "maybe/polyhedron with an optional convexity argument."
    (is (= (maybe/polyhedron [[0 0 0]] [[0]] :convexity 3)
           `(:polyhedron {:points [[0 0 0]], :faces [[0]], :convexity 3})))))
