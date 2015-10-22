(ns sidequarter-api.sidekiqs
  (:require [yesql.core :refer [defqueries]]
            [environ.core :refer [env]]))

(defqueries "queries/sidekiqs.sql"
  {:connection (env :database-url)})

(defn find-by-id [id]
  (first (where-id {:id id})))
