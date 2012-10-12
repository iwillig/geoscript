(ns geoscript.style-test
  (:use
   [clojure.test]
   [geoscript.style])
  (:import
   [org.geotools.metadata.iso.citation OnLineResourceImpl]
   [org.opengis.style Graphic]
   [org.geotools.styling
    StyledLayerDescriptor
    StyleImpl
    StyledLayer
    TextSymbolizer
    PolygonSymbolizer
    LineSymbolizer
    LinePlacement
    PointPlacement
    Font
    FontImpl
    AnchorPoint
    Halo
    Displacement
    Mark
    MarkImpl
    Fill
    Stroke]))

(def halo
  {:radius "2px"
   :color "#FFFFFF"
   :opacity 0.7})

;; should support text symbolizer
(def text-symb
  (merge
   halo
   {:color "green"
    :opacity "50%"}))

;; should only produce a line symbolizer
(def line
  {:color "#eee"
   :opacity 0.6
   :width 1})

(def polygon
  {:color "#eee"
   :opacity 0.5})

(def line&polygon
  {:fill polygon
   :stroke line})

(defn mark
  {:name "hello"
   :size 10
   :rotation 10
   :stroke {:color "#eee"}
   :fill   {:color "#eee"}})


(deftest basic-symbolizer-funcs
  (testing "basic constructors functions"
    (are [cls ist] (instance? cls ist)
         FontImpl (make-font 
                   {:family "Times New Roman"
                    :style "oblique"
                    :weight "bold"
                    :size "17px"})         
         Mark (make-mark mark)
         TextSymbolizer (make-text text-symb)
         Stroke (make-stroke {:color "#333"})
         Fill (make-fill {:color "#333"})
         PolygonSymbolizer (make-polygon {:fill {:color "#eee"} :stroke {:color "#eee"}})
         LineSymbolizer (make-line {:stroke {:color "#eee"}})))

  ;; each constructor function should take a empty map without
  ;; throwing an exception
  (testing "Fill creation"
    (is (instance? Fill (make-fill {})))
    (let [fill (make-fill {:color "#000"})]
      (is (instance? Fill fill))
      (are [actual expected] (= actual (literal expected))
           (.getColor fill) "#000"
           (.getOpacity fill) 1)))

  (testing "Stroke creation"
    (is (instance? Stroke (make-stroke {})))
    (let [stroke (make-stroke {:color "#000" :width 3})]
      (is (instance? Stroke stroke))
      (are [actual expected] (= actual (literal expected))
           (.getColor stroke) "#000"
           (.getOpacity stroke) 1
           (.getWidth stroke)  3)))

  (testing "Halo creation"
    (is (instance? Halo (make-halo {})))
    (let [halo (make-halo {:color "#000" :radius 10 :opacity 0.5})]
      (is (instance? Halo halo))
      (is (instance? Fill (.getFill halo)))
      (is (= (.getRadius halo) (literal 10)))))

  (testing "Displacement creation"
    (is (instance? Displacement (make-displacement {})))
    (let [disp (make-displacement {:x 12 :y 10})]
      (is (instance? Displacement disp))
      (is (= (.getDisplacementX disp) (literal 12)))
      (is (= (.getDisplacementY disp)  (literal 10)))))
  
  (testing "Label placement"
    (testing "high level label placement function"
      (testing "point placement"
        (let [p (make-label-placement {:point {}})]
          (is (instance? PointPlacement p))))
      (testing "line placement"
        (let [lp (make-label-placement {:line {}})]
          (is (instance? LinePlacement lp))
          )))

    (testing "Point Anchor objects"
      ;; can we pass an empty map to the constructor 
      (is (instance? AnchorPoint (make-anchor-point {})))
      (let [p (make-anchor-point {:x 1 :y 2})]
        (is (instance? AnchorPoint p))
        (is (= (.getAnchorPointX p) (literal 1)))
        (is (= (.getAnchorPointY p) (literal 2)))))

    (testing "Line Anchor Placement objects"
      ;; can we pass a empty map to the constructor
      (is (instance? LinePlacement (make-line-placement {})))
      (let [line-pl (make-line-placement
                     {:offset 1
                      :repeated? true
                      :gap 1
                      :inital-gap 2})]
        (is (instance? LinePlacement line-pl))
        (is (true? (.isRepeated line-pl)))
        (are [acutal expected] (= acutal (literal expected))
             (.getGap line-pl) 1
             (.getInitialGap line-pl) 2))))
  
  ;; font creation is hard because geotools excepts default values for
  ;; each of the four required arguments, but does not provide those defaults
  ;; allow users to create a font without providing an defaults
  (testing "Font creation"
    (testing  "Without any arguments the make-font function should return a working font"
      (let [font (make-font {})]
        (is (instance? Font font))
        (are [actual expected] (= actual (literal expected))
             (.getSize font) 10
             (.getFontFamily font) "Arial"
             (.getFontStyle font) "normal"
             (.getFontWeight font) "normal")))

    (testing "Make sure that the arguments becomes values on the font object"
      (let [font (make-font {:size 20})]
        (is (= (.getSize font)  (literal 20))))))


  (testing "Text symbolize"
    (let [text-symb (make-text
                     {:halo {:color "#eee"}
                      :placement {:point {}}
                      :follow-line? true
                      :vendor
                      {:followLine true
                       :group "Yes"}
                      :font {:color "#eee"}})]
      (is (instance? TextSymbolizer text-symb))
      (is (instance? Fill (.getFill text-symb)))
      (is (instance? PointPlacement (.getLabelPlacement text-symb)))

      (let [options (.getOptions text-symb )]
        (is (true? (.get options "followLine"))))))
  
  (testing "Mark creation"
    (testing "Without args a mark object is created correctly"
      (let [mark (make-mark {})]
        (is (instance? Mark mark)))))

  (testing "online graphic"
    (let [online (make-online-resource {:url "file:city.png"})]
      (is (instance? OnLineResourceImpl online))
      (is (.getProtocol online) "file")))

  (testing "making a graphic symbolize"
    (is (instance? OnLineResourceImpl (make-symbol {:online {:url "file:city.png"}})))
    (is (instance? Mark (make-symbol {:mark {}}))))

  (testing "creating a graphic"
    (let [graphic (make-graphic {:symbols [{:mark {}}]})]
      (is (instance? Graphic graphic)))))

(deftest style-generation
  (testing "A simple map should generate a style object"
    (let [style (make-style
                 {:name "test"
                  :title "a test style"
                  :feature-styles
                  {:rules
                   [{:where "boroname = 'Brooklyn'"
                     :name "a test rule"
                     :symbolizer
                     [{:line {:color "#eee"}}]}]}})]
      (is (instance? StyleImpl style)))))
