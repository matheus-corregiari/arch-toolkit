package br.com.arch.toolkit.livedata.response

import android.support.annotation.NonNull
import br.com.arch.toolkit.livedata.response.DataResultStatus.ERROR
import br.com.arch.toolkit.livedata.response.DataResultStatus.LOADING
import br.com.arch.toolkit.livedata.response.DataResultStatus.SUCCESS

/**
 * A custom implementation of ResponseLiveData with methods to post new values
 */
class MutableResponseLiveData<T> : ResponseLiveData<T>() {

    /**
     * Post a new DataResult with:
     * - data: null
     * - error: null
     * - status: LOADING
     */
    fun postLoading() {
        postValue(DataResult(null, null, LOADING))
    }

    /**
     * Post a new DataResult with:
     * - data: null
     * - error: received from parameter
     * - status: LOADING
     *
     * @param error Value to be posted into the new DataResult
     */
    fun postError(@NonNull error: Throwable) {
        postValue(DataResult(null, error, ERROR))
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
        postValue(DataResult(data, null, SUCCESS))
    }
}