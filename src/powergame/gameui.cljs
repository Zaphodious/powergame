(ns powergame.gameui
  (:require [rum.core :as rum]
            [powergame.bizlogic :as gc]))

(rum/defc board-area [{:keys [piece power status y x selected input-fn]}]
  [:td.board-area
   [:button {:class (if selected "selected" "quiet")
             :type "button"
             :onClick #(input-fn {:type :selected :y y :x x :value (not selected)})}
    (str "(" x "," y ")")]])

(rum/defc board-component [{:keys [board input-fn]}]
  [:table
   [:tbody
    (map (fn [a]
           [:tr (map #(board-area (assoc % :input-fn input-fn))
                     a)])
         board)]])

(rum/defc game-frame < rum/reactive
  [app-state-atom]
  (let [{:keys [board] :as app-state} (rum/react app-state-atom)]
    [:#frame
     [:#menubar [:ul [:li "item 1"] [:li "item 2"]]]
     [:#board (board-component app-state)]
     ;[:#state-print (pr-str app-state)]
     [:#footer]]))
