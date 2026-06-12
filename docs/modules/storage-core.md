# Storage Core

Artifact: `io.github.matheus-corregiari:storage-core`

## Purpose

`storage-core` defines the reactive `StorageProvider` and `KeyValue` contracts
used by Arch Toolkit storage backends. It lets application code depend on typed
keys without knowing where values are stored.

## Use It When

- A shared API must accept a storage abstraction.
- Production and test backends must be interchangeable.
- Values need Flow observation, delegates, or Compose state adapters.

Do not use it alone when you need a ready backend. Add `storage-memory`,
`storage-datastore`, or your own `StorageProvider`.

## Installation

```kotlin
commonMain.dependencies {
    implementation("io.github.matheus-corregiari:storage-core:<latest-version>")
}
```

Current versions are shown on the [home page](../index.md).

## Targets

Android, JVM, iOS arm64, iOS simulator arm64, JS, and WasmJS.

## Concepts and Behavior

Providers create typed keys for primitives, enums, and serialized models.
`KeyValue<T>` exposes reactive reads with `get()`, suspending reads with
`current()`, writes with `set(...)`, and adapters such as `required`, `default`,
and `map`.

`instant()` performs a platform-specific immediate read. Prefer Flow or
`current()` in asynchronous application paths.

## Example

```kotlin
val userName = storage.string("user-name").default { "Guest" }

userName.set("Ada")
check(userName.instant() == "Ada")

scope.launch {
    userName.get().collect(::renderName)
}
```

## Architecture

The module has no persistence backend. `storage-memory` and
`storage-datastore` implement its contracts, which keeps callers independent
from storage choice.

## Known Limitations

- `set` uses a coroutine scope and may complete after the call returns.
- Immediate reads have platform-specific blocking behavior.
- Model compatibility depends on the configured JSON serializer and stored
  schema.

## Troubleshooting

If a write is lost, verify that the key's coroutine scope remains active. If a
model cannot be decoded after an update, provide a compatible `Json`
configuration or migrate the stored value.

[Open the Storage Core API reference](../api/storage-core/html/index.html).
