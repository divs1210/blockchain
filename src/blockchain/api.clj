(ns blockchain.api
  (:require [blockchain.impl :as impl]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.middleware.json :as middleware]))

(defroutes app-routes
  (GET  "/mine"             []  (impl/mine))
  (GET  "/chain"            []  (impl/chain))
  (POST "/transactions/new" req (impl/transaction-new req))
  (POST "/nodes/register"   req (impl/nodes-register req))
  (GET  "/nodes/resolve"    []  (impl/nodes-resolve))
  (route/not-found              "Not Found"))

(def app
  (do
    (println (str "Initialzing blockchain with identifier " impl/node-id "."))

    (println "Inserting genesis block...")
    (impl/genesis-block)

    (println "Genesis block inserted. Starting API...")
    (-> app-routes
        (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
        (middleware/wrap-json-body {:keywords? true})
        middleware/wrap-json-response)))
