(ns sidequarter-api.util-test
  (:require [midje.sweet :refer :all]
            [sidequarter-api.util :refer :all]))

(defn ^:private context []
  {:request {:query-params {"key" "val"}}})

(facts "about `opt-query-param!`"
       (fact "it returns valid available vals"
             (opt-query-param! (context) "key" identity nil) => "val")
       (fact "it returns default if key not available"
             (opt-query-param! (context) "invalid" identity "default") => "default"
             (opt-query-param! {} "invalid" identity "default") => "default")
       (fact "it throws parser exceptions"
             (opt-query-param! (context) "key" bigint nil) => (throws Exception)
             (opt-query-param! (context) "key" bigint nil) => (throws NumberFormatException))
       (fact "it does not parse default"
             (opt-query-param! (context) "invalid" bigint "default") =not=> (throws Exception)
             (opt-query-param! {} "invalid" bigint "default") =not=> (throws Exception)
             (opt-query-param! {} "invalid" bigint "default") => "default"))

(facts "about `status-response`"
       (fact "it has a code & a message"
             (keys (status-response nil nil)) => [:code :message])
       (fact "it sets first & second param correctly"
             (:code (status-response "code" nil)) => "code"
             (:message (status-response nil "message")) "message"))

(facts "about `internal-error-resp`"
       (fact "it has code 500"
             (:code (internal-error-resp nil)) => 500))

(facts "about `not-found-resp`"
       (fact "it has code 404"
             (:code (not-found-resp nil)) => 404))

(facts "about `unprocessable-entity-resp`"
       (fact "it has code 422"
             (:code (unprocessable-entity-resp nil)) => 422))
