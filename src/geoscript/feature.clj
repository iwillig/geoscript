(ns geoscript.feature
  (:import
   [org.geotools.feature AttributeTypeBuilder]
   [org.opengis.feature.simple SimpleFeature]
   [org.opengis.feature.type FeatureType]
   [org.geotools.feature.simple SimpleFeatureTypeBuilder SimpleFeatureBuilder]))

(defn get-type [type]
  (Class/forName
   (case type
     "String"           "java.lang.String"
     "Integer"          "java.lang.Integer"
     "Short"            "java.lang.Short"
     "Float"            "java.lang.Float"
     "Long"             "java.lang.Long"
     "Geometry"         "com.vividsolutions.jts.geom.Geometry"
     "Point"            "com.vividsolutions.jts.geom.Point"
     "LineString"       "com.vividsolutions.jts.geom.LineString"
     "Polygon"          "com.vividsolutions.jts.geom.Polygon"
     "MultiPoint"       "com.vividsolutions.jts.geom.MultiPoint"
     "MulitLineString"  "com.vividsolutions.jts.geom.MultiLineString"
     "MulitPolygon"     "com.vividsolutions.jts.geom.MultiPolygon")))

(defn make-field [name type]
  (let [builder (doto (AttributeTypeBuilder.)
                  (.setName name)
                  (.setBinding (get-type type)))]
    (.buildType builder)))

(defprotocol ISchema
  (get-name          [this])
  (get-geometry      [this])
  (get-field-names   [this]))

(extend-type FeatureType
  ISchema
  (get-geometry [this] (.getGeometryDescriptor this))
  (get-field-names [this]
    (for [type (.getTypes this)]
      (-> type .getName .getLocalPart)))
  (get-name  [this] (.getTypeName this)))


(defn make-schema
  [& {:keys [name fields]}]
  (let [builder (doto (SimpleFeatureTypeBuilder.)
                (.setName name))]
    (doseq [field fields]
      (.add builder (field 0) (get-type (field 1))))
    (.buildFeatureType builder)))

(defprotocol IFeature
  (get-bounds       [this])
  (get-properties   [this])
  (get-attribute    [this n])
  (set-attribute    [this n v]))

(extend-type SimpleFeature
  IFeature
  (get-bounds [this]         (.getBounds this))
  (get-attribute [this n]    (.getAttribute this (name n)))
  (set-attribute [this n v]  (.setAttribute this (name n) v)))

(defn make-feature [& {:keys [properties geometry schema id]}]
  (let [builder (SimpleFeatureBuilder. schema)]
    (.add    builder geometry)
    (doseq [property properties]
      (.set builder (name (key property)) (val property)))
    (.buildFeature builder (str id))))