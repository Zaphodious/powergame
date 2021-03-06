(ns powergame.board-defs
  (:require [com.rpl.specter :as sp]
            [powergame.knowledge :as pk]))

(def unit-ops-no-upgrade [:info :upgrade :sell])
(def unit-ops (conj unit-ops-no-upgrade :upgrade))
(def units
  {:empty             {:name                 "empty"
                       :type                 :empty
                       :knowledge            nil
                       :description          "Empty space in your dungeon, full of possibility and waiting to be used."
                       :purchasable?         false
                       :operations           [:info :purchase]
                       :traversable          true
                       :conserve-on-traverse false
                       :cost                 {:juice 0
                                              :money 0}
                       :sells-for            {:juice 0
                                              :money 0}
                       :sprite               "img/empty.png"}
   :elf               {:name          "deep-elf"
                       :description   "Subjugated, abused, and oppressed by their demonic spider priestesses, these natural magic users have escaped from servitude and certain death in order to earn a wage from you. As they are, put them in the path of magic and they'll pass it forward... provided that the land below them grants them enough strength to do so. Once you've trained them, they'll magnify it, and at their apex they'll no longer need an external source. If you get rid of them before training them, however, their doom is sealed as they'll be snatched up by agents of the spider priestesses."
                       :type          :creature
                       :knowledge     ::pk/terrestrial
                       :purchasable?  true
                       :cost          {:juice 0
                                       :money 10
                                       :knowledge-held {::pk/terrestrial 1}}
                       :sells-for     {:juice 0
                                       :money 6}
                       :upgrade-paths [:elf-mage]
                       :operations    (conj unit-ops :move)
                       :sprite        "img/crawl-tiles/monster/deep_elf_conjurer.png"}
   :elf-mage          {:name          "elf-mage"
                       :description   "Uplifted and enlightened, empowered and liberated, these elves have come a long way since they came into your service. They've gained the ability to draw a little bit of power from the world, and use it to amplify power already passing through them. Don't feel bad about dismissing them from your service - when they leave, they'll go into the world a force all their own."
                       :type          :creature
                       :knowledge     ::pk/sylvan
                       :purchasable?  false
                       :cost          {:juice 50
                                       :money 120
                                       :knowledge-held {::pk/sylvan 1}}
                       :sells-for     {:juice 45
                                       :money 60}
                       :upgrade-paths [:high-elf]
                       :operations    (conj unit-ops :move)
                       :sprite        "img/crawl-tiles/monster/deep_elf_annihilator.png"}
   :high-elf          {:name          "high-elf"
                       :description   "An elf at the apex of his power. These masters of magic can fully utilize the power below their feet, passing it forward with or without a bolt to add to. They are no longer your servants, master. They are your allies, won through your service to them. As such, they'll teach you what more they learn."
                       :type          :creature
                       :knowledge     ::pk/fair
                       :purchasable?  false
                       :upgrade-paths nil
                       :operations    (conj unit-ops-no-upgrade :move)
                       :cost          {:juice 1000
                                       :money 750
                                       :knowledge-held {::pk/fair 1}}
                       :sells-for     {:juice 1100
                                       :money 850}
                       :sprite        "img/crawl-tiles/monster/deep_elf_sorcerer.png"}
   :devil             {:name          "devil"
                       :type          :creature
                       :knowledge     ::pk/demonic
                       :description   "Untrustworthy. Vexing. Litigious. They're only pliant because their Hellish liege is hungry for your magic. Unfortunately for you, the first ones sent up are weak, barely capable of flying let alone trans-dimensional power transfer. Should their liege receive an impressive enough gift, he'll send a more capable (and more efficient) collector to replace them."
                       :purchasable?  true
                       :cost          {:juice 1
                                       :money 0
                                       :knowledge-held {::pk/planar 1}}
                       :sells-for     {:juice 1
                                       :money 0}
                       :upgrade-paths [:corpulent-devil]
                       :operations    (conj unit-ops :move)
                       :sprite        "img/crawl-tiles/monster/demons/blue_devil_new.png"}
   :corpulent-devil   {:name          "corpulent-devil"
                       :type          :creature
                       :knowledge     ::pk/demonic
                       :description   "A devil grown slovenly and fat. Decidedly older then the usual devil, and therefore smarter- which is usually a very dangerous thing. Not for you, not now. Your contract with their liege still holds. These fiends are far more capable then their younger brothers, and can transmit more power back home and get a much better price per point of energy sent."
                       :purchasable?  false
                       :cost          {:juice 50
                                       :money 0
                                       :knowledge-held {::pk/demonic 1}}
                       :sells-for     {:juice 5
                                       :money 0}
                       :upgrade-paths [:gold-devil]
                       :operations    (conj unit-ops :move)
                       :sprite        "img/crawl-tiles/monster/demons/blue_death.png"}
   :gold-devil        {:name          "gold-devil"
                       :type          :creature
                       :knowledge     ::pk/greed
                       :description   "The devil lord's most senior collectors. These evil minions have handled so much gold that their essence is now partially made of the stuff. They are ancient, wise, and might grant you hellish secrets if they stay in your dungeon for long enough."
                       :purchasable?  false
                       :cost          {:juice 1000
                                       :money 0
                                       :knowledge-held {::pk/greed 1}}
                       :sells-for     {:juice 500
                                       :money 0}
                       :upgrade-paths nil
                       :operations    (conj unit-ops-no-upgrade :move)
                       :sprite        "img/crawl-tiles/monster/demons/cacodemon.png"}
   :fountain          {:name          "fountain"
                       :type          :fixture
                       :knowledge     ::pk/terrestrial
                       :description   "Eons in the past, the ancient wizards of Thraum infused the world with magic. While they only intended to make their own workings easier, their power was immense, and the results of their efforts persist to this day! We are not as powerful, so to use this magic we must build special structures to condense and concentrate it. When the fountain is full, it overflows, spilling magic onto the ground next to it. Special rituals exist which can enhance these fountains, and perhaps through them we'll get to where the ancient wizards left off..."
                       :purchasable?  true
                       :cost          {:juice 1
                                       :money 1
                                       :knowledge-held {::pk/totality 1}}
                       :sells-for     {:juice 1
                                       :money 0}
                       :upgrade-paths [:crystal-fountain]
                       :operations    (conj unit-ops :rotate)
                       :sprite        "img/crawl-tiles/dungeon/dry_fountain.png"}
   :crystal-fountain  {:name          "crystal-fountain"
                       :type          :fixture
                       :knowledge     ::pk/terrestrial
                       :description   "A fountain deconstructed and remade with bricks bathed in the blood of magic salamanders. The benefit? It allows the fountain to pull in magic from the world around it, instead of just the ground it sits upon."
                       :purchasable?  false
                       :cost          {:juice 500
                                       :money 300}
                       :sells-for     {:juice 500
                                       :money 0}
                       :upgrade-paths [:blood-fountain]
                       :operations    (conj unit-ops :rotate)
                       :sprite        "img/crawl-tiles/dungeon/sparkling_fountain.png"}
   :blood-fountain    {:name          "blood-fountain"
                       :type          :fixture
                       :knowledge     ::pk/disasterous
                       :description   "You know not how this fountain is constructed- only the orcs have access to this knowledge. What you do know is that this doesn't use the magic given to the world by the ancient wizards. Rather, it pulls life essence from the world, defiling it and rendering it forever incapable of hosting magic (or most living creatures). This fountain will *never* run dry, for the blood of the world is infinite."
                       :purchasable?  false
                       :cost          {:juice 1000
                                       :money 1000}
                       :sells-for     {:juice 1000
                                       :money 0}
                       :upgrade-paths nil
                       :operations    (conj unit-ops-no-upgrade :rotate)
                       :sprite        "img/crawl-tiles/dungeon/blood_fountain.png"}
   :altar             {:name          "old-altar"
                       :type          :altar
                       :knowledge     ::pk/totality
                       :description   "Strange forces govern the world, compelling and coercing it as their whims dictate. Stranger still is their relationship with us mortals. Structures of stone and precious metals, shaped as they decree, allow us to bargain with them for reasons we cannot fathom. Take this one, for example- blasting it with the world's magic imbues you with a small amount of eldrich power directly! You might never know the name of the being giving you this power... but you're thankful all the same, no?"
                       :purchasable?  true
                       :cost          {:juice 10
                                       :money 100
                                       :knowledge-held {::pk/totality 1}}
                       :sells-for     {:juice 1
                                       :money 75}
                       :upgrade-paths nil
                       :operations    (conj unit-ops :rotate)
                       :sprite        "img/crawl-tiles/dungeon/altars/altar_old.png"}
   :human             {:name          "human"
                       :type          :creature
                       :knowledge     ::pk/totality
                       :description   "Weak, short-lived, pathetic. Humanity is little more then a blight upon the world... according to the fair races. Yet you know differently. Humanity was created with an endless hunger for knowledge and power that only the gods themselves can fulfill. Let them near secrets hidden by the ancients, and they'll be secrets no longer. Pay them in power, and they'll tell you all they know."
                       :purchasable?  true
                       :cost          {:juice 1
                                       :money 1
                                       :knowledge-held {::pk/totality 1}}
                       :sells-for     {:juice 0
                                       :money 1}
                       :upgrade-paths []
                       :operations    (conj unit-ops :move)
                       :sprite        "img/crawl-tiles/monster/human_new.png"}
   :enlightened-human {:name          "enlightened-human"
                       :description   "The soul of man is voracious for wisdom. It learns like a lion stalking prey, and it devours its knowledge just as quickly. How tragic, then, that most learned men will never know what lies beyond perception. How mournful that their lives are spent scratching the bare surface of what there is to know? How fortunate, then, that one of your loyal servants has learned how to peak behind reality's veil. How wonderful that he comes to you, shining like a star with the brilliance of his discovery, seeking guidance. How blessed you are, to be able to shape his life after enlightenment."
                       :type          :creature
                       :knowledge     ::pk/totality
                       :purchasable?  false
                       :sells-for     {:juice 0
                                       :money 0}
                       :upgrade-paths [:initiated :forsaken :naturist]
                       :operations    (conj unit-ops :move)
                       :sprite        "img/crawl-tiles/monster/human_monk_ghost.png"}
   :initiated {:name "initiated"
               :description "Here stands a human who has seen beyond this reality. Next to us, all around us, and yet an unknowable distance away from us are infinite other realities. Vast wonders and immense tragedy lie in wait of discovery! This man wishes to find them all, learn them all, and bring them all back home."
               :type :creature
               :knowledge ::pk/planar
               :purchasable? false
               :cost          {:juice 260
                               :money 40
                               :knowledge-held {::pk/totality 1}}
               :sells-for {:juice 10
                           :money 1}
               :upgrade-paths []
               :operations (conj unit-ops :move)
               :sprite "img/crawl-tiles/monster/wizard.png"}
   :forsaken {:name "Forsaken"
              :description "He who gives himself up for the good of his fellows is a hero true and noble. The path before this man is a dark one. Your enemy is terrible, depraved, and merciless. None among the good understand them. In order to claim victory, that has to change... which is why this man does what he does. In his peek beyond the vail, he has seen the horrible false truths that drive your enemies. He has seen the dangers that lurk in the darkness. He is capable of understanding them, and in doing so knowing what will win the war. In the process, though, he will lose himself. He is frightened, but willing to make the sacrifice."
              :type :creature
              :knowledge ::pk/inemigo
              :purchasable? false
              :cost          {:juice 80
                              :money 80
                              :knowledge-held {::pk/totality 200}}
              :sells-for {:juice 80
                          :money 40}
              :upgrade-paths []
              :operations (conj unit-ops :move)
              :sprite "img/crawl-tiles/monster/necromancer_new.png"}
   :naturist {:name "Naturist"
              :description "The world is an immense place, full of wonder and delight. It is grand beyond our grandest visions, and ancient beyond our oldest memories. In his view behind the veil this man has seen the totality of the future, and has realized that our world will endure whatever catastrophe causes us to perish. He saw how the forests will regrow, and how the seas will continue to swirl, and how whoever comes after us will struggle and triumph just as we did."
              :type :creature
              :knowledge ::pk/terrestrial
              :purchasable? false
              :cost          {:juice 80
                              :money 80
                              :knowledge-held {::pk/totality 30}}
              :sells-for {:juice 80
                          :money 40}
              :upgrade-paths []
              :operations (conj unit-ops :move)
              :sprite "img/crawl-tiles/monster/human_slave.png"}})




(def history ["The ancient gods who created reality made many beings to inhabit the world, and to give it meaning. All were narrow in focus, supposedly confined to their own niche and designed to work together to administer the world for the gods. Unfortunately, the gods were naive and short-sighted; The Dark Lord corrupted the hearts and minds of the fair races, and they looked upon each other with scorn and envy. For untold ages, continuous war raged. The Shining One, he who had created the bedrock of reality on which all else was placed, decided that a world of bloodshed did not please him. Thus, he created many lesser gods to manage the world, powerful enough that the Dark Lord could not corrupt them but weak enough that they could not themselves become corrupters. To serve these gods he created humanity. An individual human, unaided, has little strength or power. They are, however, inset with a small spark of divinity, and should a human choose a god they can be transformed into something incredible. These transformations can be subtle, like being given divine martial prowess. They can also be fantastical. Angels, centaurs, murfolk, and many more fantastic man-like creatures were once individual humans who gave themselves completely to the power that then changed them."])

(def travelers {:dart {:name "dart"
                       :type :juice
                       :sprite "img/magic_dart_anim.png"
                       :energy 10000
                       :lifetime 10000
                       :speed 0.6}
                :splash {:name "splash"
                         :type :juice
                         :sprite "img/crawl-tiles/effect/cloud_spectral_0.png"
                         :lifetime 10
                         :energy 1
                         :speed 1}
                :altar-cloud {:name "altarcloud"
                              :type :juice
                              :sprite "img/cloud_magic_anim.png"
                              :lifetime 1
                              :energy 0
                              :speed 0}
                :ready-to-learn {:name "ready-to-learn"
                                 :type :empty
                                 :sprite "img/learn-ready.png"
                                 :value 0
                                 :lifetime 1
                                 :energy 0
                                 :speed 0}
                :learning {:name "learning"
                           :type :empty
                           :sprite "img/learning.png"
                           :lifetime 1
                           :energy 0
                           :speed 0}
                :devilball {:name "devilball"
                            :type :juice
                            :sprite "img/cloud_fire_anim.png"
                            :lifetime 1
                            :energy 0
                            :speed 0}})

(def operations {:purchase {:name "purchase"
                            :description "Add A Thing"
                            :multi? true
                            :single? true
                            :image "img/crawl-tiles/item/gold/gold_pile.png"}
                 :move {:name "move"
                        :description "Move In Direction"
                        :multi? false
                        :single? true
                        :image "img/crawl-tiles/gui/startup/dungeon_sprint.png"}
                 :info {:name "info"
                        :multi? true
                        :single? true
                        :description "What is it?"
                        :image "img/crawl-tiles/gui/startup/instructions.png"}
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

(def game-step-time-in-seconds 1)