(ns turbovote.resource-config-test
  (:require [clojure.test :refer :all]
            [turbovote.resource-config :refer [config]]))

(deftest config-test
  (is (= (config [:startup-message]) "Hello, world!"))
  (is (= (config [:server :host]) "localhost"))
  (is (= (config [:server :port]) 8080)))

(deftest env-test
  (is (re-find #"java" (config [:program]))))

(deftest edn-test
  (is (= [1 {:set #{:a :b}}] (config [:edn]))))

(deftest aero-test
  (is (not-empty (config [:shell])) "environment variable found")
  (is (number? (config [:columns])) "environment variable turned into a long")
  (is (= "default-found" (config [:fallback]))) "combination of #or and #resource-config/env")

(deftest config-missing-test
  (testing "non-existing paths throw an exception"
    (is (thrown? clojure.lang.ExceptionInfo
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
       java.io.FileNotFoundException #"Config file __missing__\.edn not found in resource paths"
       (with-redefs [turbovote.resource-config/config-file-name "__missing__.edn"]
         (config :foo)))))
