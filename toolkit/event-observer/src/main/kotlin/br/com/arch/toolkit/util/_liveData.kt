@file:JvmName("LiveDataUtils")
@file:Suppress("TooManyFunctions")

package br.com.arch.toolkit.util

import android.os.Looper
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.map
import br.com.arch.toolkit.livedata.MutableResponseLiveData
import br.com.arch.toolkit.livedata.ResponseLiveData
import br.com.arch.toolkit.livedata.SwapResponseLiveData
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus

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
    observe(
        owner,
        object : Observer<T> {
            override fun onChanged(value: T) {
                if (value.let(observer)) removeObserver(this)
            }
        }
    )

/**
 * Returns an instance of a ResponseLiveData<T> with the desired value
 *
 * @param value The default value set
 * @param status The default status set
 *
 * @return An instance of ResponseLiveData<T> with a default value set
 */
fun <T> responseLiveDataOf(value: T, status: DataResultStatus = DataResultStatus.SUCCESS) =
    ResponseLiveData(DataResult(value, null, status))

/**
 * Returns an instance of a ResponseLiveData<T> with an error
 *
 * @param error The default error set
 *
 * @return An instance of ResponseLiveData<T> with an error set
 */
fun <T> responseLiveDataOf(error: Throwable) =
    ResponseLiveData<T>(DataResult(null, error, DataResultStatus.ERROR))

/**
 * Returns an instance of a MutableResponseLiveData<T> with the desired value
 *
 * @param value The default value set
 * @param status The default status set
 *
 * @return An instance of MutableResponseLiveData<T> with a default value set
 */
fun <T> mutableResponseLiveDataOf(value: T, status: DataResultStatus = DataResultStatus.SUCCESS) =
    MutableResponseLiveData(DataResult(value, null, status))

/**
 * Returns an instance of a MutableResponseLiveData<T> with an error
 *
 * @param error The default error set
 *
 * @return An instance of MutableResponseLiveData<T> with an error set
 */
fun <T> mutableResponseLiveDataOf(error: Throwable) =
    MutableResponseLiveData<T>(DataResult(null, error, DataResultStatus.ERROR))

/**
 * Returns an instance of a SwapResponseLiveData<T> with the desired value
 *
 * @param value The default value set
 * @param status The default status set
 *
 * @return An instance of SwapResponseLiveData<T> with a default value set
 */
fun <T> swapResponseLiveDataOf(value: T, status: DataResultStatus = DataResultStatus.SUCCESS) =
    SwapResponseLiveData(DataResult(value, null, status))

/**
 * Returns an instance of a SwapResponseLiveData<T> with an error
 *
 * @param error The default error set
 *
 * @return An instance of SwapResponseLiveData<T> with an error set
 */
fun <T> swapResponseLiveDataOf(error: Throwable) =
    SwapResponseLiveData<T>(DataResult(null, error, DataResultStatus.ERROR))

/**
 * Transforms a LiveData<List<T>> into a LiveData<List<R>>
 *
 * @param transformation Receive the actual non null T value and return the transformed non null R value
 */
fun <T, R> LiveData<List<T>?>.mapList(transformation: (T) -> R): LiveData<List<R>?> {
    return map { it?.map(transformation) }
}

/**
 * Transforms a ResponseLiveData<List<T>> into a ResponseLiveData<List<R>>
 *
 * @param transformation Receive the actual non null T value and return the transformed non null R value
 */
fun <T, R> ResponseLiveData<List<T>>.mapList(
    transformation: (T) -> R
): ResponseLiveData<List<R>> {
    return map { it.map(transformation) }
}

/**
 * This code defines an extension function named safePostValue for the MutableLiveData class in Kotlin.
 * Let's break down its purpose and functionality:
 *
 * - Purpose:
 * The primary goal of this function is to provide a safe way to update the value of a MutableLiveData object, regardless of whether the code is being executed on the main thread (UI thread) or a background thread.
 *
 * - Explanation:
 *  1. Extension Function: The safePostValue function is defined as an extension function for MutableLiveData<T>, allowing you to call it directly on any MutableLiveData instance.
 *  2. Type Parameter: The <T> indicates that this function is generic and can work with MutableLiveData objects of any type.
 *  3. Thread Safety: The core logic of the function ensures thread safety when updating the MutableLiveData. It checks if the current thread is the main thread (UI thread) using Looper.getMainLooper()?.isCurrentThread == true.
 *
 * - Main Thread: If on the main thread, it directly sets the value of the MutableLiveData using this.value = value. This is safe because modifications to UI elements should always happen on the main thread.
 * - Background Thread: If on a background thread, it uses postValue(value) to schedule the update to be executed on the main thread. This prevents potential crashes or unexpected behavior that can occur when modifying UI elements from background threads.
 *
 * ## Example Usage:
 * ```kotlin
 * val liveData = MutableLiveData<String>()
 *
 * // On a background thread
 * someBackgroundTask {
 *     val result = performSomeOperation()
 *     liveData.safePostValue(result) // Safely updates the LiveData
 * }
 * ```
 *
 * > In summary, the safePostValue extension function provides a convenient and thread-safe way to update MutableLiveData objects, ensuring that UI updates are always performed on the main thread, even when called from background threads.
 */
fun <T> MutableLiveData<T>.safePostValue(value: T?) {
    if (Looper.getMainLooper()?.isCurrentThread == true) {
        this.value = value
    } else {
        postValue(value)
    }
}
