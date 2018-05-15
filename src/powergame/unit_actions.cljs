(ns powergame.unit-actions
  (:require [powergame.board-defs :as board-defs]
            [powergame.bizlogic :as gc :refer [unit-action traveler-unit-intercept]]
            [powergame.knowledge :as pk]
            [com.rpl.specter :as sp]))

(defn add-traveler [traveler-class traveler-data statemap]
  (sp/setval [:travelers sp/END] [(into (traveler-class board-defs/travelers)
                                       (into traveler-data {:id (rand)}))]
             statemap))


(defmethod unit-action :default
  [a] a)

(defmethod unit-action :elf
  [{{:keys [x y travelers]
     {:keys [key direction]} :piece} :action-area
    :as statemap}]
  ;(println "Elf is acting!")
  statemap
  #_(sp/setval [:travelers sp/END] [(into (:dart board-defs/travelers)
                                        {:value 1 :x x :y y :id (rand) :direction direction})]
             statemap))

(defmethod unit-action :fountain
  [{{:keys [x y power] {:keys [key direction]} :piece} :action-area :as statemap}]
  ;(println "Fountain is acting!")
  (if (>= power 10)
    (->> statemap
         (sp/setval [(sp/keypath :board x y :power)] (- power 10))
         (add-traveler :splash {:value 1 :x x :y y :direction direction}))
    statemap))

(defn study [{kind :what-kind?}
             statemap])

(defmethod unit-action :human
  [{:as statemap {:as action-area :keys [x y power unit] {:keys [key direction]} :piece} :action-area}]
  (let [{:as facing-coords facing-x :x facing-y :y}
        (gc/offset-one {:x x :y y :direction direction})
        {:as facing-area {facing-key :key} :piece}
        (sp/select-first [(sp/keypath :board facing-x facing-y)] statemap)
        {:as facing-unit :keys [type knowledge]}
        (get board-defs/units facing-key)
        can-learn (and (isa? (:knowledge (key board-defs/units))
                             knowledge))]
                       ;(:ready-to-learn unit))]
    (println "knowledge is " knowledge " and type is " type)
    (if (and type knowledge (>= power 5) can-learn)
      (->> statemap
           (sp/transform [(sp/keypath :board x y :power)] (partial + -5))
           (sp/transform [(sp/keypath :board x y :piece :knowledge-held)]
                         (fn [a] (update-in a [knowledge] (partial + 1))))
           ;(sp/transform [:knowledge ::pk/totality] inc)
           (sp/setval [(sp/keypath :board x y :unit :ready-to-learn)] false)
           (add-traveler :learning {:value 0 :x x :y y :direction direction}))
      statemap)))


(defmethod traveler-unit-intercept :default
  [a] a)

(defn trade-for [{:keys [what-for accepts new-traveler-key power-required]}
                 {:as statemap
                  :keys [traveler-index]
                  {:keys [power x y] :as action-area} :action-area}]
  ;(println "trading juice for " what-for)
  (let [{:as traveler :keys [last-touched type value]}
        (sp/select-first [(sp/keypath :travelers traveler-index)] statemap)]
    ;(println "action area is " action-area)
    (if (and (not (= (:id (:piece action-area))
                     last-touched))
             (= type accepts)
             (>= power power-required))
      (->> statemap
           (sp/setval (sp/keypath :board x y :power) (- power power-required))
           (sp/transform [what-for] (partial + (* value 1)))
           (sp/transform [(sp/keypath :travelers traveler-index)]
                         (fn [a]
                           (merge a
                                  (new-traveler-key board-defs/travelers)
                                  {:last-touched (:id (:piece action-area))
                                   :value 0
                                   :x (:x action-area)
                                   :y (:y action-area)}))))
      statemap)))

(defmethod traveler-unit-intercept :devil
  [a] (trade-for {:what-for :money
                  :accepts :juice
                  :new-traveler-key :devilball
                  :power-required 10}
                 a))

(defmethod traveler-unit-intercept :altar
  [a] (trade-for {:what-for :juice
                  :accepts :juice
                  :new-traveler-key :altar-cloud
                  :power-required 10}
                 a))

(defmethod traveler-unit-intercept :portal
  [a] (trade-for {:what-for :knowhow
                  :accepts :juice
                  :new-traveler-key :altar-cloud
                  :power-required 10}
                 a))



(defmethod traveler-unit-intercept :elf
  [{:as statemap
    :keys [traveler-index]
    {:keys [power x y] :as action-area} :action-area}]
  ;(println "action area is " action-area)
  (if (>= power 5)
    (->> statemap
         (sp/setval [(sp/keypath :board x y :power)] (- power 5))
         (sp/transform [(sp/keypath :travelers traveler-index)]
                       (fn [a]
                         (if (and (not (= (:id (:piece action-area))
                                          (:last-touched a)))
                                  (= (:type a) :juice))
                           (assoc (into a (:dart board-defs/travelers))
                                  :direction (:direction (:piece action-area))
                                  :x (:x action-area)
                                  :y (:y action-area)
                                  :last-touched (:id (:piece action-area)))
                           a))))
    statemap))

(defmethod traveler-unit-intercept :human
  [{:as statemap
    :keys [traveler-index]
    {:keys [power x y piece] :as action-area} :action-area}]
  (if (and (>= power 5)
           (not (:ready-to-learn piece))
           (not (= :empty (sp/select-first [(sp/keypath :travelers traveler-index :type)] statemap))))
    (->> statemap
         (sp/setval [(sp/keypath :board x y :power)] (- power 5))
         ;(sp/setval [(sp/keypath :board x y :unit :ready-to-learn)] true)
         (sp/transform [(sp/keypath :knowledge)] (fn [a] (merge-with + (:knowledge-held piece) a)))
         (sp/setval [(sp/keypath :board x y :piece :knowledge-held)] {})
         (sp/transform [(sp/keypath :travelers traveler-index)]
                       (fn [a] (assoc (into a (:ready-to-learn board-defs/travelers))
                                 :x x
                                 :y y)))
         (sp/setval [(sp/keypath :travelers traveler-index :last-touched)] (:id piece)))

    (->> statemap
         (sp/setval [(sp/keypath :travelers traveler-index :last-touched)] (:id piece)))))
  ;(println "action area is " action-area)
