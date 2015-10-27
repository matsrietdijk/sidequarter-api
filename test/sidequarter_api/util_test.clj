(ns sidequarter-api.util-test
  (:require [midje.sweet :refer :all]
            [sidequarter-api.util :refer :all]))

(facts "about `->valid-id`"
       (fact "it returns nil with 0 or invalid value"
             (->valid-id -1) => nil
             (->valid-id 0) => nil
             (->valid-id "1a") => nil
             (->valid-id "01") => nil
             (->valid-id "a") => nil)
       (fact "it returns a valid value as int"
             (->valid-id "1") => integer?
             (->valid-id "1") => 1
             (->valid-id "10") => 10
             (->valid-id "789") => 789
             (->valid-id 1) => 1))

(facts "about `->int`"
       (fact "it returns 0 for unconvertable values"
             (->int "a") => 0
             (->int "a1") => 0)
       (fact "it returns a convertable value as int"
             (->int 1) => integer?
             (->int 1) => 1
             (->int "1") => 1
             (->int "-1") => -1
             (->int "1a") => 1
             (->int "10") => 10
             (->int "789") => 789)
       (fact "it only converts until non-numerical character"
             (->int "1,000") => 1
             (->int "1.000") => 1
             (->int "1;000") => 1))
