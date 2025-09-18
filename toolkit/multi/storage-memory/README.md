# 📦 Arch Toolkit · Storage Memory

[![Maven Central][badge-maven]][link-maven]
[![CI Status][badge-ci]][link-ci]
![Android][badge-android]
![Apple][badge-apple]
![JVM][badge-jvm]
![JS][badge-js]
![WASM][badge-wasm]
[![LICENSE][badge-license]][link-license]
[![COVERAGE][badge-coverage]][link-coverage]

An **in-memory \[StorageProvider] implementation** for \[Arch Toolkit · Storage Core].
Useful for **testing, prototyping, or ephemeral storage** without persistence.

---

## ✨ Features

* ✅ **Multiplatform**: Works on all KMP targets.
* 🧪 **Perfect for testing**: No persistence, just lives in memory.
* ⚡ **Reactive**: Backed by \[MutableStateFlow].
* 🔄 **API compatible**: Implements the same \[StorageProvider] contract.

---

## 🚀 Quick Start

```kotlin
implementation("io.github.matheus-corregiari:storage-memory:<version>")
```

---

## 📖 Usage Example

```kotlin
val memory = MemoryStoreProvider(mutableMapOf())

val flag = memory.boolean("feature_enabled")
val counter = memory.int("counter").required { 0 }

// Reactive
scope.launch {
    flag.get().collect { println("Feature enabled? $it") }
}

// Update
flag.set(true)
counter.set(counter.instant() + 1)

println("Counter = ${counter.instant()}")
```

---

## 🛠️ When to use

* ✅ Unit tests
* ✅ Rapid prototyping
* ✅ Temporary state
* ❌ Not persistent (data is lost when process dies)

---

## 📦 Part of Arch Toolkit

The **Storage Memory** module is part of
the [Arch Toolkit](https://github.com/matheus-corregiari/arch-toolkit) storage ecosystem:

* [storage-core](../storage-core) → Base abstractions & contracts
* [storage-datastore](../storage-datastore) → Persistent provider backed by DataStore
* [storage-memory](../storage-memory) → Ephemeral provider for tests

---

## 📄 License

This module is released under the **Apache 2.0 License**.
See [LICENSE](../../../LICENSE.md) for details.

---

[link-maven]: https://search.maven.org/artifact/io.github.matheus-corregiari/storage-memory

[link-ci]: https://github.com/matheus-corregiari/arch-toolkit/actions/workflows/generate-tag.yml

[link-license]: ../../../LICENSE.md

[link-coverage]: https://codecov.io/gh/matheus-corregiari/arch-toolkit

[badge-android]: http://img.shields.io/badge/-android-6EDB8D.svg?style=flat

[badge-apple]: http://img.shields.io/badge/-apple-000000.svg?style=flat

[badge-js]: http://img.shields.io/badge/-js-F7DF1E.svg?style=flat

[badge-wasm]: http://img.shields.io/badge/-wasm-654FF0.svg?style=flat

[badge-jvm]: http://img.shields.io/badge/-jvm-DB413D.svg?style=flat

[badge-maven]: https://img.shields.io/maven-central/v/io.github.matheus-corregiari/storage-memory.svg

[badge-ci]: https://github.com/matheus-corregiari/arch-toolkit/actions/workflows/generate-tag.yml/badge.svg

[badge-license]: https://img.shields.io/github/license/matheus-corregiari/arch-toolkit

[badge-coverage]: https://img.shields.io/codecov/c/github/matheus-corregiari/arch-toolkit
