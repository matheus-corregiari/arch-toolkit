# Getting Started

## Requirements

- JDK 21
- Kotlin Multiplatform project
- Maven Central

## Add Dependencies

```toml
[versions]
arch-toolkit = "2.0.0-rc14"

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

Use one Arch Toolkit version across all modules.

## Choose a Provider

- Use `storage-memory` for tests and process-local state.
- Use `storage-datastore` for persistent preferences.
- Depend on `storage-core` when exposing storage contracts.
