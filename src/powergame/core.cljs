(ns powergame.core
  (:require [rum.core :as rum]
            [powergame.bizlogic :as gc]
            [powergame.gameui :as gui]
            [clojure.core.async :as async]))

(enable-console-print!)

(println "This text is printed from src/powergame/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(def tick-speed 500)
(defonce input-buffer (async/chan 10))
(defonce logic-done-buffer (async/chan 1))
(defonce tick-chan (async/chan 1))
(defn input-this! [{:keys [type y x value] :as input-event}]
  (async/put! input-buffer input-event))
(defonce last-render (atom (gc/init-game-state {:height 12 :width 12 :input-fn input-this!})))

(defn setup-game-loop []
  (async/go-loop []
                 (println "this is... started?")
                 (let [input-event (async/<! input-buffer)]
                     (async/>! logic-done-buffer
                               (gc/process-input (assoc @last-render :next-input input-event)))
                     (recur)))

  (async/go-loop []
                 (let [new-state (async/<! logic-done-buffer)]
                   (reset! last-render new-state)
                   (recur)))



  (js/setInterval
    (fn []
      (async/put! logic-done-buffer (gc/advance-cursor @last-render)))
    tick-speed))

(rum/defc hello-world []
  [:div
   [:h1 (:text @last-render)]
   [:h3 "Edit this and watch it change!"]])

(defonce started (setup-game-loop))

(rum/mount (gui/game-frame last-render)
           (. js/document (getElementById "app")))

(defn on-js-reload [])
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)

