(ns codingame-clojure.mantra.auction
  (:require [clojure.data.csv :as csv])
  (:require [clojure.java.io :as io] )
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [clojure.data.json :as json])
  (:gen-class))

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
  (map (fn [x] (update-in x [:total_avg] #(Double/parseDouble %)))
       (rest (load-csv "resources/epl_players_22.csv"))))

(def typetrans {:avg #(Double/parseDouble %) :total_avg #(Double/parseDouble %)})

(defn epl-transformed []
  (rest (load-csv "resources/epl_players_22.csv")))

(defn top-players [x]
  (sort-by :total_avg (filter #(> (% :total_avg) (Double/parseDouble x)) (epl))))

(defn top-players-club [x club]
  (filter #(= (% :team) club) (top-players x)))

(defn top-players-pos [x pos]
  (filter #(or (= (% :pos2) pos) (= (% :pos3) pos) (= (% :pos1) pos)) (top-players x)))



