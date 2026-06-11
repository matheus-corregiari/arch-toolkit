# Storage Core

Artifact: `io.github.matheus-corregiari:storage-core`

Defines the storage contracts used by the persistent and in-memory providers.
It includes typed values, immediate reads, Flow observation, delegates, and
Compose state integration.

```kotlin
val name = storage.string("name")
name.set("Ada")
println(name.instant())
```
