(ns powergame.gardener
  (:require [garden.core :as g]
            [garden.def :as gd]
            [garden.media :as gm]
            [garden.color :as gc]
            [garden.arithmetic :as ga]
            [garden.selectors :as gs]
            [clojure.string :as str]
            [clojure.set :as set]
            [garden.stylesheet :as gss :refer [at-media]]
            [garden.types :as gt]))


(gd/defcssfn url)
(gd/defcssfn blur)
(gd/defcssfn calc)
(gd/defcssfn src)
(gd/defcssfn linear-gradient)
(defn -var [a] (str "var("(name a)")"))

(defn supports [support-statement garden-seq]
  (fn [previous-css] (str previous-css "\n\n\n" "@Supports (" support-statement ") {\n     " (g/css garden-seq) "}")))

(defn grid-area-strings [& stringers]
  (reduce str
          (map
            (fn [a] (str "\n\"" a "\""))
            stringers)))

(defn name-or-string [thing]
  (try
    (name thing)
    (catch Exception e
      (str thing))))

(defn name-if-not-symbol [thing]
  (if (symbol? thing) thing (name-or-string thing)))

(def opmap {- " - " + " + " * " * " / " / "})

(defn replace-operators [possible-op]
  (if-let [oper (get opmap possible-op)]
    oper
    possible-op))

(defn calchelper [& args]
  (let [calcstring (->> args (map replace-operators) (map name-or-string) (map #(str % " ")) (reduce str) str/trim)]
    (calc calcstring)))

(defn unit-fn-for [unittype]
  (fn [n] (str (double n) unittype)))

(def -px (unit-fn-for "px"))
(def -em (unit-fn-for "em"))
(def -% (unit-fn-for "%"))
(def -vmin (unit-fn-for "vmin"))

(defn quoth [n]
  (str "/'" n "/'"))

(def main-color (gc/from-name :green))
(def board-area-size 30)
(def menubar-height 20)

(gd/defstyles main
  [:html {:background-color :black
          :position :fixed
          :font-family "\"Courier New\", Courier, monospace"
          :width :100%}
   [:body {:width :100%
           :display :inline-block
           :position :fixed
           :padding 0
           :margin 0
           :top (-% 0)
           :bottom (-% 0)
           :right (-% 0)
           :left (-% 0)}]
   [:#frame {:position :absolute
             :top (-% 0)
             :bottom (-% 0)
             :right (-% 0)
             :left (-% 0)}]
   [:ul {:display :inline-block
         :padding (-% 0)
         :margin (-% 0)}
    [:li {:display :inline-block}]]

   [:#menubar-top {:z-index 10
                   :position :absolute
                   :top          0
                   :right        0
                   :left         0
                   :width :100%
                   :display :block
                   :height       (-vmin menubar-height)}
    [:ul#infobar {:font-size (-vmin 4)
                  :position :relative
                  :display :block
                  :top 0
                  :right 0
                  :left (-vmin 0.5)
                  :height (-vmin (- (/ menubar-height 2) 5))
                  :width :100%}
     [:li {:padding-left               (-vmin 1)
           :padding-right              (-vmin 1)
           :padding-bottom             :0px
           :padding-top                :0px
           :background-color           :white
           :margin-right               (-vmin 0.5)
           :margin-left               (-vmin 0.5)
           :border-bottom-right-radius (-vmin 3)
           :border-bottom-left-radius  (-vmin 3)
           ;:border-width               (-vmin 0.3)
           ;:border-style               :solid
           :width                      (-vmin 30.0)
           :text-align                 :center
           :display :inline-block}
      [:span.label {:width :100%
                    :text-align :right}]
      [:span.value {:float :right
                    :padding-right (-vmin 1)
                    :padding-left (-vmin 1)
                    :border-width 0
                    :border-left-width (-vmin 0.3)
                    :border-style :solid
                    :border-color :grey}]]]
    [:ul#buttonbar {:position :fixed
                    :top (-vmin (- (/ menubar-height 2) 3))
                    :right 0
                    :left 0
                    :height (-vmin (/ menubar-height 2))}
     [:button {:height  (-vmin (/ menubar-height 2))
               :width (-vmin 17.9)
               :margin-right (-vmin 1)
               :margin-left (-vmin 1)
               :padding [(-vmin 3) (-vmin 2)]
               :border :none
               :font-family :inherit
               :background-color :white
               :box-shadow "0px 0px 3px 1px black"}
      [:&:active {:box-shadow "inset 0px 0px 3px 1px black"}]]]]
   [:#menubar-bottom {:position   :absolute
                      :bottom     0
                      :right      0
                      :left       0
                      :max-height (-vmin menubar-height)
                      :min-height (-vmin menubar-height)
                      :width      :100%
                      :border-top-style :double
                      :border-width (-vmin 0.7)
                      :border-color :white}
    [:ul#buttonbar {:height :inherit
                    :max-height :inherit
                    :min-height :inherit
                    :overflow-y :scroll}
     [:li {:height :inherit
           :max-height (-vmin (- (/ menubar-height 2) 0.5))
           :min-height (-vmin (- (/ menubar-height 2) 0.5))
           :max-width (-vmin (/ menubar-height 2))
           :min-width (-vmin (/ menubar-height 2))}
      [:button {:height :inherit
                :max-height :inherit
                :min-height :inherit
                :max-width :inherit
                :min-width :inherit}]]]]
   [:#board {:position :fixed
             ;:padding :100px
             :--board-zoom-level 1
             :top 0
             :left 0
             :right 0
             :bottom 0
             :overflow :scroll
             :display :block}
    [:&.zoom-level2 {:--board-zoom-level 1.3}]
    [:&.zoom-level3 {:--board-zoom-level 1.7}]
    [:&.zoom-level4 {:--board-zoom-level 2.1}]
    [:&.zoom-level5 {:--board-zoom-level 2.7}]]
   [:table {:position :relative
            :border-collapse :collapse
            :z-index -10
            :margin-right :auto ;(-vmin 2)
            :margin-left :auto ;(-vmin 2)
            :margin-top (-vmin menubar-height)
            :margin-bottom (-vmin menubar-height)}
            ;:background-color :grey
            ;:margin "0 auto"}
    [:tr {:background-color (gc/lighten main-color 10)
          :transition ["background-color .2s"]
          :border-width :0px
          :border-style :solid}
     [:&.cursor-at {:background-color :red}
      [:td.board-area
       [:button {:background-image (url "../img/crawl-tiles/dc-dngn/floor/lava0.png")}
        [:&.selected {:background-image (url "../img/crawl-tiles/dc-dngn/floor/tutorial_pad_hot.png")}]]]]]
    [:td.board-area {:--board-zoom-level :inherit
                     :min-width (calchelper (-px board-area-size) * (-var :--board-zoom-level))
                     :height (calchelper (-px board-area-size) * (-var :--board-zoom-level))
                     :margin 0
                     :padding 0
                     :border-style :solid
                     :border-width :0px}
      [:button {:min-width :inherit
                 :height :inherit
                 :text-align :center
                 :text-shadow "0px 0px 3px white"
                 :color :white
                 :border-width :0px
                 :background-image (url "../img/crawl-tiles/dc-dngn/floor/cobble_blood1.png")
                 :background-size :cover
                 :padding 0
                 :margin 0}
       (map (fn [i]
              [(keyword (str "&.floor" i))
               {:background-image (url (str "../img/crawl-tiles/dc-dngn/floor/cobble_blood" (inc i)
                                            ".png"))}])
            (range 12))
       [:&.selected
        {:background-image (url "../img/crawl-tiles/dc-dngn/floor/tutorial_pad.png")}]
       [:span.power-collected {:--board-zoom-level :inherit
                               :position :relative
                               :font-size (calchelper :10px * (-var :--board-zoom-level))
                               :top (-px (/ board-area-size 4))}]]]]])

