package br.com.arch.toolkit.splinter.extension

import br.com.arch.toolkit.splinter.Splinter
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlin.math.max

internal fun <T> SharedFlow<T>.get(): T? = replayCache.lastOrNull()
internal fun <T> SharedFlow<T>.live(): Flow<T> = flow {
    val countToIgnore = max(0, replayCache.size - 1)
    emitAll(drop(countToIgnore))
}

internal fun <T> SharedFlow<T>.cold(splinter: Splinter<*>) = cold(
    splinter = splinter,
    default = ::replayCache
)

@OptIn(DelicateCoroutinesApi::class)
internal fun <T> Flow<T>.cold(
    splinter: Splinter<*>,
    default: () -> List<T>
): Flow<T> = channelFlow {
    if (splinter.isRunning) {
        launch {
            splinter.await()
            if (isClosedForSend.not()) close()
        }
        collect {
            trySendBlocking(it)
            if (splinter.isRunning.not()) close()
        }
    } else {
        default().onEach { trySendBlocking(it) }
        close()
    }
}
