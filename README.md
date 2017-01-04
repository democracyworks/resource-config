# resource-config

A Clojure library for reading a *single* EDN configuration file
available in your resources.

## Usage

Add to your project.clj's dependencies:

```clojure
[turbovote.resource-config "0.2.1"]
```

1. Create a `config.edn` file in your classpath.
2. Use turbovote.resource-config!

```clojure
; config.edn
{:server {:hostname "localhost"
          :port 8080}
 :startup-message "Hello, world!"
 :auth-token #resource-config/env "AUTH_TOKEN"}
```

```clojure
; core.clj
(ns my-app.core
  (:require [turbovote.resource-config :refer [config]]))

;; this will throw an exception if the value is not in the config
(defn running-locally? []
  (= "localhost" (config [:server :hostname])))

;; you can set a default value like this (never throws exception)
(defn get-database-url []
  (config [:database :url] "postgres://default-db"))
```

### Data readers

The following data readers are provided:

* `#resource-config/env`: The value is set from the environment
  variable named.
* `#resource-config/edn`: The value is read from an [edn][edn] string.

[edn]: https://github.com/edn-format/edn

## License

Copyright © 2015 TurboVote

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
