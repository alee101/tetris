(ns tetris.views
  (:require [re-frame.core :as re-frame]
            [tetris.game :as game]
            [tetris.events :as events]
            [tetris.subs :as subs]))

(defn cell [row col cell]
  [:rect
   {:height 1.0
    :width 1.0
    :stroke "black"
    :stroke-width 0.05
    :fill (game/cell->color cell)
    :x col
    :y row}])

(defn board []
  (let [cur-board @(re-frame/subscribe [::subs/cur-board])]
    (into
     [:svg
      {:height 400
       :width 200
       :view-box (clojure.string/join " " [0 0 game/board-width game/board-height])}]
     (for [row (range game/board-height)
           col (range game/board-width)]
       [cell row col (get-in cur-board [row col])]))))

(defn game-control []
  (let [game-status @(re-frame/subscribe [::subs/game-status])]
    [:div
     (when (= game-status :game-over)
       [:button {:on-click #(re-frame/dispatch [::events/new-game])} "New Game"])
     (when (= game-status :paused)
       "Game paused. Hit 'p' again to resume.")]))

(defn tetris-app []
  [:div
   [board]
   [game-control]])
