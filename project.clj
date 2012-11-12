(def gt-version "9-SNAPSHOT")

(defproject geoscript "0.6.7-SNAPSHOT"
  :description "A Clojure implementation of the geoscript project"
  :url "geoscript.org"

  :plugins [[lein-swank "1.4.4"]]
  :repositories {"OpenGeo Maven Repository" "http://repo.opengeo.org"}
  :offline? true
  :dependencies [[com.vividsolutions/jts "1.12-SNAPSHOT"]
                 [org.clojure/clojure "1.4.0"]

                 [clj-yaml "0.4.0"]
                 [hiccup "1.0.1"]

                 [org.clojure/data.json "0.1.3"]
                 [org.geotools/gt-main ~gt-version]
                 [org.geotools/gt-svg ~gt-version]
                 [org.geotools/gt-shapefile ~gt-version]
                 [org.geotools.jdbc/gt-jdbc-postgis ~gt-version]
                 [org.geotools.jdbc/gt-jdbc-h2 ~gt-version]
                 [org.geotools/gt-cql ~gt-version]
                 [org.geotools.xsd/gt-xsd-sld ~gt-version]
                 [org.geotools/gt-xml ~gt-version]
                 [org.geotools/gt-epsg-hsql ~gt-version]])
