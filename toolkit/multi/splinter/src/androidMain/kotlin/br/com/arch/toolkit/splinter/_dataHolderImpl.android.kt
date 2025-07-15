@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.splinter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.arch.toolkit.livedata.MutableResponseLiveData
import br.com.arch.toolkit.livedata.ResponseLiveData
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.util.dataResultNone
import br.com.arch.toolkit.util.safePostValue

internal actual class TargetRegularHolderImpl<T> actual constructor() :
    RegularHolderImpl<T>(), TargetRegularHolder<T> {

    private val _liveData = MutableLiveData<T?>(null)
    override val liveData: LiveData<T?> get() = _liveData

    override suspend fun set(value: T?) {
        _liveData.safePostValue(value)
        super.set(value)
    }

    override fun trySet(value: T?): Boolean {
        _liveData.safePostValue(value)
        return super.trySet(value)
    }
}

internal actual class TargetResultHolderImpl<T : Any> actual constructor() :
    ResultHolderImpl<T>(), TargetResultHolder<T> {

    private val _liveData = MutableResponseLiveData<T>(dataResultNone())
    override val liveData: ResponseLiveData<T> get() = _liveData

    override suspend fun set(value: DataResult<T>) {
        _liveData.safePostValue(value)
        super.set(value)
    }

    override fun trySet(value: DataResult<T>): Boolean {
        _liveData.safePostValue(value)
        return super.trySet(value)
    }
}
