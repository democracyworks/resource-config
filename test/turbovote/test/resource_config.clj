(ns turbovote.test.resource-config
  (:require [clojure.test :refer :all]
            [turbovote.resource-config :refer [config]]))

(deftest config-test
  (is (= (config :startup-message) "Hello, world!"))
  (is (= (config :server :host) "localhost"))
  (is (= (config :server :port) 8080)))

(deftest env-test
  (is (re-find #"java" (config :program))))

(deftest missing-config-test
  (is (thrown-with-msg?
       Exception #"Config file __missing__\.edn not found in resource paths"
       (with-redefs [turbovote.resource-config/config-file-name "__missing__.edn"]
         (config :foo)))))
