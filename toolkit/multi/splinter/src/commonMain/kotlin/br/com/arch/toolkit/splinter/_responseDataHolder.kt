@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.splinter

import br.com.arch.toolkit.flow.ResponseFlow
import br.com.arch.toolkit.flow.ResponseStateFlow
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus

expect interface ResponseDataHolder<T> {

    /**
     * Data Observables
     */
    //region Observable Data Info
    /* Only Current execution events */
    val liveFlow: ResponseStateFlow<T>
    val liveColdFlow: ResponseFlow<T>

    /* All execution events from this instance */
    val fullFlow: ResponseStateFlow<T>
    val fullColdFlow: ResponseFlow<T>
    //endregion

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

internal expect class ResultHolder<T>() : ResponseDataHolder<T> {
    fun init(splinter: Splinter<T>)
}
