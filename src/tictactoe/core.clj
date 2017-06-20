(ns tictactoe.core
  (:gen-class)
  (require [tictactoe.console :as console]))

(defn -main
  [& args]
  (when args
    (let [cmd (keyword (first args))]
      (case cmd
        :console (console/main)))))
