(ns sidequarter-api.core-test
  (:require [midje.sweet :refer :all]
            [sidequarter-api.util :refer [not-found-resp]]
            [sidequarter-api.sidekiqs :as sidekiqs]
            [sidequarter-api.core :refer :all]))

(facts "about `resource-defaults`"
       (fact "the default media type is json"
             (:available-media-types resource-defaults) => ["application/json"])
       (fact "it provides not found handler"
             (:handle-not-found resource-defaults) =not=> nil
             (:handle-not-found resource-defaults) => not-found-resp))
