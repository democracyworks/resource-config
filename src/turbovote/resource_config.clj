(ns turbovote.resource-config
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))

(def read-config
  (memoize
   (fn []
     (let [file (io/resource "config.edn")]
       (with-open [r (io/reader file)]
         (edn/read (java.io.PushbackReader. r)))))))

(defn config [& keys]
  (get-in (read-config) keys))
