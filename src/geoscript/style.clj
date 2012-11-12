(ns geoscript.style
  (:require
   [clojure.java.io :as io]
   [clj-yaml.core :as yaml])
  (:import
   [java.net URI]
   [org.geotools.sld SLDConfiguration]
   [org.geotools.xml Parser]
   [org.geotools.styling SLDTransformer]
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

;; forward declarations
(declare make-stroke)

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

(defn to-float [x]
  (if (number? x)
    x
    (Float/parseFloat (str x))))

(defn make-font
  "Takes a clj.Map and returns a GeoTools FontImp object
   Has a set of sane default for the font family, size and weight
  "
  [options]
  (let [{:keys [family size style weight]
         :or {weight (literal "normal")
              size (literal 10)
              style (literal "normal")
              family (literal "Droid Sans")}} (make-literals options)]
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


(defn make-mark
  [{:keys [name size fill stroke size rotation]}]
  (.createMark style-factory
   (literal name)
   (make-stroke stroke)
   (make-fill fill)
   (literal size)
   (literal rotation)))

(defn make-online-resource
  [{:keys [uri format]
    :or   {format "image/png"}}]
  (.createExternalGraphic style-factory uri format))

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

(defn make-stroke
  "Returns a StrokeImpl object"
  [options]
  (let [{:keys [color width opacity line-join
                dash-array line-cap dash-offset]} (make-literals options)
        stroke (.createStroke style-factory
                       color
                       width
                       opacity
                       line-join
                       line-cap
                       nil
                       dash-offset
                       nil
                       nil)]
    ;; only set the dash-array when necessary
    (when dash-array
      (.setDashArray stroke (float-array (map to-float (:dash-array options)))))
    
    (when (:graphic-stroke options)
      (.setGraphicStroke stroke (make-graphic (:graphic-stroke options))))

    (when (:graphic-fill options)
      (.setGraphicFill stroke (make-graphic (:graphic-fill options))))
    
    stroke))

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
  (let [{:keys [offset gap inital-gap]}
        (make-literals (select-keys options [:offset :gap :inital-gap ]))
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
  [{:keys [font label fill halo placement vendor graphic]}]
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

    (when graphic
      (.setGraphic text-symb (make-graphic graphic)))

    text-symb))


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

(def scales
  [4.429438425E8
   2.2147192125E8
   1.10735960625E8
   5.53679803125E7
   2.768399015625E7
   1.3841995078125E7
   6920997.5390625
   3460498.76953125
   1730249.384765625
   865124.6923828125
   432562.34619140625
   216281.17309570312
   108140.58654785156
   54070.29327392578
   27035.14663696289
   13517.573318481445
   6758.786659240723
   3379.3933296203613
   1689.6966648101807
   844.8483324050903])

(defn set-max-denominator! [rule max]
  (when max
    (.setMaxScaleDenominator rule (double max))))

(defn set-min-denominator! [rule min]
  (when min
    (.setMinScaleDenominator rule (double min))))

(defn make-rule
  ;; consider adding support back for scale numbers
  [{:keys [name where symbolizers zoom-levels]}]
  (let [rule (doto (.createRule style-factory)
               (.setName name))
        symbs (make-symbolizers symbolizers)
        max (get scales (get zoom-levels :max))
        min (get scales (get zoom-levels :min))]

    (-> rule .symbolizers (.addAll symbs))

    (when where
      (.setFilter rule (ECQL/toFilter where)))

    (set-max-denominator! rule max)
    (set-min-denominator! rule min)
    rule))

(defn make-feature-style
  [{:keys [rules name]}]
  (let [type-style (.createFeatureTypeStyle style-factory)]
  (doseq [rule rules]
    (let [r (make-rule rule)]
      (-> type-style .rules (.add r))))
  (if name
    (.setName type-style name))
  type-style))

(defn make-style
  [{:keys [name title abstract feature-styles]}]
  (let [style (doto (.createStyle style-factory)
                (.setName name)
                (.setTitle title)
                (.setAbstract abstract))
        type-styles (for [fs feature-styles]
                      (make-feature-style fs))]
    (doseq [fs feature-styles]
      (let [ts (make-feature-style fs)]
        (-> style .featureTypeStyles (.add ts))))
   style))

(defn make-sld [{:keys [name title abstract style]}]
  (let [sld (doto (.createStyledLayerDescriptor style-factory)
              (.setName name)
              (.setTitle title)
              (.setAbstract abstract))
        layer (.createNamedLayer style-factory)]
    (.addStyle layer style)
    (-> sld   .layers (.add layer))
    sld))

(defn style->sld [style]
  (let [trans (doto (SLDTransformer.)
                (.setIndentation 2))]
    (.transform trans style)))


(defn parse-string [opts]
  (make-style (yaml/parse-string opts)))

(defn parse-yaml-file [path]
  (parse-string (slurp path)))

(defn convert-yaml->sld [in out]
  (let [in (io/resource in)
        style (parse-yaml-file in)
        sld  (style->sld (parse-yaml-file in))]
    (spit (io/resource out) sld)))

(defn -main [& args]
  (spit "resources/test.sld" (style->sld (parse-yaml-file "resources/test.yml"))))