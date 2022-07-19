(ns codingame-clojure.core
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
  (rest (load-csv "resources/epl_players_22.csv")))

(defn top-players [x]
  (filter #(> (Double/parseDouble (% :total_avg)) (Double/parseDouble x)) (epl)))

(defn top-players-club [x club]
  (filter #(= (% :team) club) (top-players x)))


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

; Simple Body Page
(defn simple-body-page [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "Hello Woaaarld"})

; request-example
(defn request-example [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (->>
              (pp/pprint req)
              (str "Request Object: " req))})

(defn hello-name [req] ;(3)
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (->
              (pp/pprint req)
              (str "Hello " (:name (:params req))))})

(defn mantra-top [req] ;(3)
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (->
              (pp/pprint req)
              (str (json/write-str (top-players (:ranking (:params req)))) ))})

(defroutes app-routes
           (GET "/" [] simple-body-page)
           (GET "/request" [] request-example)
           (GET "/hello" [] hello-name)
           (GET "/mantra/top" [] mantra-top)
           (route/not-found "Error, page not found!"))

(defn -main
  "This is our main entry point"
  [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
    ; Run the server with Ring.defaults middleware
    (server/run-server (wrap-defaults #'app-routes site-defaults) {:port port})
    ; Run the server without ring defaults
    ;(server/run-server #'app-routes {:port port})
    (println (str "Running webserver at http:/127.0.0.1:" port "/"))))

