# 🎭 Arch Toolkit · Event Observer Compose

[![Maven Central][badge-maven]][link-maven]
[![CI Status][badge-ci]][link-ci]
![Android][badge-android]
![Apple][badge-apple]
![JS][badge-js]
![WASM][badge-wasm]
![JVM][badge-jvm]
[![LICENSE][badge-license]][link-license]
[![COVERAGE][badge-coverage]][link-coverage]

A **Compose-first event/result observer DSL** for handling loading, success, error, and data states
declaratively.
Part of the [Arch Toolkit](https://github.com/matheus-corregiari/arch-toolkit).

---

## ✨ Features

* ✅ **Multiplatform**: Works across Android, JVM, Apple, JS, and WASM.
* 🎯 **Declarative DSL**: Chainable API for `OnData`, `OnError`, `OnLoading`, etc.
* 🔄 **Reactive**: Powered by Kotlin \[Flow] and \[DataResult].
* 🎨 **Compose-ready**: Works seamlessly with Jetpack Compose, including `AnimatedVisibility`.
* ⚡ **Animation built-in**: Configurable enter/exit transitions with sensible defaults.
* 🧪 **Side-effects**: Observe outside Compose with `outsideComposable`.

---

## 🚀 Quick Start

Add dependency:

```kotlin
implementation("io.github.matheus-corregiari:event-observer-compose:<version>")
```

---

## 📖 Usage Examples

### Basic Observation

```kotlin
val comp = myFlow.composable

comp
    .OnShowLoading { CircularProgressIndicator() }
    .OnData { data -> Text("Hello, $data") }
    .OnError { err -> Text("Oops: ${err.message}") }
    .Unwrap()
```

---

### Nested DSL

```kotlin
comp.Unwrap {
    OnShowLoading { CircularProgressIndicator() }
    OnData { Text("Loaded!") }
    OnError { Text("Something went wrong") }
}
```

---

### Animation Configuration

```kotlin
comp.animation {
    enabled = true
    enterAnimation = fadeIn(tween(300))
    exitAnimation = fadeOut(tween(200))
}
    .Unwrap()
```

Or globally:

```kotlin
ComposableDataResult.AnimationConfig.enabledByDefault = false
```

---

### Outside Composable Side-Effects

```kotlin
comp.outsideComposable {
    error { throwable: Throwable -> logError(throwable) }
}
    .Unwrap()
```

---

### Collect as State

```kotlin
val compState by myFlow.collectAsComposableState()

compState
    .OnData { Text("From State") }
    .Unwrap()
```

---

## 🛠️ Concepts

* **`ComposableDataResult`** → The core wrapper around `Flow<DataResult<T>>`.
* **`OnData / OnError / OnLoading / OnEmpty`** → DSL blocks for state rendering.
* **`AnimationConfig`** → Controls enter/exit transitions (fade by default).
* **`outsideComposable`** → Non-Compose observation via `ObserveWrapper`.

---

## 🧩 Targets Overview

| Target      | Status |
|-------------|--------|
| Android/JVM | ✅      |
| iOS/macOS   | ✅      |
| JS/WASM     | ✅      |

---

## 🧪 Testing

```kotlin
val fakeFlow = MutableStateFlow(DataResult.loading())

val comp = fakeFlow.composable
comp.OnShowLoading { Text("Loading...") }
    .OnData { Text("Loaded!") }
    .Unwrap()
```

---

## 📦 Part of Arch Toolkit

The **Event Observer Compose** is one of the building blocks
of [Arch Toolkit](https://github.com/matheus-corregiari/arch-toolkit), designed to provide:

* Reactive abstractions
* Declarative state handling
* Developer productivity

---

## 📄 License

This module is released under the **Apache 2.0 License**.
See [LICENSE](../../../LICENSE.md) for details.

---

[link-maven]: https://search.maven.org/artifact/io.github.matheus-corregiari/event-observer-compose

[link-ci]: https://github.com/matheus-corregiari/arch-toolkit/actions/workflows/generate-tag.yml

[link-license]: ../../../LICENSE.md

[link-coverage]: https://codecov.io/gh/matheus-corregiari/arch-toolkit

[badge-android]: http://img.shields.io/badge/-android-6EDB8D.svg?style=flat

[badge-apple]: http://img.shields.io/badge/-apple-000000.svg?style=flat

[badge-js]: http://img.shields.io/badge/-js-F7DF1E.svg?style=flat

[badge-wasm]: http://img.shields.io/badge/-wasm-654FF0.svg?style=flat

[badge-jvm]: http://img.shields.io/badge/-jvm-DB413D.svg?style=flat

[badge-maven]: https://img.shields.io/maven-central/v/io.github.matheus-corregiari/event-observer-compose.svg

[badge-ci]: https://github.com/matheus-corregiari/arch-toolkit/actions/workflows/generate-tag.yml/badge.svg

[badge-license]: https://img.shields.io/github/license/matheus-corregiari/arch-toolkit

[badge-coverage]: https://img.shields.io/codecov/c/github/matheus-corregiari/arch-toolkit
