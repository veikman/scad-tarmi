(ns scad-tarmi.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [clojure.spec.alpha :as spec]
            [scad-tarmi.core :as core]))

(deftest point-schemata
  (testing "the 2D point spec."
    (is (not (spec/valid? ::core/point-2d [0])))
    (is (not (spec/valid? ::core/point-2d [1])))
    (is (spec/valid? ::core/point-2d [0 0]))
    (is (spec/valid? ::core/point-2d [1 2]))
    (is (not (spec/valid? ::core/point-2d [0 0 0])))
    (is (not (spec/valid? ::core/point-2d [1 2 3]))))
  (testing "the 3D point spec."
    (is (not (spec/valid? ::core/point-3d [0])))
    (is (not (spec/valid? ::core/point-3d [1])))
    (is (not (spec/valid? ::core/point-3d [0 0])))
    (is (not (spec/valid? ::core/point-3d [1 2])))
    (is (spec/valid? ::core/point-3d [0 0 0]))
    (is (spec/valid? ::core/point-3d [1 2 3]))
    (is (not (spec/valid? ::core/point-3d [0 0 0 0])))
    (is (not (spec/valid? ::core/point-3d [1 2 3 4]))))
  (testing "the 2D-or-3D point spec."
    (is (not (spec/valid? ::core/point-2-3d [0])))
    (is (not (spec/valid? ::core/point-2-3d [1])))
    (is (spec/valid? ::core/point-2-3d [0 0]))
    (is (spec/valid? ::core/point-2-3d [1 2]))
    (is (spec/valid? ::core/point-2-3d [0 0 0]))
    (is (spec/valid? ::core/point-2-3d [1 2 3]))
    (is (not (spec/valid? ::core/point-2-3d [0 0 0 0])))
    (is (not (spec/valid? ::core/point-2-3d [1 2 3 4])))))

(deftest coll-schemata
  (testing "the 2D point collection spec."
    (is (spec/valid? ::core/point-coll-2d []))
    (is (not (spec/valid? ::core/point-coll-2d [[0]])))
    (is (spec/valid? ::core/point-coll-2d [[0 0]]))
    (is (spec/valid? ::core/point-coll-2d [[0 0] [0 0]]))
    (is (not (spec/valid? ::core/point-coll-2d [[0 0 0]])))
    (is (not (spec/valid? ::core/point-coll-2d [[0 0 0] [0 0 0]])))
    (is (not (spec/valid? ::core/point-coll-2d [[0 0] [0 0 0]]))))
  (testing "the 3D point collection spec."
    (is (spec/valid? ::core/point-coll-3d []))
    (is (not (spec/valid? ::core/point-coll-3d [[0]])))
    (is (not (spec/valid? ::core/point-coll-3d [[0 0]])))
    (is (not (spec/valid? ::core/point-coll-3d [[0 0] [0 0]])))
    (is (spec/valid? ::core/point-coll-3d [[0 0 0]]))
    (is (spec/valid? ::core/point-coll-3d [[0 0 0] [0 0 0]]))
    (is (not (spec/valid? ::core/point-coll-3d [[0 0] [0 0 0]])))
    (is (not (spec/valid? ::core/point-coll-3d [[0 0 0 0]]))))
  (testing "the 2D-or-3D point collection spec."
    (is (spec/valid? ::core/point-coll-2-3d []))
    (is (not (spec/valid? ::core/point-coll-2-3d [[0]])))
    (is (spec/valid? ::core/point-coll-2-3d [[0 0]]))
    (is (spec/valid? ::core/point-coll-2-3d [[0 0] [0 0]]))
    (is (spec/valid? ::core/point-coll-2-3d [[0 0 0]]))
    (is (spec/valid? ::core/point-coll-2-3d [[0 0 0] [0 0 0]]))
    (is (not (spec/valid? ::core/point-coll-2-3d [[0 0] [0 0 0]])))
    (is (not (spec/valid? ::core/point-coll-2-3d [[0 0 0 0]])))))

(deftest shema-resolution
  (testing "2D-or-3D point collection typing."
    (is (= (spec/conform ::core/point-coll-2-3d [])  ; Ambiguous!
           [:two []]))
    (is (= (spec/conform ::core/point-coll-2-3d [[0 0]])
           [:two [[0 0]]]))
    (is (= (spec/conform ::core/point-coll-2-3d [[0 0 0]])
           [:three [[0 0 0]]]))))

(deftest mean-fn
  (testing "core/mean with no inputs."
    (is (thrown? java.lang.AssertionError (core/mean)))
    (is (thrown? java.lang.AssertionError (apply core/mean []))))
  (testing "core/mean with 1D inputs."
    (is (thrown? java.lang.AssertionError (core/mean [1] [3]))))
  (testing "core/mean with 1 valid 2D input."
    (is (= (core/mean [0 0])
           [0 0]))
    (is (= (core/mean [0 6])
           [0 6])))
  (testing "core/mean with 2 valid 2D inputs."
    (is (= (core/mean [0 0] [0 0])
           [0 0]))
    (is (= (core/mean [0 6] [1 0])
           [1/2 3])))
  (testing "core/mean with mixed 2D and 3D inputs."
    (is (thrown? java.lang.AssertionError (core/mean [1 2] [1 2 3])))))
