# State Handle

Artifact: `io.github.matheus-corregiari:state-handle`

Provides typed state delegates backed by Android `SavedStateHandle` and
compatible scoped state on other targets.

```kotlin
var page by savedStateHandle.value<Int?>(key = "page").required { 1 }
```
