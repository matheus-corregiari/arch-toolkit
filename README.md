# Arch Toolkit

[![CI](https://github.com/matheus-corregiari/arch-toolkit/actions/workflows/pull-request.yml/badge.svg?branch=master)](https://github.com/matheus-corregiari/arch-toolkit/actions/workflows/pull-request.yml)
[![Codebeat](https://codebeat.co/badges/1add62ed-f5fc-4bd2-9054-501685ca007c)](https://codebeat.co/projects/github-com-matheus-corregiari-arch-toolkit-master)
[![Coverage](https://img.shields.io/endpoint?url=https%3A%2F%2Fgist.githubusercontent.com%2Fmatheus-corregiari%2F4fbcfa4cec61deb2262b16c19ab14138%2Fraw%2Fcoverage-badge.json&logo=kotlin)](https://github.com/matheus-corregiari/arch-toolkit/actions/workflows/pull-request.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.matheus-corregiari/storage-core?logo=apache-maven&style=flat-square)](https://central.sonatype.com/search?q=io.github.matheus-corregiari)
[![License](https://img.shields.io/github/license/matheus-corregiari/arch-toolkit?style=flat-square)](LICENSE.md)

> Kotlin Multiplatform utilities collected from real-world architecture work, shared as focused modules you can mix and match across platforms.

---

## 🎯 Objective

Provide production-ready Kotlin Multiplatform (KMP) building blocks for state management, storage, delegation utilities, and platform integrations while keeping APIs lightweight and well tested. Each module is released when it reaches a stable level of documentation, CI coverage, and publishing automation.

---

## 🧭 Modules Overview

| Module | Gradle Artifact | Stability | Supported Targets | Highlights |
|--------|-----------------|-----------|-------------------|------------|
| `toolkit/multi/storage-core` | `io.github.matheus-corregiari:storage-core` | Stable | Android, JVM, iOS, macOS, JS (stub), WASM (stub) | Reactive key–value storage contract with Flow support. |
| `toolkit/multi/storage-datastore` | `io.github.matheus-corregiari:storage-datastore` | Beta | Android, JVM, iOS, macOS | DataStore-backed implementation for persistent storage. |
| `toolkit/multi/storage-memory` | `io.github.matheus-corregiari:storage-memory` | Stable | All KMP targets | In-memory storage for tests and ephemeral state. |
| `toolkit/multi/event-observer` | `io.github.matheus-corregiari:event-observer` | Beta | Android, JVM, iOS, macOS | Multiplatform event channels with lifecycle awareness. |
| `toolkit/multi/event-observer-compose` | `io.github.matheus-corregiari:event-observer-compose` | Experimental | Android | Compose extensions for event observers. |
| `toolkit/multi/state-handle` | `io.github.matheus-corregiari:state-handle` | Incubating | Android, JVM | Lifecycle-friendly state persistence for shared logic. |
| `toolkit/multi/lumber` | `io.github.matheus-corregiari:lumber` | Incubating | Android, JVM, iOS, macOS | Structured logging façade tuned for KMP. |
| `toolkit/multi/splinter` | `io.github.matheus-corregiari:splinter` | Work in Progress | Android, JVM, iOS, macOS | Code generation helpers for DI-less modularization. |
| `toolkit/android/statemachine` | `io.github.matheus-corregiari:statemachine` | Stable | Android | Declarative state transitions for UI flows. |
| `toolkit/android/recycler-adapter` | `io.github.matheus-corregiari:recycler-adapter` | Stable | Android | RecyclerView adapter utilities with View binding hooks. |
| `toolkit/android/delegate` | `io.github.matheus-corregiari:delegate` | Stable | Android | Kotlin property delegates for Android components. |
| `toolkit/android/foldable` | `io.github.matheus-corregiari:foldable` | Beta | Android | Jetpack Window Manager helpers for foldable devices. |
| `toolkit/android/storage` | `io.github.matheus-corregiari:storage` | Deprecated | Android | Legacy storage helpers superseded by KMP storage modules. |
| `toolkit/android/util` | `io.github.matheus-corregiari:util` | Deprecated | Android | Legacy utility extensions scheduled for archival. |

> ℹ️ Detailed usage guides for every module live inside their respective directories. All READMEs follow the same structure with badges, installation instructions, usage examples, and licensing notes for easy scanning.

---

## 🧪 Stability Matrix

| Stability Level | Description |
|-----------------|-------------|
| Stable | API ready for production; published to Maven Central with semantic versioning. |
| Beta | Feature complete but gathering feedback; expect minor API adjustments. |
| Experimental | Available for early adopters; APIs may change between releases. |
| Incubating / Work in Progress | Under heavy development; not yet published. |
| Deprecated | Maintained for compatibility only; prefer the newer KMP alternatives. |

---

## 🖥️ Platform Support

| Platform | Status | Notes |
|----------|--------|-------|
| Android / JVM | ✅ | Primary target with full CI coverage. |
| iOS / macOS | ✅ | Supported across storage and logging modules via KMP. |
| JavaScript | ⚠️ | Storage modules provide stub implementations; APIs compile but act as no-ops. |
| WASM | ⚠️ | Follows the same stub strategy as JavaScript until runtime libraries mature. |
| Desktop (Compose) | 🚧 | Planned via shared KMP modules; follow the [roadmap](docs/wiki/roadmap.md). |

---

## ⚙️ Installation (Gradle Kotlin DSL)

Add the toolkit artifacts you need to your shared module. Examples below use the Kotlin Multiplatform DSL with version catalogs.

```kotlin
// settings.gradle.kts
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
```

```kotlin
// gradle/libs.versions.toml
[versions]
arch-toolkit = "<latest-version>"

[libraries]
storage-core = { module = "io.github.matheus-corregiari:storage-core", version.ref = "arch-toolkit" }
```

```kotlin
// build.gradle.kts
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.storage.core)
                implementation("io.github.matheus-corregiari:storage-datastore:<latest-version>")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("io.github.matheus-corregiari:event-observer-compose:<latest-version>")
            }
        }
    }
}
```

> Tip: Align versions through your version catalog to keep every toolkit module on the same release train.

---

## 📖 Usage Examples

* **Reactive storage** – Observe values in Compose or SwiftUI with `Flow` and `state()` helpers. See [`toolkit/multi/storage-core`](toolkit/multi/storage-core/README.md).
* **Lifecycle events** – Connect shared logic to UI layers via the [`event-observer`](toolkit/multi/event-observer/README.md) channel APIs.
* **Android UI state** – Drive screen transitions predictably with [`statemachine`](toolkit/android/statemachine/README.md).
* **Delegated properties** – Simplify Android component setup with [`delegate`](toolkit/android/delegate/README.md).

Every module README includes installation, setup walkthroughs, and runnable snippets to get you productive fast.

---

## 📚 Documentation & Wiki

The project wiki consolidates in-depth guides and operational processes:

* [Overview](docs/wiki/overview.md)
* [Contribution Guide](docs/wiki/contribution-guide.md)
* [Artifact Publishing](docs/wiki/artifact-publishing.md)
* [Roadmap](docs/wiki/roadmap.md)

Each section mirrors the structure used across READMEs, keeping terminology and tone consistent. Feel free to open issues suggesting additional topics.

---

## 🛡️ License

Licensed under the [Apache License 2.0](LICENSE.md). Refer to individual module READMEs for any additional notes.

---

## 🤝 Contributing

Pull requests are always welcome! Start with the [contribution guide](docs/wiki/contribution-guide.md) to learn about tooling, coding standards, and CI expectations. For ideas, check the [roadmap](docs/wiki/roadmap.md) and join discussions in GitHub issues.

