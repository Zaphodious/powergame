(ns powergame.gameui
  (:require [rum.core :as rum]
            [powergame.bizlogic :as gc]
            [clojure.string :as str]
            [com.rpl.specter :as sp]
            [powergame.board-defs :as board-defs]))

(rum/defc board-area [{:keys [piece power status y x selected input-fn terrain]}]
  [:td.board-area
   [:button {:class (str
                      (if selected "selected" "quiet")
                      " "
                      (name status)
                      " "
                      (str terrain))
             :type "button"
             :onClick #(input-fn {:type :selected :y y :x x :value (not selected)})}
    [:span.label (str power)]]]) ;"(" x "," y ")")]])

(rum/defc board-component [{:keys [board input-fn cursor-at]}]
  [:table
   [:tbody
    (map-indexed (fn [n a]
                   [:tr {:class (when (= n cursor-at)"cursor-at")}
                    (map #(board-area (assoc % :input-fn input-fn))
                          a)])
         board)]])

(rum/defc purchase-modal [app-state]
  (let [purchasable-keys (gc/get-purchasable-units app-state)]
    [:.purchase-list
     (map (fn [a] (let [{namething :name
                          :keys [type upgrades operations sprites]
                          :as purchasable-thing} (get board-defs/units a)]
                     [:.purchasable namething])
                 purchasable-keys))]))

(rum/defc modal [{:keys [board juice money knowhow input-fn zoom-level select-amount modal-showing] :as app-state}]
  (let [{thing-name :name :as modal-info} (get board-defs/modals modal-showing)]
    [:#modal {:class (if modal-showing "showing" "hidden")}
     [:.titlebar [:span.name (if thing-name (str/capitalize thing-name) "")]
      [:button.close {:type "button"
                      :onClick #(input-fn {:type :close-modal})}
       "x"]]
     [:.interior (case modal-showing
                   :purchase (purchase-modal app-state)
                   nil [:.no-modal])]]))

(rum/defc game-frame < rum/reactive
  [app-state-atom]
  (let [{:keys [board juice money knowhow input-fn zoom-level select-amount modal-showing] :as app-state} (rum/react app-state-atom)]
    [:#frame
     [:#menubar-top [:ul#infobar
                     [:li.juice [:span.label "Juice"] [:span.value juice]]
                     [:li.juice [:span.label "Money"] [:span.value money]]
                     [:li.juice [:span.label "Know How"] [:span.value knowhow]]]
                [:#buttonbar {:class (if modal-showing "hidden" "showing")}
                 [:button {:type "button"
                                :onClick #(input-fn {:type :toggle-select})}
                       (str "Select " (str/capitalize  (name select-amount)))]
                 [:button {:type "button"}
                       "Info"]
                 [:button {:type "button"}
                       "Guide"]
                 [:button {:type "button"
                                :onClick #(input-fn {:type :zoom-up})}
                       "Zoom +"]
                 [:button {:type "button"
                                :onClick #(input-fn {:type :zoom-down})}
                       "Zoom -"]]]
     (modal app-state)

     [:#board  {:class (str "dragscroll zoom-level"zoom-level)}
      (board-component app-state)]
     ;[:#state-print (pr-str app-state)]
     (let [selected-ops (gc/get-operations-for-selected app-state)
           show-class (if (and (not (empty? selected-ops)) (not modal-showing))
                        "has-opts"
                        "empty")]
       [:#menubar-bottom {:class (str "dragscroll " show-class)}
        [:#buttonbar
         (map (fn [a]
                (let [{thing-name :name img :image desc :description :as option-details} (get board-defs/operations a)]
                  [:button {:type :button
                            :onClick #(input-fn {:type :operation :operation a})}
                   [:span.sprite {:class (str "operation " thing-name)}]
                   [:span.label (str/capitalize thing-name)]
                   [:hr]
                   [:span.label desc]]))
              (concat selected-ops selected-ops selected-ops))]])]))


