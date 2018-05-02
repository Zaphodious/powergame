(ns powergame.bizlogic
  (:require [com.rpl.specter :as sp]
            [powergame.board-defs :as board-defs]))

(defn make-game-board [{:keys [height width]}]
  (->> height
       range
       (map-indexed
         (fn [x a] (->> width
                        range
                        (map-indexed
                          (fn [y b] {:piece    {:key :empty
                                                :direction :up}
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
   :max-power 20
   :select-amount :single ; :multi
   :modal-showing nil;:purchase
   :input-fn input-fn})

(defn- make-fn [thing]
  (if (fn? thing) thing
      (constantly thing)))

(defn get-space [{:keys [board y x]}]
  (sp/select-first [(sp/keypath x y)] board))

(defn get-selected-areas [statemap]
  (sp/select [:board sp/ALL sp/ALL (sp/pred :selected)] statemap))

(defn get-operations-for-selected [{:keys [board] :as statemap}]
  (let [the-selected (get-selected-areas statemap)
        multi-mode (if (get the-selected 1) :multi? :single?)
        all-opts (->> the-selected
                      (sp/select [sp/ALL :piece :key])
                      (sp/transform [sp/ALL] #(% board-defs/units))
                      (sp/select [sp/ALL :operations sp/ALL])
                      (filter #(multi-mode (% board-defs/operations)))
                      (apply sorted-set))]
    all-opts))

(defn deselect-all [{:keys [board] :as state-map}]
  (sp/setval [:board sp/ALL sp/ALL :selected] false state-map))

(defn get-purchasable-units [{:keys [board] :as state-map}]
  (let [all-purchasable (->> board-defs/units
                             (filter #(-> % second :purchasable?))
                             (map first))]
    all-purchasable))

(defmulti put-thing-at (fn [a] (-> a :next-input :type)))
(defmethod put-thing-at :default
  [{:keys [board]
    {:keys [y x type value]} :next-input
    :as statemap}]
  (sp/transform [(sp/keypath :board x y type)] (make-fn value) statemap))

(defmethod put-thing-at :selected
  [{:keys [board select-amount]
    {:keys [y x type value]} :next-input
    :as statemap}]
  (sp/transform [(sp/keypath :board x y type)] (make-fn value)
    (if (= select-amount :single) (deselect-all statemap) statemap)))

(defmethod put-thing-at :unit
  [{:keys [board]
    {:keys [value]} :next-input
    :as statemap}]
  (let [selected-areas (get-selected-areas statemap)
        modmap (sp/setval [:board sp/ALL sp/ALL (sp/pred :selected) :piece :key] value statemap)]
    (println "selected is " selected-areas "and modmap is " modmap)
    modmap))

(defn put-space [{:keys [board y x space]}]
  (sp/transform [(sp/keypath x y)] (make-fn space) board))

(defn select-space [{:keys [board select-amount]
                     {:keys [y x type value]} :next-input
                     :as statemap}])


(defn toggle-select [{:keys [select-amount] :as state-map}]
  (let [new-select (case select-amount
                     :single :multi
                     :single)]
    (-> (if (= new-select :single)
          (deselect-all state-map)
          state-map)
      (assoc :select-amount new-select))))

(defn handle-operation [{:keys [next-input board zoom-level] :as state-map}]
  (println "OPERATION! " (:operation next-input))
  (let [{:keys [on-activate] :as op} (get board-defs/operations (:operation next-input))]
    (on-activate state-map)))

(defn process-input [{:keys [next-input board zoom-level] :as state-map}]
  (println "Doin' " next-input)
  (case (:type next-input)
    :toggle-select (toggle-select state-map)
    :zoom-up (assoc state-map :zoom-level (-> zoom-level inc (min 5) (max 1)))
    :zoom-down (assoc state-map :zoom-level (-> zoom-level dec (min 5) (max 1)))
    :operation (handle-operation state-map)
    :close-modal (assoc state-map :modal-showing nil)
    (put-thing-at state-map)))

(defn advance-cursor [{:keys [cursor-at height] :as app-state}]
  (let [next (inc cursor-at)
        adjusted (min height next)
        actual (if (= adjusted height) 0 adjusted)]
    (assoc app-state :cursor-at actual)))

(defn charge-board [app-state]
  (sp/transform [(sp/collect-one :max-power)
                 :board sp/ALL sp/ALL
                 :power] #(-> %2 inc (max 1) (min %1))
                app-state))

(defn make-tick [level-state]
  (-> level-state
      charge-board
      advance-cursor))