@file:OptIn(DelicateCoroutinesApi::class)

package br.com.arch.toolkit.splinter

import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.splinter.extension.error
import br.com.arch.toolkit.splinter.extension.info
import br.com.arch.toolkit.splinter.extension.lazyJob
import br.com.arch.toolkit.splinter.strategy.Strategy
import br.com.arch.toolkit.util.dataResultError
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow.SUSPEND
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED

internal class Apprentice<T>(
    val id: String,
    scope: CoroutineScope,
    private val holder: ResponseDataHolder<T>,
    private val strategy: Strategy<T>,
) {
    private val lock = Any()
    private val completableDeferred = CompletableDeferred<Unit>()
    val dataChannel = Channel<DataResult<T>>(capacity = BUFFERED, onBufferOverflow = SUSPEND)
    val logChannel = Channel<Splinter.Message>(capacity = BUFFERED, onBufferOverflow = SUSPEND)

    private val job: Job by scope.lazyJob(
        onCreate = { logChannel.info("[Apprentice $id] Created") },
        job = {
            logChannel.info("[Apprentice $id] Started")
            runCatching {
                strategy.execute(holder, dataChannel, logChannel)
            }.onSuccess {
                logChannel.info("[Apprentice $id] Success")
            }.onFailure { error ->
                logChannel.error("[Apprentice $id] Emit - Error", error)
                dataChannel.trySend(dataResultError(error))
            }
        },
        onComplete = { error ->
            logChannel.info("[Apprentice $id] Closed")
            dataChannel.close(error)
            logChannel.close(error)
        }
    )

    val isRunning get() = job.isActive || completableDeferred.isActive
    val isClosing get() = job.isActive.not() && completableDeferred.isActive

    fun start() = synchronized(lock) { job.start() }

    fun stop() = synchronized(lock) {
        cancel()
        completableDeferred.complete(Unit)
    }

    fun cancel() = synchronized(lock) {
        if (isRunning.not()) return@synchronized
        if (job.isActive.not()) return@synchronized
        logChannel.info("[Apprentice $id] Cancel")
        runCatching { job.cancel() }
    }

    suspend fun await() {
        if (isRunning.not()) return
        runCatching { completableDeferred.join() }
        runCatching { job.join() }
    }

    override fun toString() = "[Apprentice $id] Running: $isRunning | Closing: $isClosing"
}
