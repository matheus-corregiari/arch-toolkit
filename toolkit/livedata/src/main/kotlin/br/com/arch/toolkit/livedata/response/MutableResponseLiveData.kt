package br.com.arch.toolkit.livedata.response

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import br.com.arch.toolkit.common.DataResult
import br.com.arch.toolkit.common.DataResultStatus
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
    constructor() : super()

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

    override fun scope(scope: CoroutineScope): MutableResponseLiveData<T> {
        return super.scope(scope) as MutableResponseLiveData<T>
    }

    override fun transformDispatcher(dispatcher: CoroutineDispatcher): MutableResponseLiveData<T> {
        return super.transformDispatcher(dispatcher) as MutableResponseLiveData<T>
    }

    public override fun setValue(value: DataResult<T>?) {
        super.setValue(value)
    }

    public override fun postValue(value: DataResult<T>?) {
        super.postValue(value)
    }
}