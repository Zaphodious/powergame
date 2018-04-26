(ns powergame.gameui
  (:require [rum.core :as rum]
            [powergame.bizlogic :as gc]))

(rum/defc game-frame < rum/reactive
  [app-state]
  [:#frame
   [:#menubar [:ul [:li "item 1"] [:li "item 2"]]]
   [:#board (pr-str (rum/react app-state))]
   [:#footer]])
