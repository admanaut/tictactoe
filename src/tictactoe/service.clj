(ns tictactoe.service
  (:require [org.httpkit.server :as httpkit]
            [compojure.api.sweet :refer :all :as sweet]
            [ring.util.http-response :refer :all]
            [ring.middleware.cors :refer [wrap-cors]]
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

(defn kewordise-players
  [board]
  (mapv (fn [row] (mapv #(if (string? %) (keyword %) %) row)) board))

(def app
  (->
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
           (ok (game/valid-move? (kewordise-players (:board body)) (:move body))))

      (PUT "/move" []
           :body [body {:board s/Any;; game/Board
                        :move game/Coord
                        :player s/Keyword}]
           :return game/Board
           (ok (game/move (kewordise-players (:board body)) (:move body) (:player body))))

      (PUT "/winner" []
           :body [body {:board s/Any ;;game/Board
                        }]
           :return (s/maybe game/Winner)
           (ok (game/winner (kewordise-players (:board body))))))

    (wrap-cors :access-control-allow-origin [#"http://localhost:3449"]
               :access-control-allow-methods [:get :put :post :delete]
               :access-control-allow-credentials true)))

(defn init
  [config]
  (reset! server
          (httpkit/run-server #'app {:port (:port config)}))
  (println "Http service running on port " (:port config) " ..."))

(defn main
  []
  (init CONFIG))
