(ns powergame.core
  (:require [rum.core :as rum]
            [powergame.bizlogic :as gc]
            [powergame.gameui :as gui]
            [clojure.core.async :as async]
            [powergame.board-defs :as board-defs]
            [powergame.unit-actions :as unit-actions]
            [oops.core :refer [oget oset! ocall oapply ocall! oapply!
                               oget+ oset!+ ocall+ oapply+ ocall!+ oapply!+]]))

(enable-console-print!)

(println "This text is printed from src/powergame/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(def tick-speed (* 1000 board-defs/game-step-time-in-seconds))
(defonce
  input-buffer (async/chan 10))
(defonce logic-done-buffer (async/chan 50))
(defn input-this! [{:keys [type y x value] :as input-event}]
  (async/put! input-buffer input-event))
(defonce
  last-render (atom (gc/init-game-state {:height 4 :width 4 :input-fn input-this!})))

(def background-level (atom (gc/init-game-state {:height 100 :width 100 :input-fn input-this!})))

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


  (let [interval-id
        (js/setInterval
          (fn []
            ;(println (pr-str background-level))
            (async/go
                (reset! background-level (gc/make-tick @background-level)))
            (async/go
              (async/put! logic-done-buffer (gc/make-tick @last-render))))
          tick-speed)]))


(rum/defc hello-world []
  [:div
   [:h1 (:text @last-render)]
   [:h3 "Edit this and watch it change!"]])

(defonce started (setup-game-loop))

(rum/mount (gui/game-frame last-render)
           (. js/document (getElementById "app")))

(ocall js/window [:dragscroll :reset])

(defn on-js-reload [])
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)

