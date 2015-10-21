(ns sidequarter-api.core
  (:require [liberator.core :refer [resource defresource]]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.core :refer [defroutes ANY]]
            [sidequarter-api.sidekiqs :refer :all]))

(defresource index-action
  :available-media-types ["application/json"]
  :allowed-methods [:get :options]
  :handle-ok {:sidekiqs (sidekiqs)})

(defroutes app
  (ANY "/" [] index-action))

(def handler
  (wrap-params app))
