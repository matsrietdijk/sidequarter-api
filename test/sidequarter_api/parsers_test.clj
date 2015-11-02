(ns sidequarter-api.parsers-test
  (:require [midje.sweet :refer :all]
            [clj-time.core :as time]
            [sidequarter-api.parsers :refer :all]))

(facts "about `date`"
       (fact "it parses YYYY-MM-dd format"
             (date "1010-10-01") => (time/date-time 1010 10 1)
             (date "2017-07-07") => (time/date-time 2017 7 7)
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
       (fact "it throws IllegalArgumentException if unparsable"
             (date! "unparsable") => (throws IllegalArgumentException)
             (date! "10-10-1010") => (throws IllegalArgumentException)
             (date! {}) => (throws IllegalArgumentException)
             (date! 1) => (throws IllegalArgumentException)))

(facts "about `undate`"
       (fact "it returns YYYY-MM-dd format"
             (undate (time/date-time 1010 10 1)) => "1010-10-01"
             (undate (time/date-time 2017 7 7)) => "2017-07-07"
             (undate (time/date-time 2015 1 1)) => "2015-01-01")
       (fact "it returns nil if unparsable"
             (undate "unparsable") => nil
             (undate "2015-10-01") => nil
             (undate {}) => nil
             (undate 1) => nil))

(facts "about `undate!`"
       (fact "it returns YYYY-MM-dd format"
             (undate! (time/date-time 1010 10 1)) => "1010-10-01"
             (undate! (time/date-time 2017 7 7)) => "2017-07-07"
             (undate! (time/date-time 2015 1 1)) => "2015-01-01")
       (fact "it throws IllegalArgumentException if unparsable"
             (undate! "unparsable") => (throws IllegalArgumentException)
             (undate! "2015-10-01") => (throws IllegalArgumentException)
             (undate! {}) => (throws IllegalArgumentException)
             (undate! 1) => (throws IllegalArgumentException)))

(facts "about `integer`"
       (fact "it returns an integer"
             (integer "1") => integer?
             (integer "1") => 1
             (integer "-1") => -1
             (integer "9000") => 9000
             (integer "-800") => -800)
       (fact "it returns nil if unparsable"
             (integer "a") => nil
             (integer "1.0") => nil
             (integer "1,0") => nil)
       (fact "it does not allow separators"
             (integer "1.000") => nil
             (integer "1;000") => nil
             (integer "1:000") => nil
             (integer "1,000") => nil)
       (fact "it does not parse bigint"
             (integer "1000000000000") => nil))

(facts "about `positive-int`"
       (fact "it returns a positive integer"
             (positive-int "1") => integer?
             (positive-int "1") => 1
             (positive-int "100") => 100
             (positive-int "9000") => 9000)
       (fact "it returns nil if unparsable"
             (positive-int "a") => nil
             (positive-int "1.0") => nil
             (positive-int "1,0") => nil)
       (fact "it does not allow separators"
             (positive-int "1.000") => nil
             (positive-int "1;000") => nil
             (positive-int "1:000") => nil
             (positive-int "1,000") => nil)
       (fact "it does not parse bigint"
             (positive-int "1000000000000") => nil)
       (fact "it returns nil for negative or 0"
             (positive-int "0") => nil
             (positive-int "-1") => nil
             (positive-int "-100") => nil))

(facts "about `positive-int!`"
       (fact "it returns a positive integer"
             (positive-int! "1") => integer?
             (positive-int! "1") => 1
             (positive-int! "100") => 100
             (positive-int! "9000") => 9000)
       (fact "it throws IllegalArgumentException if unparsable"
             (positive-int! "a") => (throws IllegalArgumentException)
             (positive-int! "1.0") => (throws IllegalArgumentException)
             (positive-int! "1,0") => (throws IllegalArgumentException))
       (fact "it does not allow separators"
             (positive-int! "1.000") => (throws IllegalArgumentException)
             (positive-int! "1;000") => (throws IllegalArgumentException)
             (positive-int! "1:000") => (throws IllegalArgumentException)
             (positive-int! "1,000") => (throws IllegalArgumentException))
       (fact "it does not parse bigint"
             (positive-int! "1000000000000") => (throws IllegalArgumentException))
       (fact "it throws IllegalArgumentException for negative or 0"
             (positive-int! "0") => (throws IllegalArgumentException)
             (positive-int! "-1") => (throws IllegalArgumentException)
             (positive-int! "-100") => (throws IllegalArgumentException)))
