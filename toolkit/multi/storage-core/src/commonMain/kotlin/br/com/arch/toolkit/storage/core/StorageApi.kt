package br.com.arch.toolkit.storage.core

/**
 * Marks APIs that belong to the storage DSL.
 *
 * This annotation is used as a [DslMarker] to clearly separate storage-related
 * builder functions and prevent accidental scope leakage when nesting multiple
 * DSLs together.
 *
 * ---
 *
 * ### Usage
 * Apply [StorageApi] to classes, functions, or extension functions that are part
 * of the storage DSL, such as [KeyValue] or [StorageProvider] helpers.
 *
 * ```kotlin
 * @StorageApi
 * fun <T> KeyValue<T?>.required(default: () -> T): KeyValue<T> { ... }
 * ```
 *
 * This ensures that DSL blocks using `storage-core` maintain a clear and safe scope.
 *
 * @see DslMarker
 */
@DslMarker
annotation class StorageApi
