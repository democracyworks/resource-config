(ns turbovote.resource-config.data-readers)

(defn env [variable]
  (System/getenv variable))
