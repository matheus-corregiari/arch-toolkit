package br.com.arch.toolkit.util

import br.com.arch.toolkit.livedata.ResponseLiveData
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus
import br.com.arch.toolkit.result.DataResultStatus.ERROR
import br.com.arch.toolkit.result.DataResultStatus.LOADING
import br.com.arch.toolkit.result.DataResultStatus.NONE
import br.com.arch.toolkit.result.DataResultStatus.SUCCESS
import br.com.arch.toolkit.result.DataResultStatus.values
import kotlin.math.max

//region Data Result Creator Methods
/**
 * Creates a new DataResult with:
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
 * @param data Data that will reflect inside DataResult
 *
 * @return DataResult(`<data>`, null, DataResultStatus.SUCCESS)
 *
 * @see DataResult
 * @see DataResultStatus
 */
fun <T> dataResultSuccess(data: T?) = DataResult(data, null, SUCCESS)

/**
 * Creates a new DataResult with:
 * - data: `<passed as optional argument>`
 * - error: `<passed as optional argument>`
 * - status: LOADING
 *
 * Usage:
 * ```kotlin
 * val result = dataResultLoading<String>(
 *     data = null, /* (optional param) Your Optional Data */
 *     error = null /* (optional param) Your Optional Exception */
 * )
 * ```
 *
 * @param data (optional) Data that will reflect inside DataResult
 * @param error (optional) Error that will reflect inside DataResult
 *
 * @return DataResult(`<data>`, `<error>`, DataResultStatus.LOADING)
 *
 * @see DataResult
 * @see DataResultStatus
 */
fun <T> dataResultLoading(data: T? = null, error: Throwable? = null) =
    DataResult(data, error, LOADING)

/**
 * Creates a new DataResult with:
 * - data: `<passed as optional argument>`
 * - error: `<passed as argument>`
 * - status: ERROR
 *
 * Usage:
 * ```kotlin
 * val result = dataResultError<String>(
 *     error = null, /* Your Exception */
 *     data = null   /* (optional param) Your Optional Data */
 * )
 * ```
 *
 * @param error Error that will reflect inside DataResult
 * @param data (optional) Data that will reflect inside DataResult
 *
 * @return DataResult(`<data>`, `<error>`, DataResultStatus.ERROR)
 *
 * @see DataResult
 * @see DataResultStatus
 */
fun <T> dataResultError(error: Throwable?, data: T? = null) =
    DataResult(data, error, ERROR)

/**
 * Creates a new DataResult with:
 * - data: null
 * - error: null
 * - status: NONE
 *
 * Usage:
 * ```kotlin
 * val result = dataResultNone()
 * ```
 *
 * @return DataResult(null, null, DataResultStatus.NONE)
 *
 * @see DataResult
 * @see DataResultStatus
 */
fun <T> dataResultNone() = DataResult<T>(null, null, NONE)
//endregion

//region Transformation Methods
/**
 * Merges a DataResult<T> with a DataResult<R>
 *
 * ## Status
 * - **NONE** Lowest in priority, returned if both results are null or have this status NONE
 * - **SUCCESS** Will return this status if both DataResult have this status, or if one of them have the status NONE)
 * - **LOADING** Will return if any of DataResults have this status
 * - **ERROR** Will return if any of DataResults have this status
 *
 * ## Throwable
 * The throwable inside the DataResult will represent the first non-null error, in priority the current DataResult
 *
 * ## Data
 * The Data will always be a union of the two data, representing a pair (Pair<T?, R?>)
 *
 * > If one of the result are null, the non-null will prevail
 * > and the result will have data with a Pair with one of the results with null
 *
 * > If both results are null, will return a result equivalent a **dataResultNone()**
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
 * @param second The DataResult<R> this will be combined with
 *
 * @see DataResult
 * @see DataResultStatus
 * @see ResponseLiveData
 * @see dataResultNone
 * @see dataResultSuccess
 * @see dataResultLoading
 * @see dataResultError
 * @see mergeNotNull
 *
 * @return DataResult<Pair<T?, R?>>
 */
fun <T, R> DataResult<T>?.merge(second: DataResult<R>?): DataResult<Pair<T?, R?>> = when {

    /* One of the results are null */
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
        status = values()[max(this.status.ordinal, second.status.ordinal)]
    )
}

/**
 * Merges a DataResult<T> with a DataResult<R> with the impossibility to return any null data inside the Pair
 *
 * It is basically the same behavior seen inside the `@see merge` method
 * But it any of the values inside the pair returns null, then, the data is null
 *
 * > If one of the results data are null, so the data is null
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
 * @param second The DataResult<R> this will be combined with
 *
 * @see DataResult
 * @see DataResultStatus
 * @see ResponseLiveData
 * @see dataResultNone
 * @see dataResultSuccess
 * @see dataResultLoading
 * @see dataResultError
 * @see merge
 *
 * @return DataResult<Pair<T?, R?>>
 */
fun <T, R> DataResult<T>?.mergeNotNull(second: DataResult<R>?): DataResult<Pair<T, R>> {
    val mergeNullable = merge(second)

    val data = mergeNullable.data?.runCatching {
        requireNotNull(this.first) to requireNotNull(this.second)
    }?.getOrNull()

    val error = mergeNullable.error
    val status = mergeNullable.status

    return DataResult(
        data = data,
        error = error,
        status = status
    )
}

/**
 * Merges multiple DataResult<*> into a Map<String, *> with all the data stored with the chosen tags
 *
 * ## Status
 * - **NONE** Lowest in priority, returned if all results are null or have this status NONE
 * - **SUCCESS** Will return this status if all DataResult have this status, or if one of them have the status NONE
 * - **LOADING** Will return if any of DataResults have this status
 * - **ERROR** Will return if any of DataResults have this status
 *
 * ## Throwable
 * The throwable inside the DataResult will represent the first non-null error
 *
 * ## Data
 * A Map<String, *> representing all results inside all DataResult
 *
 * @see DataResult
 * @see DataResultStatus
 * @see ResponseLiveData
 *
 * @return DataResult<Map<String, *>>
 */
fun List<Pair<String, DataResult<*>?>>.mergeAll(): DataResult<Map<String, *>> {

    val resultWithMaxStatus = maxBy { it.second?.status?.ordinal ?: NONE.ordinal }
    if (resultWithMaxStatus.second?.status == NONE) return dataResultNone()

    val ordinal = resultWithMaxStatus.second?.status?.ordinal ?: NONE.ordinal
    val status = DataResultStatus.values()[ordinal]

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
 * Uses **merge** to make a fun syntax
 *
 * Usage:
 * ```kotlin
 * val dataResultA = dataResultSuccess("value1")
 * val dataResultB = dataResultSuccess(2)
 * // Combine the resultA with resultB
 * val merged = dataResultA.merge(dataResultB)*
 * ```
 * @see merge
 * @return DataResult<Pair<T?, R?>>
 */
operator fun <T, R> DataResult<T>?.plus(another: DataResult<R>?) = merge(another)
//endregion