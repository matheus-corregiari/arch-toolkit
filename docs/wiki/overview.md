# Arch Toolkit overview

Arch Toolkit maintains Splinter, a Kotlin Multiplatform asynchronous loading library.
Only Splinter and its platform variants are published here. Test helpers stay internal.

Splinter supports Android, JVM, JS, Wasm, iOS ARM64 and iOS Simulator ARM64.
The sample runs on Android and desktop JVM. Apple builds require macOS and Xcode.

State management is maintained by [Arch Event Observer](https://github.com/matheus-corregiari/arch-event-observer).
See [State migration](../state-migration.md) for the rc18 changes.
Storage is maintained by [Arch Storage](https://github.com/matheus-corregiari/arch-storage);
see [Storage migration](../storage-migration.md).

## Installation

```kotlin
implementation("io.github.matheus-corregiari:splinter:2.0.0-rc18")
```

This is the candidate prepared by this branch, not a claim of publication.
See [Dependencies](../dependencies.md), [Changelog](../CHANGELOG.md),
[Contribution guide](contribution-guide.md) and [Artifact publishing](artifact-publishing.md).
