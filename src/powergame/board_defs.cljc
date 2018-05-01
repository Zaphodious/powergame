(ns powergame.board-defs
  (:require [com.rpl.specter :as sp]))

(def unit-ops [:info :rotate :upgrade :sell])
(def units
  {:empty {:name    "empty"
           :type    "empty"
           :purchasable? false
           :operations [:info :purchase]
           :sprites {:full     ""
                     :contains ""
                     :empty    ""}}
   :elf {:name "deep elf"
         :description "Subjugated, abused, and oppressed by their demonic spider priestesses, these natural magic users have escaped from servitude and certain death in order to earn a wage from you. As they are, put them near to or in the path of magic and they'll pass it forward. Once you've trained them, they'll magnify it, and at their apex they'll no longer need an external source."
         :type :creature
         :purchasable? true
         :cost {:juice 0
                :money 1
                :knowhow 1}
         :upgrades [:transmission]
         :operations unit-ops
         :sprites {:full "img/crawl-tiles/dc-mon/deep_elf_sorcerer.png"
                   :contains "img/crawl-tiles/dc-mon/deep_elf_annihilator.png"
                   :empty "img/crawl-tiles/dc-mon/deep_elf_conjurer.png"}}
   :devil {:name "devil"
           :type :creature
           :description "Untrustworthy. Vexing. Litigious. They're only pliant because their Hellish liege is hungry for your magic. Unfortunately for you, the first ones sent up are weak, barely capable of flying let alone trans-dimensional power transfer. Should their liege receive an impressive enough gift, he'll send a more capable (and more efficient) collector to replace them."
           :purchasable? true
           :cost {:juice 1
                  :money 0
                  :knowhow 1}
           :upgrades [:collection]
           :operations unit-ops
           :sprites {:full "img/crawl-tiles/dc-mon/demons/cacodemon.png"
                     :contains "img/crawl-tiles/dc-mon/demons/blue_death.png"
                     :empty "img/crawl-tiles/dc-mon/demons/blue_devil.png"}}
   :fountain {:name    "fountain"
              :type    :fixture
              :description "Eons in the past, the ancient wizards of Thraum infused the world with magic. While they only intended to make their own workings easier, their power was immense, and the results of their efforts persist to this day! We are not as powerful, so to use this magic we must build special structures to condense and concentrate it. Special rituals exist which can enhance these fountains, and perhaps through them we'll get to where the ancient wizards left off..."
              :purchasable? true
              :cost {:juice 1
                     :money 1
                     :knowhow 1}
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