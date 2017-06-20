(ns tictactoe.game
  (:require [schema.core :as s]))

(s/defschema BoardValue (s/enum nil :x :0))
(s/defschema Board [[BoardValue]])
(s/defschema Coord [(s/one s/Int :x) (s/one s/Int :y)])
(s/defschema Winner (s/maybe [(s/one (s/enum :x :y :draw) :p) (s/optional [Coord] :c)]))

(defn board-col
  [board idx]
  (map #(get-in board [% idx])
       (range 0 (count board))))

(defn win-seq?
  [col]
  (and (not (empty? col))
       (nil? (some nil? col))
       (every? (partial = (first col)) col)))

(defn horizontal-win
  [board]
  (reduce-kv
    (fn [acc idx row]
      (if (win-seq? row)
        (reduced (mapv (partial vector idx) (range 0 (count row))))
        acc))
    nil board))

(defn vertical-win
  [board]
  (reduce-kv
    (fn [acc idx _]
      (let [col (board-col board idx)]
        (if (win-seq? col)
          (reduced (mapv #(vector % idx) (range 0 (count board))))
          acc)))
    nil board))

(defn diagonal-win-l
  [board]
  (when (win-seq? (map get board (range)))
    (as-> (range 0 (count board)) posxs
        (mapv #(vector %1 %2) posxs posxs))))

(defn diagonal-win-r
  [board]
  (as-> (range (dec (count board)) -1 -1) posxs
    (when (win-seq? (map get board posxs))
      (mapv #(vector %1 %2) (range) posxs))))

(s/defn game-over? :- s/Bool
  [board :- Board]
  (nil? (some nil? (flatten board))))

(s/defn winner :- (s/maybe Winner)
  [board :- Board]
  (let [h-win (horizontal-win board)
        v-win (vertical-win board)
        dl-win (diagonal-win-l board)
        dr-win (diagonal-win-r board)]
    (cond
      h-win [(->> h-win first (get-in board)) h-win]
      v-win [(->> v-win first (get-in board)) v-win]
      dl-win [(->> dl-win first (get-in board)) dl-win]
      dr-win [(->> dr-win first (get-in board)) dr-win]
      (game-over? board) [:draw])))

(s/defn valid-move? :- s/Bool
  [board :- Board
   [x y] :- Coord]
  (and (< x (count board))
       (< y (count board))
       (nil? (get-in board [x y]))))

(s/defn new-board :- Board
  [size :- s/Int]
  (into []
        (repeatedly size
                    #(into [] (repeat size nil)))))

(s/defn move :- Board
  [board :- Board
   coord :- Coord
   v :- BoardValue]
  (println v)
  (if (valid-move? board coord)
    (assoc-in board coord v)
    board))
