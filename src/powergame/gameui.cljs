(ns powergame.gameui
  (:require [rum.core :as rum]
            [powergame.bizlogic :as gc]
            [clojure.string :as str]
            [com.rpl.specter :as sp]
            [powergame.board-defs :as board-defs]
            [powergame.knowledge :as pk]))

(rum/defc cursor-at-component [{:keys [board input-fn cursor-at width height] :as app-state}]
  (map (fn [n]
          [:.cursor-at {:style #js{:--pos-x (str cursor-at "px")
                                   :--pos-y (str n "px")}}])
       (range width)))

(rum/defc board-area [{{:keys [key direction] :as piece} :piece
                       :keys [power status y x selected input-fn terrain cursor-at]}]
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
      [:span.label (str power)]
      #_(when (= x cursor-at) [:span.cursor-at
                               {:id (str "cursor-at-" y)
                                :key (str "cursor-at-" y)}
                               ""])]])) ;"(" x "," y ")")]])

(defn printpass [a] ;(println a)
 a)
(rum/defc traveler-component
  [app-state]
  (sp/transform
    [sp/ALL]
    (fn [{:keys [x y id name value] :as traveler}]
      [:.traveler {:id    (str "traveler-" id)
                   :class name
                   :key   (str "traveler-" id)
                   :style #js{:--pos-x (str x "px")
                              :--pos-y (str y "px")}}
       [:.label value]])
    (:travelers app-state)))

(rum/defc board-component [{:keys [board input-fn cursor-at width height] :as app-state}]
  [:.board-container {:class (str "board-width-"width)}
   (traveler-component app-state)
   (cursor-at-component app-state)
   [:table
    [:tbody
     (map-indexed (fn [n a]
                    [:tr ;{:class (when (= n cursor-at)"cursor-at")}
                     (map #(board-area (assoc % :input-fn input-fn :cursor-at cursor-at))
                           a)])
          board)]]])


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
                       [:.money "Money " money]]
                       ;[:.knowhow "Minimum Know How " knowhow]]
                      "Sells for"
                      [:.cost
                       [:.juice "Magic " selljuice]
                       [:.money "Money " sellmoney]]
                       ;[:.knowhow "Recieved Know How " sellknowhow]]
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
                       [:.money "Money " money]]
                       ;[:.knowhow "Minimum Know How " knowhow]]
                      "Sells for"
                      [:.cost
                       [:.juice "Magic " selljuice]
                       [:.money "Money " sellmoney]]
                       ;[:.knowhow "Recieved Know How " sellknowhow]]
                      [:.button-bar
                       [:button {:type :button
                                 :onClick #(input-fn {:type :unit :value a :pay-cost true})}
                        "Purchase"]]]))
                      ;[:div (pr-str (gc/get-selected-areas app-state))]]))
          purchasable-keys)]))

(rum/defc info-modal [{:keys [input-fn] :as app-state}]
  (let [selected-pieces (apply sorted-set (sp/select [:board sp/ALL sp/ALL (sp/pred :selected) :piece :key] app-state))]
    ;(println "selected pieces " selected-pieces)
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
                       [:.money "Money " money]]]))
                       ;[:.knowhow "Minimum Know How " knowhow]]]))
                      ;[:div (pr-str (gc/get-selected-areas app-state))]]))
          selected-pieces)]))

(rum/defc dungeon-state-modal [{:as app-state :keys []}]
  [:.dungeon-state
   [:.state-section
    [:.label "Knowledge"]
    [:table
     [:tbody
      (let [path-map (pk/make-knowledge-path-map)
            derived (pk/get-derived-knowledge (:knowledge app-state))
            sorted-derived (pk/sort-knowledge-track-map derived)]
        (map
          (fn [[k v]]
            (when (not (zero? v))
              (let [parent-count (-> path-map k count)]
                  [:tr
                   [:td.knowledge {:class (str "level-" parent-count)}
                    ;[:span.level-0 "<"]
                    (->> (-> parent-count
                             dec
                             (take (repeat ":")))
                         (map-indexed (fn [i a]
                                        [:span {:class (str "level-" (inc i))}
                                         a])))
                                            ;(repeat "\t "))))) ;"ù"))))
                    [:span {:class (str "level-" parent-count)}
                     ">" (str/capitalize (name k))]]
                   [:td v]])))
          sorted-derived))]]]])

(rum/defc move-modal [app-state]
  [:.move-buttons
   [:button.up "up"]
   [:button.down "down"]
   [:button.left "left"]
   [:button.right "right"]])


(rum/defc modal [{:keys [board juice money knowhow input-fn zoom-level select-amount modal-showing] :as app-state}]
  (let [{thing-name :name :as modal-info} (get board-defs/modals modal-showing)]
    [:#modal {:class (if modal-showing (str "showing " (name modal-showing))
                                       "hidden")}
     [:.titlebar [:span.name (if thing-name (str/capitalize thing-name) "")]
      [:button.close {:type "button"
                      :onClick #(input-fn {:type :close-modal})}
       "x"]]
     [:.interior (case modal-showing
                   :purchase (purchase-modal app-state)
                   :info (info-modal app-state)
                   :upgrade (upgrade-modal app-state)
                   :dungeon-state (dungeon-state-modal app-state)
                   :move-modal (move-modal app-state)
                   nil [:.no-modal])]]))

(rum/defc game-frame < rum/reactive
  [app-state-atom]
  (let [{:keys [board juice money knowhow input-fn zoom-level select-amount modal-showing width] :as app-state} (rum/react app-state-atom)]
    [:div {:id "frame"
           :key "frame"
           :class "frame"}
     [:#menubar-top [:ul#infobar
                     [:li.juice [:span.label [:img {:src "img/crawl-tiles/monster/abyss/wretched_star.png"}]]
                      [:span.value juice]]
                     [:li.juice [:span.label [:img {:src "img/crawl-tiles/item/gold/gold_pile_7.png"}]]
                      [:span.value money]]]
                     ;[:li.juice [:span.label [:img {:src "img/crawl-tiles/item/book/artefact/bookmark_new.png"}]] [:span.value knowhow]]]
                [:#buttonbar {:class (if modal-showing "hidden" "showing")}
                 [:button {:type "button"
                                :onClick #(input-fn {:type :toggle-select})}
                       (str "Select " (str/capitalize  (name select-amount)))]
                 [:button {:type "button"
                           :onClick #(input-fn {:type :show-dungeon-state})}
                  "Over view"]
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
              selected-ops)]])]))


