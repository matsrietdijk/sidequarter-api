(ns sidequarter-api.core-test
  (:require [midje.sweet :refer :all]
            [sidequarter-api.util :refer [not-found-resp]]
            [sidequarter-api.sidekiqs :as sidekiqs]
            [sidequarter-api.core :refer :all]))

(def entry
  (sidekiqs/add-availability (sidekiqs/find-by-id 1)))

(facts "about `get-entry-hash`"
       (fact "it returns nil for not existing id"
             (get-entry-hash -1) => nil
             (get-entry-hash "a") => nil)
       (fact "it returns an entry hash for existing id"
             (get-entry-hash 1) =not=> nil
             (:sidequarter-api.core/entry (get-entry-hash 1)) => entry))

(facts "about `resource-defaults`"
       (fact "the default media type is json"
             (:available-media-types resource-defaults) => ["application/json"])
       (fact "it provides not found handler"
             (:handle-not-found resource-defaults) =not=> nil
             (:handle-not-found resource-defaults) => not-found-resp))
