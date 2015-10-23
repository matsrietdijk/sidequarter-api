(ns sidequarter-api.util
  (:require [taoensso.carmine :as car :refer (wcar)]))

(defn ->valid-id [id]
  (some->> (str id)
           (re-find #"^[1-9][0-9]*$")
           read-string
           int))

(defn status-response [code message]
  {:code code
   :message message})

(defn internal-error-resp []
  (status-response 500 "Internal server error"))

(defn not-found-resp []
  (status-response 404 "Not found"))

(defmacro wcar* [conn & body] `(car/wcar ~conn ~@body))
