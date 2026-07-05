# 📦 Arch Toolkit · Storage Core

[![Maven Central][badge-maven]][link-maven]  
[![CI Status][badge-ci]][link-ci]  
![Android][badge-android]  
![Apple][badge-apple]  
![JVM][badge-jvm]
![JS][badge-js]  
![WASM][badge-wasm]  
[![LICENSE][badge-license]][link-license]  
[![COVERAGE][badge-coverage]][link-coverage]

A **multiplatform reactive storage abstraction** for key–value data.
Part of the [Arch Toolkit](https://github.com/matheus-corregiari/arch-toolkit).

---

## ✨ Features

* ✅ **Multiplatform API**: Works across Java (Android/JVM), Apple (iOS/macOS), Web (JS/WASM\*).
* 🎯 **Unified contract**: Consistent `KeyValue` interface for any storage backend.
* 🔄 **Reactive**: Observe changes via Kotlin \[Flow].
* ⚡ **Synchronous access**: Immediate reads with `instant()`.
* 🧩 **Compose ready**: \[state()] integration with Jetpack Compose.
* 🖇️ **Delegates**: Property `by` syntax.
* 🔒 **Type-safe**: Built-in support for primitives, enums, and JSON models.

\* Web targets fallback to **noop** stubs.

---

## 🚀 Quick Start

Add the **core module** as dependency:

```kotlin
implementation("io.github.matheus-corregiari:storage-core:<version>")
```

Then choose one of the available providers:

* **DataStore** (persistent)

  ```kotlin
  implementation("io.github.matheus-corregiari:storage-datastore:<version>")
  ```
* **Memory** (in-memory, for tests or ephemeral cache)

  ```kotlin
  implementation("io.github.matheus-corregiari:storage-memory:<version>")
  ```

---

## 📖 Usage Examples

### Basic

```kotlin
val storage: StorageProvider = DataStoreProvider(store)

val isLoggedIn = storage.boolean("is_logged_in")
val userName = storage.string("user_name")

isLoggedIn.set(true)
println("User: ${userName.instant()}")
```

---

### Enum

```kotlin
enum class Theme { Light, Dark }

val theme = storage.enum("theme", Theme.entries, Theme.Light)

theme.set(Theme.Dark)
println("Theme is ${theme.instant()}")
```

---

### JSON Model

```kotlin
@kotlinx.serialization.Serializable
data class User(val id: String, val name: String)

val user = storage.model<User>("user")

user.set(User("42", "Alice"))
println("User: ${user.instant()}")
```

---

### Compose Integration

```kotlin
@Composable
fun UserNameInput(userName: KeyValue<String?>) {
    val state = userName.state()
    TextField(
        value = state.value.orEmpty(),
        onValueChange = { state.value = it }
    )
}
```

---

### Delegated Property

```kotlin
var counter by storage.int("counter").required { 0 }.delegate()

counter += 1
println("Counter is $counter")
```

---

## 🛠️ Providers

* **storage-datastore** → Backed by AndroidX DataStore.
    * Supported on **Java (Android/JVM)** and **Apple (iOS/macOS)**.
* **storage-memory** → In-memory, ideal for testing and mock scenarios.
* **noop** → Stub for **Web (JS/WASM)** targets.

---

## ⚙️ JSON Configuration

By default, models use the built-in `Json` configuration:

```kotlin
ignoreUnknownKeys = true
encodeDefaults = true
prettyPrint = true
```

Override globally if needed:

```kotlin
StorageProvider.json(Json {
    ignoreUnknownKeys = false
})
```

---

## 🧩 Targets Overview

| Target      | Provider(s)      | Status |
|-------------|------------------|--------|
| Android/JVM | DataStore        | ✅      |
| iOS/macOS   | DataStore (KMP)  | ✅      |
| JS/WASM     | Noop (stub only) | ⚠️     |
| Any         | Memory           | ✅      |

---

## 🧪 Testing

For unit tests or non-persistent use cases, use the in-memory provider:

```kotlin
val memory = MemoryStoreProvider(mutableMapOf())
val flag = memory.boolean("feature_enabled")

flag.set(true)
println(flag.instant()) // true
```

---

## 🔗 Related Modules

- [📦 storage-datastore](../storage-datastore)  
  Implementation backed by AndroidX DataStore.

- [📦 storage-memory](../storage-memory)  
  In-memory provider for tests and ephemeral use.

- [📦 storage-core](../storage-core)  
  Abstractions and contracts (this module).

---

## 📦 Part of Arch Toolkit

The **Storage Core** is one of the building blocks
of [Arch Toolkit](https://github.com/matheus-corregiari/arch-toolkit), designed to provide:

* Consistency across platforms
* Type safety
* Developer productivity

---

## 📄 License

This module is released under the **Apache 2.0 License**.
See [LICENSE](../../../LICENSE.md) for details.

---

[link-maven]: https://search.maven.org/artifact/io.github.matheus-corregiari/storage-core

[link-ci]: https://github.com/matheus-corregiari/arch-toolkit/actions/workflows/generate-tag.yml

[link-license]: ../../../LICENSE.md

[link-coverage]: https://codecov.io/gh/matheus-corregiari/arch-toolkit

[badge-android]: http://img.shields.io/badge/-android-6EDB8D.svg?style=flat

[badge-apple]: http://img.shields.io/badge/-apple-000000.svg?style=flat

[badge-js]: http://img.shields.io/badge/-js-F7DF1E.svg?style=flat

[badge-wasm]: http://img.shields.io/badge/-wasm-654FF0.svg?style=flat

[badge-jvm]: http://img.shields.io/badge/-jvm-DB413D.svg?style=flat

[badge-maven]: https://img.shields.io/maven-central/v/io.github.matheus-corregiari/storage-core.svg

[badge-ci]: https://github.com/matheus-corregiari/arch-toolkit/actions/workflows/generate-tag.yml/badge.svg

[badge-license]: https://img.shields.io/github/license/matheus-corregiari/arch-toolkit

[badge-coverage]: https://img.shields.io/codecov/c/github/matheus-corregiari/arch-toolkit

```