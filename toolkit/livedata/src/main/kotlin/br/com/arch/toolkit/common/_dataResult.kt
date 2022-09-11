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
 * The possible types of statuses of DataResult
 */
enum class DataResultStatus {
    LOADING, SUCCESS, ERROR
}