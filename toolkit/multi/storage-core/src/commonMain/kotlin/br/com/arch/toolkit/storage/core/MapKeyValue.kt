package br.com.arch.toolkit.storage.core

import br.com.arch.toolkit.storage.core.KeyValue.Companion.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map

/**
 * A [KeyValue] adapter that transforms values between two types.
 *
 * [MapKeyValue] wraps another [KeyValue] and exposes its data as a different type by
 * applying mapping functions both ways:
 *
 * - [mapTo]: Converts the underlying value into the exposed type [Transformed].
 * - [mapBack]: Converts the exposed value back into the underlying type [Current].
 *
 * This allows building higher-level abstractions on top of existing [KeyValue] entries,
 * such as mapping `String` values to `Enum`, or serializing/deserializing models.
 *
 * ---
 *
 * ### Example: Enum mapping
 * ```kotlin
 * enum class Theme { Light, Dark }
 *
 * val themePref: KeyValue<String?> = storageProvider.string("theme")
 *
 * val theme: KeyValue<Theme> = themePref
 *     .required { Theme.Light }
 *     .map(
 *         mapTo = { value -> Theme.valueOf(value ?: "Light") },
 *         mapBack = { it.name }
 *     )
 *
 * // Store enum directly
 * theme.set(Theme.Dark)
 *
 * // Observe as enum
 * lifecycleScope.launch {
 *     theme.get().collect { println("Theme is $it") }
 * }
 * ```
 *
 * @param Current The underlying type stored by the wrapped [KeyValue].
 * @param Transformed The exposed type after applying the transformation.
 *
 * @see KeyValue.map Convenient factory method.
 */
@StorageApi
internal class MapKeyValue<Current, Transformed> internal constructor(
    private val keyValue: KeyValue<Current>,
    private val mapTo: (Current) -> Transformed,
    private val mapBack: (Transformed) -> Current
) : KeyValue<Transformed>() {

    override var lastValue: Transformed
        get() = mapTo.invokeCatching(keyValue.lastValue).getOrThrow()
        set(value) = set(value)

    override fun get() = keyValue.get().map(mapTo)

    override fun set(value: Transformed, scope: CoroutineScope) {
        val transformed = mapBack.invokeCatching(value).getOrNull()
        transformed?.let { keyValue.set(transformed, scope) }
    }

    private fun <T, R> ((T) -> R).invokeCatching(data: T) = runCatching { invoke(data) }
}
