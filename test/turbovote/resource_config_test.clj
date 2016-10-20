(ns turbovote.resource-config-test
  (:use [org.httpkit.server :only [run-server]])
  (:require [clojure.test :refer :all]
            [turbovote.resource-config :refer [config]]
            [turbovote.resource-config.cache-test :refer [reset-caches]]
            [compojure.core :refer [defroutes GET]]
            [compojure.handler :refer [site]]))

(deftest config-test
  (is (= (config [:startup-message]) "Hello, world!"))
  (is (= (config [:server :host]) "localhost"))
  (is (= (config [:server :port]) 8080)))

(deftest env-test
  (is (re-find #"java" (config [:program]))))

(deftest edn-test
  (is (= [1 {:set #{:a :b}}] (config [:edn]))))

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

(def remote-count (atom 0))

(defroutes all-routes
  (GET "/remote" [] (fn [_]
                      (swap! remote-count inc)
                      {:status 200
                       :headers {"Content-Type" "text/plain"}
                       :body "\"remote\""})))

(deftest remote-test
  (reset! remote-count 0)
  (reset-caches)
  (let [stop-fn (run-server #'all-routes {:port 6463})]
    (try
      (testing "first access returns default"
        (is (= "default" (config [:remote]))))
      (testing "after loaded, returns remote value"
        (Thread/sleep 1000)
        (is (= 1 @remote-count))
        (is (= "remote" (config [:remote]))))
      (finally
        (stop-fn)))))
