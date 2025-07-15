@file:Suppress(
    "EXPECT__CLASSIFIERS_ARE_IN_BETA_WARNING",
    "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING",
)

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
import kotlin.coroutines.EmptyCoroutineContext

internal expect class TargetRegularHolderImpl<T>() : RegularHolderImpl<T>, TargetRegularHolder<T>

internal expect class TargetResultHolderImpl<T : Any>() : ResultHolderImpl<T>, TargetResultHolder<T>

internal open class RegularHolderImpl<T> : RegularHolder<T>, DataSetter<T?> {
    private val _flow = MutableStateFlow<T?>(null)

    //region Getters
    override val flow: Flow<T?> get() = _flow.asSharedFlow()

    override fun get(): T? = _flow.value
    //endregion

    //region Setters
    override suspend fun set(value: T?) = _flow.emit(value)

    override fun trySet(value: T?): Boolean = _flow.tryEmit(value)
    //endregion

    fun setter(): DataSetter<T?> = this
}

internal open class ResultHolderImpl<T : Any> : ResultHolder<T>, DataSetter<DataResult<T>> {

    private val _flow = MutableResponseFlow(dataResultNone<T>())
    var scope: () -> CoroutineScope = { CoroutineScope(EmptyCoroutineContext) }
        internal set
    var running: () -> Boolean = { false }
        internal set

    //region Getters
    override val coldFlow: ColdResponseFlow<T>
        get() = _flow.cold { running() }.scope(scope())
    override val flow: ResponseFlow<T>
        get() = _flow.shareIn(
            scope = scope(),
            started = SharingStarted.WhileSubscribed(),
        )

    override fun get(): DataResult<T> = _flow.value
    //endregion

    //region Setters
    override suspend fun set(value: DataResult<T>) = _flow.emit(value)

    override fun trySet(value: DataResult<T>) = _flow.tryEmit(value)
    //endregion

    fun setter(): DataSetter<DataResult<T>> = this
}
