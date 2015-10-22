(ns sidequarter-api.core
  (:require [liberator.core :refer [resource defresource]]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.core :refer [defroutes ANY]]
            [sidequarter-api.util :refer [->valid-id]]
            [sidequarter-api.sidekiqs :as sidekiqs]))

(defn status-response [code message]
  {:code code
   :message message})

(def resource-defaults
  {:available-media-types ["application/json"]
   :handle-not-found (status-response 404 "Not found")
   :handle-exception (status-response 500 "Internal server error")})

(defresource index-action resource-defaults
  :allowed-methods [:get :options]
  :handle-ok {:sidekiqs (sidekiqs/all)})

(defresource show-action [id] resource-defaults
  :allowed-methods [:get :options]
  :exists? (some->> (->valid-id id)
                    sidekiqs/find-by-id
                    (hash-map ::entry))
  :handle-ok (fn [ctx]
               {:sidekiq (::entry ctx)}))

(defroutes app
  (ANY "/" [] index-action)
  (ANY "/:id" [id] (show-action id)))

(def handler
  (wrap-params app))
