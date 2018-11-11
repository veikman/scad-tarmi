;;; ISO 262 fasteners and ISO 7089 washers.

(ns scad-tarmi.threaded
  (:require [clojure.spec.alpha :as spec]
            [scad-clj.model :as model]
            [scad-tarmi.core :refer [sin cos τ maybe-scale]]))


;;;;;;;;;;;;;;
;; INTERNAL ;;
;;;;;;;;;;;;;;

(def iso-data
  "Various constants from ISO metric fastener standards.
  This is a map of nominal ISO bolt diameter (in mm) to various other
  measurements according to spec. Generally, actual nuts tend to be a little
  smaller than the spec says, in which case these standard sizes, when used
  for negatives, are good for a very tight fit in 3D printing, after accounting
  for printer inaccuracy and material shrinkage."
  {3 {:socket-diameter 5.5
      :hex-nut-height 2.4
      :thread-pitch 0.5
      :iso7089-inner-diameter 3.2
      :iso7089-outer-diameter 7
      :iso7089-thickness 0.5}
   4 {:socket-diameter 7
      :hex-nut-height 3.2
      :thread-pitch 0.7
      :iso7089-inner-diameter 4.3
      :iso7089-outer-diameter 9
      :iso7089-thickness 0.8}
   5 {:socket-diameter 8.5
      :hex-short-diagonal 8
      :hex-nut-height 4.7
      :hex-head-height-minimum 3.35
      :thread-pitch 0.8
      :iso7089-inner-diameter 5.3
      :iso7089-outer-diameter 10
      :iso7089-thickness 1}
   6 {:socket-diameter 10
      :hex-nut-height 5.2
      :hex-head-height-minimum 3.85
      :thread-pitch 1
      :iso7089-inner-diameter 6.4
      :iso7089-outer-diameter 12
      :iso7089-thickness 1.6}
   8 {:socket-diameter 13
      :hex-nut-height 6.8
      :hex-head-height-minimum 5.1
      :thread-pitch 1.25
      :iso7089-inner-diameter 8.4
      :iso7089-outer-diameter 16
      :iso7089-thickness 1.6}})

(spec/def ::iso-nominal #(contains? iso-data %))
(spec/def ::iso-property #{:hex-short-diagonal
                           :hex-long-diagonal
                           :hex-head-height-minimum
                           :hex-nut-height
                           :socket-diameter
                           :socket-height
                           :button-diameter
                           :button-height
                           :countersunk-diameter
                           :countersunk-height
                           :thread-pitch
                           :iso7089-inner-diameter
                           :iso7089-outer-diameter
                           :iso7089-thickness})
(spec/def ::head-style #{:hex     ; Hex head with the diameter of a nut.
                         :socket  ; Full cylindrical counterbore cap.
                         :button  ; Partial (low, smooth-edged) socket cap.
                         :countersunk}) ; Flat head tapering toward the bolt.


(defn- get-datum
  [nominal-diameter key]
  {:pre [(spec/valid? ::iso-nominal nominal-diameter)
         (spec/valid? ::iso-property key)]}
  (let [data (get iso-data nominal-diameter)]
   (case key
     :hex-short-diagonal  ; Flat-to-flat width of a hex head.
       ;; For most sizes, this value is equal to socket diameter.
       (get data key (get-datum nominal-diameter :socket-diameter))
     :hex-long-diagonal  ; Corner-to-corner diameter of a hex head.
       (* 2 (/ (get-datum nominal-diameter :hex-short-diagonal) (Math/sqrt 3)))
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

(defn- hex-item
  [iso-size height]
  (let [diagonal (get-datum iso-size :hex-long-diagonal)]
    (->>
      (model/cylinder (/ diagonal 2) height)
      (model/with-fn 6)
      (model/rotate [0 0 (/ Math/PI 6)]))))

(defn- get-head-height
  [iso-size head]
  {:pre [(spec/valid? ::iso-nominal iso-size)
         (spec/valid? ::head-style head)]}
  (get-datum iso-size
    (case head
      :hex :hex-head-height-minimum
      :socket :socket-height
      :button :button-height
      :countersunk :countersunk-height)))

(defn- bolt-head
  "A model of the head of a bolt, without a drive."
  [{:keys [iso-size head] :as options}]
  {:pre [(spec/valid? ::iso-nominal iso-size)
         (spec/valid? ::head-style head)]}
  (let [height (get-head-height iso-size head)]
    (case head
      :hex
        (hex-item iso-size height)
      :socket
        (let [diameter (get-datum iso-size :socket-diameter)]
          (model/cylinder (/ diameter 2) height))
      :button
        (let [diameter (get-datum iso-size :button-diameter)]
          (model/cylinder (/ diameter 2) height))
      :countersunk
        (let [diameter (get-datum iso-size :countersunk-diameter)
              edge (Math/log iso-size)]
          (model/hull
            (model/translate [0 0 (+ (/ edge -4) (/ height 2))]
              (model/cylinder (/ diameter 2) (/ edge 2)))
            (model/translate [0 0 (+ (/ edge -4) (/ height -2))]
              (model/cylinder (/ iso-size 2) edge)))))))

(defn- bolt-drive
  "A model (negative) of the thing you stick your bit in."
  [_])

(defn- thread
  "A model of threading, as on a screw.
  This model has a solid interior, thus needing no union with an inner
  cylinder. Its grooves are not flattened at the bottom.

  The ‘outer-diameter’ argument corresponds to the nominal major diameter of
  an ISO 262 thread.

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
  threading."
  [limit]
  {:pre [(number? limit)]}
  (fn [coordinate] (max 0 (min limit coordinate))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; INTERFACE FUNCTIONS — MINOR ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; The following three functions should rarely be need be in an application.
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
  between the flat part of a long bolt and its threaded section.
  The closure will allow a radius of zero to be unchanged. This special case
  if needed for the segments of a piece of threading to continue to build from
  the middle even when the inner radius converges toward the outer, keeping
  polyhedrons legal."
  [inner-radius outer-radius length]
  {:pre [(number? inner-radius)
         (number? outer-radius)
         (number? length)]}
  (let [distance-fn (distance-to-end length)
        flattener (flat-end-z length)]
    (fn [base-radius base-z]
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
  [& {:keys [iso-size length taper-fn]
      :or {taper-fn rounding-taper} :as options}]
  {:pre [(spec/valid? ::iso-nominal iso-size)]}
  (thread (merge options {:outer-diameter iso-size
                          :pitch (get-datum iso-size :thread-pitch)
                          :taper-fn taper-fn})))

(defn bolt
  "A model of an ISO metric bolt.
  The very top of the head sits at [0 0 0] with the bolt pointing down.
  The total length of the bolt is the sum of head height (computed from
  nominal ISO size), unthreaded and threaded length parameters."
  [& {:keys [iso-size negative head drive unthreaded-length threaded-length
             scale]
      :or {head :hex, unthreaded-length 0, threaded-length 10, scale 1}
      :as options}]
  {:pre [(spec/valid? ::iso-nominal iso-size)
         (spec/valid? ::head-style head)]}
  (let [r (/ iso-size 2)
        head-height (get-head-height iso-size head)]
    (maybe-scale scale
      (if (or negative (nil? drive))
        ;; A model without a drive in its head.
        (model/union
          (model/translate [0 0 (- (/ head-height 2))]
            (bolt-head (merge options {:head head})))
          (when (pos? unthreaded-length)
            (model/translate [0 0 (- (- head-height) (/ unthreaded-length 2))]
              (model/cylinder r unthreaded-length)))
          (when (pos? threaded-length)
            (model/translate [0 0 (- (- (+ head-height unthreaded-length)) (/ threaded-length 2))]
              (rod :iso-size iso-size
                   :length threaded-length
                   :taper-fn bolt-taper))))
        ;; A model with a drive.
        (model/difference
          (apply bolt (merge options {:scale 1}))
          (drive options))))))

(defn nut
  "A single hex nut centred at [0 0 0]."
  [& {:keys [iso-size negative height scale]
      :or {scale 1}}]
  {:pre [(spec/valid? ::iso-nominal iso-size)]}
  (let [height (or height (get-datum iso-size :hex-nut-height))]
    (maybe-scale scale
      (if negative
        ;; A convex model of a nut, without a hole. For use as negative space.
        (hex-item iso-size height)
        ;; A more complete model.
        (model/difference
          (hex-item iso-size height)
          (rod :iso-size iso-size :length height :taper-fn flare))))))

(defn washer
  "A flat, round washer centred at [0 0 0]."
  [& {:keys [iso-size inner-diameter outer-diameter height]}]
  (let [id (or inner-diameter (get-datum iso-size :iso7089-inner-diameter))
        od (or outer-diameter (get-datum iso-size :iso7089-outer-diameter))
        thickness (or height (get-datum iso-size :iso7089-thickness))]
    (model/difference
      (model/cylinder (/ outer-diameter 2) thickness);
      (model/cylinder (/ inner-diameter 2) (+ thickness 1)))))
