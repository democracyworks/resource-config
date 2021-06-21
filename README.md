# resource-config

# *DEPRECATED* please use [Aero][aero] instead

A Clojure library for reading a *single* EDN configuration file
available in your resources. It wraps [aero][aero] and provides some convenience functions.

[![Build Status](https://travis-ci.com/democracyworks/resource-config.svg?branch=master)](https://travis-ci.com/democracyworks/resource-config)

## Usage

Add to your project's dependencies:

[![Clojars Project](https://img.shields.io/clojars/v/democracyworks/resource-config.svg)](https://clojars.org/democracyworks/resource-config)

Note that previous versions used a `[turbovote/resource-config]` dependency! This must be changed when updating to 1.0.0.

1. Create a `config.edn` file in your classpath.
2. Use resource-config!

```clojure
; config.edn
{:server {:hostname "localhost"
          :port 8080}
 :startup-message "Hello, world!"
 :auth-token #env "AUTH_TOKEN"}
```

```clojure
; core.clj
(ns my-app.core
  (:require [resource-config.core :refer [config]]))

;; this will throw an exception if the value is not in the config
(defn running-locally? []
  (= "localhost" (config [:server :hostname])))

;; you can set a default value like this (never throws exception)
(defn get-database-url []
  (config [:database :url] "postgres://default-db"))
```

### Using it with the mount library

```clojure
(ns my-app.core
  (:require [mount.core :refer [defstate] :as mount]
            [resource-config.core :as rc]))
  
(defstate config
  :start rc/config
  :stop (rc/reload-config!))
  
(mount/start)

(config [:database :url] "postgres://default-db")
```

### Data readers

The following data readers are provided:

* `#resource-config/edn`: The value is read from an [edn][edn] string.

### Set env vars AERO_PROFILE and AERO_ROLE

see test-resources/config.edn for examples and test env vars in project.clj 

In config.edn, with the env var "AERO_PROFILE" set to "a", `#profile {:a 1 :b 2 :default 0}`, will result in `1`. If "AERO_PROFILE" is not set, the result will be the default, `0`. 

The same behavior is true for the `#role` reader macro and the "AERO_ROLE" env var.

## tests

There is a lein plugin that sets environment vars. This allows them to be checked into
the repo. It needs to be invoked, so run the tests like this: 
```
lein with-env-vars test
```

## License

Copyright © 2015-2020 Democracy Works, Inc.

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

[edn]: https://github.com/edn-format/edn
[aero]: https://github.com/juxt/aero
