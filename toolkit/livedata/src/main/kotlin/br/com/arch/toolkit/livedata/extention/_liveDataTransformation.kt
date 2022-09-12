@file:JvmName("LiveDataTransformations")

package br.com.arch.toolkit.livedata.extention

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import br.com.arch.toolkit.livedata.response.ResponseLiveData

/**
 * Transforms a LiveData<T> into a LiveData<R>
 *
 * @param transformation Receive the actual non null T value and return the transformed non null R value
 */
fun <T, R> LiveData<T>.map(transformation: (T) -> R): LiveData<R> {
    val liveData = MediatorLiveData<R>()
    liveData.addSource(this) {
        it?.let(transformation)?.let(liveData::setValue)
    }
    return liveData
}

/**
 * Transforms a LiveData<List<T>> into a LiveData<List<R>>
 *
 * @param transformation Receive the actual non null T value and return the transformed non null R value
 */
fun <T, R> LiveData<List<T>>.mapList(transformation: (T) -> R): LiveData<List<R>> {
    return map { it.map(transformation) }
}

/**
 * Transforms a ResponseLiveData<List<T>> into a ResponseLiveData<List<R>>
 *
 * @param transformAsync Indicate map will execute synchronously or asynchronously
 * @param transformation Receive the actual non null T value and return the transformed non null R value
 */
fun <T, R> ResponseLiveData<List<T>>.mapList(
    transformation: (T) -> R
): ResponseLiveData<List<R>> {
    return map { it.map(transformation) }
}