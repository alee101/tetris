(ns tetris.subs
  (:require [re-frame.core :as re-frame]
            [tetris.game :as game]))

(re-frame/reg-sub
 ::cur-board
 (fn [db]
   (game/cur-board db)))

(re-frame/reg-sub
 ::game-status
 (fn [db]
   (game/game-status db)))
