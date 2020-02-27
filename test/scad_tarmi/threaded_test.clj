(ns scad-tarmi.threaded-test
  (:require [clojure.test :refer [deftest testing is]]
            [scad-tarmi.threaded :as threaded]))


(deftest flare-sample
  "Test a flaring closure for a non-standard body of height 4."
  (let [flare (threaded/flare 1 2 4)]
    (testing "Below base level."
      (is (= (flare 0 -1) [0 -0.001]))
      (is (= (flare 1 -1) [2 -0.001]))
      (is (= (flare 2 -1) [2 -0.001])))
    (testing "Base level."
      (is (= (flare 0 0) [0 0]))
      (is (= (flare 1 0) [2 0]))
      (is (= (flare 2 0) [2 0])))
    (testing "Very near base."
      (is (= (flare 0 0.1) [0 0.1]))
      (is (= (flare 1 0.1) [1.9 0.1]))
      (is (= (flare 2 0.1) [2 0.1])))
    (testing "Sort of near base."
      (is (= (flare 0 0.5) [0 0.5]))
      (is (= (flare 1 0.5) [1.5 0.5]))
      (is (= (flare 2 0.5) [2 0.5])))
    (testing "Base-to-middle transition zone."
      (is (= (flare 0 1) [0 1]))
      (is (= (flare 1 1) [1 1]))
      (is (= (flare 2 1) [2 1])))
    (testing "Precise middle."
      (is (= (flare 0 2) [0 2]))
      (is (= (flare 1 2) [1 2]))
      (is (= (flare 2 2) [2 2])))
    (testing "Middle-to-top transition zone."
      (is (= (flare 0 3) [0 3]))
      (is (= (flare 1 3) [1 3]))
      (is (= (flare 2 3) [2 3])))
    (testing "Sort of near top."
      (is (= (flare 0 3.5) [0 3.5]))
      (is (= (flare 1 3.5) [1.5 3.5]))
      (is (= (flare 2 3.5) [2 3.5])))
    (testing "Very near top."
      (is (= (flare 0 3.9) [0 3.9]))
      (is (= (flare 1 3.9) [1.9 3.9]))
      (is (= (flare 2 3.9) [2 3.9])))
    (testing "Top level."
      (is (= (flare 0 4) [0 4]))
      (is (= (flare 1 4) [2 4]))
      (is (= (flare 2 4) [2 4])))
    (testing "Above top level."
      (is (= (flare 0 5) [0 4.001]))
      (is (= (flare 1 5) [2 4.001]))
      (is (= (flare 2 5) [2 4.001])))))

(deftest length-predictor
    "Testing the predictor of total bolt length."
    (testing "Inadequate parameter set."
      (is (thrown? java.lang.AssertionError
            (threaded/total-bolt-length {:iso-size 3}))))
    (testing "Total length only."
      (is (= (threaded/total-bolt-length
               {:iso-size 3, :total-length 1})
             1)))
    (testing "Head type only."
      (is (= (threaded/total-bolt-length
               {:iso-size 3, :head-type :countersunk})
             3/2)))
    (testing "Mutual redundance."
      (is (= (threaded/total-bolt-length
               {:iso-size 3, :total-length 1, :unthreaded-length 2,
                :threaded-length 3, :head-type :countersunk})
             1)))
    (testing "Sum without explicit total length."
      (is (= (threaded/total-bolt-length
               {:iso-size 3, :unthreaded-length 2,
                :threaded-length 3, :head-type :countersunk})
             13/2))))
