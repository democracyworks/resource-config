(ns turbovote.resource-config.cache
  (:require [clj-http.client :as http]
            [clojure.core.cache :as cache]
            [clojure.edn :as edn]))

(def in-retrieval (atom #{}))

(def previous-cache (atom (cache/basic-cache-factory {})))

(def ttl-cache (atom (cache/ttl-cache-factory {} :ttl 120000)))

(defn retrieve-config
  "Makes an HTTP GET call to the url and when successful, parses
  the response as EDN with the usual data readers, minus the url reader."
  [url]
  (try
    (let [response (http/get url {:conn-timeout 5000
                                  :socket-timeout 5000})]
      (if (http/success? response)
        (edn/read-string {:readers *data-readers*}
                         (:body response))))
    (catch Exception ex
      ;;treat as a failed lookup
      nil)))

(defn add-to-cache
  "Checks to see if the url is already being retrieved, and if not, kicks
  off the retrieval. When a value comes back, it refreshes both caches. Whether
  or not a value comes back, it removes the url from the in-retrieval set when
  done."
  [url]
  (let [current-retrievals (set @in-retrieval)]
    (when-not (= (swap! in-retrieval conj url) current-retrievals)
      (when-let [new-value (retrieve-config url)]
        (swap! ttl-cache #(cache/miss % url new-value))
        (swap! previous-cache #(cache/miss % url new-value)))
      (swap! in-retrieval disj url))))

(defn lookup
  "Looks up the config value associated with the url. The config obtained
  will be retrained for 2 minutes in the primary cache, at which point the
  next lookup will kick off a refresh for the next lookup, and the previous
  value will be used until the new value is ready. If no previous value has
  yet been set, the default value will be returned."
  [{:keys [url default] :as url-spec}]
  (if-let [value (cache/lookup @ttl-cache url)]
    value
    (do
      (future (add-to-cache url))
      (if-let [previous-value (cache/lookup @previous-cache url)]
        previous-value
        default))))
