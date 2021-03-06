(ns geoscript.feature
  (:import
   [org.geotools.data DefaultTransaction]
   [org.geotools.feature AttributeTypeBuilder NameImpl]
   [org.opengis.feature.simple SimpleFeature]
   [org.opengis.feature.type FeatureType AttributeType]
   [org.geotools.feature.simple SimpleFeatureTypeBuilder SimpleFeatureBuilder]))


(defmulti convert-type (fn [x] x))

(defmethod convert-type :integer [x]
  java.lang.Integer)

(defmethod convert-type :int [x]
  java.lang.Integer)

(defmethod convert-type :short [x]
  java.lang.Short)

(defmethod convert-type :float [x]
  java.lang.Float)

(defmethod convert-type :long [x]
  java.lang.Long)

(defmethod convert-type :geometry [x]
  com.vividsolutions.jts.geom.Geometry)

(defmethod convert-type :point [x]
  com.vividsolutions.jts.geom.Point)



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
      (.setBinding builder type)
      (.setBinding builder (get-type type)))
    (.buildType builder)))

(defprotocol ISchema
  "A protocol that allows us to attach geoscript methods to a
   FeatureType object"
  (get-geometry      [this])
  (get-fields        [this])
  (get-field-names   [this]))

(extend-type FeatureType
  ISchema
  (get-geometry [this] (.getGeometryDescriptor this))
  (get-fields   [this]
    (for [t (.getTypes this)]
      {:name (-> t .getName .getLocalPart) 
       :type (.getBinding t)})))

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
  (get-attr    [this n])
  (set-attr!   [this attr value])
  (get-attrs   [this])
  (set-attrs!  [this attrs]))

(extend-type SimpleFeature
  IFeature
  (get-attrs [this]
    (reduce (fn [rs f]
              (assoc rs (-> f .getDescriptor .getLocalName keyword) (.getValue f)))
            {} (.getProperties this)))
  (get-attr  [this n]    (.getAttribute this (name n)))
  (set-attr! [this n v]  (.setAttribute this (name n) v)))

(defn schema-from-values [values]
  (make-schema
   {:fields
    (for [value values]
      {:name (name (value 0)) :type (type (value 1))})}))

(defn make-feature [{:keys [properties schema]}]
  (let [s       (if (nil? schema) (schema-from-values properties) schema)
        builder (SimpleFeatureBuilder. s)]
    (doseq [property properties]
      (.set builder (name (key property)) (val property)))
    (.buildFeature builder nil)))

(defn write-features [source sink]
  (let [trans (DefaultTransaction. "trans")]
    (try
      (.addFeatures sink source)
      (.commit trans)
      (catch Exception e (.printStackTrace e) (.rollback trans))
      (finally (.close trans)))))