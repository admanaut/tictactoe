(ns tictactoe.game-test
  (:require [tictactoe.game :as game]
            [clojure.test :refer [deftest is] :as t]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :as check-test]))

(deftest win-seq-empty
  (is (= false
         (game/win-seq? nil)))
  (is (= false
         (game/win-seq? []))))

(deftest win-seq-nil
  (is (= false
         (game/win-seq? [nil :x :0])))
  (is (= false
         (game/win-seq? [nil nil nil]))))

(check-test/defspec win-seq
  100
  (prop/for-all [v (gen/such-that (partial apply =) ;; all elements the same
                                  (gen/vector (gen/elements [:x :0]) 1 3)
                                  20)]
                (= true (game/win-seq? v))))

(deftest horizontal-win
  (is (nil?
        (game/horizontal-win nil)))
  (is (nil?
        (game/horizontal-win [])))
  (is (nil?
        (game/horizontal-win [[]])))
  (is (nil?
        (game/horizontal-win [[nil]])))

  (is (= [[0 0]]
         (game/horizontal-win [[:x]])))
  (is (nil?
        (game/horizontal-win [[:x :0]])))
  (is (nil?
        (game/horizontal-win [[:x :0 :0]])))
  (is (= [[0 0] [0 1]]
         (game/horizontal-win [[:x :x]])))
  (is (= [[1 0] [1 1] [1 2]]
         (game/horizontal-win [[:x :0 :x]
                               [:0 :0 :0]])))
  (is (= [[1 0] [1 1] [1 2]]
         (game/horizontal-win [[:x :0 :x]
                               [:0 :0 :0]
                               [:x :x :x]]))))

(deftest vertical-win
  (is (nil?
        (game/vertical-win nil)))
  (is (nil?
        (game/vertical-win [])))
  (is (nil?
        (game/vertical-win [[]])))
  (is (nil?
        (game/vertical-win [[nil]])))
  (is (nil?
        (game/vertical-win [[:x :0]
                            [:0 :x]])))

  (is (= [[0 0]]
         (game/vertical-win [[:x]])))
  (is (= [[0 0]]
         (game/vertical-win [[:x :0]])))
  (is (= [[0 0] [1 0]]
         (game/vertical-win [[:x :x]
                             [:x :0]])))
  (is (= [[0 1] [1 1] [2 1]]
         (game/vertical-win [[:x :0]
                             [:0 :0]
                             [:x :0]]))))
(deftest diagonal-win-l
  (is (nil?
        (game/diagonal-win-l nil)))
  (is (nil?
        (game/diagonal-win-l [])))
  (is (nil?
        (game/diagonal-win-l [[]])))
  (is (nil?
        (game/diagonal-win-l [[nil]])))

  (is (= [[0 0] [1 1] [2 2]]
        (game/diagonal-win-l [[:x :0 :x]
                              [:0 :x :0]
                              [:0 :x :x]])))
  (is (nil?
        (game/diagonal-win-l [[:x :0 :x]
                              [:0 :0 :0]
                              [:0 :x :x]]))))

(deftest diagonal-win-r
  (is (nil?
        (game/diagonal-win-r nil)))
  (is (nil?
        (game/diagonal-win-r [])))
  (is (nil?
        (game/diagonal-win-r [[]])))
  (is (nil?
        (game/diagonal-win-r [[nil]])))

  (is (= [[0 2] [1 1] [2 0]]
        (game/diagonal-win-r [[:x :0 :x]
                              [:0 :x :0]
                              [:x :x :x]])))
  (is (nil?
        (game/diagonal-win-r [[:x :0 :x]
                              [:0 :0 :0]
                              [:0 :x :x]]))))

(deftest game-over?
  (is (= true
         (game/game-over? nil)))
  (is (= true
         (game/game-over? [])))

  (is (= false
         (game/game-over? [nil])))
  (is (= false
         (game/game-over? [nil :x])))

  (is (= true
         (game/game-over? [:0 :x])))
  (is (= true
         (game/game-over? [[:0 :x]
                           [:0 :x]])))
  (is (= false
         (game/game-over? [[:0 nil]
                           [:0 :x]]))))

(deftest winner
  (is (= [:draw]
         (game/winner nil)))
  (is (= [:draw]
         (game/winner [])))
  (is (= [:x [[0 0] [0 1] [0 2]]]
         (game/winner [[:x :x :x]])))
  (is (= [:0 [[0 0] [1 0]]]
         (game/winner [[:0 :0 :x]
                       [:0 :x :0]])))
  (is (= [:x [[0 0] [1 1] [2 2]]]
         (game/winner [[:x :0 :x]
                       [:0 :x :0]
                       [:0 :0 :x]]))))

(deftest valid-move
  (is (true?
        (game/valid-move? (game/new-board 3)  [0 0])))
  (is (false?
        (game/valid-move? (game/new-board 3)  [0 3])))
  (is (false?
        (game/valid-move? (game/new-board 3)  [3 3]))))

(deftest new-board
  (is (= [[nil nil nil]
          [nil nil nil]
          [nil nil nil]]
        (game/new-board 3))))

(deftest move
  (is (= [[:x nil nil]
          [nil nil nil]
          [nil nil nil]]
         (game/move (game/new-board 3) [0 0] :x)))
  (is (= [[nil nil nil]
          [nil nil nil]
          [nil nil nil]]
         (game/move (game/new-board 3) [3 3] :x)))

  (is (= [[nil nil nil]
          [nil nil nil]
          [nil nil nil]]
         (-> (game/new-board 3)
             (game/move [3 3] :x)
             (game/move [3 3] :x))))

  (is (= [[:x nil nil]
          [nil nil nil]
          [nil nil nil]]
         (-> (game/new-board 3)
             (game/move [0 0] :x)
             (game/move [0 0] :x)))))
