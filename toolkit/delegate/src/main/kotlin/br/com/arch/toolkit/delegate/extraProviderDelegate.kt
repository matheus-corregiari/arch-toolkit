package br.com.arch.toolkit.delegate

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlin.reflect.KProperty

class ExtraProviderDelegate<T>(
    private val extraName: String,
    private val keepState: Boolean,
    private val defaultValue: () -> T
) {

    private var extra: T? = null

    operator fun getValue(thisRef: AppCompatActivity, property: KProperty<*>): T {
        if (!keepState) return getExtra(extra, extraName, thisRef) ?: defaultValue.invoke()
        extra = getExtra(extra, extraName, thisRef)
        return extra ?: defaultValue.invoke()
    }

    operator fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        if (!keepState) return getExtra(extra, extraName, thisRef) ?: defaultValue.invoke()
        extra = getExtra(extra, extraName, thisRef)
        return extra ?: defaultValue.invoke()
    }

    operator fun setValue(thisRef: AppCompatActivity, property: KProperty<*>, value: T) {
        extra = value
    }

    operator fun setValue(thisRef: Fragment, property: KProperty<*>, value: T) {
        extra = value
    }
}

fun <T> extraProvider(extra: String) = extraProvider<T?>(extra, true)
fun <T> extraProvider(extra: String, keepState: Boolean) = extraProvider<T?>(extra, keepState, null)
fun <T> extraProvider(extra: String, default: T) = extraProvider(extra, true, default)
fun <T> extraProvider(extra: String, keepState: Boolean, default: T) = extraProvider(extra, keepState) { default }
fun <T> extraProvider(extra: String, default: () -> T) = ExtraProviderDelegate(extra, true, default)
fun <T> extraProvider(extra: String, keepState: Boolean, default: () -> T) =
        ExtraProviderDelegate(extra, keepState, default)

@Suppress("UNCHECKED_CAST")
private fun <T> getExtra(oldExtra: T?, extraName: String, thisRef: AppCompatActivity): T? =
        oldExtra ?: thisRef.intent?.extras?.get(extraName) as T?

@Suppress("UNCHECKED_CAST")
private fun <T> getExtra(oldExtra: T?, extraName: String, thisRef: Fragment): T? =
        oldExtra ?: thisRef.arguments?.get(extraName) as T?