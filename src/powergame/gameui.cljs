(ns powergame.gameui
  (:require [rum.core :as rum]
            [powergame.bizlogic :as gc]))

(rum/defc board-area [{:keys [piece power status x y]}]
  [:td.board-area
   (str "("x","y")")])

(rum/defc board-component [board]
  [:table
   [:tbody
    (map (fn [a]
           [:tr (map board-area a)])
         board)]])

(rum/defc game-frame < rum/reactive
  [app-state-atom]
  (let [{:keys [board] :as app-state} (rum/react app-state-atom)]
    [:#frame
     [:#menubar [:ul [:li "item 1"] [:li "item 2"]]]
     [:#board (board-component board)]
     ;[:#state-print (pr-str app-state)]
     [:#footer]]))
