(ns turbovote.resource-config.cache-test
  (:use [org.httpkit.server :only [run-server]])
  (:require [clojure.test :refer :all]
            [clojure.core.cache :as cache]
            [turbovote.resource-config.cache :refer :all]
            [compojure.core :refer [defroutes GET]]
            [compojure.handler :refer [site]]))

(def string-count (atom 0))

(defn string-handler [req]
  (swap! string-count inc)
  {:status  200
   :headers {"Content-Type" "text/plain"}
   :body    "\"hello\""})

(defn int-handler [req]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "10"})

(def sleepy-count (atom 0))

(defn sleepy-handler [req]
  (swap! sleepy-count inc)
  (Thread/sleep 1000)
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "\"sleepy\""})

(defn timeout-handler [req]
  (Thread/sleep 6000)
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "\"timeout\""})

(defn erroring-handler [req]
  {:status 500})

(defroutes all-routes
  (GET "/string" [] string-handler)
  (GET "/int" [] int-handler)
  (GET "/sleepy" [] sleepy-handler)
  (GET "/erroring" [] erroring-handler))

(defn with-test-server [f]
  (let [stop-fn (run-server (site #'all-routes) {:port 6464})]
    (try
      (f)
      (finally
        (stop-fn)))))

(use-fixtures :once with-test-server)

(defn reset-caches
  ([]
   (reset-caches {} 120000 {}))
  ([ttl-base ttl-ms previous-base]
   (reset! ttl-cache (cache/ttl-cache-factory ttl-base :ttl ttl-ms))
   (reset! previous-cache (cache/basic-cache-factory previous-base))))

(deftest simple-lookup-test
  (reset-caches)
  (testing "first returns the default value"
    (is (= "default" (lookup {:url "http://localhost:6464/string"
                              :default "default"}))))
  (testing "second lookup should get actual value that's now loaded"
    (Thread/sleep 1000)
    (is (= "hello" (lookup {:url "http://localhost:6464/string"
                            :default "default"})))))

(deftest parse-edn-lookup-test
  (reset-caches)
  (testing "first returns the default value"
    (is (= 5 (lookup {:url "http://localhost:6464/int"
                      :default 5}))))
  (testing "second lookup should get actual value that's loaded and parsed"
    (Thread/sleep 1000)
    (is (= 10 (lookup {:url "http://localhost:6464/int"
                       :default 5})))))

(deftest previous-lookup-test
  (reset-caches {} 120000 {"http://localhost:6464/erroring" "previous"})
  (testing "returns the previous value because ttl-cache is missing it"
    (is (= "previous" (lookup {:url "http://localhost:6464/erroring"
                               :default "default"}))))
  (testing "continues to return previous value because error is ignored"
    (Thread/sleep 1000)
    (is (= "previous" (lookup {:url "http://localhost:6464/erroring"
                               :default "default"})))))

(deftest timeout-lookup-test
  (reset-caches)
  (testing "returns the default value on timeout"
    (is (= "default" (lookup {:url "http://localhost:6464/timeout"
                               :default "default"}))))
  (testing "continues to return default value, timeout never returned a value"
    (Thread/sleep 5500)
    (is (= "default" (lookup {:url "http://localhost:6464/timeout"
                              :default "default"})))))

(deftest limit-retrieval-test
  (reset-caches)
  (reset! sleepy-count 0)
  (testing "returns the default value on sleepy"
    (is (= "default" (lookup {:url "http://localhost:6464/sleepy"
                              :default "default"})))
    (Thread/sleep 500)
    (is (= 1 @sleepy-count)))
  (testing "second call also returns default value"
    (is (= "default" (lookup {:url "http://localhost:6464/sleepy"
                              :default "default"})))
    (Thread/sleep 1000)
    (is (= 1 @sleepy-count)))
  (testing "returns the sleepy value, sleepy count doesn't get bigger"
    (is (= "sleepy" (lookup {:url "http://localhost:6464/sleepy"
                             :default "default"})))
    (is (= 1 @sleepy-count))))

(deftest ttl-test
  (reset! string-count 0)
  (reset-caches {} 1000 {})
  (testing "returns default value on first call"
    (is (= "default" (lookup {:url "http://localhost:6464/string"
                              :default "default"})))
    (Thread/sleep 200)
    (is (= 1 @string-count)))
  (testing "returns hello on second call"
    (is (= "hello" (lookup {:url "http://localhost:6464/string"
                            :default "default"})))
    (Thread/sleep 200)
    (is (= 1 @string-count)))
  (testing "after ttl expires, still get previous value, call count goes up"
    (Thread/sleep 1000)
    (is (= "hello" (lookup {:url "http://localhost:6464/string"
                            :default "default"})))
    (Thread/sleep 200)
    (is (= 2 @string-count))))
