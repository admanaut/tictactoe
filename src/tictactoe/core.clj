(ns tictactoe.core
  (:gen-class)
  (require [tictactoe.console :as console]
           [tictactoe.service :as service]))

(defn -main
  [& args]
  (when args
    (let [cmd (keyword (first args))]
      (case cmd
        :console (console/main)
        :service (service/main)))))
