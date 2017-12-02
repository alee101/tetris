(ns tetris.events
  (:require [cljs.spec.alpha :as s]
            [re-frame.core :as re-frame]
            [tetris.db :as db]
            [tetris.game :as game]))

(def check-spec-interceptor
  (re-frame/after
   (fn [db _]
     (when-not (s/valid? ::db/db db)
       (throw (ex-info (str "spec check failed: " (s/explain-str ::db/db db)) {}))))))

(re-frame/reg-event-db
 ::initialize-db
 check-spec-interceptor
 (fn [_ _]
   db/default-db))

(re-frame/reg-event-db
 ::new-game
 check-spec-interceptor
 (fn [_ _]
   (game/new-game)))

(re-frame/reg-event-db
 ::move
 check-spec-interceptor
 (fn [db [_ action]]
   (game/move-if-valid db action)))

(re-frame/reg-event-db
 ::gravitate
 check-spec-interceptor
 (fn [db _]
   (game/gravitate db)))

(re-frame/reg-event-db
 ::toggle-pause
 check-spec-interceptor
 (fn [db _]
   (game/toggle-pause db)))
