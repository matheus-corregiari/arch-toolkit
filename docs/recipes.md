# Practical Recipes

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

## Required Storage Value

```kotlin
val page = storage.int("page").required { 1 }
page.set(2)
check(page.instant() == 2)
```

## Restorable State Delegate

```kotlin
var query by savedStateHandle.value<String?>(key = "query").default { "" }
```

Keep persisted state small. Store identifiers and user input, not large object
graphs.

## One-Shot Request

```kotlin
val request = splinter(
    strategy = Strategy.oneShot {
        request { repository.loadProfile() }
    }
)

request.execute()
request.resultHolder.fullFlow.collect { result ->
    render(result)
}
```

Use explicit execution and stop policies when the default behavior does not
match the screen lifecycle. See [Splinter](modules/splinter.md).
