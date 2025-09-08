@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.splinter

import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus
import kotlinx.coroutines.flow.Flow

expect interface TargetDataHolder<T> : ResponseDataHolder<T>

internal expect class ResultHolder<T>() : TargetDataHolder<T> {

    fun init(splinter: Splinter<T>)

    /**
     * Data Observables
     */
    //region Observable Data Info
    /* Only Current execution events */
    override val liveFlow: Flow<DataResult<T>>
    override val liveColdFlow: Flow<DataResult<T>>

    /* All execution events from this instance */
    override val fullFlow: Flow<DataResult<T>>
    override val fullColdFlow: Flow<DataResult<T>>
    //endregion

    /**
     * Current Data
     */
    //region Current Data Info
    override fun get(): DataResult<T>
    override val status: DataResultStatus
    override val data: T?
    override val error: Throwable?
    //endregion
}
