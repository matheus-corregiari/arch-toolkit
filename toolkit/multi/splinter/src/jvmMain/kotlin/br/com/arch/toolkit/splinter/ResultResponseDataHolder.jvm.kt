@file:Suppress(
    "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING",
    "VariableNaming",
    "MatchingDeclarationName",
)

package br.com.arch.toolkit.splinter

import br.com.arch.toolkit.flow.ColdResponseFlow
import br.com.arch.toolkit.flow.MutableResponseFlow
import br.com.arch.toolkit.flow.ResponseFlow
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus
import br.com.arch.toolkit.util.dataResultNone
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted

actual abstract class ResultResponseDataHolder<T> internal actual constructor() {
    //region Auxiliary Properties
    protected actual abstract val scope: () -> CoroutineScope
    protected actual abstract val running: () -> Boolean
    //endregion

    //region Observable Return Types
    protected actual val _flow = MutableResponseFlow(dataResultNone<T>())
    actual val coldFlow: ColdResponseFlow<T>
        get() = _flow.cold { running() }.scope(scope())

    actual val flow: ResponseFlow<T>
        get() = _flow.shareIn(
            scope = scope(),
            started = SharingStarted.WhileSubscribed(),
        )
    //endregion

    //region Return Types
    actual val data: T? get() = get().data
    actual val error: Throwable? get() = get().error
    actual val status: DataResultStatus get() = get().status
    //endregion

    //region Functions
    actual fun get(): DataResult<T> = _flow.value

    protected actual suspend fun set(value: DataResult<T>) = _flow.emit(value)

    protected actual fun trySet(value: DataResult<T>) {
        _flow.tryEmit(value)
    }
    //endregion
}
