# Storage DataStore

Artifact: `io.github.matheus-corregiari:storage-datastore`

## Purpose

`storage-datastore` implements `StorageProvider` with AndroidX DataStore
Preferences for durable, reactive key-value storage.

## Use It When

- Preferences must survive process restarts.
- Android, JVM, and Apple clients should share one storage contract.
- Existing code already creates a platform DataStore instance.

Do not construct this provider on JS or WasmJS. Do not use preferences for
relational data, large payloads, or transactional domain models.

## Installation

```kotlin
commonMain.dependencies {
    implementation("io.github.matheus-corregiari:storage-datastore:<latest-version>")
}
```

## Targets

Operational on Android, JVM, iOS arm64, and iOS simulator arm64. JS and WasmJS
publish a compile-time stub whose factory throws `IllegalStateException`.

## Concepts and Behavior

Primitive values map to DataStore preference keys. Enums use their names.
Models are encoded as JSON strings. Reads observe `DataStore.data`; writes use
DataStore's asynchronous edit transaction.

## Example

```kotlin
val storage = DataStoreProvider(dataStore)
val acceptedTerms = storage.boolean("accepted-terms").required { false }

acceptedTerms.set(true)
```

Create `dataStore` in platform code, then expose the provider through the
shared `StorageProvider` type.

## Architecture

The operational implementation lives in shared Android/JVM/Apple source sets.
Web source sets retain the common factory signature but deliberately fail at
runtime instead of pretending persistence exists.

## Known Limitations

- No operational JS or WasmJS backend.
- Model schema changes can make existing JSON unreadable.
- DataStore is designed for preferences, not complex datasets.

## Troubleshooting

If web construction fails, bind `MemoryStoreProvider` or another web provider.
If decoding fails after a model change, migrate or clear the stored JSON.

[Open the Storage DataStore API reference](../api/storage-datastore/html/index.html).
