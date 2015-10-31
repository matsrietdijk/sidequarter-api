(ns sidequarter-api.util
  (:require [taoensso.carmine :as car :refer (wcar)]))

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

(defmacro wcar* [conn & body] `(car/wcar ~conn ~@body))
