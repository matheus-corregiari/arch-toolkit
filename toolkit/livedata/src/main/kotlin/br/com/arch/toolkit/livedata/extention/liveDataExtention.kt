package br.com.arch.toolkit.livedata.extention

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Observer

fun <T> LiveData<T>.observe(owner: LifecycleOwner, observer: (T) -> Unit) = observe(owner, Observer { it?.let(observer) })

fun <T> LiveData<T>.observeSingleNullable(owner: LifecycleOwner, observer: ((T?) -> Unit)) = observeUntil(owner) {
    it.let(observer)
    true
}

fun <T> LiveData<T>.observeSingle(owner: LifecycleOwner, observer: ((T) -> Unit)) = observeUntil(owner) {
    it?.let(observer)
    it != null
}

fun <T> LiveData<T>.observeUntil(owner: LifecycleOwner, observer: ((T?) -> Boolean)) = observe(owner, object : Observer<T> {
    override fun onChanged(data: T?) {
        if (data.let(observer)) removeObserver(this)
    }
})

fun <T> LiveData<T>.observeSingle(observer: ((T?) -> Unit)) = observeUntil {
    it.let(observer)
    true
}

fun <T> LiveData<T>.observeUntil(observer: ((T?) -> Boolean)) = observeForever(object : Observer<T> {
    override fun onChanged(data: T?) {
        if (data.let(observer)) removeObserver(this)
    }
})


fun <T> MediatorLiveData<T>.addSource(source: LiveData<T>) = addSource(source) {
    value = it
}