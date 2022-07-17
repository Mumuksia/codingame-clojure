(ns codingame-clojure.core
  (:require [clojure.data.csv :as csv])
  (:require [clojure.java.io :as io] ))

(def csv-file (.getFile  (clojure.java.io/resource "epl_players_22.csv")))

(defn empty-string-to-nil
  "Returns a nil if given an empty string S, otherwise returns S."
  [s]
  (if (and (string? s) (empty? s))
    nil
    s))

(defn load-csv
  "Returns a data structure loaded from a CSV file at FILEPATH."
  [filepath]
  (with-open [reader (clojure.java.io/reader filepath)]
    (->> (csv/read-csv reader)
         (map (fn [row]
                (zipmap [:player :team :pos1 :pos2 :pos3 :games :avg :total_avg]
                        (take 8 (map empty-string-to-nil row)) ))
                )
         (doall))))

(defn epl []
  (rest (load-csv "resources/epl_players_22.csv")))

(defn top-players [x]
  (filter #(> (Double/parseDouble (% :total_avg)) x) (epl)))
(defn swap [v i1 i2]
      (assoc v i2 (v i1) i1 (v i2)))

(defn swap [v ipair]
      (assoc v (nth ipair 1) (v (nth ipair 0)) (nth ipair 0) (v (nth ipair 1))))

(defn adjust-swappers [p]
      #{(- (first p) 1), (+ (second p) 1)}
      )

(defn ad-s [p]
      (list (+ (nth p 1) 1)
            (- (nth p 0) 1)))


(defn process [^String s]
      (group-by #(.charAt s %) (range (count s))))

(defn get-swappers-2 [line]
      (partition 2  (get (process line) \-)))

(defn get-swappers [line]
      (map ad-s (get-swappers-2 line)))

(defn ad-line [result swappers]
      (if (empty? swappers)
            result
            (ad-line (swap result (first swappers)) (rest swappers)))
      )

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))
