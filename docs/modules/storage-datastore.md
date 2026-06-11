# Storage DataStore

Artifact: `io.github.matheus-corregiari:storage-datastore`

Provides persistent storage backed by AndroidX DataStore Preferences.

- Android, JVM, and Apple use the DataStore implementation.
- JS and WasmJS retain the common API through a no-op fallback.

```kotlin
val storage = DataStoreProvider(dataStore)
val accepted = storage.boolean("accepted")
accepted.set(true)
```
