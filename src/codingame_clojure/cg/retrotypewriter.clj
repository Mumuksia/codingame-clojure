(ns codingame-clojure.cg.retrotypewriter
  (:require [clojure.string :as str])
  (:gen-class))

(defn inp []
  "1sp 1/ 1bS 1_ 1/ 1bS nl 1( 1sp 1o 1. 1o 1sp 1) nl 1sp 1> 1sp 1^ 1sp 1< nl 2sp 3|")

(defn map-special [word]
  [(Long/parseLong(str/join (butlast word))) (str/join (take-last 1 word))]
  )

(defn repl-symb [word]
  (str/replace (str/replace (str/replace word #"sp" " ") #"bS" "\\\\")
               #"sQ" "'")
  )

(defn add-symbols [w]
  (str/join (repeat (first w) (last w)))
  )

(defn process-command [w]
  (if (= w "nl")
    "\n"
    (add-symbols(map-special (repl-symb w)))
    )
  )

(defn rloop [line]

  (loop [l line
         result ""]
    (if (empty? l)
      result
      (recur (rest l) (str result (process-command (first l))))
      )
    ))

(defn process [input]
  (rloop (clojure.string/split input #" ")))
