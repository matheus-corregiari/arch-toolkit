package br.com.arch.toolkit.livedata.extention

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.MutableLiveData
import br.com.arch.toolkit.livedata.MutableResponseLiveData
import br.com.arch.toolkit.util.map
import br.com.arch.toolkit.util.mapList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyBlocking

@OptIn(ExperimentalCoroutinesApi::class)
class LiveDataTransformationTest {

    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var mockedTransformation: (String) -> Int

    private var owner = object : LifecycleOwner {
        private val registry = LifecycleRegistry(this)
        override val lifecycle: Lifecycle
            get() {
                registry.currentState = Lifecycle.State.RESUMED
                return registry
            }
    }

    init {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Before
    fun setup() = runTest {
        mockedTransformation = mock()

        given(mockedTransformation.invoke("ONE")).willReturn(0)
        given(mockedTransformation.invoke("TWO")).willReturn(0)
    }

    @Test
    fun mapShouldTransformThePostedData() = runTest {
        val mockedObserver: (Int) -> Unit = mock()
        val liveData = MutableLiveData<String>()
        val transformedLiveData = liveData.map(mockedTransformation)

        transformedLiveData.observe(owner, mockedObserver)

        liveData.postValue(null)

        Mockito.verifyNoInteractions(mockedObserver)

        liveData.postValue("ONE")
        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke(0) }
    }

    @Test
    fun mapShouldTransformEachItemInPostedData() = runTest {
        val mockedObserver: (List<Int>) -> Unit = mock()
        val liveData = MutableLiveData<List<String>>()
        val transformedLiveData = liveData.mapList(mockedTransformation)

        transformedLiveData.observe(owner, mockedObserver)

        liveData.postValue(null)

        Mockito.verifyNoInteractions(mockedObserver)

        liveData.postValue(listOf("ONE", "TWO"))
        advanceUntilIdle()
        verifyBlocking(mockedTransformation, times(2)) { invoke(any()) }
        advanceUntilIdle()
        verifyBlocking(mockedObserver, times(1)) { invoke(any()) }
    }

    @Test
    fun map_withTransformAsync_shouldTransformEachItemInPostedResponseDataStartingThreads() =
        runTest {

            val mockedObserver: (List<Int>) -> Unit = mock()
            val liveData = MutableResponseLiveData<List<String>>()
                .transformDispatcher(Dispatchers.Main)
            val transformedLiveData = liveData.mapList(mockedTransformation)

            transformedLiveData.observe(owner) { data(observer = mockedObserver) }

            liveData.postData(listOf("ONE", "TWO"))

            advanceUntilIdle()
            verifyBlocking(mockedTransformation, times(2)) { invoke(any()) }
            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke(any()) }
        }

    @Test
    fun map_withoutTransformAsync_shouldTransformEachItemInPostedResponseDataWithoutStartingThreads() =
        runTest {

            val mockedObserver: (List<Int>) -> Unit = mock()
            val liveData = MutableResponseLiveData<List<String>>()
                .transformDispatcher(Dispatchers.Main)
            val transformedLiveData = liveData.mapList(mockedTransformation)

            transformedLiveData.observe(owner) { data(observer = mockedObserver) }

            liveData.postData(listOf("ONE", "TWO"))

            advanceUntilIdle()
            verifyBlocking(mockedTransformation, times(2)) { invoke(any()) }
            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke(any()) }
        }
}