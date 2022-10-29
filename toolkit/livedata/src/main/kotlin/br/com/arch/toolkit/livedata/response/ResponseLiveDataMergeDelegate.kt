package br.com.arch.toolkit.livedata.response

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import br.com.arch.toolkit.common.DataResult
import br.com.arch.toolkit.common.mergeAll
import br.com.arch.toolkit.common.mergeWith
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
        onMerge: () -> DataResult<T>,
        sources: List<ResponseLiveData<*>>
    ): ResponseLiveData<T> {
        resetObservers()

        val result = MutableResponseLiveData(onMerge.invoke())
            .scope(scope)
            .transformDispatcher(transformDispatcher)

        sources.forEach { liveData ->
            if (!liveData.hasObservers()) liveData.observeForever(sourceObserver)
            lastSources.add(liveData)
            mergerLiveData.addSource(liveData) {
                val merged = onMerge.invoke()
                if (result.value != merged) result.value = merged
            }
        }

        return result
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
    ): ResponseLiveData<Map<String, *>> =
        addSources(scope, transformDispatcher, { sources.merged }, sources.map { it.second })

    @Suppress("UNCHECKED_CAST")
    override fun <T, R> merge(
        first: ResponseLiveData<T>,
        second: ResponseLiveData<R>,
        scope: CoroutineScope,
        transformDispatcher: CoroutineDispatcher
    ): ResponseLiveData<Pair<T, R>> =
        addSources(
            scope,
            transformDispatcher,
            { first.value.mergeWith(second.value) },
            listOf(first, second)
        )
}