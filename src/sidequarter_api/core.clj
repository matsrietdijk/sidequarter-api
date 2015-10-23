(ns sidequarter-api.core
  (:require [liberator.core :refer [resource defresource]]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.core :refer [defroutes ANY]]
            [compojure.route :as route]
            [sidequarter-api.util :refer [->valid-id not-found-resp
                                          internal-error-resp]]
            [sidequarter-api.sidekiqs :as sidekiqs]))

(defn get-entry-hash [id]
  (some->> (->valid-id id)
           sidekiqs/find-by-id
           (hash-map ::entry)))

(def resource-defaults
  {:available-media-types ["application/json"]
   :handle-not-found not-found-resp})
   ; :handle-exception internal-error-resp})

(defresource not-found-action resource-defaults
  :handle-ok not-found-resp)

(defresource index-action resource-defaults
  :allowed-methods [:get :options]
  :handle-ok {:sidekiqs (sidekiqs/all)})

(defresource show-action [id] resource-defaults
  :allowed-methods [:get :options]
  :exists? (get-entry-hash id)
  :handle-ok (fn [ctx]
               {:sidekiq (::entry ctx)}))

(defresource queues-action [sidekiq-id] resource-defaults
  :allowed-methods [:get :options]
  :exists? (get-entry-hash sidekiq-id)
  :handle-ok (fn [ctx]
               (->> (::entry ctx)
                    (sidekiqs/queues)
                    (hash-map :queues))))

(defresource stats-action [sidekiq-id] resource-defaults
  :allowed-methods [:get :options]
  :exists? (get-entry-hash sidekiq-id)
  :handle-ok (fn [ctx]
               (->> (::entry ctx)
                    (sidekiqs/stats)
                    (hash-map :stats))))

(defroutes app
  (ANY "/" [] index-action)
  (ANY "/:id" [id] (show-action id))
  (ANY "/:sidekiq-id/queues" [sidekiq-id] (queues-action sidekiq-id))
  (ANY "/:sidekiq-id/stats" [sidekiq-id] (stats-action sidekiq-id))
  (route/not-found not-found-action))

(def handler
  (wrap-params app))
