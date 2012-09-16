(ns geoscript.core-test
  (:import
   [org.opengis.feature.simple SimpleFeature]
   [com.vividsolutions.jts.geom
    Point LineString Polygon MultiPoint MultiLineString])
  (:require
   [geoscript.feature   :as feature]
   [geoscript.workspace :as workspace])
  (:use clojure.test geoscript.geom))

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
      (workspace/with-datastore [pg (workspace/make-postgis :database "gis")]
        (is (= (count (workspace/names pg))  1))
        (is (= (count (workspace/get-layers pg)) 1))
        (for [layer (workspace/get-layers pg)]
          (is (isa? (class layer) org.geotools.data.AbstractFeatureSource)))
        (is (= (isa? (class pg) org.geotools.data.AbstractDataStore)))))

  (testing "A shapefile workspace"
    (workspace/with-datastore [shp (workspace/make-shape :path "data/nybb.shp")]
      (is (isa? (class shp) org.geotools.data.AbstractDataStore))
      (is (= (count (workspace/names shp)) 1))
      (for [layer (workspace/get-layers shp)]
        (isa? (class layer) org.geotools.data.AbstractFeatureSource))))))


(deftest test-a-schema
  (testing "The constructor function should return a gt.Schema object"
    (let [schema
          (feature/make-schema
           :name "test"
           :fields [{:name "location"
                     :type "Point"}
                    {:name "type"
                     :type "String"}
                    {:name "number-field"
                     :type "Integer"}])]
      (is (= (feature/get-name schema) "test"))
      (is (= (feature/get-field-names schema) ["location" "type" "number-field"])))))


(deftest test-creating-features
  (testing "The constructor function should return a gt.Feature"
    (let [f (feature/make-feature
             :geometry (make-point 1 1)
             :properties {})]
      (isa? (class f) SimpleFeature))))