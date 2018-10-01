package br.com.arch.toolkit.livedata

import br.com.arch.toolkit.livedata.model.DataResult
import br.com.arch.toolkit.livedata.model.enum.DataResultStatus.ERROR
import br.com.arch.toolkit.livedata.model.enum.DataResultStatus.LOADING
import br.com.arch.toolkit.livedata.model.enum.DataResultStatus.SUCCESS

class MutableResponseLiveData<T> : ResponseLiveData<T>() {

    fun postLoading() {
        postValue(DataResult(null, null, LOADING))
    }

    fun postError(error: Throwable) {
        postValue(DataResult(null, error, ERROR))
    }

    fun postData(data: T) {
        postValue(DataResult(data, null, SUCCESS))
    }

    override fun compute() = Unit

    override fun abort() = Unit
}