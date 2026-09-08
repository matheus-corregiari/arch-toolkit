# Storage migration for RC17

Arch Toolkit no longer builds or publishes storage-core, storage-memory or storage-datastore.
Use [Arch Storage 1.0.0](https://github.com/matheus-corregiari/arch-storage/releases/tag/1.0.0).
The group `io.github.matheus-corregiari` and packages `br.com.arch.toolkit.storage.*` stay unchanged.

Replace local project dependencies with the independent Maven artifacts. The sample catalog applies
one shared strict 1.0.0 constraint to all storage modules: Gradle otherwise ranks old 2.0.0 RCs higher.

```kotlin
implementation("io.github.matheus-corregiari:storage-datastore") {
    version { strictly("1.0.0") }
}
```

Apply the same constraint to core and memory when used. Resolve conflicting callers together.

```sh
./gradlew :sample:shared:structure:repository:dependencyInsight --dependency storage-core --configuration jvmRuntimeClasspath -Pandroid.injected.invoked.from.ide=true
```

The enabled sample targets are Android and desktop, using DataStore. Web provider sources use memory;
DataStore is unavailable on web. Storage supports iOS ARM64 and Simulator ARM64, without iosX64.
See [upstream behavioral changes](https://github.com/matheus-corregiari/arch-storage/blob/master/docs/migration-1.0.0.md)
for cancellation, caching, Compose state and asynchronous persistence corrections.
