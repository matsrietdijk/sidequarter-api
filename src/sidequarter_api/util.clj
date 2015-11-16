(ns sidequarter-api.util
  (:require [taoensso.carmine :as car :refer (wcar)]
            [clj-time.coerce :as coerce]))

(defn opt-query-param! [context name parser default]
  (let [raw (get-in context [:request :query-params name])]
    (if (nil? raw) default (parser raw))))

(defn status-response [code message]
  {:code code
   :message message})

(defn internal-error-resp [_]
  (status-response 500 "Internal server error"))

(defn not-found-resp [_]
  (status-response 404 "Not found"))

(defn unprocessable-entity-resp [_]
  (status-response 422 "Unprocessable entity"))

(defmacro wcar* [conn & body] `(car/wcar ~conn ~@body))

(defn from-seconds [sec]
  (-> (* 1000 sec)
      long
      coerce/from-long))

(defn update-multiple [ms ks f]
  (let [update-if #(if (contains? % %2) (update % %2 f) %)]
    (map #(reduce update-if % ks) ms)))
