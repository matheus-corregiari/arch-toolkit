# 🌲 Lumber – A Lightweight Logging Library for Kotlin Multiplatform

[![Maven Central][badge-maven]][link-maven]  
[![CI Status][badge-ci]][link-ci]  
![Android][badge-android]  
![Apple][badge-apple]  
![JS][badge-js]  
![WASM][badge-wasm]  
![JVM][badge-jvm]

A flexible, type-safe, and multiplatform logging library inspired
by [Timber](https://github.com/JakeWharton/timber).  
Lumber brings a **simple API**, **prebuilt DebugTrees**, and **extensibility** for your Kotlin
Multiplatform projects.

---

## 📑 Table of Contents

* [Features](#-features)
* [Installation](#-installation)
* [Usage](#-usage)
    * [1. Plant a DebugTree](#1-plant-a-debugtree)
    * [2. Log messages](#2-log-messages)
    * [3. Manage Oaks](#3-manage-oaks)
* [Prebuilt DebugTrees](#-prebuilt-debugtrees)
* [Custom Oaks](#-custom-oaks)
* [Comparison with Timber](#-comparison-with-timber)
* [API Reference](#-api-reference)
* [License](#-license)

---

## 🏷️ Features

* **Kotlin Multiplatform Ready** — Runs on Android, Apple (Darwin/iOS/macOS), Kotlin/JS (IR), *
  *Kotlin/WASM (IR)**, and JVM.
* **Multiple Log Levels** — `Verbose`, `Debug`, `Info`, `Warn`, `Error`, `Assert`.
* **Prebuilt DebugTrees** — Drop-in defaults per target:
    * Android → Logcat
    * Apple → ANSI colored `println` (via [Mordant](https://github.com/ajalt/mordant))
    * JS/WASM → Native `console.*` (browser or Node.js/wasm runtime)
* **Composable System** — Plant one or many Oaks (`ConsoleOak`, `FileOak`, custom).
* **Thread-Safe** — Safe for concurrent logging.
* **Inspired by Timber** — Similar ergonomics, extended for KMP.

---

## 🚀 Installation

Add Lumber to your Gradle build:

```kotlin
dependencies {
    implementation("io.github.matheus-corregiari:lumber:<arch-toolkit-version>")
}
````

---

## 💡 Usage

### 1. Plant a DebugTree

```kotlin
// Android → Logcat
Lumber.plant(DebugTree())

// Apple → ANSI-colored println
Lumber.plant(DebugTree())

// JS/WASM → console.log / console.info / console.warn / console.error
Lumber.plant(DebugTree())
```

### 2. Log messages

```kotlin
Lumber.verbose("Verbose details")
Lumber.debug("Debugging info")
Lumber.info("Initialization complete")
Lumber.warn("This might need attention")
Lumber.error(message = "Something failed!", error = Exception("Boom"))
Lumber.wtf("Critical failure!", Exception("Should never happen"))
```

### 3. Manage Oaks

```kotlin
val console = ConsoleOak()
Lumber.plant(console)

// Remove one oak
Lumber.uproot(console)

// Or clear all oaks
Lumber.uprootAll()
```

---

## 🌲 Prebuilt DebugTrees

| Target    | Implementation | Backend                                |
|-----------|----------------|----------------------------------------|
| Android   | `DebugTree`    | `android.util.Log` (Logcat)            |
| Apple     | `DebugTree`    | `println` with ANSI colors via Mordant |
| JS (IR)   | `DebugTree`    | Native `console.log/info/warn/error`   |
| WASM (IR) | `DebugTree`    | Native `console.log/info/warn/error`   |
| JVM       | (fallback)     | Standard `println`                     |

Example (WASM, browser):

```kotlin
Lumber.plant(DebugTree())
Lumber.debug("Running in WebAssembly")
```

Browser console output:

```
DEBUG null : Running in WebAssembly
```

---

## 🛠️ Custom Oaks

Extend `Lumber.Oak` for your own logging:

```kotlin
class ConsoleOak : Lumber.Oak() {
    override fun log(level: Level, tag: String?, message: String, error: Throwable?) {
        println("$level: [$tag] $message")
    }
}
```

Plant it like this:

```kotlin
Lumber.plant(ConsoleOak(), FileOak())
Lumber.debug("Logged to console and file")
```

---

## 🔄 Comparison with Timber

| Feature                 | Timber (Android-only) | Lumber (Multiplatform)                               |
|-------------------------|-----------------------|------------------------------------------------------|
| **Targets**             | Android only          | Android, Apple (Darwin), JS (IR), **WASM (IR)**, JVM |
| **Prebuilt DebugTree**  | Logcat                | Logcat, ANSI-colored println, JS/WASM console        |
| **Custom Trees**        | ✅ Supported           | ✅ Supported (`Oak`)                                  |
| **Thread-Safe**         | ✅                     | ✅                                                    |
| **API Style**           | `Timber.d("msg")`     | `Lumber.debug("msg")`                                |
| **Multiplatform Ready** | ❌                     | ✅                                                    |
| **Quiet Mode**          | ❌                     | ✅ `Lumber.quiet(true)`                               |
| **Tag Override**        | ✅                     | ✅ `Lumber.tag("MyTag").debug("msg")`                 |

### Side-by-Side Example

**Timber (Android-only):**

```kotlin
Timber.plant(Timber.DebugTree())
Timber.d("Debug message")
Timber.e(Throwable("Boom"), "Error occurred")
```

**Lumber (KMP):**

```kotlin
Lumber.plant(DebugTree())
Lumber.debug("Debug message")
Lumber.error(Throwable("Boom"), "Error occurred")
```

---

## 📚 API Reference

### `Lumber` – static API

| Method                    | Description                       |
|---------------------------|-----------------------------------|
| `plant(vararg oaks: Oak)` | Add logging trees                 |
| `uproot(oak: Oak)`        | Remove a specific oak             |
| `uprootAll()`             | Remove all oaks                   |
| `tag(tag: String)`        | Set a custom tag for the next log |
| `quiet(enabled: Boolean)` | Enable/disable quiet mode         |

### `Lumber.Level`

| Level   | Purpose                          |
|---------|----------------------------------|
| Verbose | Detailed logs, tracing/debugging |
| Debug   | Debugging information            |
| Info    | Informational messages           |
| Warn    | Warnings, potential issues       |
| Error   | Errors and exceptions            |
| Assert  | Critical failures (WTF)          |

---

## 🙏 Honorable Mention

Lumber exists because the excellent [Timber](https://github.com/JakeWharton/timber) does not support
Kotlin Multiplatform.
Special thanks to Jake Wharton for the inspiration and making Timber the go-to logging library for
Android.

---

## 📄 License

This module is released under the **Apache 2.0 License**.
See [LICENSE](../../../LICENSE.md) for details.

---

[link-maven]: https://search.maven.org/artifact/io.github.matheus-corregiari/lumber

[link-ci]: https://github.com/matheus-corregiari/arch-toolkit/actions/workflows/generate-tag.yml

[badge-android]: http://img.shields.io/badge/-android-6EDB8D.svg?style=flat

[badge-apple]: http://img.shields.io/badge/-apple-000000.svg?style=flat

[badge-js]: http://img.shields.io/badge/-js-F7DF1E.svg?style=flat

[badge-wasm]: http://img.shields.io/badge/-wasm-654FF0.svg?style=flat

[badge-jvm]: http://img.shields.io/badge/-jvm-DB413D.svg?style=flat

[badge-maven]: https://img.shields.io/maven-central/v/io.github.matheus-corregiari/lumber.svg

[badge-ci]: https://github.com/matheus-corregiari/arch-toolkit/actions/workflows/generate-tag.yml/badge.svg

```