package br.com.arch.toolkit.livedata

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer

open class InterceptableLiveData<T> : LiveData<T>() {

    private var valueChangeInterceptors: MutableList<(T?) -> T?> = mutableListOf()
    private var valueInterceptors: MutableList<(T?) -> T?> = mutableListOf()
    private var observers: HashMap<Observer<T>, Observer<T>> = hashMapOf()

    override fun setValue(value: T?) {
        var newValue = value
        valueChangeInterceptors.forEach { newValue = it.invoke(newValue) }
        super.setValue(newValue)
    }

    override fun postValue(value: T?) {
        var newValue = value
        valueChangeInterceptors.forEach { newValue = it.invoke(newValue) }
        super.postValue(newValue)
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<T>) {
        val interceptorObserver = createInterceptorObserver(observer)
        observers[observer] = interceptorObserver
        super.observe(owner, interceptorObserver)
    }

    override fun observeForever(observer: Observer<T>) {
        val interceptorObserver = createInterceptorObserver(observer)
        observers[observer] = interceptorObserver
        super.observeForever(interceptorObserver)
    }

    override fun removeObserver(observer: Observer<T>) {
        observers[observer]?.let { super.removeObserver(it) }
    }

    override fun onInactive() {
        super.onInactive()
        observers.clear()
    }

    fun addValueInterceptor(interceptor: (T?) -> T?) {
        if (!valueChangeInterceptors.contains(interceptor))
            valueChangeInterceptors.add(interceptor)
    }

    fun removeValueInterceptor(interceptor: (T?) -> T?) {
        valueChangeInterceptors.remove(interceptor)
    }

    fun clearValueInterceptors() {
        valueChangeInterceptors = mutableListOf()
    }

    fun addInterceptor(interceptor: (T?) -> T?) {
        if (!valueInterceptors.contains(interceptor))
            valueInterceptors.add(interceptor)
    }

    fun removeInterceptor(interceptor: (T?) -> T?) {
        valueInterceptors.remove(interceptor)
    }

    fun clearInterceptors() {
        valueInterceptors = mutableListOf()
    }

    private fun createInterceptorObserver(observer: Observer<T>) = Observer<T> {
        var newValue = it
        valueInterceptors.forEach { newValue = it.invoke(newValue) }
        observer.onChanged(newValue)
    }

}