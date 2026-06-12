# State Handle

Artifact: `io.github.matheus-corregiari:state-handle`

## Purpose

`state-handle` adds typed delegates and observable state wrappers to AndroidX
`SavedStateHandle` for Kotlin Multiplatform ViewModels.

## Use It When

- Small UI inputs or selection state must survive recreation.
- A ViewModel needs a typed `StateFlow` backed by `SavedStateHandle`.
- Request state should retain the last successful value.

Do not use it for durable preferences, large objects, files, or database state.

## Installation

```kotlin
commonMain.dependencies {
    implementation("io.github.matheus-corregiari:state-handle:<latest-version>")
}
```

## Targets

Android, JVM, iOS arm64, iOS simulator arm64, JS, and WasmJS.

## Concepts and Behavior

`value(...)` creates a property delegate. `required` and `default` adapt
nullable values. `saveState(...)` exposes `ViewModelState.Regular`, while
`saveResponseState(...)` exposes result-oriented state with loading, data, and
error updates.

## Example

```kotlin
class SearchViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    var query by savedStateHandle.value<String?>(key = "query").default { "" }

    val profile by savedStateHandle.saveResponseState<User>()
}
```

## Architecture

The module builds on multiplatform AndroidX Lifecycle. Values use direct
saved-state storage when supported and state wrappers may use injected
`kotlinx.serialization` JSON for typed restoration.

## Known Limitations

- Platform saved-state rules still apply.
- `saveState` and `saveResponseState` expect a `Json` instance from Koin.
- Restored serialized models must remain schema-compatible.

## Troubleshooting

If delegation fails, verify the value is saveable on the target. If a
`saveState` delegate cannot obtain JSON, register `Json` in Koin before the
ViewModel is created.

[Open the State Handle API reference](../api/state-handle/html/index.html).
