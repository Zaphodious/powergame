(ns powergame.core
  (:require [rum.core :as rum]
            [powergame.bizlogic :as gc]
            [powergame.gameui :as gui]
            [clojure.core.async :as async]))

(enable-console-print!)

(println "This text is printed from src/powergame/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce input-buffer (async/chan 10))
(defn input-this! [{:keys [type y x value] :as input-event}]
  (async/put! input-buffer input-event))
(defonce last-render (atom (gc/init-game-state {:height 12 :width 12 :input-fn input-this!})))

(async/go-loop []
               (println "this is... started?")
               (let [input-event (async/<! input-buffer)]
                   (swap! last-render (fn [a] (gc/process-input (assoc a :next-input input-event))))
                   (recur)))


(rum/defc hello-world []
  [:div
   [:h1 (:text @last-render)]
   [:h3 "Edit this and watch it change!"]])

(rum/mount (gui/game-frame last-render)
           (. js/document (getElementById "app")))

(defn on-js-reload [])
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)

