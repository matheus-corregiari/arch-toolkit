# Splinter

Splinter provides asynchronous request execution, loading/result state and execution strategies.
It remains a release candidate in Arch Toolkit `2.0.0-rc18`.

```kotlin
implementation("io.github.matheus-corregiari:splinter:2.0.0-rc18")
```

Supported library targets: Android, JVM, JS, Wasm, iOS ARM64 and iOS Simulator ARM64.
The Android/desktop sample demonstrates repository requests and Compose observation.

State persistence is supplied separately by
[Event Observer State](https://github.com/matheus-corregiari/arch-event-observer).
See the [state migration guide](../../../docs/state-migration.md).
