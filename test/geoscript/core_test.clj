(ns geoscript.core-test
  (:import
   [org.opengis.feature.type FeatureType]
   [org.opengis.feature.simple SimpleFeature]
   [org.opengis.geometry Envelope]
   [com.vividsolutions.jts.geom Point LineString Polygon MultiPoint MultiLineString])
  (:use [clojure.test]
        [geoscript.feature]
        [geoscript.layer]
        [geoscript.workspace]
        [geoscript.geom]))

(deftest test-basic-geometries
  (testing "Creating a jts.Coordinate")

  (testing "Creating a 2d jts.Point object"
    (let [point (make-point 1 2)]
      (is (= (type point) Point) "Make sure the constructor returns a jts Point")      
      (is (= (count (coordinates point)) 1) "Make sure that the coordinates function returns an array")
      (is (= (x point) 1.0))
      (is (= (y point) 2.0))
      (is (= (area point) 0.0))
      (is (Double/isNaN (z (make-point 1 1))) "no Z coordinate")))
  
  (testing "Creating a jts.LineString object"
    (let [ls (make-linestring [[-180 -90] [0 0] [180 90]] )]
      (is (= (type ls) LineString))
      (is (= (area ls) 0.0))
      (is (= (count (coordinates ls)) 3))))

  (testing "Creating a jts.Polygon object"
    (let [p (make-polygon [[-180 -90] [-180 90] [180 90] [180 -90] [-180 -90]]
                          [[[-90 -45] [-90 45] [90 45] [90 -45] [-90 -45]]]  )]
      (is (= (type p) Polygon))
      (is (= (area p) 48600.0))
      (is (= (count (coordinates p)) 5))))
  
  (testing "Creating a jts.Envelope")

  (testing "Create a jts.MultiPoint object"
    (let [mp (make-multi-point [[1 0] [2 3]])]
      (is (= (type mp) MultiPoint))))
  
  (testing "Create a jts.MultiLineString object"
    (let [mls (make-multi-linestring
               [[[-180 -90] [0 0] [180 90]]
                [[-180 -90] [0 0] [180 90]]])]
      (is (= (type mls) MultiLineString)))))

(def test-postgis true)
(def db "gis")

(deftest test-workspaces

  (if test-postgis
    (testing "A postgis workspace"
      (with-datastore [pg (postgis :database db)]
        (is (= (count (get-names pg))  1))
        (is (= (count (get-layers pg)) 1))
        (is (= (isa? (class pg) org.geotools.data.AbstractDataStore)))
        (for [layer (get-layers pg)]
          (is (isa? (class layer) org.geotools.data.AbstractFeatureSource)))))

  (testing "A shapefile workspace"
    (with-datastore [shp (shape :path "data/nybb.shp")]
      (is (isa? (class shp) org.geotools.data.AbstractDataStore))
      (is (= (count (get-names shp)) 1))
      (for [layer (get-layers shp)]
        (isa? (class layer) org.geotools.data.AbstractFeatureSource))))))

(deftest test-schema
  (testing "The constructor function should return a gt.Schema object"
    (let [s (make-schema
             {:name "test"
              :fields [{:name "name" :type java.lang.String}
                       {:name "date" :type "String"}
                       (make-field {:name "other-name" :type "String"})
                       (make-field {:name "another-name" :type java.lang.String})]})]
      (is (isa? (class s) FeatureType))
      (let [fields (get-fields s)]
        (is (= (count fields) 4))
        (is (seq? fields))))))

(deftest test-features
  (testing "GeoScript's handling of the gt.Feature object"
    (testing "Creating a feature object with a schema"
      (let [s (make-schema {:name "test" :fields [{:name "name" :type java.lang.String}]})
            f (make-feature {:properties {:name "test"} :schema s})]
        (is (isa? (class f) org.opengis.feature.Feature))
        (is (= (get-attr f :name) "test"))))
    (testing "Creating a feature without an schema object"
      (let [f (make-feature {:properties {:name "test"}})]
        (is (isa? (class f) org.opengis.feature.Feature))
        (let [attrs (get-attrs f)]
          (is (map? attrs ))
          (is (contains? attrs :name)))))
    (testing "Setting the value of an attribute"
      (let [f (make-feature {:properties {:name "Ivan"}})]
        (is (= (get-attr f :name)) 1)
        (is (= (get-fields (.getFeatureType f)) [{:name "name" :type java.lang.String}]))
        (.setAttribute f "name" "Ivan Willig")
        (is (= (.getAttribute f "name")) "Ivan Willig")))))

(deftest test-layers
  (testing "GeoScript handling of 'Layers'"
    (with-datastore [pg (postgis :database db)]
      (let [fs (get-layer pg :layer "nybb")
            s (get-schema fs)]
        (is (map? s))
        (is (contains? s :name))
        (is (= (:name s) "nybb"))
        (is (contains? s :fields))
        (is (seq? (:fields s)))))))

(deftest test-iter-features
  (testing "Users should be allowed to iterator through features"
    (with-datastore [pg (postgis :database db)]
      (with-features [fs (get-features (get-layer pg :layer "nybb"))]
        (doseq [f fs]
          (is (isa? (class f) org.opengis.feature.Feature))
          (is (map? (get-attrs f))))))))
