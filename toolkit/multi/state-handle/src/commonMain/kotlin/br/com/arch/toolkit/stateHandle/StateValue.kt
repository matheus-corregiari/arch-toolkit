package br.com.arch.toolkit.stateHandle

import androidx.lifecycle.SavedStateHandle
import br.com.arch.toolkit.lumber.Lumber
import br.com.arch.toolkit.stateHandle.StateValue.Optional
import br.com.arch.toolkit.stateHandle.StateValue.Required
import br.com.arch.toolkit.stateHandle.StateValue.WithDefault
import kotlinx.coroutines.flow.StateFlow
import kotlin.reflect.KProperty

/**
 * Represents a typed value stored in a [SavedStateHandle], supporting:
 *
 * - Delegated access via `by`
 * - Two-way read/write
 * - Reactive observation through [flow]
 *
 * This abstraction provides a consistent way to hold UI state across
 * configuration changes and screen recreation, while keeping the API
 * type-safe and concise.
 *
 * ## Variants
 *
 * | Type           | Nullable? | Persistence Behavior |
 * |----------------|-----------|----------------------|
 * | [Optional]     | Yes       | Stores/removes values normally. |
 * | [Required]     | No        | Guarantees a non-null value by supplying a default. |
 * | [WithDefault]  | Yes       | Returns a default on read, but does **not** persist it automatically. |
 *
 * ## When to use
 * - UI state that needs to be restorable.
 * - Small, lightweight data associated with screen logic.
 *
 * ## When **not** to use
 * - Long-term persistence (use storage/database instead).
 * - Large objects (store only identifiers/metadata here).
 *
 * ## Usage Example
 * ```
 * class MyViewModel(private val state: StateHandle) {
 *
 *     // Nullable state — may or may not exist.
 *     var filter by state.optional<String>("filter")
 *
 *     // Always non-null — default is applied when missing.
 *     var page by state.required("page") { 1 }
 *
 *     // Nullable with fallback only on read (not written back).
 *     var query by state.default("query") { "" }
 *
 *     fun next() { page += 1 }
 * }
 * ```
 */
sealed class StateValue<T> {
    /** Key used to store and retrieve this value. */
    protected abstract val key: String

    /** Underlying state storage implementation. */
    protected abstract val handle: SavedStateHandle

    /** Reads the value using delegated property syntax (`val x by value`). */
    abstract operator fun getValue(ref: Any, property: KProperty<*>): T

    /** Writes the value using delegated property syntax (`x = newValue`). */
    abstract operator fun setValue(ref: Any, property: KProperty<*>, value: T)

    /**
     * Returns a [StateFlow] representing this state.
     * Use in reactive UI (Compose, SwiftUI, etc.).
     */
    abstract fun flow(): StateFlow<T>

    companion object {
        /**
         * Converts an optional value into a [Required] one,
         * injecting a default when the value is missing.
         */
        fun <T> StateValue<T?>.required(default: () -> T) = Required(this, default)

        /**
         * Returns the default value **only when read**.
         * The default is **not** persisted automatically.
         */
        fun <T> StateValue<T?>.default(default: () -> T?) = WithDefault(this, default)
    }

    /**
     * Nullable state value stored in a [SavedStateHandle].
     *
     * Use when the value is optional (`T?`).
     */
    class Optional<T> internal constructor(
        override val key: String,
        override val handle: SavedStateHandle,
        private val getError: (Throwable) -> Unit = {},
        private val setError: (T, Throwable) -> Unit = { _, _ -> }
    ) : StateValue<T?>() {

        override fun flow(): StateFlow<T?> = handle.getStateFlow(key, null)

        override operator fun getValue(ref: Any, property: KProperty<*>): T? {
            val attName = key.ifBlank { property.name }
            return runCatching {
                if (handle.contains(attName)) {
                    handle.get<T>(attName)
                } else {
                    error("SavedStateHandle does not contain key $attName")
                }
            }.onFailure { error ->
                Lumber.tag("SavedStateHandle").wtf("Does not contain key - $attName")
                error.runCatching(getError)
            }.onSuccess {
                Lumber.tag("SavedStateHandle").info("Obtained with success - $attName")
            }.getOrNull()
        }

        override operator fun setValue(ref: Any, property: KProperty<*>, value: T?) {
            val attName = key.ifBlank { property.name }
            runCatching {
                if (value == null) {
                    handle.remove<T>(attName)
                } else {
                    handle.set(attName, value)
                }
            }.onFailure {
                if (value == null) {
                    Lumber.tag("SavedStateHandle").info("Removed with success - $attName")
                } else {
                    Lumber.tag("SavedStateHandle").wtf("Error saving - $attName", it)
                }
            }.onSuccess {
                if (value == null) {
                    Lumber.tag("SavedStateHandle").info("Removed with success - $attName")
                } else {
                    Lumber.tag("SavedStateHandle").info("Saved with success - $attName")
                }
            }.onFailure {
                value ?: return@onFailure
                runCatching { setError(value, it) }
            }
        }
    }

    /**
     * Non-nullable state value that guarantees a value by providing a default.
     */
    class Required<T> internal constructor(
        private val other: StateValue<T?>,
        private val default: () -> T,
    ) : StateValue<T>() {

        override val key: String = other.key
        override val handle: SavedStateHandle = other.handle

        override fun flow(): StateFlow<T> = other.handle.getStateFlow(other.key, default())

        override operator fun getValue(ref: Any, property: KProperty<*>): T =
            other.getValue(ref, property) ?: default()

        override operator fun setValue(ref: Any, property: KProperty<*>, value: T) =
            other.setValue(ref, property, value)
    }

    /**
     * Nullable state value that returns a default **when read**, without persisting it.
     */
    class WithDefault<T> internal constructor(
        private val other: StateValue<T?>,
        private val default: () -> T?,
    ) : StateValue<T?>() {

        override val key: String = other.key
        override val handle: SavedStateHandle = other.handle

        override fun flow(): StateFlow<T?> = other.handle.getStateFlow(
            key = other.key,
            initialValue = runCatching { default() }.getOrNull()
        )

        override operator fun getValue(ref: Any, property: KProperty<*>): T? =
            other.getValue(ref, property) ?: runCatching { default() }.getOrNull()

        override operator fun setValue(ref: Any, property: KProperty<*>, value: T?) =
            other.setValue(ref, property, value)
    }
}
