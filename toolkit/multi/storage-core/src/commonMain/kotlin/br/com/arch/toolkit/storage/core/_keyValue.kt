@file:Suppress("MemberVisibilityCanBePrivate", "KDocUnresolvedReference")

package br.com.arch.toolkit.storage.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SnapshotMutationPolicy
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import br.com.arch.toolkit.storage.core.KeyValue.Companion.map
import br.com.arch.toolkit.storage.core.KeyValue.Companion.required
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.timeout
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.time.Duration.Companion.milliseconds

/**
 * Returns the current value of this [KeyValue] immediately.
 *
 * This is a platform-specific, blocking shortcut to fetch the most recent value
 * without collecting the [Flow]. It should be used in cases where synchronous
 * access is required (e.g., property delegates, initialization).
 *
 * ---
 *
 * ### Behavior
 * - On supported platforms, this function may block the current thread.
 * - Falls back to [KeyValue.lastValue] if no value is available in the flow.
 *
 * ---
 *
 * ### Example
 * ```kotlin
 * val userName = storageProvider.string("user_name")
 *
 * // Synchronous read
 * println("User: ${userName.instant()}")
 * ```
 *
 * @throws IllegalStateException if the platform implementation cannot provide a value.
 */
expect fun <T> KeyValue<T>.instant(): T

/**
 * Provides the default [CoroutineScope] used by [KeyValue] operations
 * when no custom scope is supplied.
 *
 * This is a platform-specific helper that defines where asynchronous storage
 * operations (e.g., [KeyValue.set]) should run by default.
 *
 * ---
 *
 * ### Platform defaults
 * - **Java (Android/JVM):** Uses [Dispatchers.IO].
 * - **Apple (iOS/macOS):** Uses [Dispatchers.Default].
 * - **Web (JS/WASM):** Uses [Dispatchers.Default].
 *
 * ---
 *
 * ### Example
 * ```kotlin
 * val counter = storageProvider.int("counter")
 *
 * // Uses platform default scope internally
 * counter.set(42)
 * ```
 *
 * You can override this behavior by calling [KeyValue.scope].
 */
internal expect fun <T> KeyValue<T>.defaultScope(): CoroutineScope

/**
 * Represents a generic reactive key–value entry.
 *
 * A [KeyValue] abstracts reading and writing a single value of type [DATA],
 * providing reactive observation through [Flow], integration with Jetpack Compose
 * state management, and delegation support for property syntax.
 *
 * ### Core responsibilities
 * - **Reactive access:** Consumers observe changes through [get] which returns a [Flow].
 * - **Synchronous access:** Consumers can fetch the current value via [instant] (platform-specific).
 * - **Mutable state integration:** Provides [state] to bind values directly to Compose UI.
 * - **Delegated properties:** Allows `by` syntax using [delegate].
 *
 * This class is not tied to any particular storage backend. Implementations may wrap
 * persistent storage (e.g., DataStore), in-memory caches, or any custom provider.
 *
 * ---
 *
 * ### Example: Simple read & write
 * ```kotlin
 * val userName = storageProvider.string("user_name")
 *
 * // Set a value
 * userName.set("Alice")
 *
 * // Observe changes
 * lifecycleScope.launch {
 *     userName.get().collect { println("User is now $it") }
 * }
 * ```
 *
 * ### Example: Using Compose integration
 * ```kotlin
 * @Composable
 * fun UserNameInput(userName: KeyValue<String?>) {
 *     val state = userName.state()
 *     TextField(
 *         value = state.value.orEmpty(),
 *         onValueChange = { state.value = it }
 *     )
 * }
 * ```
 *
 * ### Example: Delegated property
 * ```kotlin
 * var counter by storageProvider.int("counter").required { 0 }.delegate()
 *
 * // Now works as a normal property
 * counter += 1
 * println("Counter is $counter")
 * ```
 *
 * @param DATA The type of the stored value.
 *
 * @see StorageProvider For factory methods to create [KeyValue] instances.
 * @see required To enforce non-null values.
 * @see map To transform values between types.
 */
@StorageApi
abstract class KeyValue<DATA> {

    protected var scope: CoroutineScope = defaultScope()
        private set

    abstract var lastValue: DATA
        protected set

    abstract fun get(): Flow<DATA>
    abstract fun set(value: DATA, scope: CoroutineScope = this.scope)

    fun scope(scope: CoroutineScope) = apply { this.scope = scope }

    @OptIn(FlowPreview::class)
    suspend fun current(): DATA = runCatching {
        get().timeout(50.milliseconds).catch { emit(lastValue) }.firstOrNull()
    }.getOrNull() ?: lastValue

    @Composable
    fun state(scope: CoroutineScope = this.scope): MutableState<DATA> {
        val current by get().collectAsState(lastValue)
        return remember(current, scope) {
            mutableStateOf(
                value = current,
                policy = object : SnapshotMutationPolicy<DATA> {
                    override fun equivalent(a: DATA, b: DATA) = (a == b).also { set(b, scope) }
                }
            )
        }
    }

    fun delegate() = object : ReadWriteProperty<Any, DATA> {
        override fun getValue(thisRef: Any, property: KProperty<*>): DATA = instant()

        override fun setValue(thisRef: Any, property: KProperty<*>, value: DATA) =
            set(value = value, scope = scope)
    }

    companion object {
        @StorageApi
        fun <T> KeyValue<T?>.required(): KeyValue<T> = RequiredKeyValue(
            keyValue = this,
            default = null
        )

        @StorageApi
        fun <T> KeyValue<T?>.required(default: () -> T): KeyValue<T> = RequiredKeyValue(
            keyValue = this,
            default = default
        )

        @StorageApi
        fun <T, R> KeyValue<T>.map(mapTo: (T) -> R, mapBack: (R) -> T): KeyValue<R> = MapKeyValue(
            keyValue = this,
            mapTo = mapTo,
            mapBack = mapBack
        )
    }
}
