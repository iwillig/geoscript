(ns geoscript.geom
  (:import
   [com.vividsolutions.jts.geom
    Polygon MultiLineString MultiPolygon
    LinearRing Geometry Point Coordinate LineString GeometryFactory]))

(def factory (GeometryFactory.))

(defprotocol IGeometry
  (area [this])
  (bounds [this])
  (dimension [this])
  (json [this])
  (length [this])
  (coordinates [this]))

(defprotocol IPoint
  (x [this])
  (y [this])
  (z [this]))

;; these methods follow the geoscript site
(extend-type Geometry
  IGeometry
  (area [this] (.getArea this))
  (bounds [this] (.getBounds this))
  (dimension [this] (.getDimension this))
  (json [this])
  (length [this] (.getLength this))
  (coordinates [this]
    (-> this .getCoordinateSequence .toCoordinateArray)))

(extend-type Point
  IPoint
  (x [this] (.getX this))
  (y [this] (.getY this))
  (z [this] (.z (first (coordinates this)))))

(extend-type Polygon
  IGeometry
  (area [this] (.getArea this))
  (bounds [this] (.getBounds this))
  (dimension [this] (.getDimension this))
  (json [this])
  (length [this] (.getLength this))
  (coordinates [this]
    (-> this .getExteriorRing coordinates)))

(defn make-coordinate
  ([x y] (Coordinate. x y))
  ([x y z] (Coordinate. x y z))
  ([coord] (Coordinate. (coord 0) (coord 1))))

(defn make-point
  ([x y]
     (.createPoint factory (Coordinate. x y)))
  ([x y z]
     (.createPoint factory (Coordinate. x y z)))
  ([coords] (.createPoint factory (make-coordinate coords))))

(defn seq->coord-array [aseq]
  (into-array Coordinate (map make-coordinate aseq)))

(defn make-linestring [aseq]
  (.createLineString factory (seq->coord-array aseq)))

(defn make-linearring [aseq]
  (.createLinearRing factory (seq->coord-array aseq)))

(defn make-polygon
  ([outer-ring inner-rings]
     (.createPolygon
      factory
      (make-linearring outer-ring)
      (into-array LinearRing (map make-linearring inner-rings)))))

(defn make-multi-point
  [aseq]
  (.createMultiPoint
   factory (into-array Point (map make-point aseq))))

(defn make-multi-linestring
  [aseq]
  (.createMultiLineString
   factory (into-array LineString (map make-linestring aseq))))

(defn make-multi-polygon
  [aseq]
  (.createMultiPolygon
   factory (into-array Polygon (map make-polygon aseq))))
