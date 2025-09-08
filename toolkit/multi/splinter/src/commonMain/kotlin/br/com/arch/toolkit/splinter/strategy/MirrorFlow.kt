@file:OptIn(ExperimentalAtomicApi::class)

package br.com.arch.toolkit.splinter.strategy

import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.splinter.ResponseDataHolder
import br.com.arch.toolkit.splinter.Splinter
import br.com.arch.toolkit.splinter.extension.error
import br.com.arch.toolkit.splinter.extension.info
import br.com.arch.toolkit.splinter.extension.invokeCatching
import br.com.arch.toolkit.util.dataResultError
import br.com.arch.toolkit.util.dataResultLoading
import br.com.arch.toolkit.util.dataResultNone
import br.com.arch.toolkit.util.dataResultSuccess
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

/**
 * Strategy to mirror results from another flow using the DataResult
 *
 * @see Strategy
 */
class MirrorFlow<T>(
    private val config: Config<T>
) : Strategy<T>() {

    override suspend fun execute(
        holder: ResponseDataHolder<T>,
        dataChannel: Channel<DataResult<T>>,
        logChannel: Channel<Splinter.Message>
    ) {
        val lastReceivedData = AtomicReference<DataResult<T>>(dataResultNone())
        val onError: suspend (Throwable) -> Unit = { error ->
            if (lastReceivedData.load().isSuccess.not()) {
                logChannel.error("[MirrorFlow] Emit - Error", error)
                val errorData = dataResultError(
                    error = config.mapError?.invoke(error) ?: error,
                    data = lastReceivedData.load().data
                        ?: config.fallback?.invokeCatching(error)?.getOrNull(),
                )
                lastReceivedData.store(errorData)
                dataChannel.send(errorData)
            } else {
                logChannel.error("[MirrorFlow] Silent error - $error", null)
            }
        }

        runCatching {
            logChannel.info("[MirrorFlow] Emit - Loading")
            dataChannel.trySend(dataResultLoading())
            requireNotNull(config.flow) { "Flow value must be set!" }.invoke()
                .catch { error -> onError(error) }
                .collect { data ->
                    logChannel.info("[MirrorFlow] New data Arrived")
                    val nextData = dataResultLoading(data)
                    if (config.emitOnlyDistinct.not() || lastReceivedData.load() != nextData) {
                        logChannel.info("[MirrorFlow] Emit - Loading Data - $data")
                        lastReceivedData.store(nextData)
                        dataChannel.send(nextData)
                    }

                }
        }.onFailure { error -> onError(error) }

        if (lastReceivedData.load().hasError.not()) {
            logChannel.info("[MirrorFlow] Emit - Success Data! - ${lastReceivedData.load().data}")
            dataChannel.send(dataResultSuccess(lastReceivedData.load().data))
        }
        logChannel.info("[MirrorFlow] Finished")
    }

    companion object Creator {
        operator fun <T> invoke(config: Config.Builder<T>.() -> Unit = {}) = MirrorFlow(
            config = Config.Builder<T>().apply(config).build()
        )
    }

    /**
     *
     */
    @ConsistentCopyVisibility
    data class Config<T> private constructor(
        val mapError: (suspend (Throwable) -> Throwable)?,
        val fallback: (suspend (Throwable) -> T)?,
        val emitOnlyDistinct: Boolean,
        val flow: (suspend () -> Flow<T>)?,
    ) {
        class Builder<T> internal constructor() {
            internal var mapError: (suspend (Throwable) -> Throwable)? = null
            internal var fallback: (suspend (Throwable) -> T)? = null
            internal var emitOnlyDistinct: Boolean = false
            internal var flow: (suspend () -> Flow<T>)? = null

            fun mapError(func: suspend (Throwable) -> Throwable) = apply { this.mapError = func }
            fun fallback(func: suspend (Throwable) -> T) = apply { this.fallback = func }
            fun flow(flow: suspend () -> Flow<T>) = apply { this.flow = flow }
            fun emitOnlyDistinct(enable: Boolean) = apply { this.emitOnlyDistinct = enable }

            internal fun build() = Config(
                mapError = mapError,
                fallback = fallback,
                emitOnlyDistinct = emitOnlyDistinct,
                flow = flow,
            )
        }
    }
}
