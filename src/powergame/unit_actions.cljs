(ns powergame.unit-actions
  (:require [powergame.board-defs :as board-defs]
            [powergame.bizlogic :as gc :refer [unit-action traveler-unit-intercept]]
            [com.rpl.specter :as sp]))


(defmethod unit-action :default
  [a] a)

(defmethod unit-action :elf
  [{{:keys [x y travelers]
     {:keys [key direction]} :piece} :action-area
    :as statemap}]
  (println "Elf is acting!")
  statemap
  #_(sp/setval [:travelers sp/END] [(into (:dart board-defs/travelers)
                                        {:value 1 :x x :y y :id (rand) :direction direction})]
             statemap))

(defmethod unit-action :fountain
  [{{:keys [x y power] {:keys [key direction]} :piece} :action-area :as statemap}]
  (println "Fountain is acting!")
  (if (>= power 10)
    (->> statemap
         (sp/setval [(sp/keypath :board x y :power)] (- power 10))
         (sp/setval [:travelers sp/END] [( (direction gc/travel-by-direction)
                                           (into (:splash board-defs/travelers)
                                                 {:value 1 :x x :y y :id (rand) :direction direction}))]))
    statemap))

(defmethod traveler-unit-intercept :default
  [a] a)

(defmethod traveler-unit-intercept :elf
  [{:as statemap
    :keys [traveler-index
           action-area]}]
  (println "action area is " action-area)
  (sp/transform [(sp/keypath :travelers traveler-index)]
                (fn [a]
                  (if (not (= (:id (:piece action-area))
                              (:last-touched a)))
                    (assoc (into a (:dart board-defs/travelers))
                           :direction (:direction (:piece action-area))
                           :x (:x action-area)
                           :y (:y action-area)
                           :last-touched (:id (:piece action-area)))
                    a))
                statemap))

