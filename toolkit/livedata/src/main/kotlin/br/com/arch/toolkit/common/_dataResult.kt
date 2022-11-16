package br.com.arch.toolkit.common

/**
 * The capsule with contains a optional data, optional error and a non null status
 *
 * This model of interpretation was based on Google Architecture Components Example
 * @see <a href="https://github.com/googlesamples/android-architecture-components">Google's github repository</a>
 * @see DataResultStatus
 * @see br.com.arch.toolkit.livedata.response.ResponseLiveData
 */
data class DataResult<T>(
    val data: T?,
    val error: Throwable?,
    val status: DataResultStatus
)

/**
 * Merges a DataResult<T> with a DataResult<R>
 *
 * @param second The DataResult<R> this will be combined with
 *
 * @see DataResult
 * @see br.com.arch.toolkit.livedata.response.ResponseLiveData
 *
 * @return DataResult<Pair<T, R>>
 */
internal fun <T, R> DataResult<T>?.mergeWith(second: DataResult<R>?): DataResult<Pair<T, R>> {
    val errorList = listOf(this, second).mapNotNull { it?.error }
    val dataList = listOf(this, second).mapNotNull { it?.data }
    return when {
        errorList.isNotEmpty() -> DataResult(null, errorList.first(), DataResultStatus.ERROR)
        dataList.size == 2 -> {
            DataResult(
                this?.data!! to second?.data!!,
                null,
                DataResultStatus.SUCCESS
            )
        }
        else -> DataResult(null, null, DataResultStatus.LOADING)
    }
}

/**
 * Merges multiple DataResult<*> into a Map<String, *> with all the data stored with the chosen tags
 *
 * @see DataResult
 * @see br.com.arch.toolkit.livedata.response.ResponseLiveData
 *
 * @return DataResult<Map<String, *>>
 */
internal fun List<Pair<String, DataResult<*>?>>.mergeAll(): DataResult<Map<String, *>> {
    val errorList = mapNotNull { it.second?.error }
    val dataList = mapNotNull { it.second?.data }
    return when {
        errorList.isNotEmpty() -> DataResult(null, errorList.first(), DataResultStatus.ERROR)
        dataList.size == this.size -> {
            DataResult(
                mapOf(*map { it.first to it.second?.data }.toTypedArray()),
                null,
                DataResultStatus.SUCCESS
            )
        }
        else -> DataResult(null, null, DataResultStatus.LOADING)
    }
}

/**
 * The possible types of statuses of DataResult
 */
enum class DataResultStatus {
    LOADING, SUCCESS, ERROR
}