# Arch Toolkit

[![Build](https://github.com/matheus-corregiari/arch-toolkit/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/matheus-corregiari/arch-toolkit/actions/workflows/build.yml)
[![Lint](https://github.com/matheus-corregiari/arch-toolkit/actions/workflows/lint.yml/badge.svg?branch=master)](https://github.com/matheus-corregiari/arch-toolkit/actions/workflows/lint.yml)
[![Coverage](https://img.shields.io/codecov/c/github/matheus-corregiari/arch-toolkit)](https://codecov.io/gh/matheus-corregiari/arch-toolkit)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.matheus-corregiari/storage-core)](https://central.sonatype.com/search?q=io.github.matheus-corregiari)
[![License](https://img.shields.io/github/license/matheus-corregiari/arch-toolkit)](LICENSE.md)

Focused Kotlin Multiplatform utilities for storage, state, and request orchestration.

## Modules

| Artifact | Purpose | Targets |
|:---------|:--------|:--------|
| `storage-core` | Reactive key-value contracts and delegates | Android, JVM, Apple, JS, WasmJS |
| `storage-datastore` | Persistent AndroidX DataStore provider | Android, JVM, Apple; no-op web fallback |
| `storage-memory` | In-memory storage provider | Android, JVM, Apple, JS, WasmJS |
| `state-handle` | Restorable state delegates | Android, JVM, Apple, JS, WasmJS |
| `splinter` | Request orchestration strategies | Android, JVM, Apple, JS, WasmJS |

The `event-observer` artifacts are maintained in the
[Arch Event Observer](https://github.com/matheus-corregiari/arch-event-observer) repository.

## Installation

Use the same release for all Arch Toolkit artifacts:

```toml
[versions]
arch-toolkit = "2.0.0-rc14"

[libraries]
storage-core = { module = "io.github.matheus-corregiari:storage-core", version.ref = "arch-toolkit" }
storage-datastore = { module = "io.github.matheus-corregiari:storage-datastore", version.ref = "arch-toolkit" }
storage-memory = { module = "io.github.matheus-corregiari:storage-memory", version.ref = "arch-toolkit" }
state-handle = { module = "io.github.matheus-corregiari:state-handle", version.ref = "arch-toolkit" }
splinter = { module = "io.github.matheus-corregiari:splinter", version.ref = "arch-toolkit" }
```

```kotlin
kotlin {
    sourceSets.commonMain.dependencies {
        implementation(libs.storage.core)
        implementation(libs.storage.datastore)
    }
}
```

## Development

Use JDK 21 and the project wrapper:

```bash
./gradlew ciBuild
./gradlew ciTest
./gradlew ciLint
./gradlew ciCoverage
./gradlew ciDocs
python -m mkdocs build --strict
```

Samples are validated separately:

```bash
./gradlew -PincludeSamples=true ciSamples
```

See the [documentation site](https://matheus-corregiari.github.io/arch-toolkit/)
and [contribution guide](CONTRIBUTING.md) for details.

## License

Apache License 2.0. See [LICENSE.md](LICENSE.md).
