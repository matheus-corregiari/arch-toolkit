package br.com.arch.toolkit

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

internal val alwaysOnOwner = object : LifecycleOwner {
    private val registry = LifecycleRegistry(this)
    override val lifecycle: Lifecycle
        get() {
            registry.currentState = Lifecycle.State.RESUMED
            return registry
        }
}


@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {

    private var exceptionCheckEnabled: Boolean
        get() = Class.forName("kotlinx.coroutines.test.TestScopeKt")
            .getDeclaredMethod("getCatchNonTestRelatedExceptions", Boolean::class.java)
            .invoke(null) == true
        set(value) {
            Class.forName("kotlinx.coroutines.test.TestScopeKt")
                .getDeclaredMethod("setCatchNonTestRelatedExceptions", Boolean::class.java)
                .invoke(null, value)
        }

    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
        exceptionCheckEnabled = false
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
        exceptionCheckEnabled = true
    }
}
