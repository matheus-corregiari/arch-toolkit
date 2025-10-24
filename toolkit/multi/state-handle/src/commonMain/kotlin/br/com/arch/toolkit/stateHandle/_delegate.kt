package br.com.arch.toolkit.stateHandle

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.arch.toolkit.result.DataResult
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * ViewModel delegate that exposes a [ViewModelState.Regular] backed by [SavedStateHandle].
 *
 * ## What it does
 * - Binds a typed, observable state named `name` (or the property name if blank).
 * - Persists via `SavedStateHandle`, falling back to JSON string when direct write fails.
 * - Emits `Flow<T?>` for UI observation.
 *
 * ## Parameters
 * @param name Optional explicit key for the state (defaults to property name).
 * @param default Optional initial value to set on first access.
 *
 * ## Returns
 * A `ReadOnlyProperty<ViewModel, ViewModelState.Regular<T>>` for `by` delegation.
 *
 * ## Example
 * ```
 * class MyVm(
 *   private val handle: SavedStateHandle
 * ) : ViewModel() {
 *
 *   val user by handle.saveState<User>(default = null)
 *
 *   fun setUser(u: User?) { user.set(u) }
 *   fun userFlow(): Flow<User?> = user.flow()
 * }
 * ```
 */
inline fun <reified T : Any> SavedStateHandle.saveState(
    name: String = "",
    default: T? = null
): ReadOnlyProperty<ViewModel, ViewModelState.Regular<T>> =
    object : ReadOnlyProperty<ViewModel, ViewModelState.Regular<T>>, KoinComponent {
        var state: ViewModelState.Regular<T>? = null
        override fun getValue(thisRef: ViewModel, property: KProperty<*>) = state ?: kotlin.run {
            val json by inject<Json>()
            ViewModelState.Regular(
                name = name.ifBlank { property.name },
                json = json,
                serializer = runCatching { serializer<T>() }.getOrNull(),
                stateHandle = this@saveState,
                scope = thisRef.viewModelScope
            ).also {
                it.set(default)
                state = it
            }
        }
    }

/**
 * ViewModel delegate that exposes a [ViewModelState.Result] backed by [SavedStateHandle],
 * emitting `Flow<DataResult<T>>` and keeping last success in saved state.
 *
 * Use when you have a request/response lifecycle and still want the last
 * known good value to survive process death or configuration changes.
 *
 * ## Parameters
 * @param name Optional explicit key for the state (defaults to property name).
 * @param default Optional initial result to set on first access.
 *
 * ## Returns
 * A `ReadOnlyProperty<ViewModel, ViewModelState.Result<T>>` for `by` delegation.
 *
 * ## Example
 * ```
 * class MyVm(private val handle: SavedStateHandle) : ViewModel() {
 *   val profile by handle.saveResponseState<User>()
 *
 *   fun refresh() = profile.load { repo.fetchUser() }
 *   fun results(): Flow<DataResult<User>> = profile.flow()
 * }
 * ```
 */
inline fun <reified T : Any> SavedStateHandle.saveResponseState(
    name: String = "",
    default: DataResult<T>? = null
): ReadOnlyProperty<ViewModel, ViewModelState.Result<T>> =
    object : ReadOnlyProperty<ViewModel, ViewModelState.Result<T>>, KoinComponent {
        var state: ViewModelState.Result<T>? = null
        override fun getValue(thisRef: ViewModel, property: KProperty<*>) = state ?: kotlin.run {
            val json by inject<Json>()
            ViewModelState.Result(
                name = name.ifBlank { property.name },
                json = json,
                serializer = runCatching { serializer<T>() }.getOrNull(),
                stateHandle = this@saveResponseState,
                scope = thisRef.viewModelScope
            ).also {
                it.set(default)
                state = it
            }
        }
    }

/**
 * Overload of [saveResponseState] that accepts a plain `T?` default.
 *
 * Sets the underlying data immediately; the first emitted `DataResult` will be `None`
 * until `load { ... }` is called or another result is posted.
 */

inline fun <reified T : Any> SavedStateHandle.saveResponseState(
    name: String = "",
    default: T? = null
): ReadOnlyProperty<ViewModel, ViewModelState.Result<T>> =
    object : ReadOnlyProperty<ViewModel, ViewModelState.Result<T>>, KoinComponent {
        var state: ViewModelState.Result<T>? = null
        override fun getValue(thisRef: ViewModel, property: KProperty<*>) = state ?: kotlin.run {
            val json by inject<Json>()
            ViewModelState.Result(
                name = name.ifBlank { property.name },
                json = json,
                serializer = runCatching { serializer<T>() }.getOrNull(),
                stateHandle = this@saveResponseState,
                scope = thisRef.viewModelScope
            ).also {
                it.set(default)
                state = it
            }
        }
    }

/**
 * Creates an **optional** state delegate bound to a [SavedStateHandle], supporting
 * `by` delegation and reactive observation via [StateValue.flow].
 *
 * ## What it does
 * Constructs a [StateValue.Optional] that:
 * - Reads/writes a nullable value associated with a key in the `SavedStateHandle`.
 * - If `key` is blank, the **property name** of the delegate is used.
 * - Assigning `null` **removes** the entry from the state handle.
 *
 * ## When to use
 * - UI state that must survive configuration/process recreation.
 * - Values that are genuinely **optional** (`T?`) in the ViewModel state.
 *
 * ## When **not** to use
 * - Long-term persistence (database, file storage).
 * - Large payloads (images, binary blobs). Store **identifiers**, not data.
 *
 * ## Parameters
 * @param key Key used in `SavedStateHandle`. If empty, defaults to the delegated property name.
 * @param getError Callback invoked when a read operation fails. Does not throw.
 * @param setError Callback invoked when a write operation fails. Receives the attempted value and the error. Does not throw.
 *
 * ## Returns
 * A [StateValue.Optional] to be used with `by` delegation.
 *
 * ## Example
 * ```
 * class MyViewModel(private val state: SavedStateHandle) : ViewModel() {
 *
 *     var query by state.value<String?>(key = "query")
 *
 *     fun update(q: String) {
 *         query = q   // saves to SavedStateHandle
 *     }
 *
 *     fun clear() {
 *         query = null // removes key from SavedStateHandle
 *     }
 * }
 * ```
 */
fun <T : Any> SavedStateHandle.value(
    key: String = "",
    getError: (Throwable) -> Unit = {},
    setError: (T, Throwable) -> Unit = { _, _ -> },
) = StateValue.Optional(
    key = key,
    handle = this,
    getError = getError,
    setError = setError
)

