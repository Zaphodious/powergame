(ns powergame.knowledge
  (:require [com.rpl.specter :as sp]))

(def knowledge-tree
  [::knowledge
   [::planar
    [::elemental
        ::raw
        ::constructive
        ::disasterous]
    [::celestial
        ::light
        ::vengeance]
    [::demonic
        ::power]]
   [::terrestrial
    [::arborial]
    [::maritime]]
   [::elder]])


(declare recpath) ;otherwise the system freaks out. Used as a symbol in a macro call, so it shouldn't, but whatever.

(defn flatten-tree [thing]
  (->> thing
       (sp/select [(sp/recursive-path
                     [] recpath
                      (sp/if-path
                        vector?
                        [(sp/collect-one sp/FIRST) sp/ALL recpath]
                        sp/STAY))])
       (map (fn [a]
              (if (= (count a) (count (set a)))
                a
                (vec (drop-last a)))))))

(defn dual-split-path [thing]
  (reduce
    (fn [c i]
      (conj c [(get thing i) (get thing (inc i))]))
    []
    (range (dec (count thing)))))

(defn derive-tree [tree-thing]
  (->> tree-thing
       flatten-tree
       (map dual-split-path)
       (reduce into)
       set
       (map reverse)
       (map (partial apply derive))))

(derive-tree knowledge-tree)