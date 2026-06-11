# Storage Core

[![Maven Central](https://img.shields.io/maven-central/v/io.github.matheus-corregiari/storage-core)](https://central.sonatype.com/artifact/io.github.matheus-corregiari/storage-core)

Reactive storage contracts shared by the Arch Toolkit storage providers.

```kotlin
implementation("io.github.matheus-corregiari:storage-core:<version>")
```

```kotlin
val name = storage.string("name")
name.set("Ada")
println(name.instant())
```

See the [module documentation](../../../docs/modules/storage-core.md).
