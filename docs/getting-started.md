# Getting Started

## Requirements

- A Kotlin Multiplatform project
- Maven Central in dependency resolution
- JDK 21 when building Arch Toolkit from source

## Select the Current Version

Use the latest Maven Central badge shown on the [home page](index.md). All Arch
Toolkit modules use the same release version.

## Add a Dependency

```toml
[versions]
arch-toolkit = "<latest-version>"

[libraries]
storage-core = { module = "io.github.matheus-corregiari:storage-core", version.ref = "arch-toolkit" }
storage-memory = { module = "io.github.matheus-corregiari:storage-memory", version.ref = "arch-toolkit" }
```

```kotlin
kotlin {
    sourceSets.commonMain.dependencies {
        implementation(libs.storage.core)
        implementation(libs.storage.memory)
    }
}
```

For a direct dependency:

```kotlin
commonMain.dependencies {
    implementation("io.github.matheus-corregiari:storage-memory:<latest-version>")
}
```

## Verify the Setup

```kotlin
val storage = MemoryStoreProvider(mutableMapOf())
val enabled = storage.boolean("enabled")

enabled.set(true)
check(enabled.instant() == true)
```

Writes use the `KeyValue` coroutine scope. For production code, prefer
observing `get()` or reading with the suspending `current()` operation when a
blocking instant read is inappropriate.

Next, [choose the modules](using/choosing-modules.md) required by the
application.
