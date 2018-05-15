(ns powergame.bizlogic
  (:require [com.rpl.specter :as sp]
            [powergame.board-defs :as board-defs]
            [powergame.knowledge :as pk]))

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
   :juice 400
   :money 400
   :cursor-at 0
   :daily-charge 10
   :zoom-level 1
   :max-power 20
   :select-amount :single ; :multi
   :modal-showing nil;:purchase
   :knowledge (pk/make-knowledge-track-map #::pk{:vengeance 10
                                                 :power 3})
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

(defn get-immediate-upgrades-for [entity-key]
  (-> board-defs/units entity-key :upgrade-paths))

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
  [{:keys [board juice money knowhow]
    {:keys [value pay-cost]} :next-input
    :as statemap}]
  (let [selected-areas (get-selected-areas statemap)
        units-to-add (count selected-areas)
        {{cost-money :money
          cost-juice :juice} :cost :as thing-to-add} (get board-defs/units value)
        can-pay? (and (<= (* units-to-add cost-money)
                          money)
                      (<= (* units-to-add cost-juice)
                          juice))
        price-adjusted-map (if (and pay-cost can-pay?)
                             (->> statemap
                                  (sp/transform [:money] (fn [a] (- a (* units-to-add cost-money))))
                                  (sp/transform [:juice] (fn [a] (- a (* units-to-add cost-juice)))))
                             statemap)
        modmap
        (if (or (not pay-cost) can-pay?)
          (sp/transform [:board sp/ALL sp/ALL (sp/pred :selected) :piece] (fn [a] (assoc a :key value
                                                                                           :id (rand)))
                        price-adjusted-map)
          statemap)]
    ;(println "money cost is " (* units-to-add cost-money))
   ; (println "selected is " selected-areas)
    (assoc modmap :modal-showing nil)))

(defmethod put-thing-at :sell-unit
  [{:keys [next-input] :as statemap}]
  ;(println "recoupe type is " next-input)
  (let [selected-units (sp/select [:board sp/ALL sp/ALL (sp/pred :selected) :piece :key] statemap)
        as-costs (sp/transform [sp/ALL]
                               (fn [a] (get (get board-defs/units a) (:recoupe-type next-input)))
                               selected-units)
        recouped (reduce #(merge-with + %1 %2)
                         {:money 0
                          :juice 0}
                         as-costs)
        units-removed (sp/setval [:board sp/ALL sp/ALL (sp/pred :selected) :piece :key] :empty statemap)]
    (merge-with + units-removed
                recouped)))



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

(defmulti unit-action #(-> % :action-area :piece :key))
(defmulti traveler-unit-intercept #(-> % :action-area :piece :key))

(defmulti handle-operation (fn [a] (-> a :next-input :operation)))


(defn process-input [{:keys [next-input board zoom-level] :as state-map}]
  ;(println "Doin' " next-input)
  (case (:type next-input)
    :toggle-select (toggle-select state-map)
    :zoom-up (assoc state-map :zoom-level (-> zoom-level inc (min 5) (max 1)))
    :zoom-down (assoc state-map :zoom-level (-> zoom-level dec (min 5) (max 1)))
    :operation (handle-operation state-map)
    :show-dungeon-state (assoc state-map :modal-showing :dungeon-state)
    :close-modal (assoc state-map :modal-showing nil)
    (put-thing-at state-map)))

(defn advance-cursor [{:keys [cursor-at height] :as app-state}]
  (let [next (inc cursor-at)
        adjusted (min height next)
        actual (if (= adjusted height) 0 adjusted)]
    (assoc app-state :cursor-at actual)))

(defn charge-board [{:as app-state
                     :keys [cursor-at daily-charge]}]
  (if (zero? cursor-at)
    (sp/transform [(sp/collect-one :max-power)
                   :board sp/ALL sp/ALL
                   :power] #(-> %2 (+ daily-charge)
                                (max 1) (min %1))
                  app-state)
    app-state))

(defn make-units-act [{:keys [cursor-at] :as app-state}]
  (let [areas-at-cursor (sp/select-first [:board (sp/keypath cursor-at)] app-state)]
    (reduce (fn [a b]
              (unit-action (assoc a :action-area b)))
            app-state
            areas-at-cursor)))

(def travel-by-direction
  {:up (fn [{:keys [x y speed] :as traveler}]
         (assoc traveler :x (- x speed)))
   :down (fn [{:keys [x y speed] :as traveler}]
           (assoc traveler :x (+ x speed)))
   :left (fn [{:keys [x y speed] :as traveler}]
           (assoc traveler :y (- y speed)))
   :right (fn [{:keys [x y speed] :as traveler}]
            (assoc traveler :y (+ y speed)))})

(defn offset-one [{:keys [x y direction]}]
  ((get travel-by-direction direction) {:x x :y y :speed 1}))

(defn tick-down-traveler [{:keys [lifetime energy] :as traveler}]
  (assoc traveler :lifetime (dec lifetime) :energy (dec energy)))

(defn make-travelers-travel [{:keys [travelers] :as app-state}]
  (sp/transform [:travelers sp/ALL]
                (fn [{:keys [direction energy lifetime type] :as traveler}]
                  (tick-down-traveler
                    (if (and (< 0 energy) (not (= :empty type)))
                        ((get travel-by-direction direction) traveler)
                        traveler)))
                app-state))

(defn make-travelers-trigger-units [{:keys [travelers] :as app-state}]
  (reduce-kv (fn [a ind {:as b :keys [x y]}]
               (let [x-int (int (+ x 0.5))
                     y-int (int (+ y 0.5))
                     area-below (sp/select-first [(sp/keypath :board x-int y-int)] a)]
                 (traveler-unit-intercept (assoc a :action-area area-below
                                                   :traveler-index ind))))
             app-state
             travelers))

(defn cull-travelers-out-of-bounds [{:keys [height width] :as app-state}]
  (sp/transform [:travelers]
                (fn [travs]
                  (vec
                    (filter (fn [{:keys [x y lifetime] :as traveler}]
                              ;(println "x "x " y "y)
                              ;(println "h " height " w " width)
                              (and (< -0.2 x)
                                   (> (- height 0.3)
                                      x)
                                   (< -0.2 y)
                                   (> (- width 0.3)
                                      y)
                                   (> lifetime 0)))

                            travs)))
                app-state))

(defn make-tick [level-state]
  (-> level-state
      advance-cursor
      charge-board
      make-travelers-travel
      make-travelers-trigger-units
      make-units-act
      cull-travelers-out-of-bounds))