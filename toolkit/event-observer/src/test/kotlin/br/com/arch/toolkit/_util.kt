package br.com.arch.toolkit

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.LiveData
import br.com.arch.toolkit.livedata.ResponseLiveData
import br.com.arch.toolkit.result.DataResult

internal val alwaysOnOwner = object : LifecycleOwner {
    private val registry = LifecycleRegistry(this)
    override val lifecycle: Lifecycle
        get() {
            registry.currentState = Lifecycle.State.RESUMED
            return registry
        }
}

internal fun <T> ResponseLiveData<T>.testSetValue(value: DataResult<T>?) {
    LiveData::class.java.declaredMethods.find { it.name == "setValue" }?.let {
        it.isAccessible = true
        it.invoke(this, value)
    }
}

internal fun enableExceptionCheck() {
    Class.forName("kotlinx.coroutines.test.TestScopeKt")
        .getDeclaredMethod("setCatchNonTestRelatedExceptions", Boolean::class.java)
        .invoke(null, false)
}

internal fun disableExceptionCheck() {
    Class.forName("kotlinx.coroutines.test.TestScopeKt")
        .getDeclaredMethod("setCatchNonTestRelatedExceptions", Boolean::class.java)
        .invoke(null, true)
}
