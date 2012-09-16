(ns geoscript.core-test
  (:import
   [org.opengis.feature.simple SimpleFeature]
   [org.opengis.geometry Envelope]
   [com.vividsolutions.jts.geom
    Point LineString Polygon MultiPoint MultiLineString])
  (:use [clojure.test]
        [geoscript.feature]
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

(deftest test-workspaces

  (if test-postgis
    (testing "A postgis workspace"
      (with-datastore [pg (postgis :database "gis")]
        (is (= (count (names pg))  1))
        (is (= (count (get-layers pg)) 1))
        (for [layer (get-layers pg)]
          (is (isa? (class layer) org.geotools.data.AbstractFeatureSource)))
        (is (= (isa? (class pg) org.geotools.data.AbstractDataStore)))))

  (testing "A shapefile workspace"
    (with-datastore [shp (shape :path "data/nybb.shp")]
      (is (isa? (class shp) org.geotools.data.AbstractDataStore))
      (is (= (count (names shp)) 1))
      (for [layer (get-layers shp)]
        (isa? (class layer) org.geotools.data.AbstractFeatureSource))))))


(deftest test-a-schema
  (testing "The constructor function should return a gt.Schema object"
    (let [schema (make-schema
                  :name "test"
                  :fields [["location" "Point"] ["type" "String"] ["number-field" "Integer"]])]
      (is (= (get-name schema) "test"))
      (is (= (get-field-names schema) ["location" "type" "number-field"])))))


(deftest test-creating-features
  (testing "The constructor function should return a gt.Feature"
    (let [f (make-feature
             :schema (make-schema :name "test" :fields [["name" "String"] ["location" "Point"]])
             :id 1
             :geometry (make-point 1 1)
             :properties {:name "NYC"})]
      (is (isa? (class f) SimpleFeature))
      (is (isa? (class (get-bounds f)) Envelope))
      (let [name (get-attribute f :name)]
        (is (isa? (class name) java.lang.String))
        (is (= name "NYC"))))))