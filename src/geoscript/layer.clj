(ns geoscript.layer
  (:import [org.geotools.data FeatureSource]))

(defprotocol ILayer
  (add-feature  [this])
  (get-features [this]))

(extend-type FeatureSource
  ILayer
  (add-feature  [this] )
  (get-features [this] (.getFeatures this)))


(defmacro with-features [feat-binding & body]
  `(let [feature-coll# ~(feat-binding 1)
         feature-iter# (.iterator feature-coll#)
         ~(feat-binding 0) (iterator-seq feature-iter#)]
     (try
       ~@body
       (finally (.close feature-coll# feature-iter#)))))