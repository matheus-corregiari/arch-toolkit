# Event Observer – Compose Module

[![Maven Central][badge-maven]][link-maven]
[![CI Status][badge-ci]][link-ci]
![Android][badge-android]
![JVM][badge-jvm]

Bring Arch Toolkit’s reactive `DataResult`, `ResponseFlow` and `ResponseLiveData` into Jetpack
Compose with a single, elegant DSL. Define loading, error, data and list-state handlers in a fluent,
chainable API—no boilerplate, fully type-safe and animation-ready.

---

## 📑 Table of Contents

* [Features](#-features)
* [Installation](#-installation)
* [Usage](#-usage)
    * [1. Wrap your DataResult / Flow / LiveData](#1-wrap-your-dataResult--flow--liveData)
    * [2. Chain your UI callbacks](#2-chain-your-ui-callbacks)
    * [3. Attach side-effects](#3-attach-non-compose-side-effects)
* [API Reference](#-api-reference)
* [Examples](#-examples)
* [License](#-license)

---

## 🏷️ Features

* **Fluent DSL** — Chainable composable callbacks: `OnShowLoading`, `OnData`, `OnError`, etc.
* **Animation support** — Customize enter/exit transitions for loading, data or error states.
* **Outside-Compose hooks** — Attach a traditional `ObserveWrapper<T>` for side-effects or logging.
* **Empty & List helpers** — `OnEmpty`, `OnNotEmpty`, `OnSingle`, `OnMany` for collection handling.
* **Kotlin Multiplatform** — Compatible with all Arch Toolkit targets (`JVM`, `Android`, `JS`,
  etc.).

---

## 🚀 Installation

Add this module to your Gradle build:

```kotlin
dependencies {
    implementation("io.github.matheus-corregiari:event-observer-compose:<arch-toolkit-version>")
}
```

> **Note:** Don’t forget the Compose BOM or required Compose libraries in your project.

---

## 💡 Usage

### 1. Wrap your DataResult / Flow / LiveData

```kotlin
import br.com.arch.toolkit.compose.composable

val myDataResult: DataResult<MyModel> = DataResult()
val comp = myDataResult.composable
```

Or with a Flow:

```kotlin
val myFlow: ResponseFlow<MyModel> = ResponseFlow()
val comp = myFlow.composable
```

Or with LiveData:

```kotlin
val myLiveData: ResponseLiveData<MyModel> = ResponseLiveData()
val comp = myLiveData.composable
```

### 2. Chain your UI callbacks

```kotlin
comp
    .animation {
        enabled = true
        defaultEnterDuration = 200L
    }
    .OnShowLoading(EventDataStatus.DoesNotMatter) {
        CircularProgressIndicator()
    }
    .OnData { data: T ->
        Text(text = data.title)
    }
    .OnError { error: Throwable ->
        Text("Oops: ${error.message}")
    }
    .OnEmpty {
        Text("No items found")
    }
    .Unwrap()
```

* **`OnShowLoading` / `OnHideLoading`** — Show or hide a loader
* **`OnData`** — Render your data (overloads allow status or exception)
* **`OnError`** — Render static or exception-aware error UI
* **`OnEmpty` / `OnNotEmpty` / `OnSingle` / `OnMany`** — Handle list states
* **`Unwrap()`** — **must** be the final call to start collecting the flow

### 3. Attach non-Compose side-effects

```kotlin
comp.outsideComposable { 
    success { /* analytics */ }
    error { error: Throwable -> logError(error) }
}
```

These callbacks run on your configured dispatcher (default `Dispatchers.Main` / `IO`), outside
Compose.

---

## 🛠️ API Reference

### `ComposableDataResult<T>`

#### Configuration

| Method                                                    | Description                  |
|-----------------------------------------------------------|------------------------------|
| `animation(config: AnimationConfig.() -> Unit)`           | Customize animation settings |
| `outsideComposable(config: ObserveWrapper<T>.() -> Unit)` | Attach non-compose observers |

#### Loading

| Method                                                                                 | Description                   |
|----------------------------------------------------------------------------------------|-------------------------------|
| `@Composable OnShowLoading(dataStatus: EventDataStatus, func: @Composable () -> Unit)` | Render when status is LOADING |
| `@Composable OnHideLoading(dataStatus: EventDataStatus, func: @Composable () -> Unit)` | Render when loading ends      |

#### Error

| Method                                                                                    | Description                      |
|-------------------------------------------------------------------------------------------|----------------------------------|
| `@Composable OnError(dataStatus: EventDataStatus, func: @Composable () -> Unit)`          | Render on error (no exception)   |
| `@Composable OnError(dataStatus: EventDataStatus, func: @Composable (Throwable) -> Unit)` | Render on error (with exception) |

#### Data

| Method                                                                            | Description                     |
|-----------------------------------------------------------------------------------|---------------------------------|
| `@Composable OnData(func: @Composable (T) -> Unit)`                               | Render on data available        |
| `@Composable OnData(func: @Composable (T, DataResultStatus) -> Unit)`             | Render on data + status         |
| `@Composable OnData(func: @Composable (T, DataResultStatus, Throwable?) -> Unit)` | Render on data + status + error |

#### List Types

| Method                                                    | Description                  |
|-----------------------------------------------------------|------------------------------|
| `@Composable OnEmpty(func: @Composable () -> Unit)`       | Render on empty collection   |
| `@Composable OnNotEmpty(func: @Composable (T) -> Unit)`   | Render on non-empty list     |
| `@Composable <R> OnSingle(func: @Composable (R) -> Unit)` | Render on single-item result |
| `@Composable OnMany(func: @Composable (T) -> Unit)`       | Render on multi-item result  |

#### Unwrap

| Method                                                                     | Description                             |
|----------------------------------------------------------------------------|-----------------------------------------|
| `@Composable Unwrap(func: @Composable ComposableDataResult<T>.() -> Unit)` | Configure in nested DSL                 |
| `@Composable Unwrap()`                                                     | Start collection and dispatch callbacks |

---

## 📝 Examples

See the **[sample-compose][link-sample]** folder for a minimal end-to-end demo:

1. Launch `MainActivity` collecting a `Flow<DataResult<List<String>>>`.
2. Display a loader, list of strings, or error message.

---

## 📄 License

This module is released under the **Apache 2.0 License**.
See [LICENSE](../../../LICENSE.md) for details.

---

[link-sample]: https://github.com/matheus-corregiari/arch-toolkit/tree/master/toolkit/compose/sample-compose
[link-maven]: https://search.maven.org/artifact/io.github.matheus-corregiari/event-observer-compose
[link-ci]: https://github.com/matheus-corregiari/arch-toolkit/actions/workflows/generate-tag.yml
[badge-android]: http://img.shields.io/badge/-android-6EDB8D.svg?style=flat
[badge-jvm]: http://img.shields.io/badge/-jvm-DB413D.svg?style=flat
[badge-maven]: https://img.shields.io/maven-central/v/io.github.matheus-corregiari/event-observer-compose.svg
[badge-ci]: https://github.com/matheus-corregiari/arch-toolkit/actions/workflows/generate-tag.yml/badge.svg
