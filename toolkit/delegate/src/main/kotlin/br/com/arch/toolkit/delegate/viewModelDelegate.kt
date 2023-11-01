package br.com.arch.toolkit.delegate

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

inline fun <reified T : ViewModel> viewModelProvider() = viewModelProvider(T::class)

fun <T : ViewModel> viewModelProvider(kClass: KClass<T>) =
    ViewModelProviderDelegate(kClass = kClass)

class ViewModelProviderDelegate<out T : ViewModel> internal constructor(private val kClass: KClass<T>) {

    operator fun getValue(thisRef: AppCompatActivity, property: KProperty<*>): T {
        return thisRef.defaultViewModelProviderFactory.create(kClass.java)
    }

    operator fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        return thisRef.defaultViewModelProviderFactory.create(kClass.java)
    }

    operator fun getValue(thisRef: View, property: KProperty<*>): T {
        return (thisRef.context as AppCompatActivity).defaultViewModelProviderFactory.create(kClass.java)
    }
}
