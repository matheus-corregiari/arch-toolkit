package br.com.arch.toolkit.util

import br.com.arch.toolkit.livedata.ResponseLiveData
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus
import br.com.arch.toolkit.result.DataResultStatus.ERROR
import br.com.arch.toolkit.result.DataResultStatus.LOADING
import br.com.arch.toolkit.result.DataResultStatus.NONE
import br.com.arch.toolkit.result.DataResultStatus.SUCCESS
import kotlin.math.max

//region Data Result Creator Methods
/**
 * Creates a new [DataResult] with:
 * - data: `<passed as argument>`
 * - error: null
 * - status: SUCCESS
 *
 * Usage:
 * ```kotlin
 * val result = dataResultSuccess(
 *     data = null /* Your nullable data */
 * )
 * ```
 *
 * @param data Data that will be set inside the [DataResult].
 *
 * @return A [DataResult] with the specified data, null error, and [DataResultStatus.SUCCESS] status.
 *
 * @see DataResult
 * @see DataResultStatus
 */
fun <T> dataResultSuccess(data: T?) = DataResult(data, null, SUCCESS)

/**
 * Creates a new [DataResult] with:
 * - data: `<passed as optional argument>`
 * - error: `<passed as optional argument>`
 * - status: LOADING
 *
 * Usage:
 * ```kotlin
 * val result = dataResultLoading<String>(
 *     data = null, /* (optional) Your Optional Data */
 *     error = null /* (optional) Your Optional Exception */
 * )
 * ```
 *
 * @param data (optional) Data that will be set inside the [DataResult].
 * @param error (optional) Error that will be set inside the [DataResult].
 *
 * @return A [DataResult] with the specified data, error, and [DataResultStatus.LOADING] status.
 *
 * @see DataResult
 * @see DataResultStatus
 */
fun <T> dataResultLoading(data: T? = null, error: Throwable? = null) =
    DataResult(data, error, LOADING)

/**
 * Creates a new [DataResult] with:
 * - data: `<passed as optional argument>`
 * - error: `<passed as argument>`
 * - status: ERROR
 *
 * Usage:
 * ```kotlin
 * val result = dataResultError<String>(
 *     error = null, /* Your Exception */
 *     data = null   /* (optional) Your Optional Data */
 * )
 * ```
 *
 * @param error Error that will be set inside the [DataResult].
 * @param data (optional) Data that will be set inside the [DataResult].
 *
 * @return A [DataResult] with the specified data, error, and [DataResultStatus.ERROR] status.
 *
 * @see DataResult
 * @see DataResultStatus
 */
fun <T> dataResultError(error: Throwable?, data: T? = null) =
    DataResult(data, error, ERROR)

/**
 * Creates a new [DataResult] with:
 * - data: null
 * - error: null
 * - status: NONE
 *
 * Usage:
 * ```kotlin
 * val result = dataResultNone()
 * ```
 *
 * @return A [DataResult] with null data, null error, and [DataResultStatus.NONE] status.
 *
 * @see DataResult
 * @see DataResultStatus
 */
fun <T> dataResultNone() = DataResult<T>(null, null, NONE)
//endregion

//region Transformation Methods
/**
 * Transforms a [DataResult] with a pair of nullable values to a [DataResult] with non-null values.
 *
 * If any value in the pair is null, the result will be null.
 *
 * @return A [DataResult] with a pair of non-null values, or null if any value is null.
 */
fun <T, R> DataResult<Pair<T?, R?>>.onlyWithValues(): DataResult<Pair<T, R>>? = when (val data = data) {
    null -> DataResult(null, error, status)
    else -> data.runCatching { requireNotNull(first) to requireNotNull(second) }
        .mapCatching { DataResult<Pair<T, R>>(it, error, status) }
        .getOrNull()
}

/**
 * Merges this [DataResult] with another [DataResult].
 *
 * ## Status
 * - **NONE**: Lowest priority, returned if both results are null or have this status.
 * - **SUCCESS**: Returned if both DataResults have this status or if one of them has status NONE.
 * - **LOADING**: Returned if any of the DataResults have this status.
 * - **ERROR**: Returned if any of the DataResults have this status.
 *
 * ## Throwable
 * The throwable inside the resulting [DataResult] will represent the first non-null error, with priority given to the current [DataResult].
 *
 * ## Data
 * The resulting data will be a pair of values from the two [DataResult] objects.
 * If both results are null, the resulting data will also be null.
 *
 * ## Usage:
 * ```kotlin
 * val dataResultA = dataResultSuccess("value1")
 * val dataResultB = dataResultSuccess(2)
 *
 * // Combine the resultA with resultB
 * val merged = dataResultA.merge(dataResultB)
 *
 * // The merged result will be a union of the two results
 * assert(merged.data == ("value1" to 2))
 * assert(merged.status == DataResultStatus.SUCCESS)
 * assert(merged.error == null)
 * ```
 *
 * @param second The [DataResult] to combine with.
 *
 * @return A [DataResult] containing a pair of data from both results, the first non-null error, and the highest status.
 *
 * @see DataResult
 * @see DataResultStatus
 * @see ResponseLiveData
 * @see dataResultNone
 * @see dataResultSuccess
 * @see dataResultLoading
 * @see dataResultError
 * @see mergeNotNull
 */
fun <T, R> DataResult<T>?.merge(second: DataResult<R>?): DataResult<Pair<T?, R?>> = when {
    /* One of the results is null */
    this == null || second == null -> DataResult(
        data = Pair(this?.data, second?.data)
            .takeIf { it.first != null || it.second != null },
        error = this?.error ?: second?.error,
        status = this?.status ?: second?.status ?: NONE
    )

    /* Both results have NONE status */
    this.status == NONE && second.status == NONE -> dataResultNone()

    /* Both non-null results without any NONE status */
    else -> DataResult(
        data = (this.data to second.data)
            .takeIf { it.first != null || it.second != null },
        error = this.error ?: second.error,
        status = DataResultStatus.entries[max(this.status.ordinal, second.status.ordinal)]
    )
}

/**
 * Merges this [DataResult] with another [DataResult] ensuring that the resulting pair contains non-null values.
 *
 * If any value in the resulting pair is null, the resulting data will be null.
 *
 * ## Usage:
 * ```kotlin
 * val dataResultA = dataResultSuccess("value1")
 * val dataResultB = dataResultLoading<Int>()
 *
 * // Combine the resultA with resultB
 * val merged = dataResultA.mergeNotNull(dataResultB)
 *
 * // The merged result will be a union of the two results
 * assert(merged.data == null)
 * assert(merged.status == DataResultStatus.LOADING)
 * assert(merged.error == null)
 * ```
 *
 * @param second The [DataResult] to combine with.
 *
 * @return A [DataResult] with non-null data, if both results contain non-null data. If either result's data is null, the resulting data will be null.
 *
 * @see DataResult
 * @see DataResultStatus
 * @see ResponseLiveData
 * @see dataResultNone
 * @see dataResultSuccess
 * @see dataResultLoading
 * @see dataResultError
 * @see merge
 */
fun <T, R> DataResult<T>?.mergeNotNull(second: DataResult<R>?): DataResult<Pair<T, R>> {
    val mergeNullable = merge(second)

    val data = mergeNullable.data?.onlyWithValues()

    val error = mergeNullable.error
    val status = mergeNullable.status

    return DataResult(
        data = data,
        error = error,
        status = status
    )
}

/**
 * Merges multiple [DataResult] objects into a single [DataResult] containing a map of data.
 *
 * ## Status
 * - **NONE**: Lowest priority, returned if all results are null or have this status.
 * - **SUCCESS**: Returned if all DataResults have this status, or if one of them has status NONE.
 * - **LOADING**: Returned if any of the DataResults have this status.
 * - **ERROR**: Returned if any of the DataResults have this status.
 *
 * ## Throwable
 * The throwable inside the resulting [DataResult] will represent the first non-null error.
 *
 * ## Data
 * A map of string keys to data values from all [DataResult] objects.
 *
 * @see DataResult
 * @see DataResultStatus
 * @see ResponseLiveData
 *
 * @return A [DataResult] containing a map of all data from the given [DataResult] objects.
 */
fun List<Pair<String, DataResult<*>?>>.mergeAll(): DataResult<Map<String, *>> {
    val resultWithMaxStatus = maxBy { it.second?.status?.ordinal ?: NONE.ordinal }
    if (resultWithMaxStatus.second?.status == NONE) return dataResultNone()

    val ordinal = resultWithMaxStatus.second?.status?.ordinal ?: NONE.ordinal
    val status = DataResultStatus.entries[ordinal]

    return DataResult(
        associate { (key, result) -> key to result?.data }
            .takeIf { it.values.filterNotNull().isNotEmpty() },
        firstOrNull { (_, result) -> result?.error != null }?.second?.error,
        status
    )
}
//endregion

//region Operator Methods
/**
 * Combines this [DataResult] with another [DataResult] using the plus (`+`) operator.
 *
 * Usage:
 * ```kotlin
 * val dataResultA = dataResultSuccess("value1")
 * val dataResultB = dataResultSuccess(2)
 * // Combine the resultA with resultB
 * val merged = dataResultA + dataResultB
 * ```
 *
 * @param another The [DataResult] to combine with.
 *
 * @return A [DataResult] containing the combined data, the first non-null error, and the highest status.
 *
 * @see merge
 */
operator fun <T, R> DataResult<T>?.plus(another: DataResult<R>?) = merge(another)
//endregion
