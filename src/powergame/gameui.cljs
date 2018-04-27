(ns powergame.gameui
  (:require [rum.core :as rum]
            [powergame.bizlogic :as gc]))

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
    [:span.power-collected (str power)]]]) ;"(" x "," y ")")]])

(rum/defc board-component [{:keys [board input-fn cursor-at]}]
  [:table
   [:tbody
    (map-indexed (fn [n a]
                   [:tr {:class (when (= n cursor-at)"cursor-at")}
                    (map #(board-area (assoc % :input-fn input-fn))
                          a)])
         board)]])

(rum/defc game-frame < rum/reactive
  [app-state-atom]
  (let [{:keys [board juice money knowhow input-fn zoom-level] :as app-state} (rum/react app-state-atom)]
    [:#frame
     [:#menubar-top [:ul#infobar
                     [:li.juice [:span.label "Juice"] [:span.value juice]]
                     [:li.juice [:span.label "Money"] [:span.value money]]
                     [:li.juice [:span.label "Know How"] [:span.value knowhow]]]
                [:ul#buttonbar
                 [:li [:button {:type "button"
                                :onClick #(input-fn {:type :deselect-all})}
                       "Deselect"]]
                 [:li [:button {:type "button"}
                       "Info"]]
                 [:li [:button {:type "button"}
                       "Guide"]]
                 [:li [:button {:type "button"
                                :onClick #(input-fn {:type :zoom-up})}
                       "Zoom +"]]
                 [:li [:button {:type "button"
                                :onClick #(input-fn {:type :zoom-down})}
                       "Zoom -"]]]]
     [:#board  {:class (str "dragscroll zoom-level"zoom-level)}
      (board-component app-state)]
     ;[:#state-print (pr-str app-state)]
     [:#menubar-bottom {:class "dragscroll"}
      [:ul#buttonbar
       (map (fn [a] [:li [:button {:type :button} (str a)]])
            (range 30))]]]))

