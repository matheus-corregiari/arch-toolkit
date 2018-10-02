package br.com.arch.toolkit.livedata

import br.com.arch.toolkit.livedata.ExecutorUtil.async
import br.com.arch.toolkit.livedata.response.MutableResponseLiveData
import br.com.arch.toolkit.livedata.response.ResponseLiveData

/**
 * Create a ResponseLiveData Instance posting Loading, Success or Error
 * All parameters are executes on the WorkerThread
 *
 * @param errorTransformer To invoke the regular Exception caused by the param "func"
 * @param func Return the success value on the ResponseLiveData
 */
inline fun <T> makeAsyncOperation(crossinline errorTransformer: (Exception) -> Exception, crossinline func: () -> T): ResponseLiveData<T> {
    val liveData = MutableResponseLiveData<T>()
    async {
        try {
            liveData.postLoading()
            liveData.postData(func.invoke())
        } catch (error: Exception) {
            liveData.postError(errorTransformer.invoke(error))
        }
    }
    return liveData
}

/**
 * Create a ResponseLiveData Instance posting Loading, Success or Error
 * With a default ErrorTransformer with returns the actual "func" block exception
 *
 * @param func Return the success value on the ResponseLiveData
 */
inline fun <T> makeAsyncOperation(crossinline func: () -> T): ResponseLiveData<T> {
    return makeAsyncOperation(errorTransformer = { error -> error }, func = func)
}