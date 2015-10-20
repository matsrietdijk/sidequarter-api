(ns sidequarter-api.core
  (:require [liberator.core :refer [resource defresource]]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.core :refer [defroutes GET]]))

(defroutes app
  (GET "/" [] index-action))

(def handler
  (-> app
      wrap-params))

(defresource index-action
  :available-media-types ["application/json"]
  :handle-ok { :message "Hello, World!" })
