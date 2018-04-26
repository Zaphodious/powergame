(ns powergame.bizlogic
  (:require [com.rpl.specter :as sp]))

(defn make-game-board [{:keys [height width]}]
  (->> height
       range
       (map-indexed
         (fn [y a] (->> width
                        range
                        (map-indexed
                          (fn [x b] {:piece {:type :empty}
                                     :power 10
                                     :x x
                                     :y y
                                     :status :ok}))
                        vec)))
       vec))

(defn init-game-state [{:keys [height width] :as init-args}]
  {:height    height
   :width     width
   :board     (make-game-board init-args)
   :travelers []})

(defn- make-fn [thing]
  (if (fn? thing) thing
      (constantly thing)))

(defn get-space [{:keys [board x y]}]
  (sp/select-first [(sp/keypath x y)] board))

(defn put-space [{:keys [board x y space]}]
  (sp/transform [(sp/keypath x y)] (make-fn space) board))

(defn put-piece [{:keys [board x y piece]}]
  (sp/transform [(sp/keypath x y :piece)] (make-fn piece) board))

(defn put-power [{:keys [board x y power]}]
  (sp/transform [(sp/keypath x y :piece)] (make-fn power) board))

(defn put-status [{:keys [board x y status]}]
  (sp/transform [(sp/keypath x y :status)] (make-fn status) board))

