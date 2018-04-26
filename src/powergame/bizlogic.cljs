(ns powergame.bizlogic
  (:require [com.rpl.specter :as sp]))

(defn make-game-board [{:keys [height width]}]
  (->> height
       range
       (map-indexed
         (fn [x a] (->> width
                        range
                        (map-indexed
                          (fn [y b] {:piece    {:type :empty}
                                     :power    10
                                     :y        y
                                     :x        x
                                     :status   :ok
                                     :selected false}))
                        vec)))
       vec))

(defn init-game-state [{:keys [height width input-fn] :as init-args}]
  {:height    height
   :width     width
   :board     (make-game-board init-args)
   :travelers []
   :juice 10
   :money 20
   :knowhow 30
   :input-fn input-fn})

(defn- make-fn [thing]
  (if (fn? thing) thing
      (constantly thing)))

(defn get-space [{:keys [board y x]}]
  (sp/select-first [(sp/keypath x y)] board))

(defn put-space [{:keys [board y x space]}]
  (sp/transform [(sp/keypath x y)] (make-fn space) board))

(defn put-thing-at [{:keys [board y x type value]}]
  (sp/transform [(sp/keypath x y type)] (make-fn value) board))

(defn process-input [{:keys [next-input board] :as state-map}]
  (case type
    (assoc state-map :board (put-thing-at (assoc next-input :board board)))))

