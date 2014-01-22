(ns turbovote.test.resource-config
  (:require [clojure.test :refer :all]
            [turbovote.resource-config :refer [config]]))

(deftest config-test
  (is (= (config :startup-message) "Hello, world!"))
  (is (= (config :server :host) "localhost"))
  (is (= (config :server :port) 8080)))
