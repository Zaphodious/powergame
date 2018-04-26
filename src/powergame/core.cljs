(ns powergame.core
    (:require [rum.core :as rum]
              [powergame.bizlogic :as gc]
              [powergame.gameui :as gui]))

(enable-console-print!)

(println "This text is printed from src/powergame/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom (gc/init-game-state {:height 12 :width 12})))


(rum/defc hello-world []
  [:div
   [:h1 (:text @app-state)]
   [:h3 "Edit this and watch it change!"]])

(rum/mount (gui/game-frame app-state)
           (. js/document (getElementById "app")))

(defn on-js-reload [])
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)

