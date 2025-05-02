package br.com.arch.toolkit.livedata.response

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import br.com.arch.toolkit.livedata.MutableResponseLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runners.MethodSorters

// TODO Pensar numa forma melhor de fazer explodir
@OptIn(ExperimentalCoroutinesApi::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class LongTransformTest {

    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    init {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    private class StoppableObserver : LifecycleOwner {
        private val registry = LifecycleRegistry(this)

        init {
            registry.currentState = Lifecycle.State.RESUMED
        }

        override val lifecycle: Lifecycle
            get() = registry

        fun stop() {
            registry.currentState = Lifecycle.State.DESTROYED
        }
    }

    @Test
    fun `0 - `() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        liveData.scope(this)

        val owner = StoppableObserver()
        liveData.observe(owner) {
            data(
                transformer = {
                    println("1 - CHAMOU AQUI!")
                    owner.stop()
                    delay(500L)
                },
                observer = {
                    println("2 - CHAMOU AQUI!")
                }
            )
        }
        owner.stop()
        liveData.setData("String")
        delay(1000L)
    }
}
