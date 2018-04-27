package br.com.arch.toolkit.livedata

open class VariableLiveData<T> : InterceptableLiveData<T>() {

    public override fun setValue(value: T?) = super.setValue(value)

    public override fun postValue(value: T?) = super.postValue(value)

}