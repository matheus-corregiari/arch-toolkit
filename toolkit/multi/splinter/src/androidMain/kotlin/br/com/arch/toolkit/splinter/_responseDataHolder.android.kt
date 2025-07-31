@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.splinter

import br.com.arch.toolkit.flow.ResponseFlow
import br.com.arch.toolkit.flow.ResponseStateFlow
import br.com.arch.toolkit.livedata.ResponseLiveData
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus
import br.com.arch.toolkit.splinter.extension.cold
import br.com.arch.toolkit.splinter.extension.get
import br.com.arch.toolkit.splinter.extension.live
import br.com.arch.toolkit.util.dataResultNone
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.buffer

actual interface ResponseDataHolder<T> {
    /**
     * Response Observables
     */
    val liveData: ResponseLiveData<T>

    /**
     * Data Observables
     */
    actual val liveFlow: ResponseStateFlow<T>
    actual val liveColdFlow: ResponseFlow<T>
    actual val fullFlow: ResponseStateFlow<T>
    actual val fullColdFlow: ResponseFlow<T>

    /**
     * Current Data
     */
    actual fun get(): DataResult<T>
    actual val status: DataResultStatus
    actual val data: T?
    actual val error: Throwable?
}

internal actual class ResultHolder<T> actual constructor() : ResponseDataHolder<T> {

    private lateinit var splinter: Splinter<T>

    actual fun init(splinter: Splinter<T>) {
        this.splinter = splinter
    }

    /**
     * Response Observables
     */
    override val liveData get() = ResponseLiveData.from(liveFlow)

    /**
     * Data Observables
     */
    //region Observable Data Info
    /* Only Current execution events */
    override val liveFlow
        get() = ResponseStateFlow.from(
            flow = splinter.dataFlow.live(),
            initial = get()
        )
    override val liveColdFlow
        get() = ResponseFlow.from(
            flow = liveFlow.cold(splinter = splinter, default = { listOf(get()) })
        )

    /* All execution events from this instance */
    override val fullFlow
        get() = ResponseStateFlow.from(
            flow = splinter.dataFlow.asSharedFlow().buffer(),
            initial = get()
        )
    override val fullColdFlow
        get() = ResponseFlow.from(flow = splinter.dataFlow.cold(splinter = splinter).buffer())
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

