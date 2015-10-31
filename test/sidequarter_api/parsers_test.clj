(ns sidequarter-api.parsers-test
  (:require [midje.sweet :refer :all]
            [clj-time.core :as time]
            [sidequarter-api.parsers :refer :all]))

(facts "about `date`"
       (fact "it parses YYYY-MM-dd format"
             (date "1010-10-01") => (time/date-time 1010 10 1)
             (date "2017-07-07") => (time/date-time 2017 07 7)
             (date "2015-01-01") => (time/date-time 2015 1 1))
       (fact "it returns nil if unparsable"
             (date "unparsable") => nil
             (date "10-10-1010") => nil
             (date {}) => nil
             (date 1) => nil))

(facts "about `date!`"
       (fact "it parses YYYY-MM-dd format"
             (date! "1010-10-01") => (time/date-time 1010 10 1)
             (date! "2017-07-07") => (time/date-time 2017 07 7)
             (date! "2015-01-01") => (time/date-time 2015 1 1))
       (fact "it returns nil if unparsable"
             (date! "unparsable") => (throws IllegalArgumentException)
             (date! "10-10-1010") => (throws IllegalArgumentException)
             (date! {}) => (throws IllegalArgumentException)
             (date! 1) => (throws IllegalArgumentException)))
