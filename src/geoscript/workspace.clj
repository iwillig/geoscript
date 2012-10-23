(ns geoscript.workspace
  (:import
   [java.io File]
   [org.geotools.data.store ContentDataStore]
   [org.geotools.data DataStoreFinder]))


(defprotocol IWorkspace
  (get-layers [this])
  (get-names      [this])
  (add-layer  [this new-layer])
  (get-layer  [this & {:keys [layer] :or {layer nil}}]))

(extend-type ContentDataStore
  IWorkspace
  (get-names   [this] (.getTypeNames this))
  (get-layers  [this]
    (for [name (get-names this)]
      (get-layer this :layer name)))
  (add-layer [this new-layer])
  (get-layer [this & {:keys [layer] :or {layer nil}}]
    (if layer
      (.getFeatureSource this layer)
      (.getFeatureSource this))))

(defn make-datastore
  "Constructs a gt.DataStore from Clojure key value pairs"
  [params]
  (DataStoreFinder/getDataStore
   (into {} (for [kv params] [(name (kv 0)) (kv 1)]))))

(defn postgis
  "Convenience function for constructing a gt.PostGIS datastore"
  [& {:keys [port host user passwd database]
      :or {port "5432" host "localhost" user "postgres" passwd ""}}]
  (make-datastore
   {:port port
    :host host
    :user user
    :passwd passwd
    :dbtype "postgis"
    :database database}))

(defn shape
  [& {:keys [path]}]
  (make-datastore {:url (.toURL (File. path))}))

(defn h2
  [& {:keys [database]}]
  (make-datastore {:dbtype "h2" :database database}))

(defmacro with-datastore
  [bindings & body]
  `(let ~(subvec bindings 0 2)
     (try
       ~@body
       (finally (.dispose ~(bindings 0))))))



