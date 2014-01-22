# resource-config

A Clojure library for reading a *single* EDN configuration file
available in your resources.

## Usage

Add to your project.clj's dependencies:

```clojure
[turbovote.resource-config "0.1.0"]
```

1. Create a `config.edn` file in your classpath.
2. Use turbovote.resource-config!

```clojure
# config.edn
{:server {:hostname "localhost"
          :port 8080}
 :startup-message "Hello, world!"}
```

```clojure
# core.clj
(ns my-app.core
  (:require [turbovote.resource-config :refer [config]]))

(defn running-locally? []
  (= "localhost" (config :server :hostname)))
```

## License

Copyright © 2014 TurboVote

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
