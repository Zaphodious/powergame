(ns powergame.board-defs
  (:require [com.rpl.specter :as sp]))

(def units
  {:empty {:name    "empty"
           :type    "empty"
           :sprites {:full     ""
                     :contains ""
                     :empty    ""}}
   :fountain {:name    "fountain"
              :type    "fixture"
              :sprites {:full     "img/crawl-tiles/dc-dngn/dngn_blood_fountain.png"
                        :contains "img/crawl-tiles/dc-dngn/dngn_sparkling_fountain.png"
                        :empty    "img/crawl-tiles/dc-dngn/dngn_dry_fountain.png"}}})