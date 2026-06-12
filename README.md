# Arch Toolkit

[![Build](https://github.com/matheus-corregiari/arch-toolkit/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/matheus-corregiari/arch-toolkit/actions/workflows/build.yml)
[![Latest tag](https://img.shields.io/github/v/tag/matheus-corregiari/arch-toolkit?sort=semver&label=latest%20tag)](https://github.com/matheus-corregiari/arch-toolkit/tags)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.matheus-corregiari/storage-core)](https://central.sonatype.com/search?q=io.github.matheus-corregiari)
[![Coverage](https://img.shields.io/codecov/c/github/matheus-corregiari/arch-toolkit)](https://codecov.io/gh/matheus-corregiari/arch-toolkit)
[![License](https://img.shields.io/github/license/matheus-corregiari/arch-toolkit)](LICENSE.md)

Focused Kotlin Multiplatform libraries for reactive storage, restorable state,
and request orchestration.

## Modules

| Artifact | Purpose |
|:---------|:--------|
| `storage-core` | Reactive storage contracts |
| `storage-memory` | In-memory storage |
| `storage-datastore` | AndroidX DataStore preferences |
| `state-handle` | Typed `SavedStateHandle` delegates |
| `splinter` | Request and polling orchestration |

## Installation

Use the Maven Central badge above as the current published version:

```kotlin
commonMain.dependencies {
    implementation("io.github.matheus-corregiari:storage-core:<latest-version>")
}
```

Use one version for all Arch Toolkit artifacts.

## Documentation

- [Using Arch Toolkit](https://matheus-corregiari.github.io/arch-toolkit/getting-started/)
- [Module guides](https://matheus-corregiari.github.io/arch-toolkit/modules/)
- [API reference](https://matheus-corregiari.github.io/arch-toolkit/api/)
- [Contributing](CONTRIBUTING.md)

## License

Apache License 2.0. See [LICENSE.md](LICENSE.md).
