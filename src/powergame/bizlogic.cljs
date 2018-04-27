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
                                     :terrain  (str "floor"(inc (rand-int 12)))
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
   :knowhow 1
   :cursor-at 0
   :zoom-level 1
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

(defn deselect-all [{:keys [board] :as state-map}]
  (sp/setval [:board sp/ALL sp/ALL :selected] false state-map))

(defn process-input [{:keys [next-input board] :as state-map}]
  (println "Doin' " next-input)
  (case (:type next-input)
    :deselect-all (deselect-all state-map)
    (assoc state-map :board (put-thing-at (assoc next-input :board board)))))

(defn advance-cursor [{:keys [cursor-at height] :as app-state}]
  (let [next (inc cursor-at)
        adjusted (min height next)
        actual (if (= adjusted height) 0 adjusted)]
    (assoc app-state :cursor-at actual)))

(defn charge-board [app-state]
  (sp/transform [:board sp/ALL sp/ALL :power] inc app-state))