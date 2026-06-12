# Arch Toolkit

[![Latest tag](https://img.shields.io/github/v/tag/matheus-corregiari/arch-toolkit?sort=semver&label=latest%20tag)](https://github.com/matheus-corregiari/arch-toolkit/tags)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.matheus-corregiari/storage-core?label=Maven%20Central)](https://central.sonatype.com/search?q=io.github.matheus-corregiari)

Arch Toolkit is a set of focused Kotlin Multiplatform libraries for reactive
storage, restorable state, and request orchestration. It keeps these concerns
small and composable so applications can adopt one module without adopting a
framework.

The documentation always represents the current `master` branch. The badges
above show the latest repository tag and the latest version available from
Maven Central.

## Modules

| Module | Purpose |
|:-------|:--------|
| [`storage-core`](modules/storage-core.md) | Reactive key-value contracts and typed adapters |
| [`storage-memory`](modules/storage-memory.md) | Process-local storage for tests and temporary state |
| [`storage-datastore`](modules/storage-datastore.md) | Persistent preferences backed by AndroidX DataStore |
| [`state-handle`](modules/state-handle.md) | Typed delegates over `SavedStateHandle` |
| [`splinter`](modules/splinter.md) | One-shot, polling, and mirrored-flow orchestration |

## Supported Targets

The common target set is Android, JVM, iOS arm64, iOS simulator arm64,
JavaScript, and WasmJS. `storage-datastore` is operational only on Android, JVM,
and Apple targets because AndroidX DataStore does not provide the web backend
used by this project.

## Start Here

- [Install Arch Toolkit](getting-started.md)
- [Choose the right module](using/choosing-modules.md)
- [Review target compatibility](using/compatibility.md)
- [Explore the sample application](sample/index.md)
- [Contribute to the project](contributing.md)
- [Browse the API reference](api/index.md)
