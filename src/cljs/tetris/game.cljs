(ns tetris.game)

(def board-height 20)
(def board-width 10)

(def ^:private empty-board (vec (repeat board-height (vec (repeat board-width 0)))))

(def ^:private pieces
  [[[1 1]
    [1 1]]
   [[0 1 0]
    [1 1 1]]
   [[0 1 1]
    [1 1 0]]
   [[1 1 0]
    [0 1 1]]
   [[1 0 0]
    [1 1 1]]
   [[0 0 1]
    [1 1 1]]
   [[1 1 1 1]]])

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
  (->> (add-piece-to-board game)
       clear-completed-rows
       (assoc (spawn-piece) :board)))

(defn new-game []
  (merge {:board empty-board :cur-status :in-progress} (spawn-piece)))

(defn move-left [game]
  (update game :col-offset dec))

(defn move-right [game]
  (update game :col-offset inc))

(defn move-down [game]
  (update game :row-offset inc))

(defn rotate-clockwise [game]
  (update game :piece (comp transpose flip)))

(defn rotate-counter-clockwise [game]
  (update game :piece (comp flip transpose)))

(defn hard-drop [game]
  (-> (take-while valid-game? (iterate move-down game))
      last
      lock-piece))

(defn gravitate [game]
  (let [new-game-state (move-down game)]
    (if (valid-game? new-game-state)
      new-game-state
      (lock-piece game))))

(defn cur-board [game]
  (add-piece-to-board game))
