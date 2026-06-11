# Storage Memory

[![Maven Central](https://img.shields.io/maven-central/v/io.github.matheus-corregiari/storage-memory)](https://central.sonatype.com/artifact/io.github.matheus-corregiari/storage-memory)

In-memory `StorageProvider` for tests, previews, and temporary state.

```kotlin
implementation("io.github.matheus-corregiari:storage-memory:<version>")
```

```kotlin
val storage = MemoryStoreProvider(mutableMapOf())
```

See the [module documentation](../../../docs/modules/storage-memory.md).
