(ns sidequarter-api.sidekiqs
  (:require [yesql.core :refer [defqueries]]
            [clojure.string :refer [blank?]]
            [clojure.walk :refer [keywordize-keys]]
            [sidequarter-api.util :refer [wcar* ->int]]
            [taoensso.carmine :as car]
            [environ.core :refer [env]]))

(defqueries "queries/sidekiqs.sql"
  {:connection (env :database-url)})

(defn find-by-id [id]
  (first (where-id {:id id})))

(defn conn [sk]
  {:spec {:uri (:redis_url sk)
          :db 0}})

(defn with-ns [sk key]
  (let [namespace (:namespace sk)]
    (if (blank? namespace)
      key
      (str namespace ":" key))))

(defn queues [sk]
  (let [queues (wcar* (conn sk)
                      (car/smembers (with-ns sk "queues")))
        raw-sizes (wcar* (conn sk)
                         (mapv #(car/llen (with-ns sk (str "queue:" %))) queues))
        sizes (->> (flatten [raw-sizes])
                   (mapv ->int))]
    (mapv (fn [n c] {:name n :count c}) queues sizes)))

(defn info [sk]
  (let [info (-> (wcar* (conn sk)
                        (car/info*))
                 keywordize-keys)
        keys [:used_memory_human :used_memory_peak_human :uptime_in_seconds :redis_version
              :connected_clients]]
    (select-keys info keys)))

(defn stats [sk]
  (let [key #(with-ns sk %)
        data (wcar* (conn sk)
                    (car/get (key "stat:processed")) ; 0
                    (car/get (key "stat:failed")) ; 1
                    (car/zcard (key "retry")) ; 2
                    (car/zcard (key "schedule")) ; 3
                    (car/zcard (key "dead")) ; 4
                    (car/scard (key "processes")) ; 5
                    (car/scard (key "queues")) ; 6
                    (car/smembers (key "processes")) ; 7
                    (car/smembers (key "queues"))) ; 8
        [raw-vals [procs queues]] (split-at 7 data)
        vals (mapv ->int raw-vals)
        more-data (wcar* (conn sk)
                         (mapv #(car/hget (key %) "busy") procs)
                         (mapv #(car/llen (key (str "queue:" %))) queues))
        more-vals (->> (mapv ->int more-data)
                       (split-at (count procs))
                       (mapv #(reduce + %)))
        keys [:processed :failed :retries :scheduled :dead :processes :queues :busy :enqueued]]
    (->> (concat vals more-vals)
         (interleave keys)
         (apply array-map))))
