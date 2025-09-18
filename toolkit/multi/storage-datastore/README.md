# 📦 Arch Toolkit · Storage DataStore

[![Maven Central][badge-maven]][link-maven]
[![CI Status][badge-ci]][link-ci]
![Android][badge-android]
![Apple][badge-apple]
![JVM][badge-jvm]
![JS][badge-js]
![WASM][badge-wasm]
[![LICENSE][badge-license]][link-license]
[![COVERAGE][badge-coverage]][link-coverage]

A **persistent \[StorageProvider] implementation** backed by **AndroidX DataStore Preferences**.
Part of the [Arch Toolkit](https://github.com/matheus-corregiari/arch-toolkit).

---

## ✨ Features

* ✅ **Persistent storage** across app restarts.
* 🔄 **Reactive**: Observed via Kotlin \[Flow].
* ⚡ **Synchronous access** with `instant()`.
* 🔒 **Type-safe**: Supports primitives, enums, and JSON models.
* 🌍 **Multiplatform**: Works on **Android/JVM** and **Apple** (iOS/macOS).
* ⚠️ **Stubbed on Web**: For JS/WASM targets, \[DataStoreProvider] is a **noop** (throws on use).

---

## 🚀 Quick Start

Add as dependency:

```kotlin
implementation("io.github.matheus-corregiari:storage-datastore:<version>")
```

---

## 📖 Usage Examples

### Primitives

```kotlin
val provider = DataStoreProvider(store)

val isLoggedIn = provider.boolean("is_logged_in")
val userName = provider.string("user_name")

isLoggedIn.set(true)
println("User: ${userName.instant()}")
```

---

### Enum

```kotlin
enum class Theme { Light, Dark }

val theme = provider.enum("theme", Theme.entries, Theme.Light)

theme.set(Theme.Dark)
println("Theme is ${theme.instant()}")
```

---

### JSON Model

```kotlin
@kotlinx.serialization.Serializable
data class User(val id: String, val name: String)

val user = provider.model(
    key = "user",
    fromJson = { Json.decodeFromString<User>(it) },
    toJson = { Json.encodeToString(it) }
)

user.set(User("42", "Alice"))
println("User: ${user.instant()}")
```

---

## 🛠️ Providers Overview

* **storage-core** → Contracts and abstractions.
* **storage-datastore** → Persistent provider backed by DataStore.
* **storage-memory** → Ephemeral provider for testing.

---

## 🧩 Targets

| Target      | Status                       |
|-------------|------------------------------|
| Android/JVM | ✅ Persistent (DataStore)     |
| iOS/macOS   | ✅ Persistent (KMP DataStore) |
| JS/WASM     | ⚠️ Stub (noop)               |

---

## 📦 Part of Arch Toolkit

The **Storage DataStore** module is part of
the [Arch Toolkit](https://github.com/matheus-corregiari/arch-toolkit) storage ecosystem:

* [storage-core](../storage-core) → Base abstractions & contracts
* [storage-datastore](../storage-datastore) → Persistent provider backed by DataStore
* [storage-memory](../storage-memory) → Ephemeral provider for tests

---

## 📄 License

This module is released under the **Apache 2.0 License**.
See [LICENSE](../../../LICENSE.md) for details.

---

[link-maven]: https://search.maven.org/artifact/io.github.matheus-corregiari/storage-datastore

[link-ci]: https://github.com/matheus-corregiari/arch-toolkit/actions/workflows/generate-tag.yml

[link-license]: ../../../LICENSE.md

[link-coverage]: https://codecov.io/gh/matheus-corregiari/arch-toolkit

[badge-android]: http://img.shields.io/badge/-android-6EDB8D.svg?style=flat

[badge-apple]: http://img.shields.io/badge/-apple-000000.svg?style=flat

[badge-jvm]: http://img.shields.io/badge/-jvm-DB413D.svg?style=flat

[badge-js]: http://img.shields.io/badge/-js-F7DF1E.svg?style=flat

[badge-wasm]: http://img.shields.io/badge/-wasm-654FF0.svg?style=flat

[badge-maven]: https://img.shields.io/maven-central/v/io.github.matheus-corregiari/storage-datastore.svg

[badge-ci]: https://github.com/matheus-corregiari/arch-toolkit/actions/workflows/generate-tag.yml/badge.svg

[badge-license]: https://img.shields.io/github/license/matheus-corregiari/arch-toolkit

[badge-coverage]: https://img.shields.io/codecov/c/github/matheus-corregiari/arch-toolkit
