@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.splinter

import br.com.arch.toolkit.flow.ColdResponseFlow
import br.com.arch.toolkit.flow.MutableResponseFlow
import br.com.arch.toolkit.flow.ResponseFlow
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.util.dataResultNone
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow

internal actual class RegularHolderImpl<T> : RegularHolder<T> {

    actual constructor()

    //region Observable Return Types
    private val _flow = MutableStateFlow<T?>(null)
    override val flow: Flow<T?> get() = _flow.asSharedFlow()
    //endregion

    //region Functions
    override fun get(): T? = _flow.value

    override suspend fun set(value: T?) = _flow.emit(value)

    override fun trySet(value: T?) = _flow.tryEmit(value)
    //endregion
}

internal actual class ResultHolderImpl<T> actual constructor(
    private val scope: () -> CoroutineScope,
    private val running: () -> Boolean,
) : ResultHolder<T> {

    //region Observable Return Types
    private val _flow = MutableResponseFlow(dataResultNone<T>())
    override val coldFlow: ColdResponseFlow<T>
        get() = _flow.cold { running() }.scope(scope())

    override val flow: ResponseFlow<T>
        get() = _flow.shareIn(
            scope = scope(),
            started = SharingStarted.WhileSubscribed(),
        )
    //endregion

    //region Functions
    override fun get(): DataResult<T> = _flow.value

    override suspend fun set(value: DataResult<T>) = _flow.emit(value)

    override fun trySet(value: DataResult<T>) = _flow.tryEmit(value)
    //endregion
}
