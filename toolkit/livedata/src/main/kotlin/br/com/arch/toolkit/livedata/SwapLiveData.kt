package br.com.arch.toolkit.livedata

import android.arch.lifecycle.MediatorLiveData

open class SwapLiveData<T> : ComputableLiveData<T>() {

    private val sourceLiveData = MediatorLiveData<Any>()
    private val sourceObserver: (Any?) -> Unit = {}
    private var lastSource: ComputableLiveData<*>? = null

    val hasDataSource: Boolean
        get() = lastSource != null

    fun swapSource(source: ComputableLiveData<T>) {
        clearSource()
        sourceLiveData.addSource(source, ::setValue)
        lastSource = source
    }

    fun <R> swapSource(source: ComputableLiveData<R>, transformation: (R?) -> T?) {
        clearSource()
        sourceLiveData.addSource(source) {
            value = it.let(transformation)
        }
        lastSource = source
    }

    override fun onActive() {
        super.onActive()
        if (!sourceLiveData.hasObservers()) sourceLiveData.observeForever(sourceObserver)
    }

    override fun onInactive() {
        super.onInactive()
        sourceLiveData.removeObserver(sourceObserver)
    }

    override fun compute() = Unit

    override fun abort() = Unit

    override fun invalidate() {
        super.invalidate()
        lastSource?.invalidate()
    }

    override fun interrupt() {
        super.interrupt()
        lastSource?.interrupt()
    }

    private fun clearSource() {
        lastSource?.let { sourceLiveData.removeSource(it) }
        lastSource = null
    }
}
