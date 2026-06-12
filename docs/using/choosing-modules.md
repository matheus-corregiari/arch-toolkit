# Choosing Modules

Choose the smallest set that owns the behavior you need.

| Need | Module |
|:-----|:-------|
| Define storage-facing contracts | `storage-core` |
| Keep values only for the current process | `storage-memory` |
| Persist preferences on Android, JVM, or Apple | `storage-datastore` |
| Restore small ViewModel or UI values | `state-handle` |
| Coordinate requests, polling, cache, or mirrored flows | `splinter` |

## Common Combinations

Use `storage-core` plus `storage-datastore` in production and
`storage-memory` in tests when callers should not know the backend.

Use `state-handle` for screen state and a storage provider for durable
preferences. They solve different lifetime problems.

Splinter is independent from the storage modules. Add it only when request
execution needs policies, lifecycle handling, polling, cache behavior, or
observable result state.

## Avoid Installing Everything

There is no umbrella artifact. This is intentional: dependencies and platform
constraints remain visible at the module boundary.
