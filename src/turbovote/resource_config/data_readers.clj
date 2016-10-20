(ns turbovote.resource-config.data-readers
  (require [clojure.edn :as edn]))

(defn env [variable]
  (System/getenv variable))

(defn edn [str]
  (edn/read-string {:readers *data-readers*} str))

(defn url [url-spec]
  {:pre [(and (contains? url-spec :url)
              (contains? url-spec :default))]}
  (with-meta url-spec {:url-spec true}))
