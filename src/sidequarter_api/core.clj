(ns sidequarter-api.core
  (:require [liberator.core :refer [resource defresource]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.jsonp :refer [wrap-json-with-padding]]
            [compojure.core :refer [defroutes ANY]]
            [compojure.route :as route]
            [clj-time.core :as time]
            [environ.core :refer [env]]
            [sidequarter-api.parsers :as parser]
            [sidequarter-api.util :refer [not-found-resp
                                          unprocessable-entity-resp
                                          internal-error-resp
                                          opt-query-param!]]
            [sidequarter-api.sidekiqs :as sidekiqs]))

(defn ^:private entry [id]
  (some->> (parser/positive-int id)
           (sidekiqs/find-by-id)
           (sidekiqs/add-availability)))

(defn ^:private available-entry [id]
  (let [sidekiq (entry id)]
    (when (= (:available sidekiq) true) sidekiq)))

(def resource-defaults
  {:available-media-types ["application/json"]
   :allowed-methods [:get :head :options]
   :handle-not-found not-found-resp
   :handle-unprocessable-entity unprocessable-entity-resp})
   ; :handle-exception internal-error-resp})

(defn detail-resource-defaults [id]
  (merge resource-defaults
         {:exists? (when-let [sidekiq (available-entry id)]
                     {::entry sidekiq})}))

(defresource not-found-action
  resource-defaults
  :handle-ok not-found-resp)

(defresource index-action
  resource-defaults
  :handle-ok {:sidekiqs (map sidekiqs/add-availability (sidekiqs/all))})

(defresource show-action [id]
  resource-defaults
  :exists? (when-let [sidekiq (entry id)]
             {::entry sidekiq})
  :handle-ok (fn [ctx]
               {:sidekiq (::entry ctx)}))

(defresource queues-action [id]
  (detail-resource-defaults id)
  :handle-ok (fn [ctx]
               {:queues (sidekiqs/queues (::entry ctx))}))

(defresource queue-action [id name]
  (detail-resource-defaults id)
  :handle-ok "queue detail")

(defresource stats-action [id]
  (detail-resource-defaults id)
  :handle-ok (fn [ctx]
               (let [sidekiq (::entry ctx)]
                 {:stats (sidekiqs/stats sidekiq)
                  :info (sidekiqs/info sidekiq)})))

(defresource history-action [id]
  (detail-resource-defaults id)
  :processable? (fn [ctx]
                  (try
                    {::days (opt-query-param! ctx "days" parser/positive-int! 7)
                     ::till (opt-query-param! ctx "till" parser/date! (time/now))}
                    (catch Exception _ false)))
  :handle-ok (fn [ctx]
               {:days (sidekiqs/history (::entry ctx) (::days ctx) (::till ctx))}))

(defroutes app
  (ANY "/" [] index-action)
  (ANY "/:id" [id] (show-action id))
  (ANY "/:id/queues" [id] (queues-action id))
  (ANY "/:id/queues/:name" [id name] (queue-action id name))
  (ANY "/:id/stats" [id] (stats-action id))
  (ANY "/:id/history" [id] (history-action id))
  (route/not-found not-found-action))

(def handler
  (if (some? (env :database-url))
    (-> app
        (wrap-json-with-padding)
        (wrap-params))
    (throw (Exception. "Missing required environment variables"))))
