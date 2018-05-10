(ns powergame.operation-actions
  (:require [powergame.bizlogic :as gc :refer [handle-operation]]
            [com.rpl.specter :as sp]
            [powergame.board-defs :as board-defs]))


(defmethod handle-operation :purchase
  [a] (assoc a :modal-showing :purchase))
(defmethod handle-operation :upgrade
  [a] (assoc a :modal-showing :upgrade))
(defmethod handle-operation :info
  [a] (assoc a :modal-showing :info))
(defmethod handle-operation :rotate
  [a] (sp/transform [:board sp/ALL sp/ALL (sp/pred :selected) :piece :direction] #(% board-defs/rotation-order) a))
(defmethod handle-operation :sell
  [a]
  ;(println "selling the things! " (get-selected-areas a))
  (gc/put-thing-at (assoc a :next-input {:type :sell-unit :recoupe-type :sells-for})))

(defmethod handle-operation :move-old
  [statemap]
  (let [{:as selected-area :keys [x y piece former-piece]}
        (sp/select-first [:board sp/ALL sp/ALL (sp/pred :selected)] statemap)
        moved-coords (gc/offset-one {:x x :y y :direction (:direction piece)})
        {:as next-area next-x :x next-y :y
         {:as next-piece}
         :piece}
        (sp/select-first [(sp/keypath :board
                            (:x moved-coords)
                            (:y moved-coords))]
               statemap)
        {:as next-unit :keys [traversable conserve-on-traverse]} (when next-area
                                                                   ((:key next-piece) board-defs/units))]
    ;(println "should move? " traversable ", and to " moved-coords)
    (if (and traversable next-area)
      (->> statemap
           (sp/setval [(sp/keypath :board x y :piece)] (or former-piece {:key :empty :direction :up}))
           (sp/setval [(sp/keypath :board x y :former-piece)] nil)
           (sp/setval [(sp/keypath :board next-x next-y :former-piece)] (when conserve-on-traverse next-piece))
           (sp/setval [(sp/keypath :board next-x next-y :piece)] piece)
           (sp/setval [(sp/keypath :board x y :selected)] false)
           (sp/setval [(sp/keypath :board next-x next-y :selected)] true))
      statemap)))

(defmethod handle-operation :move
  [a]
  (assoc a :modal-showing :move-modal))
