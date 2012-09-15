(def gt-version "9-SNAPSHOT")

(defproject geoscript "0.1.0-SNAPSHOT"
  :description "A Clojure implementation of the geoscript project"
  :url "geoscript.org"
  :plugins [[lein-swank "1.4.4"]]
  :repositories {"OpenGeo Maven Repository" "http://repo.opengeo.org"}
  :dependencies [[com.vividsolutions/jts "1.12-SNAPSHOT"]
                 [org.clojure/clojure "1.4.0"]
                 [org.geotools/gt-main ~gt-version]
                 [org.geotools/gt-shapefile ~gt-version]
                 [org.geotools.jdbc/gt-jdbc-postgis ~gt-version]
                 [org.geotools.jdbc/gt-jdbc-h2 ~gt-version]])
