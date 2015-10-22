(ns sidequarter-api.util)

(defn ->valid-id [id]
  (some->> (str id)
           (re-find #"^[1-9][0-9]*$")
           read-string
           int))
