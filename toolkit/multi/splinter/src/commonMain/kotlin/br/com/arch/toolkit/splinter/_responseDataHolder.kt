@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.splinter

import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus
import br.com.arch.toolkit.splinter.extension.cold
import br.com.arch.toolkit.splinter.extension.get
import br.com.arch.toolkit.splinter.extension.live
import br.com.arch.toolkit.util.dataResultNone
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.buffer

interface ResponseDataHolder<T> : DataHolder<DataResult<T>> {
    /**
     * Current Data
     */
    //region Current Data Info
    fun get(): DataResult<T>
    val status: DataResultStatus
    val data: T?
    val error: Throwable?
    //endregion
}

internal class CoreResultHolder<T> : ResponseDataHolder<T> {

    private lateinit var splinter: Splinter<T>

    internal fun init(splinter: Splinter<T>) {
        this.splinter = splinter
    }

    /**
     * Data Observables
     */
    //region Observable Data Info
    /* Only Current execution events */
    override val liveFlow get() = splinter.dataFlow.live()
    override val liveColdFlow
        get() = liveFlow.cold(
            splinter = splinter,
            default = { listOf(get()) }
        )

    /* All execution events from this instance */
    override val fullFlow get() = splinter.dataFlow.asSharedFlow().buffer()
    override val fullColdFlow get() = splinter.dataFlow.cold(splinter = splinter).buffer()
    //endregion

    /**
     * Current Data
     */
    //region Current Data Info
    override fun get(): DataResult<T> = splinter.dataFlow.get() ?: dataResultNone()
    override val status: DataResultStatus get() = get().status
    override val data: T? get() = get().data
    override val error: Throwable? get() = get().error
    //endregion
}
