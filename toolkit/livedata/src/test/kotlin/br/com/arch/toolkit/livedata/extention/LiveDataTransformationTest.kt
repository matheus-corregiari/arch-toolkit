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
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.times

@DelicateCoroutinesApi
class LiveDataTransformationTest {

    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var mockedTransformation: (String) -> Int

    private var owner = object : LifecycleOwner {
        private val registry = LifecycleRegistry(this)
        override fun getLifecycle(): Lifecycle {
            registry.currentState = Lifecycle.State.RESUMED
            return registry
        }
    }

    @Before
    fun setup() {
        mockedTransformation = mock()

        given(mockedTransformation.invoke("ONE")).willReturn(0)
        given(mockedTransformation.invoke("TWO")).willReturn(0)
    }

    @Test
    fun mapShouldTransformThePostedData() {
        val mockedObserver: (Int) -> Unit = mock()
        val liveData = MutableLiveData<String>()
        val transformedLiveData = liveData.map(mockedTransformation)

        transformedLiveData.observeNotNull(owner, mockedObserver)

        liveData.postValue(null)

        Mockito.verifyNoInteractions(mockedObserver)

        liveData.postValue("ONE")
        Mockito.verify(mockedObserver).invoke(0)
    }

    @Test
    fun mapShouldTransformEachItemInPostedData() {
        val mockedObserver: (List<Int>) -> Unit = mock()
        val liveData = MutableLiveData<List<String>>()
        val transformedLiveData = liveData.mapList(mockedTransformation)

        transformedLiveData.observeNotNull(owner, mockedObserver)

        liveData.postValue(null)

        Mockito.verifyNoInteractions(mockedObserver)

        liveData.postValue(listOf("ONE", "TWO"))
        Mockito.verify(mockedTransformation, times(2)).invoke(any())
        Mockito.verify(mockedObserver).invoke(any())
    }

    @Test
    fun map_withTransformAsync_shouldTransformEachItemInPostedResponseDataStartingThreads() {
        val threadCount = Thread.activeCount()

        val mockedObserver: (List<Int>) -> Unit = mock()
        val liveData = MutableResponseLiveData<List<String>>()
        liveData.scope(GlobalScope)
        val transformedLiveData = liveData.mapList(mockedTransformation)

        transformedLiveData.observeData(owner, mockedObserver)

        liveData.postData(listOf("ONE", "TWO"))
        Assert.assertNotEquals(threadCount, Thread.activeCount())
        Thread.sleep(100)

        Mockito.verify(mockedTransformation, times(2)).invoke(any())
        Mockito.verify(mockedObserver).invoke(any())
    }

    @Test
    fun map_withoutTransformAsync_shouldTransformEachItemInPostedResponseDataWithoutStartingThreads() {
        val threadCount = Thread.activeCount()

        val mockedObserver: (List<Int>) -> Unit = mock()
        val liveData = MutableResponseLiveData<List<String>>()
        val transformedLiveData = liveData.mapList(mockedTransformation)

        transformedLiveData.observeData(owner, mockedObserver)

        liveData.postData(listOf("ONE", "TWO"))
        Assert.assertEquals(threadCount, Thread.activeCount())
        Thread.sleep(50)

        Mockito.verify(mockedTransformation, times(2)).invoke(any())
        Mockito.verify(mockedObserver).invoke(any())
    }
}