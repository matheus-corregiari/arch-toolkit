# Splinter

Splinter provides asynchronous request execution, loading/result state and execution strategies.
It remains a release candidate in Arch Toolkit `2.0.0-rc19`.

```kotlin
implementation("io.github.matheus-corregiari:splinter:2.0.0-rc19")
```

Supported library targets: Android, JVM, JS, Wasm, iOS ARM64 and iOS Simulator ARM64.
The Android/desktop sample demonstrates repository requests and Compose observation.

## Requests, cancellation and timing

`OneShot` emits loading (including optional `sendSnapshot` data), then success or failure.
Its minimum duration defaults to **200 ms on success and zero on error** to keep short
loading indicators visible. Use `minDuration(Duration.ZERO)` for immediate completion,
or `minDuration(onSuccess, onError)` for separate limits. The minimum overlaps the
request; a request that already exceeds it does not wait again. All waiting is cancellable.
`maxDuration` limits the request itself; a request timeout is reported as a failure.

OneShot/polling suspend callbacks and requests, cache checks and `await()` propagate cancellation.
Cancellation does not become a failure result, trigger fallback or start another polling
attempt. Ordinary callback failures retain their existing best-effort behavior.
`cancel()` stops active operations; `kill()` permanently stops the instance. To execute
again, set `stop(Splinter.StopPolicy.UntilRequest)` explicitly: platform defaults differ,
and JVM/JS/Wasm default to `AfterFirstExecution`.

`IgnoreWhenHasRunningOperations` ignores overlapping execute calls. `SequentialQueue`
queues them; `ParallelQueue` starts requests concurrently (their channels are collected
in queue order). `CancelWhenHasRunningBeforeStart` cancels active work before queuing
another operation, and ignores calls while an operation is still starting.

## Bounded history

The defaults keep the last **500 data events and 500 messages**, plus **50 extra buffer
positions per flow** for slow active collectors, with `DROP_OLDEST`. They are bounded,
not evidence of a leak. Actual retained memory depends on the objects referenced by
those entries. Cold/full flows expose the retained history, not an unlimited audit log.

```kotlin
val operation = splinter("profile", Strategy.oneShot {
    request { loadProfile() }
}) {
    scope(screenScope)
    stop(Splinter.StopPolicy.UntilRequest)
    history(data = 1, logs = 20, extraBufferCapacity = 50)
}
```

Data history must keep at least one entry because `resultHolder.get()` reads its latest
value. Log history can be zero when the extra buffer is positive. Reducing history
changes what new full/cold collectors can replay. `logging(false)` disables forwarding
to Lumber while retaining internal messages and statistics.

The Showcase Toolkit page contains executable local OneShot and polling tasks, cancellation,
failure/retry controls and source-generated snippets. No network is required. It compiles
the same library sources and regression tests against the existing EasyNavigation-compatible
Lumber pin. The published library keeps its own dependencies. External logging is disabled
in these demos; normal consumers use Splinter's declared Lumber version.

State persistence is supplied separately by
[Event Observer State](https://github.com/matheus-corregiari/arch-event-observer).
See the [state migration guide](../../../docs/state-migration.md).
