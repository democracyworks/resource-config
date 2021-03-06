(ns resource-config.core-test
  (:require [clojure.test :refer :all]
            [resource-config.core :refer [config reload-config!]])
  (:import (java.io FileNotFoundException)
           (clojure.lang ExceptionInfo)))

(deftest config-test
  (is (= (config [:startup-message]) "Hello, world!"))
  (is (= (config [:server :host]) "localhost"))
  (is (= (config [:server :port]) 8080)))

(deftest edn-env-test
  (testing "edn can be used after the env reader"
    (is (number? (config [:edn-env-reader])))))

(deftest edn-test
  (is (= [1 {:set #{:a :b}}] (config [:edn]))))

(deftest aero-test
  (is (not-empty (config [:shell])) "environment variable found")
  (is (number? (config [:columns])) "environment variable turned into a long")
  (is (= "default-found" (config [:fallback]))) "#or reader and a default")

(deftest aero-profile
  (is (= :a-default (config [:profile])) "uses default")
  (is (= :not-default (config [:profile2])) "uses AERO_PROFILE env var"))

(deftest config-missing-test
  (testing "non-existing paths throw an exception"
    (is (thrown? ExceptionInfo
                 (config [:some :random :path :that :does :not :exist]))))
  (testing "false and nil in the config does not trigger exception"
    (is (false? (config [:test :false])))
    (is (nil? (config [:test :nil])))))

(deftest config-default-arity
  (testing "can add default to config"
    (is (= (config [:startup-message] "Hey, dude!") "Hello, world!"))
    (is (= ::default (config [:some :random :path :that :does :not :exist]
                             ::default)))))

(deftest missing-config-test
  (is (thrown-with-msg?
       FileNotFoundException #"Config file __missing__\.edn not found in resource paths"
       (with-redefs [resource-config.core/config-file-name "__missing__.edn"]
         (config :foo)))))

(deftest reloaded-config-test
  (is (= (config [:startup-message]) "Hello, world!"))
  (reload-config!)
  (with-redefs [resource-config.core/config-file-name "other-config.edn"]
    (is (= (config [:startup-message]) "Hello, upside down!"))))
