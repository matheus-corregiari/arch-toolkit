@file:Suppress("ClassNaming", "ClassName")

package br.com.arch.toolkit.util

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import br.com.arch.toolkit.MainDispatcherRule
import br.com.arch.toolkit.alwaysOnOwner
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runners.MethodSorters
import org.mockito.Mockito
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyBlocking
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class _liveDataTest {

    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val rule = MainDispatcherRule()

    @Test
    fun `01 - observeNotNull`() {
        val mockedObserver: (Any) -> Unit = mock()
        val liveData = MutableLiveData<Any>()
        Assert.assertNull(liveData.value)

        liveData.observeNotNull(alwaysOnOwner, mockedObserver)

        liveData.value = null
        Mockito.verifyNoInteractions(mockedObserver)

        liveData.value = "nonNullData"
        verifyBlocking(mockedObserver) { invoke("nonNullData") }
    }

    @Test
    fun `02 - observeNull`() {
        val mockedObserver: () -> Unit = mock()
        val liveData = MutableLiveData<Any>()
        Assert.assertNull(liveData.value)

        liveData.observeNull(alwaysOnOwner, mockedObserver)

        liveData.value = null
        verifyBlocking(mockedObserver) { invoke() }

        liveData.value = "nonNullData"
        Mockito.verifyNoMoreInteractions(mockedObserver)
    }

    @Test
    fun `03 - observeSingle`() {
        val mockedObserver: (Any) -> Unit = mock()
        val liveData = MutableLiveData<Any>()
        Assert.assertNull(liveData.value)

        liveData.observeSingle(alwaysOnOwner, mockedObserver)

        liveData.value = null
        Mockito.verifyNoInteractions(mockedObserver)
        Assert.assertTrue(liveData.hasObservers())

        liveData.value = "nonNullData"
        verifyBlocking(mockedObserver) { invoke("nonNullData") }
        Assert.assertFalse(liveData.hasObservers())
    }

    @Test
    fun `04 - observeUntil`() {
        val mockedObserver: (Any?) -> Boolean = mock()
        whenever(mockedObserver.invoke(null)) doReturn false
        whenever(mockedObserver.invoke("FALSE")) doReturn false
        whenever(mockedObserver.invoke("TRUE")) doReturn true

        val liveData = MutableLiveData<Any>()
        Assert.assertNull(liveData.value)

        liveData.observeUntil(alwaysOnOwner, mockedObserver)

        liveData.value = null
        Assert.assertTrue(liveData.hasObservers())
        verifyBlocking(mockedObserver) { invoke(null) }

        liveData.value = "FALSE"
        Assert.assertTrue(liveData.hasObservers())
        verifyBlocking(mockedObserver) { invoke("FALSE") }

        liveData.value = "TRUE"
        verifyBlocking(mockedObserver) { invoke("TRUE") }
        Assert.assertFalse(liveData.hasObservers())
    }

    @Test
    fun `05 - LiveData mapList`() {
        val mockedTransformer: (String) -> Int = mock()
        val mockedObserver: (List<Int>?) -> Unit = mock()

        whenever(mockedTransformer.invoke("String")) doReturn 123

        val liveData = MutableLiveData<List<String>>()
        val transformedLiveData = liveData.mapList(mockedTransformer)

        transformedLiveData.observe(alwaysOnOwner, mockedObserver)
        verifyNoInteractions(mockedTransformer)
        verifyNoInteractions(mockedObserver)

        Assert.assertNull(liveData.value)
        Assert.assertNull(transformedLiveData.value)

        liveData.value = null
        verifyNoInteractions(mockedTransformer)
        verifyBlocking(mockedObserver) { invoke(null) }

        liveData.value = listOf("String")
        verifyBlocking(mockedTransformer) { invoke("String") }
        verifyBlocking(mockedObserver) { invoke(listOf(123)) }
    }

    @Test
    fun `06 - LiveData mapNotNull`() {
        val mockedTransformer: (String?) -> Int? = mock()
        val mockedObserver: (Int) -> Unit = mock()

        whenever(mockedTransformer.invoke(null)) doReturn 321
        whenever(mockedTransformer.invoke("String")) doReturn 123
        whenever(mockedTransformer.invoke("null")) doReturn null

        val liveData = MutableLiveData<String>()
        val transformedLiveData = liveData.mapNotNull(mockedTransformer)

        transformedLiveData.observe(alwaysOnOwner, mockedObserver)
        verifyNoInteractions(mockedTransformer)
        verifyNoInteractions(mockedObserver)

        Assert.assertNull(liveData.value)
        Assert.assertNull(transformedLiveData.value)

        liveData.value = null
        verifyBlocking(mockedTransformer) { invoke(null) }
        verifyBlocking(mockedObserver) { invoke(321) }

        liveData.value = "null"
        verifyBlocking(mockedTransformer) { invoke("null") }
        verifyNoMoreInteractions(mockedObserver)

        liveData.value = "String"
        verifyBlocking(mockedTransformer) { invoke("String") }
        verifyBlocking(mockedObserver) { invoke(123) }
    }
}
