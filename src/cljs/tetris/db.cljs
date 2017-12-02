(ns tetris.db
  (:require [cljs.spec.alpha :as s]
            [tetris.game :as game]))

(s/def ::cell (set (range 8)))
(s/def ::piece (s/coll-of (s/coll-of ::cell)))
(s/def ::row (s/coll-of ::cell :count game/board-width))
(s/def ::board (s/coll-of ::row :count game/board-height))

(s/def ::cur-status #{:in-progress :paused :game-over})

(s/def ::col-offset (s/and #(>= % 0) #(< % game/board-width)))
(s/def ::row-offset (s/and #(>= % 0) #(< % game/board-height)))

(s/def ::db (s/keys :req-un [::board ::piece ::cur-status ::row-offset ::col-offset]))

(def default-db
  (game/new-game))
