(ns blockchain.core
  (:require [blockchain.utils :as utils]))

(defonce chain
  (atom []))

(defonce current-transactions
  (atom []))

(defn chain-add
  [-data]
  (swap! chain conj -data))

(defn chain-reset
  [new-chain]
  (reset! chain (vec new-chain)))

(defn last-block
  []
  (peek @chain))

(defn transactions-add
  [-data]
  (swap! current-transactions conj -data))

(defn transactions-reset
  []
  (reset! current-transactions []))

(defn new-transaction
  [sender recipient amount]
  (transactions-add (sorted-map
                     :sender sender
                     :recipient recipient
                     :amount amount))

  (-> (last-block) :index inc))

(defn new-block
  ([proof previous-hash]
   (chain-add (sorted-map
               :index (count @chain)
               :timestamp (System/currentTimeMillis)
               :transactions @current-transactions
               :proof proof
               :previous-hash previous-hash))
   (transactions-reset)
   (count @chain))
  ([proof]
   (new-block proof
              (utils/sha256hash (pr-str (last-block))))))
