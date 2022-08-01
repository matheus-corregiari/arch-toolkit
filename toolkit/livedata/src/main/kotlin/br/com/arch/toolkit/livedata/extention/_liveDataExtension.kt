@file:JvmName("LiveDataUtils")

package br.com.arch.toolkit.livedata.extention

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

/**
 * Observes a LiveData<T> with non null values
 *
 * @param owner The desired Owner to observe
 * @param observer Will be called on every non null data
 */
inline fun <T> LiveData<T>.observeNotNull(
    owner: LifecycleOwner,
    crossinline observer: (T) -> Unit
) = observe(owner) { it?.let(observer) }

/**
 * Observes a LiveData<T> with null values
 *
 * @param owner The desired Owner to observe
 * @param observer Will be called on every non null data
 */
inline fun <T> LiveData<T>.observeNull(owner: LifecycleOwner, crossinline observer: () -> Unit) =
    observe(owner) {
        if (it == null) {
            observer.invoke()
        }
    }

/**
 * Observes a LiveData<T> with non null values only one time
 *
 * @param owner The desired Owner to observe
 * @param observer Will be called one time with a non null data
 */
fun <T> LiveData<T>.observeSingle(owner: LifecycleOwner, observer: ((T) -> Unit)) =
    observeUntil(owner) {
        it?.let(observer)
        it != null
    }

/**
 * Observes a LiveData<T> until a condition be true
 *
 * @param owner The desired Owner to observe
 * @param observer Will be called on every data changes until it returns true
 */
fun <T> LiveData<T>.observeUntil(owner: LifecycleOwner, observer: ((T?) -> Boolean)) =
    observe(owner, object : Observer<T> {
        override fun onChanged(data: T?) {
            if (data.let(observer)) removeObserver(this)
        }
    })