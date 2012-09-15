(ns geoscript.workspace
  (:import
   [java.io File]
   [org.geotools.data.store ContentDataStore]
   [org.geotools.data DataStoreFinder]))


(defprotocol IWorkspace
  (layers    [this])
  (names     [this])
  (add-layer [this new-layer])
  (get-layer [this & {:keys [layer]}]))

(extend-type ContentDataStore
  IWorkspace
  (names     [this] (.getTypeNames this))
  (layers  [this]
    (for [name (names this)]
      (get-layer this :layer name)))
  (add-layer [this new-layer])
  (get-layer [this & {:keys [layer]}]
    (if layer
      (.getFeatureSource this layer)
      (.getFeatureSource this))))

(defn make-datastore
  "Constructs a gt.DataStore from Clojure key value pairs"
  [params]
  (DataStoreFinder/getDataStore
   (reduce (fn [rs kv] (assoc rs (name (kv 0)) (kv 1) )) {} params)))

(defn make-postgis
  "Convenience function for constructing a gt.PostGIS datastore"
  [& {:keys [port host user passwd database]
    :or {port "5432" host "localhost" user "postgres" passwd ""}}]
  (make-datastore {:port port :host host
                   :user user :passwd passwd
                   :dbtype "postgis" :database database}))

(defn make-shape
  [& {:keys [path]}]
  (make-datastore {:url (.toURL (File. path))}))

(defmacro with-datastore
  [bindings & body]
  `(let ~(subvec bindings 0 2)
     (try
       ~@body
       (finally (.dispose ~(bindings 0))))))



