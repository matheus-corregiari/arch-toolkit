package br.com.arch.toolkit.storage.datastore

import br.com.arch.toolkit.storage.core.StorageProvider

/**
 * Stub [StorageProvider] for unsupported targets (JS/WASM).
 *
 * This function exists only to preserve API compatibility in multiplatform projects.
 * On platforms where DataStore is not available, calling [DataStoreProvider] will
 * immediately throw an [IllegalStateException].
 *
 * ---
 *
 * ### Behavior
 * - **Deprecated**: Always fails with `error("Target not supported yet!")`.
 * - Exists only so that common code can compile without `expect/actual` errors.
 * - Should never be invoked at runtime on unsupported platforms.
 *
 * ---
 *
 * ### Example
 * ```kotlin
 * // Common code compiles:
 * val provider: StorageProvider = DataStoreProvider()
 *
 * // But in JS/WASM targets this will throw:
 * // java.lang.IllegalStateException: Target not supported yet!
 * ```
 *
 * ---
 *
 * @throws IllegalStateException Always thrown when invoked.
 * @see DataStoreProvider For supported platforms (Android, JVM, Apple).
 */
@Deprecated("Target not supported")
@Suppress("DeprecatedCallableAddReplaceWith", "FunctionName")
fun DataStoreProvider(): StorageProvider = error("Target not supported yet!")
