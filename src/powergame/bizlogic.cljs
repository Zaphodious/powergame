(ns powergame.bizlogic
  (:require [com.rpl.specter :as sp]))

(defn make-game-board [{:keys [width height]}]
  (->> width
       range
       (map (fn [a] (->> height
                         range
                         (map (fn [b] {:piece {:type :empty}
                                       :power 10
                                       :status :ok}))
                         vec)))
       vec))

(defn make-fn [thing]
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




