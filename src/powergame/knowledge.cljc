(ns powergame.knowledge
  (:require [com.rpl.specter :as sp]))

(def knowledge-tree
  [::totality
   [::planar
    [::elemental
     [::raw]
     [::constructor]
     [::disastrous]]
    [::celestial
     [::light]
     [::vengeance]]
    [::demonic
     [::greed]
     [::power]]]
   [::terrestrial
    [::arcane]
    [::arborial]
    [::maritime]]
   [::elder]])

(defn make-knowledge-track-map
  ([] (make-knowledge-track-map {}))
  ([m] (->> knowledge-tree
         (sp/select [(sp/walker keyword?)])
         (map (fn [a] [a 0]))
         (reduce #(apply (partial assoc %1) %2) {})
         ((fn [a] (merge a m))))))

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

(defn make-knowledge-path-map []
  (->> knowledge-tree
       flatten-tree
       (map (fn [a] [(last a) a]))
       (into {})))

(defn sort-knowledge-track-map [track-map]
  (->> knowledge-tree
       (sp/select [(sp/walker keyword?)])
       (map (fn [a] [a (get track-map a)]))))

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

(into []
  (derive-tree knowledge-tree))

(defn get-derived-knowledge [m]
  (let [am (make-knowledge-track-map m)]
    (->> am
      (map (fn [[k a]]
             [k
              (filter (fn [[k2 a2]]
                        (isa? k2 k))
                      am)]))
      (map (fn [[k a]]
             [k (sp/select [sp/ALL sp/LAST] a)]))
      (map (fn [[k a]]
             [k (reduce + a)]))
      (reduce (fn [rm [k a]] (assoc rm k a)) {}))))