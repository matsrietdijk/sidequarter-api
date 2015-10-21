(ns sidequarter-api.core
  (:require [liberator.core :refer [resource defresource]]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.core :refer [defroutes ANY]]))

(defresource index-action
  :available-media-types ["application/json"]
  :allowed-methods [:get :options]
  :handle-ok { :message "Hello, World!" })

(defroutes app
  (ANY "/" [] index-action))

(def handler
  (-> app
      wrap-params))
