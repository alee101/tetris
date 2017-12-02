(ns tetris.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [tetris.events :as events]
            [tetris.views :as views]
            [tetris.config :as config]))

(def keycode->action
  {32 :hard-drop
   37 :move-left
   38 :rotate-clockwise
   39 :move-right
   40 :move-down
   80 :toggle-pause
   90 :rotate-counter-clockwise})

(defn handle-keydown [e]
  (when-let [action (keycode->action (.-keyCode e))]
    (.preventDefault e)
    (re-frame/dispatch (if (= action :toggle-pause)
                         [::events/toggle-pause]
                         [::events/move action]))))

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
