package br.com.arch.toolkit.livedata.response

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import br.com.arch.toolkit.common.DataResult
import br.com.arch.toolkit.common.DataResultStatus
import br.com.arch.toolkit.common.mergeAll
import br.com.arch.toolkit.common.mergeWith
import br.com.arch.toolkit.livedata.extention.responseLiveDataOf
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

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
        next: (DataResult<T>) -> ResponseLiveData<R>,
        scope: CoroutineScope,
        transformDispatcher: CoroutineDispatcher,
        condition: (T) -> Boolean,
        successOnConditionError: Boolean
    ): ResponseLiveData<Pair<T, R?>>
}

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
        result.notifyOnlyOnDistinct = true

        newSources.forEach { liveData ->
            if (!liveData.hasObservers()) liveData.observeForever(sourceObserver)
            lastSources.add(liveData)
            mergerLiveData.addSource(liveData) {
                val merged = onMerge.invoke()
                if (result.value != merged) result.swapSource(ResponseLiveData(merged))

                if (onChanged != null) result.onChanged(merged)
            }
        }

        return result
    }

    private fun <T, R> chainSources(
        scope: CoroutineScope,
        transformDispatcher: CoroutineDispatcher,
        source: ResponseLiveData<T>,
        next: (DataResult<T>) -> ResponseLiveData<R>,
        condition: (T) -> Boolean,
        successOnConditionError: Boolean
    ): ResponseLiveData<Pair<T, R?>> = addSources(
        scope,
        transformDispatcher,
        listOf(source),
        { source.value.mergeWith(DataResult(null, null, DataResultStatus.LOADING)) }
    ) {
        val sourceValue = source.value ?: return@addSources

        val conditionMet = sourceValue.data?.let(condition) == true
        val nextSource: ResponseLiveData<Pair<T, R?>> = when {
            !conditionMet && !successOnConditionError -> {
                responseLiveDataOf(IllegalStateException("Pre-condition not met for merge"))
            }
            !conditionMet && successOnConditionError -> {
                responseLiveDataOf(sourceValue.data!! to null)
            }
            else -> source.mergeWith(next.invoke(sourceValue)).map { it }
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
        scope,
        transformDispatcher,
        sources.map { it.second },
        { sources.map { it.first to it.second.value }.mergeAll() }
    )

    override fun <T, R> merge(
        first: ResponseLiveData<T>,
        second: ResponseLiveData<R>,
        scope: CoroutineScope,
        transformDispatcher: CoroutineDispatcher
    ): ResponseLiveData<Pair<T, R>> = addSources(
        scope,
        transformDispatcher,
        listOf(first, second),
        { first.value.mergeWith(second.value) }
    )

    override fun <T, R> followedBy(
        source: ResponseLiveData<T>,
        next: (DataResult<T>) -> ResponseLiveData<R>,
        scope: CoroutineScope,
        transformDispatcher: CoroutineDispatcher,
        condition: (T) -> Boolean,
        successOnConditionError: Boolean
    ): ResponseLiveData<Pair<T, R?>> = chainSources(
        scope,
        transformDispatcher,
        source,
        next,
        condition,
        successOnConditionError
    )
}