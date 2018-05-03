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
            [garden.types :as gt]
            [powergame.board-defs :as board-defs]
            [com.rpl.specter :as sp]))


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
  [
   [:html {:background-color :black
           :position :fixed
           :font-family "\"Courier New\", Courier, monospace"
           :width :100%
           :image-rendering :pixelated}
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
     [:#buttonbar {:position :fixed
                   :display :flex
                   :top (-vmin (- (/ menubar-height 2) 3))
                   :right 0
                   :left 0
                   :height (-vmin (/ menubar-height 2))
                   :overflow-wrap :break-word
                   :transition "top 0.5s"}
      [:&.showing {:top (-vmin (- (/ menubar-height 2) 3))}]
      [:&.hidden {:top (-vmin (- menubar-height))}]
                   ;:overflow :scroll}
      [:button {:height  (-vmin (/ menubar-height 2))
                :width (-vmin 17.9)
                :margin-right (-vmin 1)
                :margin-left (-vmin 1)
                ;:padding [(-vmin 3) (-vmin 2)]
                :display :inline
                :border :none
                :font-family :inherit
                :background-color :white
                :box-shadow "0px 0px 3px 1px black"}
       [:&:active {:box-shadow "inset 0px 0px 3px 1px black"}]]]]
    [:#menubar-bottom {:position   :absolute
                       :bottom     0
                       :right      0
                       :left       0
                       :overflow-y :hidden
                       :overflow-x :scroll
                       :max-height (-vmin menubar-height)
                       :min-height (-vmin menubar-height)
                       :width      :100%
                       :border-top-style :double
                       :border-width (-vmin 0.7)
                       :border-color :white
                       :transition "bottom 0.5s"}
     [:&.empty {:bottom (-vmin (-  menubar-height))}]
     [:#buttonbar {:display :flex
                   :height (-vmin menubar-height)
                   :width :100%}
      [:button {:display :flex
                :flex-direction :column
                :justify-content :flex-end
                :align-items :center
                :font-family :inherit
                :background-color :white


                :margin-top (-vmin (* menubar-height 0.1))
                :margin-left (-vmin (* menubar-height 0.05))
                :margin-right (-vmin (* menubar-height 0.05))
                :height (-vmin (* menubar-height 0.8))
                :min-width (-vmin (* menubar-height 1.7))}
       [:.sprite {:display :inline-block
                  :min-width (-vmin (* menubar-height 0.3))
                  :min-height (-vmin (* menubar-height 0.3))
                  :background-image (-var :--sprite)
                  :background-size :cover
                  :background-repeat :no-repeat
                  :background-position "center center"}]
       [:.label {:font-family :inherit
                 :font-size :0.8em}]
       [:hr {:margin (-vmin 0.05)
             :padding (str "0 " (-vmin (/ menubar-height 2)))}]]]]
    [:#modal {:transition "top 0.5s, bottom 0.5s, left 0.5s, right 0.5s"
              :position :fixed
              :top (-vmin (* 0.4 menubar-height))
              :bottom (-vmin (* 0.25 menubar-height))
              :left (-vmin (* 0.25 menubar-height))
              :right (-vmin (* 0.25 menubar-height))
              :background-color :black
              :z-index 10}
     [:.titlebar {:background-color :grey
                  :padding (-vmin 0.5)}
      [:.name {:margin (-vmin 2)}]
      [:.close {:float :right
                :margin 0
                :margin-right (-vmin 0.5)
                :border-radius (-vmin 4)
                :background-color :red
                :border-color :red}]]
     [:.interior {:color :white
                  :overflow :scroll
                  :min-width :90%
                  :max-width :90%
                  :min-height :90%
                  :max-height :90%
                  :padding :5%}
      [:.purchase-list
       [:.purchasable {:display :block
                       :padding-bottom (-vmin 2)
                       :padding-top (-vmin 2)
                       :border-style :none
                       :border-bottom-style :solid
                       :border-width (-vmin 0.1)}
        [:.info
         [:img {:width (-vmin (/ menubar-height 1.5))
                :height (-vmin (/ menubar-height 1.5))
                :float :left
                :margin-right (-vmin 1)}]
         [:.description {:font-size :0.8em}]]
        [:.cost {:display :flex
                 :padding (-vmin 0.3)}

         [:* {:padding (-vmin 1)
              :border-style :solid
              :border-width (-vmin 0.3)}]]
        [:.button-bar
         [:button {:font-family :inherit
                   :background-color :white
                   :width :100%}]]]]]
     [:&.showing {}]
     [:&.hidden {:top (-% 120)
                 :bottom (-% (- 10))
                 :left (-% 45)
                 :right (-% 45)}]]
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

    [:.board-container {:display :block
                        :position :relative
                        :margin-right :auto ;(-vmin 2)
                        :margin-left (calchelper :auto / 2)
                        :width :100px
                        :margin-top (-vmin menubar-height)
                        :margin-bottom (-vmin menubar-height)} ;(-vmin 2)}

     [:.traveler {:--board-zoom-level :inherit
                  :--pos-x :10px
                  :--pos-y :0px
                  :--new-pos-x (calchelper (-px board-area-size) * (-var :--pos-x))
                  :--sprite (url "")
                  :min-width (calchelper (-px board-area-size) * (-var :--board-zoom-level))
                  :height (calchelper (-px board-area-size) * (-var :--board-zoom-level))
                  :position :absolute
                  :border-radius :10px
                  :background-image (-var :--sprite)
                  :background-repeat :no-repeat
                  :background-position :center
                  :background-size (calchelper (-px board-area-size) * (-var :--board-zoom-level))
                  :color :white
                  :top (calchelper board-area-size * (-var :--pos-x) * (-var :--board-zoom-level)) ;+ (-px (/ board-area-size 2)))
                  :left (calchelper board-area-size * (-var :--pos-y) * (-var :--board-zoom-level)) ;+ (-px (/ board-area-size 2)))
                  :transition [(str "top " board-defs/game-step-time-in-seconds"s linear")
                               (str "left " board-defs/game-step-time-in-seconds"s linear")]
                  :transition-timing-function :linear}
      (map (fn [[a {:keys [name sprite] :as traveler}]]
             [(keyword (str "&." name)) {:--sprite (url (pr-str (str "../" sprite)))}])
           board-defs/travelers)]
     [:table {:border-collapse :collapse
              :z-index -10}
              ;:background-color :grey
              ;:margin "0 auto"}
      [:tr {:background-color (gc/lighten main-color 10)
            :transition ["background-color .2s"]
            :border-width :0px
            :border-style :solid}
       [:&.cursor-at {:background-color :red}
        [:td.board-area
         [:button {:--selected-box (url "../img/highlight_box.png")}
          [:&.selected {:--selected-box (url "../img/highlight_box_select.png")}]]]]]
      [:td.board-area {:--board-zoom-level :inherit
                       :min-width (calchelper (-px board-area-size) * (-var :--board-zoom-level))
                       :height (calchelper (-px board-area-size) * (-var :--board-zoom-level))
                       :margin 0
                       :padding 0
                       :border-style :solid
                       :border-width :0px}
                      (map (fn [i]
                             [(keyword (str "button.floor" i))
                              {:--tile (url (str \" "../img/crawl-tiles/dungeon/floor/cobble_blood_"i"_new"
                                                 ".png" \"))}])
                           (range 13))
        [:button {:--sprite (url "")
                  :--tile (url "")
                  :--direction (url "")
                  :--board-zoom-level :inherit
                  :--selected-box (url "")
                  :min-width       :inherit
                  :height          :inherit
                  :text-align      :center
                  :text-shadow     "0px 0px 3px white"
                  :color           :white
                  :border-width    :0px
                  :background-image      [(-var :--selected-box)
                                          (-var :--sprite)
                                          (-var :--direction)
                                          (-var :--tile)]
                  :background-size [:cover
                                    (calchelper :26px * (-var :--board-zoom-level))
                                    (calchelper :10px * (-var :--board-zoom-level))
                                    :cover]
                  :background-repeat :no-repeat
                  :background-position [:center
                                        "bottom right"
                                        "top left"
                                        :center]
                  :padding         0
                  :margin          0}
         [:&.selected
          {:--selected-box (url "../img/box_select.png")}]

         [:span.label {:--board-zoom-level :inherit
                                 :position :relative
                                 :font-size (calchelper :7px * (-var :--board-zoom-level))
                                 :top (-px (/ board-area-size 4))}]]]]
     (map (fn [a] [(keyword (str ".direction." (name a))) {:--direction (url (pr-str (str "../img/" (name a) "_arrow.png")))}])
          [:up :down :left :right])
     (map (fn [[thingname img]]
            [(keyword (str ".unit." thingname))
             {:--sprite (url (pr-str (str "../" img)))}])
          (sp/select
            [sp/ALL sp/LAST (sp/collect-one :name) :sprite]
            board-defs/units))]]

   (map (fn [[thingname img]]
          [(keyword (str ".operation." thingname))
           {:--sprite (url (pr-str (str "../" img)))}])
        (sp/select [sp/ALL sp/LAST (sp/collect-one :name) :image] board-defs/operations))])

