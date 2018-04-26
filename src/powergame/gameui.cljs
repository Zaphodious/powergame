(ns powergame.gameui
  (:require [rum.core :as rum]
            [powergame.bizlogic :as gc]))

(rum/defc board-area [{:keys [piece power status y x selected input-fn]}]
  [:td.board-area
   [:button {:class (str
                      (if selected "selected" "quiet")
                      " "
                      (name status))
             :type "button"
             :onClick #(input-fn {:type :selected :y y :x x :value (not selected)})}
    (str power)]]) ;"(" x "," y ")")]])

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
  (let [{:keys [board juice money knowhow input-fn] :as app-state} (rum/react app-state-atom)]
    [:#frame
     [:#menubar-top [:ul#infobar
                     [:li.juice (str "Juice "juice)]
                     [:li.money (str "Money "money)]
                     [:li.knowhow (str "Know How "knowhow)]]
                [:ul#buttonbar
                 [:li [:button {:type "button"
                                :onClick #(input-fn {:type :deselect-all})}
                       "Deselect All"]]
                 [:li [:button {:type "button"}
                       "Info On Selected"]]
                 [:li [:button {:type "button"}
                       "History"]]]]
     [:#board (board-component app-state)]
     ;[:#state-print (pr-str app-state)]
     [:#menubar-bottom]]))
