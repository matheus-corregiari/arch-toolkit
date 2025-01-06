@file:Suppress("LongParameterList")

package br.com.arch.toolkit.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import br.com.arch.toolkit.annotation.Experimental
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus
import br.com.arch.toolkit.util.mergeAll
import br.com.arch.toolkit.util.mergeNotNull
import br.com.arch.toolkit.util.responseLiveDataOf
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

@Deprecated("Try to use chain or combine method variations")
internal interface ResponseLiveDataMergeDelegate {
    fun start()

    fun stop()

    fun merge(
        scope: CoroutineScope,
        transformDispatcher: CoroutineDispatcher,
        sources: List<Pair<String, ResponseLiveData<*>>>
    ): ResponseLiveData<Map<String, *>>

    fun <T, R> merge(
        first: ResponseLiveData<T>,
        second: ResponseLiveData<R>,
        scope: CoroutineScope,
        transformDispatcher: CoroutineDispatcher
    ): ResponseLiveData<Pair<T, R>>

    fun <T, R> followedBy(
        source: ResponseLiveData<T>,
        next: (T) -> ResponseLiveData<R>,
        scope: CoroutineScope,
        transformDispatcher: CoroutineDispatcher,
        condition: (T) -> Boolean,
        successOnConditionError: Boolean
    ): ResponseLiveData<Pair<T, R?>>
}

@Deprecated("Try to use chain or combine method variations")
internal class DefaultResponseLiveDataMergeDelegate : ResponseLiveDataMergeDelegate {

    private val mergerLiveData = MediatorLiveData<Any>()
    private val sourceObserver: (Any?) -> Unit = {}

    private var lastSources = mutableListOf<Any>()

    private val List<Pair<String, ResponseLiveData<*>>>.merged
        get() = map { it.first to it.second.value }.mergeAll()

    private fun resetObservers() = lastSources.forEach {
        val liveData = it as LiveData<*>
        liveData.removeObserver(sourceObserver)
        mergerLiveData.removeSource(liveData)
    }

    private fun <T> addSources(
        scope: CoroutineScope,
        transformDispatcher: CoroutineDispatcher,
        newSources: List<ResponseLiveData<*>>,
        onMerge: () -> DataResult<T>,
        shouldReset: Boolean = true,
        onChanged: (SwapResponseLiveData<T>.(DataResult<T>) -> Unit)? = null
    ): ResponseLiveData<T> {
        if (shouldReset) resetObservers()

        val result = SwapResponseLiveData(onMerge.invoke())
            .scope(scope)
            .transformDispatcher(transformDispatcher)
        result.notifyOnlyOnDistinct(true)

        newSources.forEach { liveData ->
            if (!liveData.hasObservers()) liveData.observeForever(sourceObserver)
            lastSources.add(liveData)
            mergerLiveData.addSource(liveData) {
                val merged = onMerge.invoke()
                if (result.value != merged) {
                    result.swapSource(
                        ResponseLiveData(merged)
                            .scope(scope)
                            .transformDispatcher(transformDispatcher)
                    )
                }

                if (onChanged != null) result.onChanged(merged)
            }
        }

        return result
    }

    @OptIn(Experimental::class)
    private fun <T, R> chainSources(
        scope: CoroutineScope,
        transformDispatcher: CoroutineDispatcher,
        source: ResponseLiveData<T>,
        next: (T) -> ResponseLiveData<R>,
        condition: (T) -> Boolean,
        successOnConditionError: Boolean
    ): ResponseLiveData<Pair<T, R?>> = addSources(
        scope = scope,
        transformDispatcher = transformDispatcher,
        newSources = listOf(source),
        onMerge = { source.value.mergeNotNull(DataResult(null, null, DataResultStatus.LOADING)) }
    ) {
        val sourceValue = source.value ?: return@addSources
        val sourceData = sourceValue.data

        val conditionMet = sourceData?.let(condition) == true
        val nextSource: ResponseLiveData<Pair<T, R?>> = when {
            !conditionMet && !successOnConditionError -> {
                responseLiveDataOf(IllegalStateException("Pre-condition not met for merge"))
            }

            !conditionMet && successOnConditionError -> {
                responseLiveDataOf(sourceData!! to null)
            }

            else -> source.mergeWith(next.invoke(sourceData!!)).map { it }
        }

        swapSource(nextSource)
    }

    override fun start() {
        if (!mergerLiveData.hasObservers()) mergerLiveData.observeForever(sourceObserver)
        lastSources.forEach {
            val liveData = it as LiveData<*>
            if (!liveData.hasObservers()) liveData.observeForever(sourceObserver)
        }
    }

    override fun stop() {
        mergerLiveData.removeObserver(sourceObserver)
        lastSources.forEach { (it as LiveData<*>).removeObserver(sourceObserver) }
    }

    override fun merge(
        scope: CoroutineScope,
        transformDispatcher: CoroutineDispatcher,
        sources: List<Pair<String, ResponseLiveData<*>>>
    ): ResponseLiveData<Map<String, *>> = addSources(
        scope = scope,
        transformDispatcher = transformDispatcher,
        newSources = sources.map { it.second },
        onMerge = { sources.merged }
    )

    override fun <T, R> merge(
        first: ResponseLiveData<T>,
        second: ResponseLiveData<R>,
        scope: CoroutineScope,
        transformDispatcher: CoroutineDispatcher
    ): ResponseLiveData<Pair<T, R>> = addSources(
        scope = scope,
        transformDispatcher = transformDispatcher,
        newSources = listOf(first, second),
        onMerge = { first.value.mergeNotNull(second.value) }
    )

    override fun <T, R> followedBy(
        source: ResponseLiveData<T>,
        next: (T) -> ResponseLiveData<R>,
        scope: CoroutineScope,
        transformDispatcher: CoroutineDispatcher,
        condition: (T) -> Boolean,
        successOnConditionError: Boolean
    ): ResponseLiveData<Pair<T, R?>> = chainSources(
        scope = scope,
        transformDispatcher = transformDispatcher,
        source = source,
        next = next,
        condition = condition,
        successOnConditionError = successOnConditionError
    )
}
