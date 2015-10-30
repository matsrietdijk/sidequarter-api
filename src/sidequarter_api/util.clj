(ns sidequarter-api.util
  (:require [taoensso.carmine :as car :refer (wcar)]
            [clj-time.format :as format]))

(def day-formatter (format/formatter "YYYY-MM-dd"))

(defn ->valid-id [id]
  (some->> (str id)
           (re-find #"^[1-9][0-9]*$")
           read-string
           int))

(defn ->int [v]
  (let [res (some->> (str v)
                     (re-find #"^-?[0-9]+")
                     read-string
                     int)]
    (if (nil? res) 0 res)))

(defn positive-int! [val]
  (let [res (some->> (str val)
                     (re-find #"^[1-9][0-9]*$")
                     bigint
                     int)]
    (if (nil? res)
      (throw (NumberFormatException. "Not parsable to a positive integer"))
      res)))

(defn date! [val]
  (format/parse day-formatter val))

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
