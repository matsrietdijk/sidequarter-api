(defproject sidequarter-api "0.0.0-ALPHA"
  :description "An API backend to monitor multiple Sidekiq instances"
  :plugins [[lein-ring "0.9.7"]
            [lein-kibit "0.1.2"]
            [lein-environ "1.0.1"]
            [migratus-lein "0.2.0"]]
  :ring {:handler sidequarter-api.core/handler}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/java.jdbc "0.4.2"]
                 [org.postgresql/postgresql "9.4-1204-jdbc42"]
                 [liberator "0.13"]
                 [compojure "1.4.0"]
                 [environ "1.0.1"]
                 [migratus "0.8.7"]
                 [yesql "0.5.1"]
                 [com.taoensso/carmine "2.12.0"]
                 [ring/ring-core "1.4.0"]
                 [ring.middleware.jsonp "0.1.6"]]
  :migratus {:store :database})
