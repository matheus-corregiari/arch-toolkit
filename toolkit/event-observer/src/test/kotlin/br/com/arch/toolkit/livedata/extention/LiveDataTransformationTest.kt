package br.com.arch.toolkit.livedata.extention

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.MutableLiveData
import br.com.arch.toolkit.livedata.response.MutableResponseLiveData
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verifyBlocking
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.times

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
        verifyBlocking(mockedObserver) { invoke(any()) }
    }

    @Test
    fun map_withTransformAsync_shouldTransformEachItemInPostedResponseDataStartingThreads() =
        runTest {

            val mockedObserver: (List<Int>) -> Unit = mock()
            val liveData = MutableResponseLiveData<List<String>>()
                .transformDispatcher(Dispatchers.Main)
            val transformedLiveData = liveData.mapList(mockedTransformation)

            transformedLiveData.observeData(owner, mockedObserver)

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

            transformedLiveData.observeData(owner, mockedObserver)

            liveData.postData(listOf("ONE", "TWO"))

            advanceUntilIdle()
            verifyBlocking(mockedTransformation, times(2)) { invoke(any()) }
            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke(any()) }
        }
}