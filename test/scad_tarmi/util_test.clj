(ns scad-tarmi.util-test
  (:require [clojure.test :refer :all]
            [scad-clj.model :refer [union hull circle]]
            [scad-tarmi.util :as util]))


(deftest pairwise-loft
  (testing "the loft function with an empty sequence."
    (is (= (util/loft [])
           nil)))
  (testing "the loft function with a one-item sequence."
    (is (= (util/loft [(circle 1)])
           (circle 1))))
  (testing "the loft function with a two-item sequence."
    (is (= (util/loft [(circle 1) (circle 2)])
           (hull (circle 1) (circle 2)))))
  (testing "the loft function with a three-item sequence."
    (is (= (util/loft [(circle 1) (circle 2) (circle 3)])
           (union
             (hull (circle 1) (circle 2))
             (hull (circle 2) (circle 3))))))
  (testing "the loft function with a four-item sequence."
    (is (= (util/loft (map circle (range 1 5)))
           (union
             (hull (circle 1) (circle 2))
             (hull (circle 2) (circle 3))
             (hull (circle 3) (circle 4)))))))

(deftest triangular-loft
  (testing "the loft function for trios, with an empty sequence."
    (is (= (util/loft 3 [])
           nil)))
  (testing "the loft function for trios, with a one-item sequence."
    (is (= (util/loft 3 [(circle 1)])
           (circle 1))))
  (testing "the loft function for trios, with a two-item sequence."
    (is (= (util/loft 3 [(circle 1) (circle 2)])
           (hull (circle 1) (circle 2)))))
  (testing "the loft function for trios, with a three-item sequence."
    (is (= (util/loft 3 [(circle 1) (circle 2) (circle 3)])
           (hull (circle 1) (circle 2) (circle 3)))))
  (testing "the loft function for trios, with a four-item sequence."
    (is (= (util/loft 3 (map circle (range 1 5)))
           (union
             (hull (circle 1) (circle 2) (circle 3))
             (hull (circle 2) (circle 3) (circle 4)))))))

(deftest filtered-loft
  (testing "the loft function with a nil element."
    (is (= (util/loft [(circle 1) (when false (circle 2))])
           (circle 1)))))

(deftest radiate
  (testing "the radiate function with an empty sequence."
    (is (= (util/radiate (circle 1) [])
           (circle 1))))
  (testing "the radiate function with a one-item sequence."
    (is (= (util/radiate (circle 1) [(circle 2)])
           (hull (circle 1) (circle 2)))))
  (testing "the radiate function with a two-item sequence."
    (is (= (util/radiate (circle 1) [(circle 2) (circle 3)])
           (union
             (hull (circle 1) (circle 2))
             (hull (circle 1) (circle 3)))))))
