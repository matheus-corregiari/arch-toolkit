package br.com.arch.toolkit.livedata

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import br.com.arch.toolkit.livedata.extention.observeUntil
import br.com.arch.toolkit.livedata.model.DataResult
import br.com.arch.toolkit.livedata.model.enum.DataResultStatus
import br.com.arch.toolkit.livedata.model.enum.DataResultStatus.ERROR
import br.com.arch.toolkit.livedata.model.enum.DataResultStatus.LOADING

abstract class ResponseLiveData<T> : ComputableLiveData<DataResult<T>>() {

    val error: Throwable?
        get() = value?.error

    val status: DataResultStatus?
        get() = value?.status

    val data: T?
        get() = value?.data

    // region Loading observer methods
    fun observeLoading(owner: LifecycleOwner, observer: (Boolean) -> Unit): ResponseLiveData<T> {
        observe(owner, Observer {
            observer.invoke(it?.status == LOADING)
        })
        return this
    }

    fun observeShowLoading(owner: LifecycleOwner, observer: () -> Unit): ResponseLiveData<T> {
        observe(owner, Observer {
            if (it?.status == LOADING) observer.invoke()
        })
        return this
    }

    fun observeHideLoading(owner: LifecycleOwner, observer: () -> Unit): ResponseLiveData<T> {
        observe(owner, Observer {
            if (it?.status != LOADING) observer.invoke()
        })
        return this
    }

    fun observeSingleShowLoading(owner: LifecycleOwner, observer: () -> Unit): ResponseLiveData<T> {
        observeUntil(owner) {
            if (it?.status == LOADING) observer.invoke()
            it?.status == LOADING
        }
        return this
    }

    fun observeSingleHideLoading(owner: LifecycleOwner, observer: () -> Unit): ResponseLiveData<T> {
        observeUntil(owner) {
            if (it?.status != LOADING) observer.invoke()
            it?.status != LOADING
        }
        return this
    }
    // endregion

    // region Error observer methods
    fun observeError(owner: LifecycleOwner, observer: (Throwable) -> Unit): ResponseLiveData<T> {
        observe(owner, Observer {
            if (it?.status == ERROR) it.error?.apply(observer)
        })
        return this
    }

    fun observeError(owner: LifecycleOwner, observer: () -> Unit): ResponseLiveData<T> {
        observe(owner, Observer {
            if (it?.status == ERROR) observer.invoke()
        })
        return this
    }

    fun observeSingleError(owner: LifecycleOwner, observer: (Throwable) -> Unit): ResponseLiveData<T> {
        observeUntil(owner) {
            if (it?.status == ERROR) it.error?.apply(observer)
            it?.status == ERROR
        }
        return this
    }

    fun observeSingleError(owner: LifecycleOwner, observer: () -> Unit): ResponseLiveData<T> {
        observeUntil(owner) {
            if (it?.status == ERROR) observer.invoke()
            it?.status == ERROR
        }
        return this
    }
    // endregion

    // region Success observer methods
    fun observeData(owner: LifecycleOwner, observer: (T) -> Unit): ResponseLiveData<T> {
        observe(owner, Observer {
            if (it?.status == DataResultStatus.SUCCESS) it.data?.apply(observer)
        })
        return this
    }

    fun observeSuccess(owner: LifecycleOwner, observer: () -> Unit): ResponseLiveData<T> {
        observe(owner, Observer {
            if (it?.status == DataResultStatus.SUCCESS) observer.invoke()
        })
        return this
    }

    fun observeSingleData(owner: LifecycleOwner, observer: (T) -> Unit): ResponseLiveData<T> {
        observeUntil(owner) {
            if (it?.status == DataResultStatus.SUCCESS) it.data?.apply(observer)
            it?.status == DataResultStatus.SUCCESS
        }
        return this
    }

    fun observeSingleSuccess(owner: LifecycleOwner, observer: () -> Unit): ResponseLiveData<T> {
        observeUntil(owner) {
            if (it?.status == DataResultStatus.SUCCESS) observer.invoke()
            it?.status == DataResultStatus.SUCCESS
        }
        return this
    }
    // endregion

    fun <R> map(transformation: ((T) -> R)): ResponseLiveData<R> {
        val liveData = MediatorResponseLiveData<R>()
        liveData.swapSource(this, transformation)
        return liveData
    }

    fun onNext(onNext: ((T) -> Unit)): ResponseLiveData<T> {
        return map {
            onNext(it)
            it
        }
    }
}