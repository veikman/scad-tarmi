;;; ISO 262/4017 fasteners and ISO 7089 washers.

(ns scad-tarmi.threaded
  (:require [clojure.spec.alpha :as spec]
            [scad-clj.model :as model]
            [scad-tarmi.core :refer [sin cos τ long-hex-diagonal]]
            [scad-tarmi.maybe :as maybe]
            [scad-tarmi.dfm :as dfm]))

;;;;;;;;;;;;;;;;;;;;;;;;
;; INTERNAL CONSTANTS ;;
;;;;;;;;;;;;;;;;;;;;;;;;

(spec/def ::iso-property #{:hex-head-short-diagonal
                           :hex-head-long-diagonal
                           :head-hex-drive-short-diagonal
                           :head-hex-drive-long-diagonal
                           :iso4017-hex-head-height-nominal
                           :hex-nut-height
                           :socket-diameter
                           :socket-height
                           :button-diameter
                           :button-height
                           :countersunk-diameter
                           :countersunk-height
                           :thread-pitch-coarse
                           :iso7089-inner-diameter
                           :iso7089-outer-diameter
                           :iso7089-thickness})

(def ^:internal iso-data
  "Various constants from ISO metric fastener standards.
  This is a map of nominal ISO bolt diameter (in mm) to various other
  measurements according to spec. Instead of relying on this raw data in
  applications, prefer the more capable datum function."
  {3 {:socket-diameter 5.5
      :hex-nut-height 2.4
      :iso4017-hex-head-height-nominal 2
      :thread-pitch-coarse 0.5
      :head-hex-drive-short-diagonal 2.5
      :iso7089-inner-diameter 3.2
      :iso7089-outer-diameter 7
      :iso7089-thickness 0.5}
   4 {:socket-diameter 7
      :hex-nut-height 3.2
      :iso4017-hex-head-height-nominal 2.8
      :thread-pitch-coarse 0.7
      :head-hex-drive-short-diagonal 3
      :iso7089-inner-diameter 4.3
      :iso7089-outer-diameter 9
      :iso7089-thickness 0.8}
   5 {:socket-diameter 8.5
      :hex-head-short-diagonal 8
      :hex-nut-height 4.7
      :iso4017-hex-head-height-nominal 3.5
      :thread-pitch-coarse 0.8
      :head-hex-drive-short-diagonal 4
      :iso7089-inner-diameter 5.3
      :iso7089-outer-diameter 10
      :iso7089-thickness 1}
   6 {:socket-diameter 10
      :hex-nut-height 5.2
      :iso4017-hex-head-height-nominal 4
      :thread-pitch-coarse 1
      :head-hex-drive-short-diagonal 5
      :iso7089-inner-diameter 6.4
      :iso7089-outer-diameter 12
      :iso7089-thickness 1.6}
   8 {:socket-diameter 13
      :hex-nut-height 6.8
      :iso4017-hex-head-height-nominal 5.3
      :thread-pitch-coarse 1.25
      :head-hex-drive-short-diagonal 6
      :iso7089-inner-diameter 8.4
      :iso7089-outer-diameter 16
      :iso7089-thickness 1.6}})


;;;;;;;;;;;;;;;;;;;;;
;; INTERFACE SPECS ;;
;;;;;;;;;;;;;;;;;;;;;

;; The following items are exposed for use in application data validation.

;; Supported nominal diameters:
(spec/def ::iso-nominal #(contains? iso-data %))

;; Supported types of bolt heads:
(spec/def ::head-type #{:hex     ; Hex head with the diameter of a nut.
                        :socket  ; Full cylindrical counterbore cap.
                        :button  ; Partial (low, smooth-edged) socket cap.
                        :countersunk}) ; Flat head tapering toward the bolt.

;; Supported types of bolt drives:
(spec/def ::drive-type #{:hex})


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; INTERFACE ACCESSOR TO CONSTANTS ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn datum
  "Retrieve or calculate a fact based on the ISO standards."
  [nominal-diameter key]
  {:pre [(spec/valid? ::iso-nominal nominal-diameter)
         (spec/valid? ::iso-property key)]}
  (let [data (get iso-data nominal-diameter)]
   (case key
     :hex-head-short-diagonal  ; Flat-to-flat width of a hex head.
       ;; For most sizes, this value is equal to socket diameter.
       (get data key (datum nominal-diameter :socket-diameter))
     :hex-head-long-diagonal  ; Corner-to-corner diameter of a hex head.
       (long-hex-diagonal
         (datum nominal-diameter :hex-head-short-diagonal))
     :head-hex-drive-long-diagonal
       (long-hex-diagonal
         (datum nominal-diameter :head-hex-drive-short-diagonal))
     :socket-height
       nominal-diameter
     :button-diameter
       (* 1.75 nominal-diameter)
     :button-height
       (* 0.55 nominal-diameter)
     :countersunk-diameter
       (* 2 nominal-diameter)
     :countersunk-height
       ;; Nominal chamfer is 89.9°. Treated here as 90°.
       (/ nominal-diameter 2)
     (if-let [value (get data key)]
       value
       (throw
         (ex-info "Unknown datum"
                  {:nominal-diameter nominal-diameter
                   :requested-property key}))))))

(defn head-height
  "Get the height of an ISO bolt head.
  This is exposed for predicting the results of the bolt function in this
  module, specifically where the transition from head to body will occur."
  [iso-size head-type]
  {:pre [(spec/valid? ::iso-nominal iso-size)
         (spec/valid? ::head-type head-type)]}
  (datum iso-size
    (case head-type
      :hex :iso4017-hex-head-height-nominal
      :socket :socket-height
      :button :button-height
      :countersunk :countersunk-height)))


;;;;;;;;;;;;;;;;;;;;;;;;
;; INTERNAL FUNCTIONS ;;
;;;;;;;;;;;;;;;;;;;;;;;;

(defn- bolt-length
  "Infer the lengths of the unthreaded and threaded parts of a bolt."
  [{:keys [total unthreaded threaded head] :as parameters}]
  (case (map some? [total unthreaded threaded])
    [true  true  true ] (if (= total (+ unthreaded threaded head))
                          [unthreaded threaded]
                          (throw
                            (ex-info "Contradictory bolt length parameters"
                                     {:parameters parameters})))
    [true  true  false] [unthreaded (- total unthreaded head)]
    [true  false false] [0 (- total head)]
    [false false false] (throw
                          (ex-info "Insufficient bolt length parameters"
                                   {:parameters parameters}))
    [false false true ] [0 threaded]
    [false true  true ] [unthreaded threaded]
    [true  false true ] [(- total threaded head) threaded]
    [false true  false] [unthreaded 0]))

(defn- hex-item
  [iso-size height & {:keys [measurement]
                      :or {measurement :hex-head-long-diagonal}}]
  (let [diagonal (datum iso-size measurement)]
    (model/rotate [0 0 (/ Math/PI 6)]
      (model/with-fn 6
        (model/cylinder (/ diagonal 2) height)))))

(defn- bolt-head
  "A model of the head of a bolt, without a drive.
  This function takes an auxiliary ‘countersink-edge-fn’ which computes the
  thickness of a countersunk head at its edge. The computed thickness will,
  effectively, lengthen the head, potentially producing a negative that is too
  shallow for the threaded portion of a real screw.
  The default ‘countersink-edge-fn’ is a slight exaggeration intended
  to make sure the head will not protrude with normal printing defects."
  [{:keys [iso-size head-type countersink-edge-fn compensator]
    :or {countersink-edge-fn (fn [iso-size] (/ (Math/log iso-size) 8))}
    :as options}]
  {:pre [(spec/valid? ::iso-nominal iso-size)
         (spec/valid? ::head-type head-type)]}
  (let [height (head-height iso-size head-type)]
    (case head-type
      :hex
        (compensator (datum iso-size :hex-head-long-diagonal) {}
          (hex-item iso-size height))
      :socket
        (let [diameter (datum iso-size :socket-diameter)]
          (model/cylinder (/ (compensator diameter) 2) height))
      :button
        (let [diameter (datum iso-size :button-diameter)]
          (model/cylinder (/ (compensator diameter) 2) height))
      :countersunk
        (let [diameter (datum iso-size :countersunk-diameter)
              edge (countersink-edge-fn iso-size)]
          (model/hull
            (model/translate [0 0 (+ (/ edge -2) (/ height 2))]
              (model/cylinder (/ (compensator diameter) 2) edge))
            (model/translate [0 0 (+ (/ edge -2) (/ height -2))]
              (model/cylinder (/ (compensator iso-size) 2) edge)))))))

(defn- bolt-drive
  "A model of the thing you stick your bit in."
  [{:keys [iso-size head-type drive-type drive-recess-depth]}]
  {:pre [(spec/valid? ::iso-nominal iso-size)
         (spec/valid? ::drive-type drive-type)]}
  (let [depth (or drive-recess-depth
                  (/ (head-height iso-size head-type) 2))]
    (model/translate [0 0 (/ depth -2)]
      (case drive-type
        :hex (hex-item iso-size depth
               :measurement :head-hex-drive-long-diagonal)))))

(defn- thread
  "A model of threading, as on a screw.
  This model has a solid interior, thus needing no union with an inner
  cylinder. Its grooves are not flattened by default.

  The ‘outer-diameter’ argument corresponds to the nominal major diameter of
  an ISO 262 thread, but is not limited here to any ISO standard.

  The ‘pitch’ describes the interval from one peak to the next, lengthwise.

  The ‘angle’ parameter controls the slope of each peak, in radians. It
  defaults to ISO 262’s 60 degrees, approximated by 1.0472 radians. For
  easier printing, consider a lower, standards-noncompliant value. The value
  will determine the ratio between the inner and outer diameters of the model.

  The ‘resolution’ parameter affects the number of edges of the thread per
  revolution of the helix: A higher number gives a more detailed model."
  [{:keys [outer-diameter length pitch angle resolution taper-fn]
    :or {angle 1.0472, resolution 1, taper-fn (fn [& _] (fn [& a] a))}}]
  {:pre [(number? outer-diameter)
         (number? length)
         (number? pitch)]}
  (let [rₒ (/ outer-diameter 2)
        rᵢ (- rₒ (/ pitch (* 2 (/ (cos angle) (sin angle)))))
        n-revolutions (+ (int (/ length pitch)) 2)  ; Amount of full turns.
        n-edges (Math/floor (* resolution τ rₒ))  ; Edges per revolution.
        θ (/ τ n-edges)  ; Angle describing each outer edge.
        Δz (/ pitch n-edges)  ; Lengthwise rise per edge.
        tape (taper-fn rᵢ rₒ length)
        turner
          (fn [[base-radius edge base-z]]
            (let [[r z] (tape base-radius base-z)]
              [(* r (cos (* edge θ)))
               (* r (sin (* edge θ)))
               z]))]
    (model/translate [0 0 (/ length -2)]
      (apply model/union
        ;; Unite a series of wedges, each modelled as a polyhedron.
        (reduce
          (fn [coll [rev edge]]
            (conj coll
              (model/polyhedron
                ;; Points, specified here as a tuple of normal radius, edge
                ;; number and normal z coordinate. The turner function produces
                ;; OpenSCAD’s 3-tuples of coordinates from this data.
                (map turner
                  [[0 edge
                    (* (dec rev) pitch)]
                   [rᵢ edge
                    (+ (* rev pitch) (* edge Δz) (- pitch))]
                   [rᵢ (inc edge)
                    (+ (* rev pitch) (* (inc edge) Δz) (- pitch))]
                   [0 0
                    (* rev pitch)]
                   [rₒ edge
                    (+ (* rev pitch) (* edge Δz) (/ pitch -2))]
                   [rₒ (inc edge)
                    (+ (* rev pitch) (* (inc edge) Δz) (/ pitch -2))]
                   [rᵢ edge
                    (+ (* rev pitch) (* edge Δz))]
                   [rᵢ (inc edge)
                    (+ (* rev pitch) (* (inc edge) Δz))]
                   [0 0
                    (* (inc rev) pitch)]])
                ;; Faces:
                [[1 0 3] [1 3 6] [6 3 8] [1 6 4] [0 1 2] [1 4 2] [2 4 5]
                 [5 4 6] [5 6 7] [7 6 8] [7 8 3] [0 2 3] [3 2 7] [7 2 5]])))
          []
          (into [] (for [r (range n-revolutions) e (range n-edges)] [r e])))))))

(defn- distance-to-end
  "Close over a function that computes the absolute distance to the nearest end
  of an object of passed length. This is used to control tapering."
  [length]
  {:pre [(number? length)]}
  (fn [coordinate] (min coordinate (- length coordinate))))

(defn- flat-end-z
  "Close over a function that limits a z-coordinate to fall within the passed
  length of an object. This is used to prevent lengthwise overshoot of
  threading, except where explicitly passed to this function."
  [limit & {:keys [overshoot] :or {overshoot 0}}]
  {:pre [(number? limit)]}
  (let [floor (- overshoot)
        ceiling (+ limit overshoot)]
    (fn [coordinate] (max floor (min ceiling coordinate)))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; INTERFACE FUNCTIONS — MINOR ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; The following three functions should rarely be needed in an application.
;; They control how a threaded fastener flares or tapers at its ends.

(defn rounding-taper
  "Close over a function to limit threading measurements as for either end of
  a threaded rod."
  [inner-radius outer-radius length]
  {:pre [(number? inner-radius)
         (number? outer-radius)
         (number? length)]}
  (let [distance-fn (distance-to-end length)
        flattener (flat-end-z length)]
    (fn [base-radius base-z]
      {:pre [(<= base-radius outer-radius)]}
      (let [distance (distance-fn base-z)]
        [(min base-radius (+ inner-radius distance))
         (flattener base-z)]))))

(defn flare
  "Close over a function to limit threading measurements as for the transition
  between the flat part of a long bolt and its threaded section, or the two
  sides of a nut.

  This permits a 1 μm overshoot to improve rendering of flared negatives
  inside hex nuts in OpenSCAD."
  [inner-radius outer-radius length]
  {:pre [(number? inner-radius)
         (number? outer-radius)
         (number? length)]}
  (let [distance-fn (distance-to-end length)
        flattener (flat-end-z length :overshoot 0.001)]
    (fn [base-radius base-z]
      ;; The closure will allow a radius of zero to be unchanged. This special
      ;; case is needed for the segments of a piece of threading to continue to
      ;; build from the middle even when the inner radius converges toward the
      ;; outer, keeping polyhedrons legal.
      {:pre [(<= base-radius outer-radius)]}
      (let [distance (distance-fn base-z)]
        [(if (zero? base-radius)
            0
            (max base-radius (- outer-radius (max 0 distance))))
         (flattener base-z)]))))

(defn bolt-taper
  "Close over a tapering function that goes inward from the outer radius at
  the top and inward again to the inner radius at the bottom, with a stretch
  of neutrality in the middle."
  [inner-radius outer-radius length]
  (let [bottom (rounding-taper inner-radius outer-radius length)
        top (flare inner-radius outer-radius length)
        transition (/ length 2)]
    (fn [base-radius base-z]
      (if (> base-z transition)
        (top base-radius base-z)
        (bottom base-radius base-z)))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; INTERFACE FUNCTIONS — MAJOR ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn rod
  "A threaded rod centred at [0 0 0]."
  [& {:keys [iso-size length taper-fn compensator negative]
      :or {taper-fn rounding-taper, compensator dfm/none, negative false}
      :as options}]
  {:pre [(spec/valid? ::iso-nominal iso-size)]}
  (compensator iso-size {:negative negative}
    (thread (merge options {:outer-diameter iso-size
                            :pitch (datum iso-size :thread-pitch-coarse)
                            :taper-fn taper-fn}))))

(defn bolt
  "A model of an ISO metric bolt.
  The very top of the head sits at [0 0 0] with the bolt pointing down.
  The total length of the bolt is the sum of head height (computed from
  nominal ISO size), unthreaded and threaded length parameters.
  Though a drive-type parameter is accepted, only a socket-cap-style hex
  drive is supported, and even that will be ignored on a negative."
  [& {:keys [iso-size head-type drive-type
             total-length unthreaded-length threaded-length
             compensator negative]
      :or {head-type :hex, compensator dfm/none, negative false}
      :as options}]
  {:pre [(spec/valid? ::iso-nominal iso-size)
         (spec/valid? ::head-type head-type)
         (spec/valid? (spec/nilable ::head-type) head-type)]}
  (let [hh (head-height iso-size head-type)
        lengths (bolt-length
                  {:total total-length, :unthreaded unthreaded-length,
                   :threaded threaded-length, :head hh})
        [unthreaded-length threaded-length] lengths
        merged (merge options {:head-type head-type
                               :unthreaded-length unthreaded-length
                               :threaded-length threaded-length
                               :compensator compensator})
        r (/ iso-size 2)]
    (if negative
      (model/union
        (model/translate [0 0 (/ hh -2)]
          (bolt-head merged))
        (compensator iso-size {}
          (when (pos? unthreaded-length)
            (model/translate [0 0 (- (- hh) (/ unthreaded-length 2))]
              (model/cylinder r unthreaded-length)))
          (when (pos? threaded-length)
            (model/translate [0 0 (- (- (+ hh unthreaded-length))
                                     (/ threaded-length 2))]
              (rod :iso-size iso-size
                   :length threaded-length
                   :taper-fn bolt-taper)))))
      ;; Else a positive. Consider including a drive.
      (maybe/difference
        (compensator iso-size {:negative false}
          ;; Request no further scaling.
          (apply bolt
            (flatten (vec (merge merged
                                 {:compensator dfm/none :negative true})))))
        (when drive-type
          (compensator iso-size {}
            (bolt-drive merged)))))))

(defn nut
  "A single hex nut centred at [0 0 0]."
  [& {:keys [iso-size height compensator negative]
      :or {compensator dfm/none}}]
  {:pre [(spec/valid? ::iso-nominal iso-size)]}
  (let [height (or height (datum iso-size :hex-nut-height))]
    (if negative
      ;; A convex model of a nut.
      (compensator (datum iso-size :hex-head-long-diagonal) {}
        (hex-item iso-size height))
      ;; A more complete model.
      (model/difference
        ;; Recurse to make the positive model.
        (compensator iso-size {:negative false}
          ;; Do not pass on the compensator.
          (nut :iso-size iso-size :height height :negative true))
        ;; Cut out the threading.
        (rod :iso-size iso-size :length height :taper-fn flare
             :compensator compensator :negative true)))))

(defn washer
  "A flat, round washer centred at [0 0 0]."
  [& {:keys [iso-size inner-diameter outer-diameter height]}]
  (let [id (or inner-diameter (datum iso-size :iso7089-inner-diameter))
        od (or outer-diameter (datum iso-size :iso7089-outer-diameter))
        thickness (or height (datum iso-size :iso7089-thickness))]
    (model/difference
      (model/cylinder (/ outer-diameter 2) thickness);
      (model/cylinder (/ inner-diameter 2) (+ thickness 1)))))
