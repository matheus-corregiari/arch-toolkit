# Modules

Arch Toolkit publishes five independent artifacts on one release train.

```mermaid
flowchart TD
    Core[storage-core]
    Memory[storage-memory] --> Core
    DataStore[storage-datastore] --> Core
    State[state-handle]
    Splinter[splinter]
```

| Module | Main use | Persistent |
|:-------|:---------|:----------:|
| [storage-core](storage-core.md) | Storage contracts, typed keys, delegates, and Flow support | Backend-defined |
| [storage-memory](storage-memory.md) | Tests, previews, and temporary state | No |
| [storage-datastore](storage-datastore.md) | Preferences on Android, JVM, and Apple | Yes |
| [state-handle](state-handle.md) | Small restorable UI and ViewModel state | Recreation only |
| [splinter](splinter.md) | Request execution, polling, cache, and flow mirroring | No |

Use the [module selection guide](../using/choosing-modules.md) when more than one
module appears to fit.
