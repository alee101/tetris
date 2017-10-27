(ns tetris.views
  (:require [re-frame.core :as re-frame]
            [tetris.subs :as subs]))

(defn tetris-app []
  (let [cur-board @(re-frame/subscribe [::subs/cur-board])]
    [:div
     [:pre (clojure.string/join "\n" (map str cur-board))]]))
