(ns geoscript.core
  (:import [org.opengis.feature.simple SimpleFeature]))

(defprotocol Transformable
  "A protocol that transforms a geographic object from one projection to another"
  (transform [this new-srid]))

(defprotocol Boundable
  "A protocol that returns the geographic extend of an object."
  (get-bounds [this]))

(extend-type SimpleFeature
  Boundable
  (get-bounds [this] (.getBounds this)))