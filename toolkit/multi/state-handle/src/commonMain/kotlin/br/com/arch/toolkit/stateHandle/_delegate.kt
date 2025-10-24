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

