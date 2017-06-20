(defproject tictactoe "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [prismatic/schema "1.1.6"]
                 [org.clojure/test.check "0.9.0"]]
  :main ^:skip-aot tictactoe.core
  :target-path "target/%s"
  :test-paths ["test"]
  :profiles {:uberjar {:aot :all}})
