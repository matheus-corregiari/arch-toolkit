# Storage DataStore

[![Maven Central](https://img.shields.io/maven-central/v/io.github.matheus-corregiari/storage-datastore)](https://central.sonatype.com/artifact/io.github.matheus-corregiari/storage-datastore)

Persistent `StorageProvider` backed by AndroidX DataStore Preferences.

```kotlin
implementation("io.github.matheus-corregiari:storage-datastore:<version>")
```

Android, JVM, and Apple use DataStore. JS and WasmJS expose the common API
through a no-op fallback.

See the [module documentation](../../../docs/modules/storage-datastore.md).
