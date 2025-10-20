package br.com.arch.toolkit.stateHandle

import androidx.lifecycle.SavedStateHandle
import br.com.arch.toolkit.lumber.Lumber
import kotlinx.coroutines.flow.StateFlow
import kotlin.reflect.KProperty

sealed class StateValue<T> {
    protected abstract val key: String
    protected abstract val handle: SavedStateHandle

    abstract operator fun getValue(ref: Any, property: KProperty<*>): T
    abstract operator fun setValue(ref: Any, property: KProperty<*>, value: T)
    abstract fun flow(): StateFlow<T>

    companion object {
        fun <T> StateValue<T?>.required(default: () -> T) = Required(this, default)
        fun <T> StateValue<T?>.default(default: () -> T?) = WithDefault(this, default)
    }

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
