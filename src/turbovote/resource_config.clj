(ns turbovote.resource-config
  (:require [turbovote.resource-config.data-readers]
            [clojure.edn :as edn]
            [clojure.java.io :as io]))

(def config-file-name "config.edn")

(def read-config
  (memoize
   (fn [config-file]
     (if-let [file (io/resource config-file)]
       (with-open [r (io/reader file)]
         (edn/read {:readers *data-readers*} (java.io.PushbackReader. r)))
       (throw (Exception. (str "Config file " config-file " not found in resource paths.")))))))

(defn config [& keys]
  (get-in (read-config config-file-name) keys))
