# resource-config

A Clojure library for reading a *single* EDN configuration file
available in your resources.

## Usage

Add to your project.clj's dependencies:

```clojure
[turbovote.resource-config "0.2.0"]
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
* `#resource-config/url`: Expects a map with two entries, `:url` is the URL to GET the config
  value from, and `:default` is the value to return when the lookup fails or hasn't yet
  completed. It'll always be the first value returned, after that, the last successful
  value returned from the `:url` location is returned, and refreshed about every 2 minutes.
  The value is run through `edn/read-string` to facilitate returning things other than
  just strings.

[edn]: https://github.com/edn-format/edn

### Reloadable URL Config Values

When the config includes a `#resource-config/url` tagged map, it will attempt
to load a config value from the included `:url` location via a GET call.

The first time this config key is requested, it'll return the corresponding
`:default` value rather than wait on an uncertain network load time. If the
request times out, or returns an error code, the value is ignored, and a new
lookup can happen the next time the config key is requested.

After a successful value has been retrieved, on subsequent lookups, that value
is returned until such time as a new successful value is retrieved. Values are
fresh for 2 minutes, at which point the next lookup will intiate a new retrieval.

```clojure
; config.edn
{:batch-size #resource-config/url {:url "http://myserver.com/batch-size"
                                   :default 10}}

(config [:batch-size])
; => 10
; GET "http://myserver.com/batch-size" => {:body "20"}

(config [:batch-size])
; => 20
; No HTTP retrieval

; one minute passes
(config [:batch-size])
; => 20
; No HTTP retrieval

; one minute passes, ttl reached
(config [:batch-size])
; => 20
; GET "http://myserver.com/batch-size" => {:body "30"}

; two minutes pass, ttl reached
(config [:batch-size])
; => 30
; GET "http://myserver.com/batch-size" => {:status 500}

; one minute passes
(config [:batch-size])
; => 30
; GET "http://myserver.com/batch-size" => {:body "20"}

(config [:batch-size])
; => 20
; No HTTP retrieval
```

## License

Copyright © 2015 TurboVote

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
