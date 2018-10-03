package br.com.arch.toolkit.livedata.extention

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.MutableLiveData
import br.com.arch.toolkit.livedata.response.MutableResponseLiveData
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.times

class LiveDataTransformationTest {

    @Rule
    @get:Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private var owner = object : LifecycleOwner {
        private val registry = LifecycleRegistry(this)
        override fun getLifecycle(): Lifecycle {
            registry.markState(Lifecycle.State.RESUMED)
            return registry
        }
    }

    @Test
    fun mapShouldTransformThePostedData() {
        val mockedTransformation: (String) -> Int = mock()
        Mockito.`when`(mockedTransformation.invoke("ONE")).thenReturn(0)
        val mockedObserver: (Int) -> Unit = mock()
        val liveData = MutableLiveData<String>()
        val transformedLiveData = liveData.map(mockedTransformation)

        transformedLiveData.observe(owner, mockedObserver)

        liveData.postValue(null)

        Mockito.verifyZeroInteractions(mockedObserver)

        liveData.postValue("ONE")
        Mockito.verify(mockedObserver).invoke(0)
    }

    @Test
    fun mapShouldTransformEachItemInPostedData() {
        val mockedTransformation: (String) -> Int = mock()
        Mockito.`when`(mockedTransformation.invoke("ONE")).thenReturn(0)
        Mockito.`when`(mockedTransformation.invoke("TWO")).thenReturn(0)
        val mockedObserver: (List<Int>) -> Unit = mock()
        val liveData = MutableLiveData<List<String>>()
        val transformedLiveData = liveData.mapList(mockedTransformation)

        transformedLiveData.observe(owner, mockedObserver)

        liveData.postValue(null)

        Mockito.verifyZeroInteractions(mockedObserver)

        liveData.postValue(listOf("ONE", "TWO"))
        Mockito.verify(mockedTransformation, times(2)).invoke(any())
        Mockito.verify(mockedObserver).invoke(any())
    }

    @Test
    fun mapShouldTransformEachItemInPostedResponseData() {
        val mockedTransformation: (String) -> Int = mock()
        Mockito.`when`(mockedTransformation.invoke("ONE")).thenReturn(0)
        Mockito.`when`(mockedTransformation.invoke("TWO")).thenReturn(0)
        val mockedObserver: (List<Int>) -> Unit = mock()
        val liveData = MutableResponseLiveData<List<String>>()
        val transformedLiveData = liveData.mapList(mockedTransformation)

        transformedLiveData.observeData(owner, mockedObserver)

        liveData.postData(listOf("ONE", "TWO"))
        Thread.sleep(5)

        Mockito.verify(mockedTransformation, times(2)).invoke(any())
        Mockito.verify(mockedObserver).invoke(any())
    }
}