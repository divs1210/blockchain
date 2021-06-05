(ns blockchain.impl
  (:require [blockchain.core :as core]
            [blockchain.proof :as proof]
            [blockchain.nodes :as nodes]
            [blockchain.utils :as utils]))

(defonce node-id
  (utils/uuidv4))

(defn output-json
  [status-code information]
  {:status status-code
   :headers {"Content-Type" "application/json"}
   :body information })

(defn json-ok
  [information]
  (output-json 200 information))

(defn genesis-block []
  (core/chain-reset [])
  (core/new-block 100 1))

(defn chain []
  (json-ok {:length (count @core/chain)
            :chain  @core/chain}))

(defn mine []
  (let [last-bloock (core/last-block)
        proof (proof/proof-of-work (:proof last-bloock))]
    (core/new-transaction "0" node-id 1)
    (core/new-block proof)
    (json-ok {:message "New block forged"
              :index (:index (core/last-block))
              :transactions (:transactions (core/last-block))
              :proof proof
              :previous_hash (:previous-hash (core/last-block))})))

(defn new-transaction
  [{{:keys [sender recipient amount]} :body}]
  (if (and sender recipient amount)
    (json-ok {:message (str "Transaction will be added to Block "
                            (core/new-transaction sender recipient amount))})
    (output-json 400 {:error "A parameter is missing on the request."})))

(defn register-node [x]
  (-> x
      :body
      :node
      nodes/register-node
      json-ok))

(defn resolve-conflicts []
  (let [k (nodes/resolve-conflicts)]
    (if (string? k)
      k
      "All conflicts were resolved. Chain was updated")))
