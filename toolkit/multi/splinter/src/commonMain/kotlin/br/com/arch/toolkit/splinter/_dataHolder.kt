@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.splinter

import br.com.arch.toolkit.splinter.extension.cold
import br.com.arch.toolkit.splinter.extension.get
import br.com.arch.toolkit.splinter.extension.live
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.buffer

interface DataHolder<T> {
    /**
     * Data Observables
     */
    //region Observable Data Info
    /* Only Current execution events */
    val liveFlow: Flow<T>
    val liveColdFlow: Flow<T>

    /* All execution events from this instance */
    val fullFlow: Flow<T>
    val fullColdFlow: Flow<T>
    //endregion
}

internal class MessageHolder : DataHolder<Splinter.Message> {
    private lateinit var splinter: Splinter<*>

    fun init(splinter: Splinter<*>) {
        this.splinter = splinter
    }

    /**
     * Data Observables
     */
    //region Observable Data Info
    /* Only Current execution events */
    override val liveFlow: Flow<Splinter.Message> get() = splinter.logFlow.live()
    override val liveColdFlow: Flow<Splinter.Message>
        get() = liveFlow.cold(
            splinter = splinter,
            default = { listOfNotNull(splinter.logFlow.get()) }
        )

    /* All execution events from this instance */
    override val fullFlow: Flow<Splinter.Message> get() = splinter.logFlow.asSharedFlow().buffer()
    override val fullColdFlow: Flow<Splinter.Message>
        get() = splinter.logFlow.cold(splinter = splinter).buffer()
    //endregion
}
