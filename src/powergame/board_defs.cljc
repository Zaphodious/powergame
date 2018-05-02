(ns powergame.board-defs
  (:require [com.rpl.specter :as sp]))

(def unit-ops-no-upgrade [:info :rotate :upgrade :sell])
(def unit-ops (conj unit-ops-no-upgrade :upgrade))
(def units
  {:empty {:name    "empty"
           :type    :empty
           :description "Empty space in your dungeon, full of possibility and waiting to be used."
           :purchasable? false
           :operations [:info :purchase]
           :cost {:juice 0
                  :money 0
                  :knowhow 0}
           :sells-for {:juice 0
                       :money 0
                       :knowhow 0}
           :sprite "img/crawl-tiles/misc/halo.png"}
   :elf {:name "deep-elf"
         :description "Subjugated, abused, and oppressed by their demonic spider priestesses, these natural magic users have escaped from servitude and certain death in order to earn a wage from you. As they are, put them near to or in the path of magic and they'll pass it forward. Once you've trained them, they'll magnify it, and at their apex they'll no longer need an external source. If you get rid of them before training them, however, their doom is sealed as they'll be snatched up by agents of the spider priestesses."
         :type :creature
         :purchasable? true
         :cost {:juice 0
                :money 1
                :knowhow 1}
         :sells-for {:juice 0
                     :money 1
                     :knowhow 0}
         :upgrade-paths [:high-elf]
         :operations unit-ops
         :sprite "img/crawl-tiles/monster/deep_elf_conjurer.png"}
   :elf-mage {:name "elf-mage"
              :description "Uplifted and enlightened, empowered and liberated, these elves have come a long way since they came into your service. They've gained the ability to draw a little bit of power from the world, and use it to amplify power already passing through them. Don't feel bad about dismissing them from your service - when they leave, they'll go into the world a force all their own."
              :type :creature
              :purchasable? false
              :cost {:juice 3
                     :money 6
                     :knowhow 10}
              :sells-for {:juice 1
                          :money 4
                          :knowhow 0}
              :upgrade-paths [:high-elf]
              :operations unit-ops
              :sprite "img/crawl-tiles/monster/deep_elf_annihilator.png"}
   :high-elf {:name "high-elf"
              :description "An elf at the apex of his power. These masters of magic can fully utilize the power below their feet, passing it forward with or without a bolt to add to. They are no longer your servants, master. They are your allies, won through your service to them. As such, they'll teach you what more they learn."
              :type :creature
              :purchasable? false
              :upgrade-paths nil
              :operations unit-ops-no-upgrade
              :sprite "img/crawl-tiles/monster/deep_elf_conjurer.png"}
   :devil {:name "devil"
           :type :creature
           :description "Untrustworthy. Vexing. Litigious. They're only pliant because their Hellish liege is hungry for your magic. Unfortunately for you, the first ones sent up are weak, barely capable of flying let alone trans-dimensional power transfer. Should their liege receive an impressive enough gift, he'll send a more capable (and more efficient) collector to replace them."
           :purchasable? true
           :cost {:juice 1
                  :money 0
                  :knowhow 1}
           :sells-for {:juice 1
                       :money 0
                       :knowhow 0}
           :upgrade-paths [:corpulent-devil]
           :operations unit-ops
           :sprite "img/crawl-tiles/monster/demons/blue_devil_new.png"}
   :corpulent-devil {:name "corpulent-devil"
                     :type :creature
                     :description "A devil grown slovenly and fat. Decidedly older then the usual devil, and therefore smarter- which is usually a very dangerous thing. Not for you, not now. Your contract with their liege still holds. These fiends are far more capable then their younger brothers, and can transmit more power back home and get a much better price per point of energy sent."
                     :purchasable? false
                     :cost {:juice 50
                            :money 0
                            :knowhow 50}
                     :sells-for {:juice 5
                                 :money 0
                                 :knowhow 0}
                     :upgrade-paths [:gold-devil]
                     :operations unit-ops
                     :sprite "img/crawl-tiles/monster/demons/blue_death.png"}
   :gold-devil {:name "gold-devil"
                :type :creature
                :description "The devil lord's most senior collectors. These evil minions have handled so much gold that their essence is now partially made of the stuff. They are ancient, wise, and might grant you hellish secrets if they stay in your dungeon for long enough."
                :purchasable? false
                :cost {:juice 1000
                       :money 0
                       :knowhow 200}
                :sells-for {:juice 500
                            :money 0
                            :knowhow 0}
                :upgrade-paths nil
                :operations unit-ops-no-upgrade
                :sprite "img/crawl-tiles/monster/demons/cacodemon.png"}
   :fountain {:name    "fountain"
              :type    :fixture
              :description "Eons in the past, the ancient wizards of Thraum infused the world with magic. While they only intended to make their own workings easier, their power was immense, and the results of their efforts persist to this day! We are not as powerful, so to use this magic we must build special structures to condense and concentrate it. Special rituals exist which can enhance these fountains, and perhaps through them we'll get to where the ancient wizards left off..."
              :purchasable? true
              :cost {:juice 1
                     :money 1
                     :knowhow 1}
              :sells-for {:juice 1
                          :money 0
                          :knowhow 0}
              :upgrade-paths [:crystal-fountain]
              :operations unit-ops
              :sprite "img/crawl-tiles/dungeon/dry_fountain.png"}
   :crystal-fountain {:name "crystal-fountain"
                      :type :fixture
                      :description "A fountain deconstructed and remade with bricks bathed in the blood of magic salamanders. The benefit? It allows the fountain to pull in magic from the world around it, instead of just the ground it sits upon."
                      :purchasable? false
                      :cost {:juice 500
                             :money 300
                             :knowhow 1000}
                      :sells-for {:juice 500
                                  :money 0
                                  :knowhow 0}
                      :upgrade-paths [:blood-fountain]
                      :operations unit-ops
                      :sprite "img/crawl-tiles/dungeon/sparkling_fountain.png"}
   :blood-fountain {:name "blood-fountain"
                    :type :fixture
                    :description "You know not how this fountain is constructed- only the orcs have access to this knowledge. What you do know is that this doesn't use the magic given to the world by the ancient wizards. Rather, it pulls life essence from the world, defiling it and rendering it forever incapable of hosting magic (or most living creatures). This fountain will *never* run dry, for the blood of the world is infinite."
                    :purchasable? false
                    :cost {:juice 1000
                           :money 1000
                           :knowhow 2500}
                    :sells-for {:juice 1000
                                :money 0
                                :knowhow 0}
                    :upgrade-paths nil
                    :operations unit-ops-no-upgrade
                    :sprite "img/crawl-tiles/dungeon/blood_fountain.png"}})

(def operations {:purchase {:name "purchase"
                            :description "Add A Thing"
                            :multi? true
                            :single? true
                            :image "img/crawl-tiles/item/gold/gold_pile.png"}
                 :swap {:name "swap"
                        :description "Move Things"
                        :multi? true
                        :single? false
                        :image "img/crawl-tiles/gui/spells/translocation/blink.png"}
                 :info {:name "info"
                        :multi? true
                        :single? true
                        :description "What is it?"
                        :image "img/crawl-tiles/item/scroll/blank_paper.png"}
                 :rotate {:name "rotate"
                          :multi? true
                          :single? true
                          :description "Turn It Around"
                          :image "img/crawl-tiles/gui/spells/enchantment/confusing_touch_old.png"}
                 :upgrade {:name "upgrade"
                           :multi? false
                           :single? true
                           :description "Make It Better"
                           :image "img/crawl-tiles/gui/spells/memorise.png"}
                 :sell {:name "sell"
                        :multi? true
                        :single? true
                        :description "Get Rid Of It"
                        :image "img/crawl-tiles/gui/spells/translocation/banishment.png"}})

(def modals {:purchase {:name "purchase"}})

(def rotation-order {:up :right
                     :right :down
                     :down :left
                     :left :up})