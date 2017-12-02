(ns tetris.game)

(def board-height 20)
(def board-width 10)

(def ^:private empty-board (vec (repeat board-height (vec (repeat board-width 0)))))

(def ^:private pieces
  [[[1 1]
    [1 1]]
   [[0 2 0]
    [2 2 2]]
   [[0 3 3]
    [3 3 0]]
   [[4 4 0]
    [0 4 4]]
   [[5 0 0]
    [5 5 5]]
   [[0 0 6]
    [6 6 6]]
   [[7 7 7 7]]])

(def cell->color
  {0 "#000000"
   1 "#FFD700"
   2 "#9400D3"
   3 "#32CD32"
   4 "#FF0000"
   5 "#0000FF"
   6 "#FFA500"
   7 "#00FFFF"})

(def ^:private empty-cell? zero?)

(defn- clear-completed-rows [board]
  (let [completed-row? (fn [row] (not-any? empty-cell? row))]
    (->> board
         (remove completed-row?)
         (concat empty-board)
         (take-last board-height)
         vec)))

(defn- count-rows [piece]
  (count piece))

(defn- count-cols [piece]
  (count (first piece)))

(defn- flip [piece]
  (vec (reverse piece)))

(defn- transpose [piece]
  (apply mapv vector piece))

(defn- translate [[i j] row-offset col-offset]
  [(+ i row-offset) (+ j col-offset)])

(defn- non-empty-coords [piece]
  (for [i (range (count-rows piece))
        j (range (count-cols piece))
        :when (not (empty-cell? (get-in piece [i j])))]
    [i j]))

(defn- add-piece-to-board [{:keys [board piece row-offset col-offset]}]
  (->> (non-empty-coords piece)
       (reduce (fn [board coord]
                 (assoc-in board (translate coord row-offset col-offset) (get-in piece coord)))
               board)))

(defn- valid-game? [{:keys [board piece row-offset col-offset]}]
  (->> (non-empty-coords piece)
       (map (fn [piece-coord] (translate piece-coord row-offset col-offset)))
       (map (fn [board-coord] (get-in board board-coord)))
       (every? empty-cell?)))

(defn- spawn-piece []
  (let [rand-piece (rand-nth pieces)]
    {:piece rand-piece
     :row-offset 0
     :col-offset (quot (- board-width (count-cols rand-piece)) 2)}))

(defn- lock-piece [game]
  (let [update-status
        (fn [game]
          (assoc game :cur-status (if (valid-game? game) :in-progress :game-over)))]
  (->> (add-piece-to-board game)
       clear-completed-rows
       (assoc (spawn-piece) :board)
       update-status)))

(defn- move-left [game]
  (update game :col-offset dec))

(defn- move-right [game]
  (update game :col-offset inc))

(defn- move-down [game]
  (update game :row-offset inc))

(defn- rotate-clockwise [game]
  (update game :piece (comp transpose flip)))

(defn- rotate-counter-clockwise [game]
  (update game :piece (comp flip transpose)))

(defn- hard-drop [game]
  (-> (take-while valid-game? (iterate move-down game))
      last
      lock-piece))

(defn- make-move [game action]
  ((action {:hard-drop hard-drop
            :move-left move-left
            :rotate-clockwise rotate-clockwise
            :move-right move-right
            :move-down move-down
            :rotate-counter-clockwise rotate-counter-clockwise})
   game))

(defn- in-progress? [{:keys [cur-status]}]
  (= cur-status :in-progress))

(defn move-if-valid [game action]
  (let [new-game-state (make-move game action)]
    (if (and (in-progress? game) (valid-game? new-game-state))
      new-game-state
      game)))

(defn gravitate [game]
  (if-not (in-progress? game)
    game
    (let [new-game-state (move-down game)]
      (if (valid-game? new-game-state)
        new-game-state
        (lock-piece game)))))

(defn toggle-pause [{:keys [cur-status] :as game}]
  (assoc game :cur-status (case cur-status
                            :in-progress :paused
                            :paused :in-progress
                            :game-over :game-over)))
(defn new-game []
  (merge {:board empty-board :cur-status :in-progress} (spawn-piece)))

(defn cur-board [game]
  (if (valid-game? game)
    (add-piece-to-board game)
    (:board game)))

(defn game-status [game]
  (:cur-status game))
