package br.com.arch.toolkit.livedata.response

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import br.com.arch.toolkit.common.DataResult
import br.com.arch.toolkit.common.DataResultStatus

/**
 * A custom implementation of ResponseLiveData with methods to post new values
 */
class MutableResponseLiveData<T> : ResponseLiveData<T>() {

    // region Post Methods
    /**
     * Post a new DataResult with:
     * - data: received from parameter (Default: null)
     * - error: null
     * - status: LOADING
     *
     * @param data Value to be posted into the new DataResult
     */
    fun postLoading(@Nullable data: T? = null) {
        postValue(DataResult(data, null, DataResultStatus.LOADING))
    }

    /**
     * Post a new DataResult with:
     * - data: received from parameter (Default: null)
     * - error: received from parameter
     * - status: LOADING
     *
     * @param data Value to be posted into the new DataResult
     * @param error Value to be posted into the new DataResult
     */
    fun postError(@NonNull error: Throwable, @Nullable data: T? = null) {
        postValue(DataResult(data, error, DataResultStatus.ERROR))
    }

    /**
     * Post a new DataResult with:
     * - data: received from parameter
     * - error: null
     * - status: SUCCESS
     *
     * @param data Value to be posted into the new DataResult
     */
    fun postData(@NonNull data: T) {
        postValue(DataResult(data, null, DataResultStatus.SUCCESS))
    }

    /**
     * Post a new DataResult with:
     * - data: null
     * - error: null
     * - status: SUCCESS
     */
    fun postSuccess() {
        postValue(DataResult(null, null, DataResultStatus.SUCCESS))
    }
    // endregion

    // region Set methods
    /**
     * Set a new DataResult with:
     * - data: received from parameter
     * - error: null
     * - status: LOADING
     *
     * @param data Value to be posted into the new DataResult
     */
    fun setLoading(@Nullable data: T? = null) {
        value = DataResult(data, null, DataResultStatus.LOADING)
    }

    /**
     * Set a new DataResult with:
     * - data: received from parameter (Default: null)
     * - error: received from parameter
     * - status: LOADING
     *
     * @param data Value to be posted into the new DataResult
     * @param error Value to be seted into the new DataResult
     */
    fun setError(@NonNull error: Throwable, @Nullable data: T? = null) {
        value = DataResult(data, error, DataResultStatus.ERROR)
    }

    /**
     * Set a new DataResult with:
     * - data: received from parameter
     * - error: null
     * - status: SUCCESS
     *
     * @param data Value to be seted into the new DataResult
     */
    fun setData(@NonNull data: T) {
        value = DataResult(data, null, DataResultStatus.SUCCESS)
    }

    /**
     * Set a new DataResult with:
     * - data: null
     * - error: null
     * - status: SUCCESS
     */
    fun setSuccess() {
        value = DataResult(null, null, DataResultStatus.SUCCESS)
    }
    // endregion
}