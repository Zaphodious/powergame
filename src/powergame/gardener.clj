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

(defn quoth [n]
  (str "/'" n "/'"))

(def main-color (gc/lighten (gc/from-name :blue) 40))
(def board-area-size 40)
(def menubar-height 60)

(gd/defstyles main
  [:html {:background-color main-color
          :position :fixed}
   [:#frame {:position :fixed
             :top 0
             :bottom 0
             :right 0
             :left 0}]
   [:ul {:display :inline-block
         :padding 0
         :margin 0}
    [:li {:display :inline-block}]]

   [:#menubar-top {:position :fixed
                   :top          0
                   :right        0
                   :left         0
                   :height       (-px menubar-height)}
    [:ul#infobar {:position :fixed
                  :top 0
                  :right 0
                  :left :3px
                  :height (-px (- (/ menubar-height 2) 5))
                  :width :100%}
     [:li {:padding-left :7px
           :padding-right :7px
           :padding-bottom :0px
           :padding-top :0px
           :margin-right :7px
           :border-bottom-right-radius :10px
           :border-bottom-left-radius :10px
           :border-width :1px
           :border-style :solid
           :width (calchelper :33.33% - :23px)
           :text-align :center}]]
    [:ul#buttonbar {:position :fixed
                    :top (-px (- (/ menubar-height 2) 5))
                    :right 0
                    :left 0
                    :height (-px (/ menubar-height 2))}
     [:button {:height  (-px (/ menubar-height 2))}]]]
   [:#board {:position :fixed
             :top (-px menubar-height)
             :left :5px
             :right :5px
             :bottom (-px menubar-height)
             :overflow :scroll
             :display :block
             :max-height (calchelper :100% - (-px menubar-height))}]
   [:table {:background-color :grey
            :margin "0 auto"}
    [:tr {:background-color :green
          :transition ["background-color .2s"]
          :border-width :3px
          :border-style :solid}
     [:&.cursor-at {:background-color :red}]]
    [:td.board-area {:min-width (-px board-area-size)
                     :height (-px board-area-size)
                     :margin 0
                     :padding 0
                     :border :none}
     [:button {:width (-px board-area-size)
               :height (-px board-area-size)
               :text-align :center
               :background-color :transparent
               :border-width :0px
               :padding 0
               :margin 0}
      [:&.selected {:background-color :blue}]]]]])

