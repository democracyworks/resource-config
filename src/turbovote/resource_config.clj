(ns turbovote.resource-config
  (:require [turbovote.resource-config.data-readers]
            [turbovote.resource-config.cache :as cache]
            [clojure.edn :as edn]
            [clojure.java.io :as io]))

(def config-file-name "config.edn")

(def read-config
  (memoize
   (fn [config-file]
     (if-let [file (io/resource config-file)]
       (with-open [r (io/reader file)]
         (edn/read {:readers *data-readers*} (java.io.PushbackReader. r)))
       (throw (java.io.FileNotFoundException.
               (str "Config file " config-file " not found in resource paths.")))))))

(defn config
  "Read a value from the configuration file at `keys` (`keys` is a
  path of keys as used in `get-in`). There are two arities.

  If the file does not exist, throws a FileNotFoundException.

  If a value at `keys` does exist, returns it.

  If a value at `keys` does not exist and no `default` is given (single
  argument), throws an exception.

  If a value at `keys` does not exist and a `default` is given,
  returns the default."
  ([] (config []))
  ([keys]
   (let [config (read-config config-file-name)
         value (get-in config keys ::not-found)]
     (when (= ::not-found value)
       (throw (ex-info
               (str "Hey! I went looking for these keys ("
                    (pr-str keys)
                    ") in the config "
                    "and I couldn't find them.")
               {:keys keys
                :config config
                :config-uri (-> config-file-name
                                io/resource
                                str)})))
     (if (:url-spec (meta value))
       (cache/lookup value)
       value)))
  ([keys default]
   (let [value (get-in (read-config config-file-name) keys default)]
     (if (:url-spec (meta value))
       (cache/lookup value)
       value))))
