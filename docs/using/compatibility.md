# Target Compatibility

| Module | Android | JVM | iOS arm64 | iOS simulator arm64 | JS | WasmJS |
|:-------|:-------:|:---:|:---------:|:-------------------:|:--:|:------:|
| `storage-core` | Yes | Yes | Yes | Yes | Yes | Yes |
| `storage-memory` | Yes | Yes | Yes | Yes | Yes | Yes |
| `storage-datastore` | Yes | Yes | Yes | Yes | Stub | Stub |
| `state-handle` | Yes | Yes | Yes | Yes | Yes | Yes |
| `splinter` | Yes | Yes | Yes | Yes | Yes | Yes |

## DataStore on Web

The web variants of `storage-datastore` exist so common source sets compile,
but constructing the provider throws `IllegalStateException`. Use
`storage-memory` or a web-specific provider on JS and WasmJS.

## Source Set Guidance

Declare broadly supported modules in `commonMain`. Place
`storage-datastore` construction in platform source sets and bind it to the
shared `StorageProvider` contract.

The repository currently builds Apple artifacts for `iosArm64` and
`iosSimulatorArm64`; it does not publish an `iosX64` variant.
