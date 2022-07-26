(ns codingame-clojure.core
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [clojure.pprint :as pp]
            [clojure.data.json :as json]
            [codingame-clojure.mantra.auction :as auc]
            [codingame-clojure.cg.retrotypewriter :as rtw])
  (:gen-class))

(defn test-epl []
  (auc/epl-stripped (auc/epl)))

(defn simple-body-page [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "Hello Woaaarld"})


(defn mantra-top [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (->
              (pp/pprint req)
              (str (json/write-str (auc/by-pos-club (:pos (:params req)) (:club (:params req)))) ))})

(defroutes app-routes
           (GET "/" [] simple-body-page)
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

