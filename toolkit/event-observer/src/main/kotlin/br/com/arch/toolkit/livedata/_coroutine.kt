@file:Suppress("Filename")

package br.com.arch.toolkit.livedata

import androidx.annotation.MainThread
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus
import br.com.arch.toolkit.util.dataResultError
import br.com.arch.toolkit.util.dataResultLoading
import br.com.arch.toolkit.util.dataResultSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Creates a [ResponseLiveData] instance with the specified configuration.
 *
 * This function sets up a [ResponseLiveData] that will use the provided [block] to update its value. It also allows specifying a timeout duration
 * and a [CoroutineContext] to control the coroutine's execution context.
 *
 * @param timeout The maximum time to wait before canceling the operation if there are no active observers. Default is 5000 milliseconds.
 * @param context The [CoroutineContext] to use for the coroutine running the [block]. Default is [EmptyCoroutineContext].
 * @param block The suspending function that will be executed within the [ResponseLiveDataScope] to update the [ResponseLiveData].
 * @return A [ResponseLiveData] instance that uses the provided block to update its value.
 *
 * Example usage:
 * ```
 * val liveData: ResponseLiveData<String> = responseLiveData {
 *     emitLoading()
 *     delay(1000)
 *     emitData("Hello World")
 * }
 * liveData.observe(this, Observer { result ->
 *     when (result) {
 *         is DataResult.Success -> println("Data: ${result.data}")
 *         is DataResult.Error -> println("Error: ${result.error}")
 *         is DataResult.Loading -> println("Loading...")
 *     }
 * })
 * ```
 *
 * @param T The type of data that this [ResponseLiveData] will emit.
 */
fun <T> responseLiveData(
    timeout: Duration = 5000.milliseconds,
    context: CoroutineContext = EmptyCoroutineContext,
    block: Block<T>
): ResponseLiveData<T> = CoroutineResponseLiveData(context, timeout, block)

internal typealias Block<T> = suspend ResponseLiveDataScope<T>.() -> Unit

/**
 * Defines the scope for a [ResponseLiveData] block execution, providing methods to emit values and status updates.
 *
 * This interface provides methods to emit different states of a [DataResult], including success, loading, and error states.
 *
 * @param T The type of data that this scope will handle.
 *
 * Example usage:
 * ```
 * class MyLiveDataBlock : ResponseLiveDataScope<String> {
 *     override val latestValue: DataResult<String>? = null
 *     override suspend fun emit(value: DataResult<String>?) { /* emit value */ }
 *     override suspend fun emitData(value: String) { emit(dataResultSuccess(value)) }
 *     override suspend fun emitLoading() { emit(dataResultLoading()) }
 *     override suspend fun emitError(error: Throwable) { emit(dataResultError(error)) }
 * }
 * ```
 */
interface ResponseLiveDataScope<T> {
    val latestValue: DataResult<T>?
    val latestData: T? get() = latestValue?.data
    val latestStatus: DataResultStatus? get() = latestValue?.status
    val latestError: Throwable? get() = latestValue?.error

    suspend fun emit(value: DataResult<T>?)
    suspend fun emitData(value: T) = emit(dataResultSuccess(value))
    suspend fun emitLoading() = emit(dataResultLoading())
    suspend fun emitError(error: Throwable) = emit(dataResultError(error))
}

/**
 * Implementation of [ResponseLiveDataScope] that interacts with a [CoroutineResponseLiveData].
 *
 * @param T The type of data that this scope will handle.
 * @param context The [CoroutineContext] in which coroutines should be executed.
 * @property target The [CoroutineResponseLiveData] that this scope interacts with.
 */
private class ResponseLiveDataScopeImpl<T>(
    context: CoroutineContext,
    private var target: CoroutineResponseLiveData<T>,
) : ResponseLiveDataScope<T> {

    private val coroutineContext = context + Dispatchers.Main.immediate

    override val latestValue: DataResult<T>? get() = target.value

    override suspend fun emit(value: DataResult<T>?) =
        withContext(coroutineContext) { target.safePostValue(value) }
}

/**
 * Manages the execution of a [Block] within a [CoroutineResponseLiveData], including handling timeouts and cancellations.
 *
 * @param T The type of data that the [Block] handles.
 * @property liveData The [CoroutineResponseLiveData] instance that this runner manages.
 * @property block The suspending function that will be executed within the runner.
 * @property timeout The maximum duration to wait before canceling the operation if there are no active observers.
 * @property scope The [CoroutineScope] in which the block will be executed.
 * @property onDone Callback to invoke when the block execution is completed.
 */
private class BlockRunner<T>(
    private val liveData: CoroutineResponseLiveData<T>,
    private val block: Block<T>,
    private val timeout: Duration,
    private val scope: CoroutineScope,
    private val onDone: () -> Unit
) {
    private var runningJob: Job? = null
    private var cancellationJob: Job? = null

    @MainThread
    fun maybeRun() {
        cancellationJob?.cancel()
        cancellationJob = null
        if (runningJob != null) {
            return
        }
        runningJob = scope.launch {
            val liveDataScope = ResponseLiveDataScopeImpl(coroutineContext, liveData)
            block(liveDataScope)
            onDone()
        }
    }

    @MainThread
    fun cancel() {
        if (cancellationJob != null) {
            error("Cancel call cannot happen without a maybeRun")
        }
        cancellationJob = scope.launch(Dispatchers.Main.immediate) {
            delay(timeout)
            if (!liveData.hasActiveObservers()) {
                runningJob?.cancel()
                runningJob = null
            }
        }
    }
}

/**
 * A [ResponseLiveData] implementation that uses coroutines to run a given block and handle timeouts.
 *
 * @param T The type of data that this [ResponseLiveData] will handle.
 * @param context The [CoroutineContext] to use for executing the block.
 * @param timeout The duration to wait before canceling the block execution if there are no active observers.
 * @param block The suspending function that will be executed to update the [ResponseLiveData].
 */
internal class CoroutineResponseLiveData<T>(
    context: CoroutineContext,
    timeout: Duration,
    block: Block<T>
) : ResponseLiveData<T>() {

    private val supervisorJob = SupervisorJob(context[Job])
    private val blockScope = CoroutineScope(Dispatchers.Main.immediate + context + supervisorJob)
    private var blockRunner: BlockRunner<T>? = BlockRunner(
        liveData = this,
        block = block,
        timeout = timeout,
        scope = blockScope,
        onDone = { blockRunner = null }
    )

    public override fun safePostValue(value: DataResult<T>?) = super.safePostValue(value)

    override fun onActive() {
        super.onActive()
        blockRunner?.maybeRun()
    }

    override fun onInactive() {
        super.onInactive()
        blockRunner?.cancel()
    }
}
