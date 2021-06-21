(ns resource-config.core
  (:require [clojure.core.memoize :as memo]
            [aero.core :as a]
            [aero.alpha.core]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [resource-config.data-readers])
  (:import (java.io FileNotFoundException PushbackReader)))

(def config-file-name "config.edn")

(defmethod a/reader 'role
  [opts tag value]
  (get value (keyword (System/getenv "AERO_ROLE"))
       (get value :default)))

(def read-config
  (memo/memo
   (fn [config-file]
     (if-let [file (io/resource config-file)]
       (a/read-config file {:profile (keyword (System/getenv "AERO_PROFILE"))})
       (throw (java.io.FileNotFoundException.
               (str "Config file " config-file " not found in resource paths.")))))))

(defn reload-config! []
  (memo/memo-clear! read-config [config-file-name]))

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
         val (get-in config keys ::not-found)]
     (when (= ::not-found val)
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
     val))
  ([keys default]
   (get-in (read-config config-file-name) keys default)))
