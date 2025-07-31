package br.com.arch.toolkit.splinter.extension

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch

internal fun CoroutineScope.lazyJob(
    job: suspend CoroutineScope.() -> Unit,
    onCreate: () -> Unit,
    onComplete: (Throwable?) -> Unit
) = lazy {
    onCreate.invokeCatching()
    launch(
        start = CoroutineStart.LAZY,
        block = job
    ).apply { invokeOnCompletion(onComplete) }
}
