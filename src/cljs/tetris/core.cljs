(ns tetris.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [tetris.events :as events]
            [tetris.views :as views]
            [tetris.config :as config]))

(def keycode->event
  {32 [::events/hard-drop]
   37 [::events/move-left]
   38 [::events/rotate-clockwise]
   39 [::events/move-right]
   40 [::events/move-down]
   90 [::events/rotate-counter-clockwise]})

(defn handle-keydown [e]
  (when-let [event (keycode->event (.-keyCode e))]
    (.preventDefault e)
    (re-frame/dispatch event)))

(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/tetris-app]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root)
  (.addEventListener js/document "keydown" handle-keydown)
  (js/setInterval #(re-frame/dispatch [::events/gravitate]) 500))
