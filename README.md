# Arch Toolkit

Kotlin Multiplatform utilities for asynchronous loading with Splinter.

[![CI](https://github.com/matheus-corregiari/arch-toolkit/actions/workflows/pull-request.yml/badge.svg)](https://github.com/matheus-corregiari/arch-toolkit/actions/workflows/pull-request.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.matheus-corregiari/splinter)](https://central.sonatype.com/artifact/io.github.matheus-corregiari/splinter)
[![License](https://img.shields.io/github/license/matheus-corregiari/arch-toolkit)](LICENSE.md)

## Modules

| Module | Artifact | Status |
| --- | --- | --- |
| Splinter | `io.github.matheus-corregiari:splinter` | `2.0.0-rc18` candidate |
| Test helpers | Internal module | Not published |

State management moved to [Arch Event Observer](https://github.com/matheus-corregiari/arch-event-observer)
as `event-observer-state`. See the [state migration guide](docs/state-migration.md).
Storage moved to [Arch Storage](https://github.com/matheus-corregiari/arch-storage);
see the [storage migration guide](docs/storage-migration.md).

## Installation

Use Maven Central. This branch prepares the following Splinter candidate:

```kotlin
commonMain.dependencies {
    implementation("io.github.matheus-corregiari:splinter:2.0.0-rc18")
    implementation("io.github.matheus-corregiari:event-observer-state:2.3.0")
}
```

The artifacts have independent versions. Add Event Observer State when you need saved state;
apply Kotlin serialization in modules declaring serializable models.

## Platforms

Splinter targets Android, JVM, JS, Wasm, iOS ARM64 and iOS Simulator ARM64.
The Compose sample targets Android and desktop JVM. Apple builds require macOS and Xcode.

## Development

Use JDK 21 and the Gradle wrapper. See the [dependency inventory](docs/dependencies.md).

```shell
./gradlew ciLint ciBuild ciTest ciCoverage -PincludeSamples
./gradlew ciSample ciDocs ciPublicationManifest -PincludeSamples -PreleaseVersion=2.0.0-rc18
python -m pip install -r .github/requirements-docs.txt
python -m mkdocs build --strict
```

See the [Splinter guide](toolkit/multi/splinter/README.md), [changelog](docs/CHANGELOG.md),
and [contribution guide](docs/wiki/contribution-guide.md).
