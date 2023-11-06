package br.com.arch.toolkit.delegate

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlin.reflect.KProperty

class ExtraProviderDelegate<T>(
    private val extraName: String,
    private val keepState: Boolean,
    private val type: ExtraType,
    private val defaultValue: () -> T
) {

    private var extra: T? = null

    operator fun getValue(thisRef: AppCompatActivity, property: KProperty<*>): T {
        val newExtra = when (type) {
            ExtraType.ARGUMENT -> getExtra(extra, extraName, thisRef) ?: defaultValue.invoke()
            ExtraType.QUERY -> getQueryParameter(extra, extraName, thisRef) ?: defaultValue.invoke()
            ExtraType.AUTO -> getExtra(extra, extraName, thisRef)
                ?: (if (extra is String?) getQueryParameter(extra, extraName, thisRef) else null)
                ?: defaultValue.invoke()
        }

        if (keepState) {
            extra = newExtra
        }

        return newExtra
    }

    operator fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        val newExtra = when (type) {
            ExtraType.ARGUMENT -> getExtra(extra, extraName, thisRef) ?: defaultValue.invoke()
            ExtraType.QUERY -> getQueryParameter(extra, extraName, thisRef) ?: defaultValue.invoke()
            ExtraType.AUTO -> getExtra(extra, extraName, thisRef)
                ?: (if (extra is String?) getQueryParameter(extra, extraName, thisRef) else null)
                ?: defaultValue.invoke()
        }

        if (keepState) {
            extra = newExtra
        }

        return newExtra
    }

    operator fun setValue(thisRef: AppCompatActivity, property: KProperty<*>, value: T) {
        extra = value
    }

    operator fun setValue(thisRef: Fragment, property: KProperty<*>, value: T) {
        extra = value
    }

    //region AppCompatActivity methods
    @Suppress("UNCHECKED_CAST", "DEPRECATION")
    private fun <T> getExtra(oldExtra: T?, extraName: String, thisRef: AppCompatActivity): T? =
        oldExtra ?: thisRef.intent?.extras?.get(extraName) as T?

    @Suppress("UNCHECKED_CAST")
    private fun getQueryParameter(oldExtra: T?, extraName: String, thisRef: AppCompatActivity): T? {
        val isStringType = oldExtra is String?
        if (isStringType) {
            return oldExtra ?: thisRef.intent?.data?.getQueryParameter(extraName) as T?
        } else {
            error("Query parameters can only be used with String type parameters")
        }
    }
    //endregion

    //region Fragment methods
    @Suppress("UNCHECKED_CAST", "DEPRECATION")
    private fun <T> getExtra(oldExtra: T?, extraName: String, thisRef: Fragment): T? =
        oldExtra ?: thisRef.arguments?.get(extraName) as T?

    @Suppress("UNCHECKED_CAST")
    private fun getQueryParameter(oldExtra: T?, extraName: String, thisRef: Fragment): T? {
        val isStringType = oldExtra is String?
        if (isStringType) {
            return oldExtra ?: thisRef.activity?.intent?.data?.getQueryParameter(extraName) as T?
        } else {
            error("Query parameters can only be used with String type parameters")
        }
    }
    //endregion
}

enum class ExtraType {
    ARGUMENT, QUERY, AUTO
}

fun <T> extraProvider(extra: String) = extraProvider<T?>(extra, true)
fun <T> extraProvider(extra: String, keepState: Boolean) = extraProvider<T?>(extra, keepState, null)
fun <T> extraProvider(extra: String, default: T) = extraProvider(extra, true, default)
fun <T> extraProvider(extra: String, keepState: Boolean, default: T) =
    extraProvider(extra, keepState, ExtraType.AUTO) { default }

fun <T> extraProvider(extra: String, type: ExtraType) = extraProvider<T?>(extra, true, type)
fun <T> extraProvider(extra: String, keepState: Boolean, type: ExtraType) =
    extraProvider<T?>(extra, keepState, type, null)

fun <T> extraProvider(extra: String, type: ExtraType, default: T) =
    extraProvider(extra, true, type, default)

fun <T> extraProvider(extra: String, keepState: Boolean, type: ExtraType, default: T) =
    extraProvider(extra, keepState, type) { default }

fun <T> extraProvider(extra: String, keepState: Boolean, type: ExtraType, default: () -> T) =
    ExtraProviderDelegate(extra, keepState, type, default)
