(ns sidequarter-api.sidekiqs
  (:require [yesql.core :refer [defqueries]]
            [clojure.string :refer [blank?]]
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
  (wcar* (conn sk)
         (car/smembers (with-ns sk "queues"))))

(defn stats [sk]
  (let [data (wcar* (conn sk)
                    (car/get (with-ns sk "stat:processed")) ; 0
                    (car/get (with-ns sk "stat:failed")) ; 1
                    (car/zcard (with-ns sk "retry")) ; 2
                    (car/zcard (with-ns sk "schedule")) ; 3
                    (car/zcard (with-ns sk "dead")) ; 4
                    (car/scard (with-ns sk "processes")) ; 5
                    (car/scard (with-ns sk "queues")) ; 6
                    (car/smembers (with-ns sk "processes")) ; 7
                    (car/smembers (with-ns sk "queues"))) ; 8
        procs (get data 7)
        more (wcar* (conn sk)
                    (mapv #(car/hget (with-ns sk %) "busy") procs)
                    (mapv #(car/llen (with-ns sk (str "queue:" %))) (get data 8)))
        more-data (mapv #(reduce + %) (split-at (count procs) (mapv ->int more)))
        vals (concat (mapv ->int (take 7 data)) more-data)
        keys [:processed :failed :retries :scheduled :dead :processes :queues :busy :enqueued]]
    (apply array-map (interleave keys vals))))
