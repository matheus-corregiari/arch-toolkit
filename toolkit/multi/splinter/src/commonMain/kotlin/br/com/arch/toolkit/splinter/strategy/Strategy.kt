package br.com.arch.toolkit.splinter.strategy

import androidx.annotation.WorkerThread
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.splinter.ResponseDataHolder
import br.com.arch.toolkit.splinter.Splinter
import kotlinx.coroutines.channels.Channel

/**
 * Main class that defines how the Splinter will work to emit the events!
 */
abstract class Strategy<RESULT> {

    @WorkerThread
    abstract suspend fun execute(
        holder: ResponseDataHolder<RESULT>,
        dataChannel: Channel<DataResult<RESULT>>,
        logChannel: Channel<Splinter.Message>,
    )

    companion object {
        /**
         * Helper method that creates a OneShot strategy and configure it
         *
         * @param config - Block that will configure the oneShot strategy
         *
         * @return br.com.arch.toolkit.splinter.strategy.OneShot
         */
        fun <T> oneShot(config: OneShot.Config.Builder<T>.() -> Unit) = OneShot(config)

        /**
         * Helper method that creates a MirrorFlow strategy and configure it
         *
         * @param config - Block that will configure the mirrorFlow strategy
         *
         * @return br.com.arch.toolkit.splinter.strategy.MirrorFlow
         */
        fun <T> mirrorFlow(config: MirrorFlow.Config.Builder<T>.() -> Unit) = MirrorFlow(config)

        /**
         *
         */
        fun <T> polling(config: Polling.Config.Builder<T>.() -> Unit) = Polling(config)
    }
}
