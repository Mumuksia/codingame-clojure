(ns codingame-clojure.mantra.auction
  (:require [clojure.data.csv :as csv])
  (:require [clojure.java.io :as io] )
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [clojure.data.json :as json]
            [clojure.string :as string])
  (:gen-class))

(defn remove-keys [pred m]
  (apply dissoc m (filter pred (keys m))))

(defn pos-eq [pos players]
  (filter #(or (= (% :pos1) pos) (= (% :pos2) pos) (= (% :pos3) pos)) players)
  )

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
  (filter #(> (% :total_avg) 0) (map (fn [x] (update-in x [:total_avg] #(Double/parseDouble %)))
                  (rest (load-csv "resources/epl_players_22.csv")))))

(defn epl-stripped [players]
  (filter #(> (% :games) 7) (map (fn [x] (update-in x [:games] #(Long/parseLong %))) players))
  )

(defn by-pos-club [pos club]
  (sort-by :total_avg (map #(dissoc % :pos1 :pos2 :pos3 )
                           (pos-eq pos
                                   (filter #(clojure.string/includes? (% :team) club)
                                           (epl-stripped (epl))))))
  )


(defn ave-pos [pos n]
  (/ (reduce + (take-last n (map :total_avg (by-pos-club pos "")))) n)
  )

(def typetrans {:avg #(Double/parseDouble %) :total_avg #(Double/parseDouble %)})

(defn epl-transformed []
  (rest (load-csv "resources/epl_players_22.csv")))

(defn top-players [x]
  (sort-by :total_avg (filter #(> (% :total_avg) (Double/parseDouble x)) (epl))))

(defn top-players-club [x club]
  (filter #(= (% :team) club) (top-players x)))

(defn top-players-pos [x pos]
  (filter #(or (= (% :pos2) pos) (= (% :pos3) pos) (= (% :pos1) pos)) (top-players x)))



