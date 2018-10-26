# Change Log

## Changes between resource-config 0.2.1 and 1.0.0-SNAPSHOT

### Added a `resource-config.core/reload-config!` fn

This resets the internal config state so that subsequent calls to
`resource-config.core/config` will pick up changes in your config file. This is
handy for integrating with application lifecycle managers like
[component](https://github.com/stuartsierra/component),
[integrant](https://github.com/weavejester/integrant),
and [mount](https://github.com/tolitius/mount).

### Renamed the library to democracyworks/resource-config

**This is a breaking change**

Renamed the library to democracyworks/resource-config and removed
`turbovote` from the namespaces. This is how our other libraries
are named and organized, but we hadn't gotten around to this one yet.

To use this version, change your lein dependency from:
`[turbovote.resource-config "n.n.n"]` to:
`[democracyworks/resource-config "1.0.0"]`.
...and then change your requires from:
`[turbovote.resource-config :refer [config]]` to:
`[resource-config.core :refer [config]]`.

## Changes between resource-config 0.2.0 and 0.2.1

`require` syntax fix to ensure compatibility with clojure.spec/Clojure 1.9

## Changes between resource-config 0.1.4 and 0.2.0

### `config` takes a sequence of keys and an optional default value

**This is a breaking change**

For example, `(config :server :port)` is now
`(config [:server :port])`. A default value can be provided as an
optional second argument if there's no value at the key path. For
example, `(config [:server :port] 80)`. Note that the default value
will not be returned if the configured value is explicitly `nil` or
`false`.

### `config` without a default value will throw if no value is configured

**This is a breaking change**

Previously, `nil` would be returned if there was no value in the
configuration. Now, an `ExceptionInfo` exception will be thrown, with
some info about the configuration and the lookup keys.
