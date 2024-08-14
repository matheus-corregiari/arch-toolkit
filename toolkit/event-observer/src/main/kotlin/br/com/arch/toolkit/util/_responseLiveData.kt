@file:JvmName("ResponseLiveDataUtils")
@file:Suppress("TooManyFunctions")

package br.com.arch.toolkit.util

import br.com.arch.toolkit.livedata.MutableResponseLiveData
import br.com.arch.toolkit.livedata.ResponseLiveData
import br.com.arch.toolkit.livedata.SwapResponseLiveData
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus

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
 * Transforms a ResponseLiveData<List<T>> into a ResponseLiveData<List<R>>
 *
 * @param transformation Receive the actual non null T value and return the transformed non null R value
 */
fun <T, R> ResponseLiveData<List<T>>.mapList(
    transformation: (T) -> R
): ResponseLiveData<List<R>> {
    return map { it.map(transformation) }
}
