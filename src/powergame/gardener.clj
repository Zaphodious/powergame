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

(gd/defstyles main
  [:* {:background-color main-color}])
