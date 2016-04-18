# Change Log

## Changes between resource-config 0.2.0 and HEAD

### renamed

**This is a breaking change**

The project has been renamed from `turbovote.resource-config` to
`democracyworks/resource-config`. This means your project.clj dependency should
become: `[democracyworks/resource-config "version"]` and your code requires
should become: `[resource-config.core :refer [config]]`.

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
