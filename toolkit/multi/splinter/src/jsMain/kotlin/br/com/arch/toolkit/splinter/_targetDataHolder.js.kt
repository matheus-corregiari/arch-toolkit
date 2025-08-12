@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.splinter

import br.com.arch.toolkit.flow.ResponseStateFlow

actual interface TargetDataHolder<T> : ResponseDataHolder<T> {
    val flow: ResponseStateFlow<T>
}

internal actual class ResultHolder<T> private constructor(
    private val core: CoreResultHolder<T>
) : TargetDataHolder<T>, ResponseDataHolder<T> by core {

    override val flow get() = ResponseStateFlow.from(flow = liveFlow, initial = get())

    actual constructor () : this(CoreResultHolder())

    actual fun init(splinter: Splinter<T>) = core.init(splinter)
}
