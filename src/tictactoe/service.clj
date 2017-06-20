(ns tictactoe.service
  (:require [org.httpkit.server :as httpkit]
            [compojure.api.sweet :refer :all :as sweet]
            [ring.util.http-response :refer :all]
            [schema.core :as s]
            [tictactoe.game :as game]))

(def CONFIG
  {:port 4444})

(defonce server (atom nil))

(defn stop-server
  []
  (when-not (nil? @server)
    (@server)
    (reset! server nil)))

(def app
  (sweet/api
    (GET "/new-game/:size" []
         :description "Returns a new game board"
         :path-params [size :- s/Int]
         :return game/Board
         (ok (game/new-board size)))
    (PUT "/valid-move" []
         :body [body {:move game/Coord
                      :board s/Any ;;game/Board
                      }]
         :return s/Bool
         (ok (game/valid-move? (:board body) (:move body))))
    (PUT "/move" []
         :body [body {:board s/Any;; game/Board
                      :move game/Coord
                      :player s/Keyword}]
         :return game/Board
         (ok (game/move (:board body) (:move body) (:player body) )))
    (GET "/winner" []
         :body [body {:board s/Any ;;game/Board
                      }]
         :return (s/maybe game/Winner)
         (ok (game/winner (:board body))))))

(defn init
  [config]
  (reset! server
          (httpkit/run-server #'app {:port (:port config)}))
  (println "Http service running on port " (:port config) " ..."))

(defn main
  []
  (init CONFIG))
