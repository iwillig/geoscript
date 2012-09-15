(ns geoscript.feature
  (:import
   [org.opengis.feature.type FeatureType]
   [org.geotools.feature.simple SimpleFeatureTypeBuilder]))

(defrecord Field [name type])

(defn make-field [opts])

(defprotocol ISchema
  (get-name         [this])
  (get-geometry     [this])
  (get-attributes   [this]))

(extend-type FeatureType
  ISchema
  (get-attributes  [this]
    (for [type (.getTypes this)]
      (-> type .getName .getLocalPart)))
  (get-name        [this] (.getTypeName this)))

(defn make-schema [& {:keys [name fields]}]
  (let [builder (doto (SimpleFeatureTypeBuilder.)
                (.setName name))]
    (doseq [field fields]
      ;; need to support fields with projections
      (if (= (count field) 2)
        (.add builder (:name field) (Class/forName (:type field)))))
    (.buildFeatureType builder)))