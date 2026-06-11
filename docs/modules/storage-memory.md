# Storage Memory

Artifact: `io.github.matheus-corregiari:storage-memory`

Provides an in-memory `StorageProvider` for tests, previews, prototypes, and
temporary state.

```kotlin
val storage = MemoryStoreProvider(mutableMapOf())
```

Values are lost when the process ends.
