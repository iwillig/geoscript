(ns geoscript.feature
  (:import
   [org.geotools.feature AttributeTypeBuilder NameImpl]
   [org.opengis.feature.simple SimpleFeature]
   [org.opengis.feature.type FeatureType AttributeType]
   [org.geotools.feature.simple SimpleFeatureTypeBuilder SimpleFeatureBuilder]))

(defn get-type [type]
  (Class/forName
   (condp = type
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
     "MulitPolygon"     "com.vividsolutions.jts.geom.MultiPolygon"
     "java.lang.String")))

(defn make-field [{:keys [name type]}]
  (let [builder (doto (AttributeTypeBuilder.)
                  (.setName name))]
    ;; this allows either a string or a java class
    (if (isa? (class type) java.lang.Class)
      (.setBinding builder (class type))
      (.setBinding builder (get-type type)))
    (.buildType builder)))

(defprotocol ISchema
  "A protocol that allows us to attach geoscript methods to a
   FeatureType object"
  (create            [this])
  (get-geometry      [this])
  (get-fields        [this])
  (get-field-names   [this]))

(extend-type FeatureType
  ISchema
  (create       [this obj])
  (get-geometry [this] (.getGeometryDescriptor this))
  (get-fields   [this])
  (get-field-names [this]
    (for [type (.getTypes this)]
      (-> type .getName .getLocalPart))))


(defn make-schema
  [{:keys [name fields srs]}]
  (let [builder (doto (SimpleFeatureTypeBuilder.)
                  (.setName (NameImpl. (or name "feature"))))]
    (doseq [f fields]
      (let [field (if (isa? (class f) AttributeType)
                    f (make-field f))]
        (.add builder
              (-> field .getName .getLocalPart)
              (.getBinding field))))
    (.buildFeatureType builder)))

(defprotocol IFeature
  (get-attributes   [this])
  (get-attribute    [this n])
  (set-attribute    [this n v]))

(extend-type SimpleFeature
  IFeature
  (get-attributes [this]
    (reduce (fn [rs f]
              (assoc rs (-> f .getDescriptor .getLocalName keyword) (.getValue f)))
            {} (.getProperties this)))
  (get-attribute  [this n]    (.getAttribute this (name n)))
  (set-attribute  [this n v]  (.setAttribute this (name n) v)))

(defn schema-from-values [values]
  (make-schema
   {:fields
    (for [value values]
      {:name (name (value 0)) :type (class (value 1))})}))

(defn make-feature [& {:keys [properties schema]}]
  (let [s       (if (nil? schema) (schema-from-values properties) schema)
        builder (SimpleFeatureBuilder. s)]
    (doseq [property properties]
      (.set builder (name (key property)) (val property)))
    (.buildFeature builder nil)))