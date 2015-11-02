(ns sidequarter-api.parsers
  (:require [clj-time.format :as format]))

(def ^:private date-formatter (format/formatter "YYYY-MM-dd"))

(defn date [val]
  (try
    (format/parse date-formatter val)
    (catch Exception _ nil)))

(defn date! [val]
  (or
   (date val)
   (throw (IllegalArgumentException. "Not parsable to a valid date"))))

(defn undate [val]
  (try
    (format/unparse date-formatter val)
    (catch Exception _ nil)))

(defn undate! [val]
  (or
   (undate val)
   (throw (IllegalArgumentException. "Can't convert to date string"))))

(defn integer [val]
  (try
    (Integer. val)
    (catch Exception _ nil)))

(defn positive-int [val]
  (let [res (integer val)]
    (when (and (integer? res) (> res 0)) res)))

(defn positive-int! [val]
  (or
   (positive-int val)
   (throw (IllegalArgumentException. "Not parsable to a positive integer"))))
