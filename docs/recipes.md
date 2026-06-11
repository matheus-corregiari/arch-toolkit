# Usage Recipes

## In-Memory Storage

```kotlin
val storage = MemoryStoreProvider(mutableMapOf())
val enabled = storage.boolean("enabled")

enabled.set(true)
check(enabled.instant() == true)
```

## Persistent Storage

Create an AndroidX DataStore instance for the platform, then expose it through
`DataStoreProvider`.

```kotlin
val provider = DataStoreProvider(dataStore)
val theme = provider.string("theme")
theme.set("dark")
```

## Restorable State

```kotlin
var query by savedStateHandle.value<String?>(key = "query")
```

Keep persisted state small. Store identifiers and user input, not large object
graphs.
