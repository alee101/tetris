(ns tetris.db
  (:require [tetris.game :as game]))

(def default-db
  (game/new-game))
