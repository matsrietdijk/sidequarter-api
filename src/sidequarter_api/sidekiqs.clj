(ns sidequarter-api.sidekiqs
  (:require [yesql.core :refer [defquery]]
            [environ.core :refer [env]]))

(defquery sidekiqs "queries/sidekiqs.sql"
  {:connection (env :database-url)})
