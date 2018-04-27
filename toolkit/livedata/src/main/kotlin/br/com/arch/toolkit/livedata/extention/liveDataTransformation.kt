package br.com.arch.toolkit.livedata.extention

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import br.com.arch.toolkit.livedata.ComputableLiveData
import br.com.arch.toolkit.livedata.SwapLiveData

fun <T, R> LiveData<T>.map(transformation: (T?) -> R?): LiveData<R> {
    val liveData = MediatorLiveData<R>()
    liveData.addSource(this, transformation)
    return liveData
}

fun <T, R> ComputableLiveData<T>.map(transformation: (T?) -> R?): ComputableLiveData<R> {
    val liveData = SwapLiveData<R>()
    liveData.swapSource(this, transformation)
    return liveData
}

fun <T, R> MediatorLiveData<T>.addSource(source: LiveData<R>, transformation: (R?) -> T?) = addSource(source) {
    value = transformation.invoke(it)
}