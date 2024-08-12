@file:JvmName("LiveDataUtils")
@file:Suppress("TooManyFunctions")

package br.com.arch.toolkit.util

import android.os.Looper
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.map

/**
 * Observes a [LiveData] with non-null values.
 *
 * @param owner The [LifecycleOwner] to observe.
 * @param observer Will be called on every non-null data change.
 *
 * Example usage:
 * ```
 * liveData.observeNotNull(this) { data ->
 *     println("Non-null data: $data")
 * }
 * ```
 */
inline fun <T> LiveData<T>.observeNotNull(
    owner: LifecycleOwner,
    crossinline observer: (T) -> Unit
) = observe(owner) { it?.let(observer) }

/**
 * Observes a [LiveData] with null values.
 *
 * @param owner The [LifecycleOwner] to observe.
 * @param observer Will be called whenever the data is null.
 *
 * Example usage:
 * ```
 * liveData.observeNull(this) {
 *     println("Data is null")
 * }
 * ```
 */
inline fun <T> LiveData<T>.observeNull(owner: LifecycleOwner, crossinline observer: () -> Unit) =
    observe(owner) {
        if (it == null) {
            observer.invoke()
        }
    }

/**
 * Observes a [LiveData] with non-null values only once.
 *
 * @param owner The [LifecycleOwner] to observe.
 * @param observer Will be called one time with a non-null data.
 *
 * Example usage:
 * ```
 * liveData.observeSingle(this) { data ->
 *     println("Received single non-null data: $data")
 * }
 * ```
 */
fun <T> LiveData<T>.observeSingle(owner: LifecycleOwner, observer: ((T) -> Unit)) =
    observeUntil(owner) {
        it?.let(observer)
        it != null
    }

/**
 * Observes a [LiveData] until a condition is met.
 *
 * @param owner The [LifecycleOwner] to observe.
 * @param observer Will be called on every data change until it returns true.
 *
 * Example usage:
 * ```
 * liveData.observeUntil(this) { data ->
 *     println("Observing data: $data")
 *     data == expectedValue
 * }
 * ```
 */
fun <T> LiveData<T>.observeUntil(owner: LifecycleOwner, observer: ((T?) -> Boolean)) =
    observe(
        owner,
        object : Observer<T> {
            override fun onChanged(value: T) {
                if (value.let(observer)) removeObserver(this)
            }
        }
    )

/**
 * Transforms a [LiveData] of a list of items into a [LiveData] of a list of transformed items.
 *
 * @param transformation The transformation function that receives a non-null [T] value and returns a non-null [R] value.
 * @return A [LiveData] containing the transformed list.
 *
 * Example usage:
 * ```
 * val transformedLiveData: LiveData<List<String>?> = liveData.mapList { item ->
 *     item.toString()
 * }
 * ```
 */
fun <T, R> LiveData<List<T>?>.mapList(transformation: (T) -> R): LiveData<List<R>?> {
    return map { it?.map(transformation) }
}

/**
 * Transforms a [LiveData] object of type [T] into a [LiveData] object of type [R], applying the provided transformation function and filtering out null results.
 *
 * @param transform A function that transforms a value of type [T] to a value of type [R] or `null`.
 * @return A new [LiveData] object that emits the non-null results of applying the transformation function to the values emitted by the source [LiveData].
 *
 * Example usage:
 * ```
 * val transformedLiveData: LiveData<String> = liveData.mapNotNull { value ->
 *     value?.toString()
 * }
 * ```
 */
fun <T, R> LiveData<T>.mapNotNull(
    transform: (T) -> R?
): LiveData<R> {
    val result = MediatorLiveData<R>()
    val applyTransform: (T) -> Unit = { value -> transform(value)?.let(result::setValue) }
    value.takeIf { isInitialized && it != null }?.let(applyTransform)
    result.addSource(this, applyTransform)
    return result
}

/**
 * Safely updates the value of a [MutableLiveData] from any thread.
 *
 * @param value The value to be set.
 *
 * This method ensures that the update is performed on the main thread.
 * If called from a background thread, it uses [MutableLiveData.postValue] to schedule the update.
 *
 * Example usage:
 * ```
 * val liveData = MutableLiveData<String>()
 *
 * someBackgroundTask {
 *     val result = performSomeOperation()
 *     liveData.safePostValue(result) // Safely updates the LiveData
 * }
 * ```
 */
fun <T> MutableLiveData<T>.safePostValue(value: T?) {
    if (Looper.getMainLooper()?.isCurrentThread == true) {
        this.value = value
    } else {
        postValue(value)
    }
}
