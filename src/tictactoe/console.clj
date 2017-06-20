(ns tictactoe.console
  (:require [tictactoe.game :as game]))

(defn read-size!
  []
  (println "Please enter the size of the board:")
  (Integer/parseInt (read-line)))

(defn read-move!
  [player]
  (println)
  (println player ", enter your move:")
  (read-string (read-line)))

(defn read-player!
  []
  (println "Who wants to start first ? [:x or :0]")
  (let [player (read-string (read-line))]
    (if-not (contains? #{:x :0} player)
      (recur)
      player)))

(defn next-player
  [player]
  ({:x :0
    :0 :x} player))

(defn print-board
  [board]
  (println)
  (println
    (clojure.string/join "\n" board))
  (println))

(defn decorate-win-moves
  [board win-moves]
  (reduce (fn [acc coord]
            (update-in acc coord (fn [v] (keyword (str (name v) "*")))))
          board win-moves))

(defn display-result
  [w board]
  (when-let [[r win-seq] w]
    (case r
      :draw (do
              (println "It's a draw ! Congratulations you're equally smart ;-|")
              (print-board board))
      (do
        (println "~~~~~~~~~~ We have a winner ~~~~~~~~~~")
        (println "Congratulations " r " you're awesome !")
        (print-board (decorate-win-moves board win-seq))))))

(defn main
  []
  (println "!!! Welcome to tic tac toe !!!")
  (let [size (read-size!)
        board (game/new-board size)]
    (print-board board)
    (loop [player (read-player!)
           board board
           coords (read-move! player)]
      (if (game/valid-move? board coords)
        (let [new-board (game/move board coords player)
              nxt-player (next-player player)]
          (if-let [w (game/winner new-board)]
            (do
              (display-result w new-board))
            (do
              (print-board new-board)
              (recur nxt-player new-board (read-move! nxt-player)))))
        (do
          (println "Invalid move ! Valid moves are in the format: [0 2]")
          (recur player board (read-move! player)))))))
