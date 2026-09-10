# State Handle migration in rc18

Arch Toolkit no longer builds or publishes State Handle starting with `2.0.0-rc18`.
Use `event-observer-state:2.3.0` from
[Arch Event Observer](https://github.com/matheus-corregiari/arch-event-observer).
Existing rc17 artifacts remain historical releases; rc18 does not publish a compatibility shim
or relocation POM for their coordinates.

| Before | After |
| --- | --- |
| `io.github.matheus-corregiari:state-handle:2.0.0-rc17` | `io.github.matheus-corregiari:event-observer-state:2.3.0` |
| `br.com.arch.toolkit.stateHandle` | `br.com.arch.toolkit.eventObserver.state` |
| `SavableObject` inheritance | `@Serializable` models or an explicit serializer |
| Native objects with a JSON fallback | JSON saved under one key |

Replace dependencies and imports explicitly. Align Event Observer core, Compose and State
to the same Event Observer version, independent of Splinter.

```kotlin
commonMain.dependencies {
    implementation("io.github.matheus-corregiari:event-observer-state:2.3.0")
}
```

Apply Kotlin serialization in each module declaring serializable models.
Remove `SavableObject` inheritance. The sample retains serialization on `RepoVO` and `PageDTO`,
and imports `br.com.arch.toolkit.eventObserver.state.saveResponseState` in its ViewModel.

## Saved state and loading

Old native/shadow snapshots are not decoded by the new library. The sample uses the new key
`github-repositories-v2`, starting fresh instead of interpreting the old `lastPageState`
snapshot as JSON. Applications requiring continuity must convert old values before
initializing the new holder.

`flow()` exposes a stable state flow; `load` starts a new operation and returns a job.
Payloads restore as success; loading and errors are transient. Defaults do not replace
restored values. Use a SavedStateHandle from a restoring owner; simply constructing a handle
does not add platform restoration.

See the upstream [migration guide](https://matheus-corregiari.github.io/arch-event-observer/migration-state/)
for API differences and platform limitations.
