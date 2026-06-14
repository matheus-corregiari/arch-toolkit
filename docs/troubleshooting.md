# Troubleshooting

## Dependency Cannot Be Resolved

Confirm Maven Central is enabled and replace `<latest-version>` with the version
shown by the Maven Central badge on the [home page](index.md).

## Missing Target Variant

Check the [compatibility matrix](using/compatibility.md). In particular,
`iosX64` is not published and `storage-datastore` has no operational JS or
WasmJS backend.

## DataStore Fails on Web

This is expected. Use `MemoryStoreProvider` or bind another implementation of
`StorageProvider` for web targets.

## A KeyValue Write Appears Delayed

`set` launches work in a coroutine scope. Provide an application-owned scope
with `scope(...)`, or use the provider from a lifecycle that remains active
long enough to finish the write.

## Saved State Rejects a Value

Keep state values small and compatible with the platform's saved-state
mechanism. Persist durable or large data through a storage or database layer.

## Splinter Does Not Start a Second Request

The default execution policy ignores a new execution while one is running.
Select `SequentialQueue`, `ParallelQueue`, or
`CancelWhenHasRunningBeforeStart` when overlap must behave differently.

## Repository Build Cannot See Sample Tasks

Sample projects are opt-in:

```bash
./gradlew -PincludeSamples=true projects
```

See [Known Sample Limitations](sample/limitations.md) before treating a sample
failure as a library regression.
