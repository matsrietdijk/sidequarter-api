(ns sidequarter-api.sidekiqs
  (:require [yesql.core :refer [defqueries]]
            [clojure.string :refer [blank?]]
            [sidequarter-api.util :refer [wcar*]]
            [taoensso.carmine :as car :refer (smembers)]
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
