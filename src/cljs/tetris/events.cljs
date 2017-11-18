(ns tetris.events
  (:require [re-frame.core :as re-frame]
            [tetris.db :as db]
            [tetris.game :as game]))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(re-frame/reg-event-db
 ::move
 (fn [db [_ action]]
   (game/move-if-valid db action)))

(re-frame/reg-event-db
 ::gravitate
 (fn [db _]
   (game/gravitate db)))
