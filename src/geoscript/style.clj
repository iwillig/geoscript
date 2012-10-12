(ns geoscript.style
  (:require
   [clojure.java.io :as io]
   [clj-yaml.core :as yaml])
  (:import
   [java.net URI]
   [org.geotools.sld SLDConfiguration]
   [org.geotools.xml Parser]
   [org.geotools.filter.text.ecql ECQL]
   [org.geotools.filter AttributeExpressionImpl]
   [org.opengis.filter.expression Literal]
   [org.geotools.factory CommonFactoryFinder]
   [org.geotools.metadata.iso.citation OnLineResourceImpl]
   [org.geotools.styling
    Font
    Symbol
    PointPlacement
    SLDTransformer
    FeatureTypeStyle
    TextSymbolizer
    PolygonSymbolizer
    PointSymbolizer
    LineSymbolizer]))

;; the geotools style factory
(def style-factory (CommonFactoryFinder/getStyleFactory))
;; the filter factory
(def filter-factory (CommonFactoryFinder/getFilterFactory2))

(defn literal
  "Short hand for creating an Literal Value"
  [value]
  (if (not (instance? Literal value))
    (.literal filter-factory value)))

(defn attr
  "Short hand for creating an AttributeExpressionImpl"
  [value]
  (AttributeExpressionImpl. value))

(defn format-map
  "Returns a new map with a function applied to each value."
  [func options]
  (into {} (for [kv options] [(kv 0) (func (kv 1))])))

(defn make-literals
  "Takes a map and passes each value through the literal function"
  [options]
  (format-map literal options))


(defn make-font
  "Takes a clj.Map and returns a GeoTools FontImp object
Has a set of sane default for the font family, size and weight
"
  [options]
  (let [{:keys [family size style weight]
         :or {weight (literal "normal")
              size (literal 10)
              style (literal "normal")
              family (literal "Arial")}} (make-literals options)]
    (.createFont style-factory
                 family
                 style
                 weight
                 size)))

(defn make-fill
  "Takes a clj.Map and returns a Geotools fill object"
  [options]
  (let [{:keys [color opacity]} (make-literals options)]
    (.createFill style-factory color opacity)))

;; color - The color of the line
;; width - The width of the line
;; opacity - The opacity of the line
;; lineJoin - - the type of Line joint
;; lineCap - - the type of line cap
;; dashArray - - an array of floats describing the dashes in the line
;; dashOffset - - where in the dash array to start drawing from
;; graphicFill - - a graphic object to fill the line with
;; graphicStroke - - a graphic object to draw the line with
(defn make-stroke
  "Returns a StrokeImpl object"
  [options]
  (let [{:keys [color width opacity]} (make-literals options)]
    (.createStroke style-factory
                   color
                   width
                   opacity)))

(defn make-halo
  [{:keys [color radius opacity]}]
  (.createHalo
   style-factory
   (make-fill {:color color :opacity opacity})
   (literal radius)))

(defn make-displacement [options]
  (let [{:keys [x y]} (make-literals options)]
    (.createDisplacement style-factory x y)))

(defn make-anchor-point [options]
  (let [{:keys [x y]} (make-literals options)]
    (.createAnchorPoint style-factory x y)))

(defn make-line-placement
  [options]
  (let [{:keys [offset gap inital-gap]} (make-literals (select-keys options [:offset :gap :inital-gap ]))
        line-placement (.createLinePlacement style-factory offset)]
    (if gap
      (.setGap line-placement gap))
    (if inital-gap
      (.setInitialGap line-placement inital-gap))
    (if (:repeated? options)
      (.setRepeated line-placement (:repeated? options)))
    line-placement))

(defn make-point-placement
  [{:keys [point rotation displacment]}]
  (.createPointPlacement
   style-factory
   (make-anchor-point point)
   (make-displacement displacment)
   (literal rotation)))

(defn make-label-placement
  [{:keys [point line rotation displacment]}]
  (cond
   (not (nil? line)) (make-line-placement line)
   (not (nil? point)) (make-point-placement
                  {:point point :displacment displacment :rotation rotation})))

(defn make-text
  [{:keys [font label fill halo placement vendor]}]
  (let [fonts (into-array Font [(make-font font)])
        halo (if halo (make-halo halo) nil)
        placement (if placement (make-label-placement placement) nil)
        font-fill (make-fill fill)
        text-symb (.createTextSymbolizer
                      style-factory
                      font-fill
                      fonts
                      halo
                      (attr (:property label))
                      placement
                      nil)
        options (.getOptions text-symb)]

    (if vendor
      (doseq [v vendor]
        (.put options (name (v 0)) (v 1))))
    text-symb))


(defn make-mark
  [{:keys [name size fill stroke size rotation]}]
  (.createMark style-factory
   (literal name)
   (make-stroke stroke)
   (make-fill fill)
   (literal size)
   (literal rotation)))

(defn make-online-resource
  [{:keys [url]}]
  (let [res (OnLineResourceImpl. (URI. url))]
    ;; disable modifying the resource at run time
    ;; the geotools
    (.freeze res)
    res))

(defn make-symbol
  [{:keys [mark online]}]
  (cond
   (not (nil? mark)) (make-mark mark)
   (not (nil? online)) (make-online-resource online)))

(defn make-graphic
  [{:keys [symbols opacity size rotation anchor displacement]}]
  (.createGraphic
   style-factory
   nil
   nil
   (into-array Symbol (map make-symbol symbols))
   (literal opacity)
   (literal size)
   (literal rotation)))


(defn make-polygon
  [{:keys [fill stroke]}]
  (.createPolygonSymbolizer style-factory (make-stroke stroke) (make-fill fill) nil))

(defn make-line
  [{:keys [stroke]}]
  (.createLineSymbolizer style-factory (make-stroke stroke) nil))

(defn make-point
  [options]
  (.createPointSymbolizer style-factory (make-graphic options) nil))

(defn make-symbolizers
  [{:keys [polygon line text graphic]}]
  (let [polygon-symb (if polygon (make-polygon polygon))
        line-symb (if line (make-line line))
        point-symb (if graphic (make-point graphic))
        text-symb (if text (make-text text))]
    (remove nil? [polygon-symb line-symb text-symb point-symb])))

(defn make-rule
  [{:keys [name where symbolizers max min]}]
  (let [rule (doto (.createRule style-factory)
               (.setName name))
        symbs (make-symbolizers symbolizers)]
    (-> rule .symbolizers (.addAll symbs))

    (if where
      (.setFilter rule (ECQL/toFilter where)))
    (if max
      (.setMaxScaleDenominator rule (double max)))
    (if min
      (.setMinScaleDenominator rule (double min)))

    rule))

(defn make-feature-style [{:keys [rules name]}]
  (let [type-style (.createFeatureTypeStyle style-factory)]
  (doseq [rule rules]
    (let [r (make-rule rule)]
      (-> type-style .rules (.add r))))
  (if name
    (.setName type-style name))
  type-style))

(defn make-style [{:keys [name title abstract feature-styles]}]
  (let [style (.createStyle style-factory)
        type-styles (for [fs feature-styles]
                      (make-feature-style fs))]
    (doseq [fs feature-styles]
      (let [ts (make-feature-style fs)]
        (-> style .featureTypeStyles (.add ts))))
   style))

(defn parse-string [opts]
  (make-style (yaml/parse-string opts)))

(defn parse-yaml-file [path]
  (parse-string (slurp path)))

(defn parse-sld-string [])
(defn parse-sld-file [])

(defn -main [& args])