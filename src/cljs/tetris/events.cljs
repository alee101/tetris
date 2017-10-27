(ns tetris.events
  (:require [re-frame.core :as re-frame]
            [tetris.db :as db]
            [tetris.game :as game]))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(re-frame/reg-event-db
 ::move-down
 (fn [db _]
   (game/move-down db)))

(re-frame/reg-event-db
 ::move-left
 (fn [db _]
   (game/move-left db)))

(re-frame/reg-event-db
 ::move-right
 (fn [db _]
   (game/move-right db)))

(re-frame/reg-event-db
 ::hard-drop
 (fn [db _]
   (game/hard-drop db)))

(re-frame/reg-event-db
 ::rotate-clockwise
 (fn [db _]
   (game/rotate-clockwise db)))

(re-frame/reg-event-db
 ::rotate-counter-clockwise
 (fn [db _]
   (game/rotate-counter-clockwise db)))

(re-frame/reg-event-db
 ::gravitate
 (fn [db _]
   (game/gravitate db)))
