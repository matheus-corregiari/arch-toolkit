package br.com.arch.toolkit

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

internal val alwaysOnOwner = object : LifecycleOwner {
    private val registry = LifecycleRegistry(this)
    override val lifecycle: Lifecycle
        get() {
            registry.currentState = Lifecycle.State.RESUMED
            return registry
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
