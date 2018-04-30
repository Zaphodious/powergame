(ns powergame.board-defs
  (:require [com.rpl.specter :as sp]))

(def unit-ops [:info :rotate :upgrade :sell])
(def units
  {:empty {:name    "empty"
           :type    "empty"
           :operations [:info :purchase]
           :sprites {:full     ""
                     :contains ""
                     :empty    ""}}
   :fountain {:name    "fountain"
              :type    "fixture"
              :upgrades [:collection :transmission :aoe]
              :operations unit-ops
              :sprites {:full     "img/crawl-tiles/dc-dngn/dngn_blood_fountain.png"
                        :contains "img/crawl-tiles/dc-dngn/dngn_sparkling_fountain.png"
                        :empty    "img/crawl-tiles/dc-dngn/dngn_dry_fountain.png"}}})

(defn no-op [n]
  n)
(def operations {:purchase {:name "purchase"
                            :description "Add A Thing"
                            :multi? true
                            :single? true
                            :on-activate #(assoc % :modal-showing :purchase)
                            :image "img/crawl-tiles/item/misc/gold_pile.png"}
                 :swap {:name "swap"
                        :description "Move Things"
                        :multi? true
                        :single? false
                        :on-activate no-op
                        :image "img/crawl-tiles/spells/translocation/blink.png"}
                 :info {:name "info"
                        :multi? true
                        :single? true
                        :on-activate no-op
                        :description "What is it?"
                        :image "img/crawl-tiles/item/scroll/blank_paper.png"}
                 :rotate {:name "rotate"
                          :multi? true
                          :single? true
                          :on-activate no-op
                          :image "img/crawl-tiles/spells/enchantment/confusing_touch.png"}
                 :upgrade {:name "upgrade"
                           :multi? false
                           :single? true
                           :on-activate no-op
                           :description "Make It Better"
                           :image "img/crawl-tiles/spells/memorise.png"}
                 :sell {:name "sell"
                        :multi? true
                        :single? true
                        :on-activate no-op
                        :description "Get Rid Of It"
                        :image "img/crawl-tiles/spells/translocation/banishment.png"}})

(def modals {:purchase {:name "purchase"}})