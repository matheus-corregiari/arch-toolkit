@file:SuppressLint("KotlinNullnessAnnotation")
@file:Suppress("TooManyFunctions")

package br.com.arch.toolkit.livedata

import android.annotation.SuppressLint
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.util.dataResultError
import br.com.arch.toolkit.util.dataResultLoading
import br.com.arch.toolkit.util.dataResultNone
import br.com.arch.toolkit.util.dataResultSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

/**
 * A custom implementation of ResponseLiveData with methods to post new values
 */
class MutableResponseLiveData<T> : ResponseLiveData<T> {

    /**
     * Empty constructor when initializing with a value is not needed
     *
     * @return An empty MutableResponseLiveData<T> instance
     */
    constructor() : this(dataResultNone())

    /**
     * Constructor for initializing with a value
     *
     * @param value The initial value for this MutableResponseLiveData
     *
     * @return An instance of MutableResponseLiveData<T> with a default value set
     */
    constructor(value: DataResult<T>) : super(value)

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
        postValue(dataResultLoading(data))
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
        postValue(dataResultError(error, data))
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
        postValue(dataResultSuccess(data))
    }

    /**
     * Post a new DataResult with:
     * - data: null
     * - error: null
     * - status: SUCCESS
     */
    fun postSuccess() {
        postValue(dataResultSuccess(null))
    }

    /**
     * Post a new DataResult with:
     * - data: null
     * - error: null
     * - status: NONE
     */
    fun postNone() {
        postValue(dataResultNone())
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
        value = dataResultLoading(data)
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
        value = dataResultError(error, data)
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
        value = dataResultSuccess(data)
    }

    /**
     * Set a new DataResult with:
     * - data: null
     * - error: null
     * - status: SUCCESS
     */
    fun setSuccess() {
        value = dataResultSuccess(null)
    }

    /**
     * Set a new DataResult with:
     * - data: null
     * - error: null
     * - status: NONE
     */
    fun setNone() {
        value = dataResultNone()
    }
    // endregion

    /**
     * @see ResponseLiveData.scope
     *
     * To further information about what this method does!
     */
    override fun scope(scope: CoroutineScope): MutableResponseLiveData<T> =
        super.scope(scope) as MutableResponseLiveData<T>

    /**
     * @see ResponseLiveData.transformDispatcher
     *
     * To further information about what this method does!
     */
    override fun transformDispatcher(dispatcher: CoroutineDispatcher): MutableResponseLiveData<T> =
        super.transformDispatcher(dispatcher) as MutableResponseLiveData<T>

    /**
     * Like Uncle Ben said, with great powers...
     */
    public override fun setValue(value: DataResult<T>?) = super.setValue(value)

    /**
     * Like Uncle Ben said, with great powers...
     */
    public override fun postValue(value: DataResult<T>?) = super.postValue(value)
}