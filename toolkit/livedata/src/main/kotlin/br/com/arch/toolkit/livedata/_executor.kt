package br.com.arch.toolkit.livedata

import br.com.arch.toolkit.livedata.response.MutableResponseLiveData
import br.com.arch.toolkit.livedata.response.ResponseLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Create a ResponseLiveData Instance posting Loading, Success or Error
 * All parameters are executes on the WorkerThread
 *
 * @param scope The coroutine Scope to run this operation
 * @param errorTransformer To invoke the regular Exception caused by the param "func"
 * @param func Return the success value on the ResponseLiveData
 */
inline fun <T> makeAsyncOperation(
    scope: CoroutineScope,
    crossinline errorTransformer: (Exception) -> Exception,
    crossinline func: () -> T
): ResponseLiveData<T> {
    val liveData = MutableResponseLiveData<T>()
    scope.launch {
        withContext(Dispatchers.IO) {
            liveData.postLoading()
            try {
                liveData.postData(func.invoke())
            } catch (error: Exception) {
                liveData.postError(errorTransformer.invoke(error))
            }
        }
    }
    return liveData
}

/**
 * Create a ResponseLiveData Instance posting Loading, Success or Error
 * All parameters are executes on the WorkerThread
 *
 * @param errorTransformer To invoke the regular Exception caused by the param "func"
 * @param func Return the success value on the ResponseLiveData
 */
@Suppress("OPT_IN_USAGE")
inline fun <T> makeAsyncOperation(
    crossinline errorTransformer: (Exception) -> Exception,
    crossinline func: () -> T
): ResponseLiveData<T> {
    return makeAsyncOperation(
        scope = GlobalScope,
        errorTransformer = { error -> error },
        func = func
    )
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