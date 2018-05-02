(ns powergame.gameui
  (:require [rum.core :as rum]
            [powergame.bizlogic :as gc]
            [clojure.string :as str]
            [com.rpl.specter :as sp]
            [powergame.board-defs :as board-defs]))

(rum/defc board-area [{{:keys [key direction] :as piece} :piece
                       :keys [power status y x selected input-fn terrain]}]
  (let [{unitname :name :as unit-info} (get board-defs/units key)]
    [:td.board-area
     [:button {:class (str
                        (if selected "selected" "quiet")
                        " "
                        (name status)
                        " "
                        (str terrain)
                        " "
                        (str "unit " unitname)
                        " "
                        (when (not (= :empty key))
                          (str "direction " (name direction))))
               :type "button"
               :onClick #(input-fn {:type :selected :y y :x x :value (not selected)})}
      [:span.label (str power)]]])) ;"(" x "," y ")")]])

(rum/defc board-component [{:keys [board input-fn cursor-at] :as app-state}]
  [:table
   [:tbody
    (map-indexed (fn [n a]
                   [:tr {:class (when (= n cursor-at)"cursor-at")}
                    (map #(board-area (assoc % :input-fn input-fn))
                          a)])
         board)]])

(rum/defc purchase-modal [{:keys [input-fn] :as app-state}]
  (let [purchasable-keys (gc/get-purchasable-units app-state)]
    [:.purchase-list
     (map (fn [a] (let [{namething :name
                         {:keys [juice money knowhow] :as cost} :cost
                         {selljuice :juice sellmoney :money sellknowhow :knowhow} :sells-for
                          :keys [type upgrades operations sprite description]
                          :as purchasable-thing} (get board-defs/units a)]
                     [:.purchasable
                      [:.info
                       [:.name (str/capitalize namething)]
                       [:img {:src sprite}]
                       [:.description description]]
                      "Costs"
                      [:.cost
                       [:.juice "Magic " juice]
                       [:.money "Money " money]
                       [:.knowhow "Minimum Know How " knowhow]]
                      "Sells for"
                      [:.cost
                       [:.juice "Magic " selljuice]
                       [:.money "Money " sellmoney]
                       [:.knowhow "Recieved Know How " sellknowhow]]
                      [:.button-bar
                       [:button {:type :button
                                 :onClick #(input-fn {:type :unit :value a :pay-cost true})}
                        "Purchase"]]]))
                      ;[:div (pr-str (gc/get-selected-areas app-state))]]))
          purchasable-keys)]))

(rum/defc upgrade-modal [{:keys [input-fn] :as app-state}]
  (let [purchasable-keys (gc/get-immediate-upgrades-for (:key (:piece (first (gc/get-selected-areas app-state)))))]
    [:.purchase-list
     (map (fn [a] (let [{namething :name
                         {:keys [juice money knowhow] :as cost} :cost
                         {selljuice :juice sellmoney :money sellknowhow :knowhow} :sells-for
                          :keys [type upgrades operations sprite description]
                          :as purchasable-thing} (get board-defs/units a)]
                     [:.purchasable
                      [:.info
                       [:.name (str/capitalize namething)]
                       [:img {:src sprite}]
                       [:.description description]]
                      "Costs"
                      [:.cost
                       [:.juice "Magic " juice]
                       [:.money "Money " money]
                       [:.knowhow "Minimum Know How " knowhow]]
                      "Sells for"
                      [:.cost
                       [:.juice "Magic " selljuice]
                       [:.money "Money " sellmoney]
                       [:.knowhow "Recieved Know How " sellknowhow]]
                      [:.button-bar
                       [:button {:type :button
                                 :onClick #(input-fn {:type :unit :value a :pay-cost true})}
                        "Purchase"]]]))
                      ;[:div (pr-str (gc/get-selected-areas app-state))]]))
          purchasable-keys)]))

(rum/defc info-modal [{:keys [input-fn] :as app-state}]
  (let [selected-pieces (apply sorted-set (sp/select [:board sp/ALL sp/ALL (sp/pred :selected) :piece :key] app-state))]
    (println "selected pieces " selected-pieces)
    [:.purchase-list
     (map (fn [a] (let [{namething :name
                         {:keys [juice money knowhow] :as sells-for} :sells-for
                          :keys [type upgrades operations sprite description]
                          :as purchasable-thing} (get board-defs/units a)]
                     [:.purchasable
                      [:.info
                       [:.name (str/capitalize namething)]
                       [:img {:src sprite}]
                       [:.description description]]
                      "Sells for"
                      [:.cost
                       [:.juice "Magic " juice]
                       [:.money "Money " money]
                       [:.knowhow "Minimum Know How " knowhow]]]))
                      ;[:div (pr-str (gc/get-selected-areas app-state))]]))
          selected-pieces)]))

(rum/defc modal [{:keys [board juice money knowhow input-fn zoom-level select-amount modal-showing] :as app-state}]
  (let [{thing-name :name :as modal-info} (get board-defs/modals modal-showing)]
    [:#modal {:class (if modal-showing "showing" "hidden")}
     [:.titlebar [:span.name (if thing-name (str/capitalize thing-name) "")]
      [:button.close {:type "button"
                      :onClick #(input-fn {:type :close-modal})}
       "x"]]
     [:.interior (case modal-showing
                   :purchase (purchase-modal app-state)
                   :info (info-modal app-state)
                   :upgrade (upgrade-modal app-state)
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


