(ns resource-config.data-readers
  (require [clojure.edn :as edn]))

(defn env [variable]
  (System/getenv variable))

(defn edn [str]
  (edn/read-string {:readers *data-readers*} str))
