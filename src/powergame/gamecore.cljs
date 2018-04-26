(ns powergame.gamecore)

(defn make-game-board [{:keys [width height]}]
  (->> width
       range
       (map (fn [a] (->> height
                         range
                         (map (fn [b] [nil]))
                         vec)))
       vec))
