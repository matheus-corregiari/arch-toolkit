# State Handle

[![Maven Central](https://img.shields.io/maven-central/v/io.github.matheus-corregiari/state-handle)](https://central.sonatype.com/artifact/io.github.matheus-corregiari/state-handle)

Typed, reactive state delegates backed by Android `SavedStateHandle` and
compatible scoped state on other targets.

```kotlin
implementation("io.github.matheus-corregiari:state-handle:<version>")
```

```kotlin
var page by savedStateHandle.value<Int?>(key = "page").required { 1 }
```

See the [module documentation](../../../docs/modules/state-handle.md).
