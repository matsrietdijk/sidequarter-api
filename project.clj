(defproject sidequarter-api "0.0.0-ALPHA"
  :description "An API backend to monitor multiple Sidekiq instances"
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler sidequarter-api.core/handler}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [liberator "0.13"]
                 [compojure "1.4.0"]
                 [ring/ring-core "1.4.0"]])
