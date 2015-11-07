(ns sidequarter-api.sidekiqs
  (:require [yesql.core :refer [defqueries]]
            [clojure.string :refer [blank?]]
            [clojure.walk :refer [keywordize-keys]]
            [clj-time.core :as time]
            [clj-time.format :as format]
            [sidequarter-api.util :refer [wcar*]]
            [sidequarter-api.parsers :as parser]
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

(defn add-availability [sk]
  (let [ping (try
               (wcar* (conn sk)
                      (car/ping))
               (catch Exception _ nil))]
    (assoc sk :available (= ping "PONG"))))

(defn has-queue? [sk queue]
  (let [res (wcar* (conn sk)
                   (car/smembers (with-ns sk "queues")))]
    (some #(= queue %) res)))

(defn queues [sk]
  (let [queues (wcar* (conn sk)
                      (car/smembers (with-ns sk "queues")))
        raw-sizes (wcar* (conn sk)
                         (mapv #(car/llen (with-ns sk (str "queue:" %))) queues))
        sizes (map #(or (parser/integer %) 0) (flatten [raw-sizes]))]
    (mapv (fn [n c] {:name n :count c}) queues sizes)))

(defn info [sk]
  (let [info (keywordize-keys (wcar* (conn sk)
                                     (car/info*)))
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
        vals (map #(or (parser/integer %) 0) raw-vals)
        more-data (wcar* (conn sk)
                         (mapv #(car/hget (key %) "busy") procs)
                         (mapv #(car/llen (key (str "queue:" %))) queues))
        more-vals (->> (map #(or (parser/integer %) 0) more-data)
                       (split-at (count procs))
                       (mapv #(reduce + %)))
        keys [:processed :failed :retries :scheduled :dead :processes :queues :busy :enqueued]]
    (->> (concat vals more-vals)
         (interleave keys)
         (apply array-map))))

(defn history [sk days till]
  (let [key #(with-ns sk %)
        dates (map #(time/minus till (time/days %)) (range days))
        day-dates (map parser/undate! dates)
        proc-keys (map #(key (str "stat:processed:" %)) day-dates)
        fail-keys (map #(key (str "stat:failed:" %)) day-dates)
        [proc-vals fail-vals] (wcar* (conn sk)
                                     (apply car/mget proc-keys)
                                     (apply car/mget fail-keys))]
    (map (fn [d p f]
           {:day d
            :processed (or (parser/integer p) 0)
            :failed (or (parser/integer f) 0)})
         day-dates proc-vals fail-vals)))
