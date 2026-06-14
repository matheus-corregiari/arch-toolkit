# Storage Memory

Artifact: `io.github.matheus-corregiari:storage-memory`

## Purpose

`storage-memory` implements `StorageProvider` with `MutableStateFlow` values
held in a caller-owned map.

## Use It When

- Tests need a deterministic storage backend.
- Previews or prototypes need temporary reactive values.
- A web target needs an alternative to `storage-datastore`.

Do not use it for values that must survive process termination.

## Installation

```kotlin
commonMain.dependencies {
    implementation("io.github.matheus-corregiari:storage-memory:<latest-version>")
}
```

## Targets

Android, JVM, iOS arm64, iOS simulator arm64, JS, and WasmJS.

## Concepts and Behavior

Each key maps to one `MutableStateFlow`. Multiple keys from the same provider
share the supplied map. Primitive, enum, and model APIs match `storage-core`;
model conversion functions are not needed because values stay in memory.

## Example

```kotlin
val database = mutableMapOf<String, MutableStateFlow<*>>()
val storage = MemoryStoreProvider(database)
val counter = storage.int("counter").required { 0 }

counter.set(counter.instant() + 1)
```

## Architecture

The provider is intentionally thin: the map owns identity and the state flow
owns observation. Supplying the same map to another provider exposes the same
in-process values.

## Known Limitations

- Values disappear with the process.
- The caller controls map lifetime and synchronization.
- It is not a durable cache or database.

## Troubleshooting

If state unexpectedly resets, verify that the map and provider are not being
recreated. If tests leak state, create a fresh map per test.

[Open the Storage Memory API reference](../api/storage-memory/html/index.html).
