package br.com.arch.toolkit.livedata.response

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.arch.toolkit.Mocks
import br.com.arch.toolkit.alwaysOnOwner
import br.com.arch.toolkit.livedata.MutableResponseLiveData
import br.com.arch.toolkit.livedata.ResponseLiveData
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus
import br.com.arch.toolkit.result.DataResultStatus.*
import br.com.arch.toolkit.testSetValue
import br.com.arch.toolkit.util.dataResultLoading
import br.com.arch.toolkit.util.dataResultSuccess
import br.com.arch.toolkit.util.mutableResponseLiveDataOf
import br.com.arch.toolkit.util.responseLiveDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyBlocking
import org.mockito.kotlin.verifyNoInteractions

@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("LargeClass")
class ResponseLiveDataTest {

    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    init {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun `0 - init without param, should init with null value`() = runTest {
        val liveData = ResponseLiveData<Any>()
        liveData.scope(this)
        liveData.transformDispatcher(Dispatchers.Main.immediate)

        Assert.assertNull(liveData.value)
        Assert.assertNull(liveData.error)
        Assert.assertNull(liveData.data)
        Assert.assertNull(liveData.status)
    }

    @Test
    fun `0 - init with param, should init with param value`() = runTest {
        val value = DataResult("String", null, SUCCESS)
        val liveData = ResponseLiveData(value)

        Assert.assertEquals(value, liveData.value)
        Assert.assertNull(liveData.error)
        Assert.assertEquals("String", liveData.data)
        Assert.assertEquals(SUCCESS, liveData.status)
    }

    @Test
    fun `1 - Null Value - Any observer must be triggered`() = runTest {
        // Mocks
        val mocks = Mocks<Any>()

        // LiveData
        val liveData = ResponseLiveData<Any>()
        mocks.apply(true, liveData)
        mocks.assertAllZeroInteractions()

        // Update Value
        liveData.testSetValue(null)

        // Verify mock interactions
        mocks.assertAllZeroInteractions()

        // Assert Data
        Assert.assertNull(liveData.value)
        Assert.assertNull(liveData.data)
        Assert.assertNull(liveData.status)
        Assert.assertNull(liveData.error)
    }

    // region Loading
    @Test
    fun `LOADING - single false - null, null, LOADING`() = runTest {
        // Mocks
        val mocks = Mocks<Any>()

        // LiveData
        val liveData = ResponseLiveData<Any>()
        mocks.apply(false, liveData)
        mocks.assertAllZeroInteractions()

        // Update Value
        liveData.testSetValue(dataResultLoading())
        advanceUntilIdle()
        verifyBlocking(mocks.showLoadingObserver) { invoke() }
        verifyNoInteractions(mocks.hideLoadingObserver)
        verifyBlocking(mocks.loadingObserver) { invoke(true) }
        verifyNoInteractions(mocks.successObserver)
        verifyBlocking(mocks.statusObserver) { invoke(LOADING) }
        verifyBlocking(mocks.resultObserver) { invoke(dataResultLoading()) }

        // Update Value
        liveData.testSetValue(dataResultSuccess(null))
        advanceUntilIdle()
        verifyNoMoreInteractions(mocks.showLoadingObserver)
        verifyBlocking(mocks.hideLoadingObserver) { invoke() }
        verifyBlocking(mocks.loadingObserver) { invoke(false) }
        verifyBlocking(mocks.successObserver) { invoke() }
        verifyBlocking(mocks.statusObserver) { invoke(SUCCESS) }
        verifyBlocking(mocks.resultObserver) { invoke(dataResultSuccess(null)) }

        // Update Value
        liveData.testSetValue(dataResultLoading())
        advanceUntilIdle()
        verifyBlocking(mocks.showLoadingObserver, times(2)) { invoke() }
        verifyNoMoreInteractions(mocks.hideLoadingObserver)
        verifyBlocking(mocks.loadingObserver, times(2)) { invoke(true) }
        verifyNoMoreInteractions(mocks.successObserver)
        verifyBlocking(mocks.statusObserver, times(2)) { invoke(LOADING) }
        verifyBlocking(mocks.resultObserver, times(2)) { invoke(dataResultLoading()) }
    }

    @Test
    fun `LOADING - single true - null, null, LOADING`() = runTest {
        // Mocks
        val mocks = Mocks<Any>()

        // LiveData
        val liveData = ResponseLiveData<Any>()
        mocks.apply(true, liveData)
        mocks.assertAllZeroInteractions()

        // Update Value
        liveData.testSetValue(dataResultLoading())
        advanceUntilIdle()
        verifyBlocking(mocks.showLoadingObserver) { invoke() }
        verifyNoInteractions(mocks.hideLoadingObserver)
        verifyBlocking(mocks.loadingObserver) { invoke(true) }
        verifyNoInteractions(mocks.successObserver)
        verifyBlocking(mocks.statusObserver) { invoke(LOADING) }
        verifyBlocking(mocks.resultObserver) { invoke(dataResultLoading()) }

        // Update Value
        liveData.testSetValue(dataResultSuccess(null))
        advanceUntilIdle()
        verifyNoMoreInteractions(mocks.showLoadingObserver)
        verifyBlocking(mocks.hideLoadingObserver) { invoke() }
        verifyBlocking(mocks.loadingObserver) { invoke(false) }
        verifyBlocking(mocks.successObserver) { invoke() }
        verifyNoMoreInteractions(mocks.statusObserver)
        verifyNoMoreInteractions(mocks.resultObserver)

        // Update Value
        liveData.testSetValue(dataResultLoading())
        advanceUntilIdle()
        verifyNoMoreInteractions(mocks.showLoadingObserver)
        verifyNoMoreInteractions(mocks.hideLoadingObserver)
        verifyNoMoreInteractions(mocks.loadingObserver)
        verifyNoMoreInteractions(mocks.successObserver)
        verifyNoMoreInteractions(mocks.statusObserver)
        verifyNoMoreInteractions(mocks.resultObserver)
    }

    @Test
    fun whenObserveShowLoading_shouldBeCalledWhenStatusIsLoading() = runTest {
        val mockedObserver: () -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
        liveData.observe(alwaysOnOwner) { showLoading(observer = mockedObserver) }

        liveData.setLoading()
        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke() }

        liveData.setError(IllegalStateException())
        Mockito.verifyNoMoreInteractions(mockedObserver)

        liveData.setLoading()
        advanceUntilIdle()
        verifyBlocking(mockedObserver, times(2)) { invoke() }
    }

    @Test
    fun whenObserveHideLoading_shouldBeCalledWhenStatusIsLoading() = runTest {
        val mockedObserver: () -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
        liveData.observe(alwaysOnOwner) { hideLoading(observer = mockedObserver) }

        liveData.setLoading()
        Mockito.verifyNoInteractions(mockedObserver)

        liveData.setError(IllegalStateException())
        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke() }

        liveData.setLoading()
        Mockito.verifyNoMoreInteractions(mockedObserver)
    }

    @Test
    fun whenObserveSingleLoading_shouldBeCalledWhenStatusIsLoading_untilReceiveAStatusDifferentThenLOADING() =
        runTest {
            val mockedObserver: (Boolean) -> Unit = mock()
            val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
            liveData.observe(alwaysOnOwner) { loading(single = true, observer = mockedObserver) }

            liveData.setLoading()
            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke(true) }

            Assert.assertTrue(liveData.hasObservers())

            liveData.setError(IllegalStateException())
            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke(false) }

            Assert.assertFalse(liveData.hasObservers())

            liveData.setLoading()
            Mockito.verifyNoMoreInteractions(mockedObserver)

            Assert.assertFalse(liveData.hasObservers())
        }

    @Test
    fun whenObserveSingleShowLoading_shouldBeCalledWhenStatusIsLoading_onlyOnce() = runTest {
        val mockedObserver: () -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
        liveData.observe(alwaysOnOwner) { showLoading(single = true, observer = mockedObserver) }

        liveData.setLoading()
        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke() }

        liveData.setError(IllegalStateException())
        Mockito.verifyNoMoreInteractions(mockedObserver)

        liveData.setLoading()
        Mockito.verifyNoMoreInteractions(mockedObserver)

        Assert.assertFalse(liveData.hasObservers())
    }

    @Test
    fun validateMergeNullValues() = runTest {
        val first = MutableResponseLiveData<String>()
        val second = MutableResponseLiveData<String>()
        val combination = first.mergeWith(second)

        Assert.assertNull(combination.data)
    }

    @Test
    fun validateMergeSingleFirstValue() = runTest {
        val first = responseLiveDataOf("first")
        val second = MutableResponseLiveData<String>()
        val combination = first.mergeWith(second)

        Assert.assertNull(combination.data)
        Assert.assertTrue(combination.status == LOADING)
    }

    @Test
    fun validateMergeSingleSecondValue() = runTest {
        val first = responseLiveDataOf("first")
        val second = MutableResponseLiveData<String>()
        val combination = first.mergeWith(second)

        Assert.assertNull(combination.data)
        Assert.assertTrue(combination.status == LOADING)
    }

    @Test
    fun validateMergeBothValues() = runTest {
        val first = responseLiveDataOf("first")
        val second = responseLiveDataOf("second")
        val combination = first.mergeWith(second)

        Assert.assertTrue(combination.data == (first.data to second.data))
    }

    @Test
    fun validateMergeSingleFirstError() = runTest {
        val firstThrowable = Throwable("first")
        val first = responseLiveDataOf<String>(firstThrowable)
        val second = MutableResponseLiveData<String>()
        val combination = first.mergeWith(second)

        Assert.assertTrue(combination.error == first.error)
    }

    @Test
    fun validateMergeSingleSecondError() = runTest {
        val first = MutableResponseLiveData<String>()
        val secondThrowable = Throwable("second")
        val second = responseLiveDataOf<String>(secondThrowable)
        val combination = first.mergeWith(second)

        Assert.assertTrue(combination.error == second.error)
    }

    @Test
    fun validateMergeBothError() = runTest {
        val firstThrowable = Throwable("first")
        val first = responseLiveDataOf<String>(firstThrowable)
        val secondThrowable = Throwable("second")
        val second = responseLiveDataOf<String>(secondThrowable)

        val combination = first.mergeWith(second)

        Assert.assertTrue(combination.error == first.error)
    }

    @Test
    fun validateObservePostMerge() = runTest {
        val mockedDataObserver: (Pair<String, String>) -> Unit = mock()
        val mockedLoadingObserver: (Boolean) -> Unit = mock()
        val mockedErrorObserver: (Throwable) -> Unit = mock()

        val first = MutableResponseLiveData<String>().transformDispatcher(Dispatchers.Main)
        val second = MutableResponseLiveData<String>().transformDispatcher(Dispatchers.Main)

        val combination = first.mergeWith(second)
        combination.observe(alwaysOnOwner) {
            loading(observer = mockedLoadingObserver)
            error(observer = mockedErrorObserver)
            data(observer = mockedDataObserver)
        }

        advanceUntilIdle()
        verifyNoMoreInteractions(mockedDataObserver)
        verifyNoMoreInteractions(mockedErrorObserver)
        verifyBlocking(mockedLoadingObserver) { invoke(true) }

        first.setData("first_set")
        advanceUntilIdle()
        verifyNoMoreInteractions(mockedDataObserver)
        verifyNoMoreInteractions(mockedErrorObserver)
        verifyBlocking(mockedLoadingObserver) { invoke(true) }

        second.setData("second_set")
        advanceUntilIdle()
        verifyBlocking(mockedLoadingObserver) { invoke(false) }
        verifyNoMoreInteractions(mockedErrorObserver)
        verifyBlocking(mockedDataObserver) { invoke("first_set" to "second_set") }
    }

    @Test
    fun validateObserveWithInitialValuePostMerge() = runTest {
        val mockedDataObserver: (Pair<String, String>) -> Unit = mock()
        val mockedLoadingObserver: (Boolean) -> Unit = mock()
        val mockedErrorObserver: (Throwable) -> Unit = mock()

        val first = mutableResponseLiveDataOf("first")
            .transformDispatcher(Dispatchers.Main)
        val second = mutableResponseLiveDataOf("second")

        val combination = first + second
        combination.observe(alwaysOnOwner) {
            loading(observer = mockedLoadingObserver)
            error(observer = mockedErrorObserver)
            data(observer = mockedDataObserver)
        }

        advanceUntilIdle()
        verifyBlocking(mockedDataObserver) { invoke("first" to "second") }
        verifyNoMoreInteractions(mockedErrorObserver)

        first.setData("first_set")
        advanceUntilIdle()
        verifyBlocking(mockedDataObserver) { invoke("first_set" to "second") }
        verifyNoMoreInteractions(mockedErrorObserver)

        second.setData("second_set")
        advanceUntilIdle()
        verifyBlocking(mockedDataObserver) { invoke("first_set" to "second_set") }
        verifyNoMoreInteractions(mockedErrorObserver)
    }

    @Test
    fun validateObservePostMultipleStepMerge() = runTest {
        val mockedDataObserver: (Pair<Pair<String, String>, String>) -> Unit = mock()

        val first = mutableResponseLiveDataOf("first")
            .transformDispatcher(Dispatchers.Main)
        val second = mutableResponseLiveDataOf("second")
            .transformDispatcher(Dispatchers.Main)
        val third = mutableResponseLiveDataOf("third")
            .transformDispatcher(Dispatchers.Main)

        val firstCombination = first.mergeWith(second)
        val secondCombination = firstCombination.mergeWith(third)
        secondCombination.observe(alwaysOnOwner) {
            data(observer = mockedDataObserver)
        }

        advanceUntilIdle()
        verifyBlocking(mockedDataObserver) { invoke(("first" to "second") to "third") }

        first.setData("first_set")
        advanceUntilIdle()
        verifyBlocking(mockedDataObserver) { invoke(("first_set" to "second") to "third") }

        second.setData("second_set")
        advanceUntilIdle()
        verifyBlocking(mockedDataObserver) { invoke(("first_set" to "second_set") to "third") }

        third.setData("third_set")
        advanceUntilIdle()
        verifyBlocking(mockedDataObserver) { invoke(("first_set" to "second_set") to "third_set") }
    }

    @Test
    fun validateObservePostMultipleMerges() = runTest {
        val mockedDataObserver: (Map<String, *>) -> Unit = mock()
        val mockedLoadingObserver: (Boolean) -> Unit = mock()
        val mockedErrorObserver: (Throwable) -> Unit = mock()

        val first = MutableResponseLiveData<String>()
            .transformDispatcher(Dispatchers.Main)
        val second = MutableResponseLiveData<String>()
        val third = MutableResponseLiveData<String>()

        val combination = first.mergeWith("first", "second" to second, "third" to third)
        combination.observe(alwaysOnOwner) {
            loading(observer = mockedLoadingObserver)
            error(observer = mockedErrorObserver)
            data(observer = mockedDataObserver)
        }

        advanceUntilIdle()
        verifyNoMoreInteractions(mockedDataObserver)
        verifyNoMoreInteractions(mockedErrorObserver)
        verifyBlocking(mockedLoadingObserver) { invoke(true) }

        first.setData("first_set")
        advanceUntilIdle()
        verifyNoMoreInteractions(mockedDataObserver)
        verifyNoMoreInteractions(mockedErrorObserver)
        verifyNoMoreInteractions(mockedLoadingObserver)

        second.setData("second_set")
        advanceUntilIdle()
        verifyNoMoreInteractions(mockedDataObserver)
        verifyNoMoreInteractions(mockedErrorObserver)
        verifyNoMoreInteractions(mockedLoadingObserver)

        third.setData("third_set")
        advanceUntilIdle()
        verifyNoMoreInteractions(mockedErrorObserver)
        verifyBlocking(mockedLoadingObserver) { invoke(false) }
        verifyBlocking(mockedDataObserver) {
            invoke(
                mapOf(
                    "first" to "first_set",
                    "second" to "second_set",
                    "third" to "third_set"
                )
            )
        }
    }

    @Test
    fun validateObserveErrorPostMultipleMerges() = runTest {
        val mockedDataObserver: (Map<String, *>) -> Unit = mock()
        val mockedLoadingObserver: (Boolean) -> Unit = mock()
        val mockedErrorObserver: (Throwable) -> Unit = mock()

        val first = MutableResponseLiveData<String>()
            .transformDispatcher(Dispatchers.Main)
        val second = MutableResponseLiveData<String>()
        val third = MutableResponseLiveData<String>()

        val combination = first.mergeWith("first", "second" to second, "third" to third)
        combination.observe(alwaysOnOwner) {
            loading(observer = mockedLoadingObserver)
            error(observer = mockedErrorObserver)
            data(observer = mockedDataObserver)
        }

        advanceUntilIdle()
        verifyNoMoreInteractions(mockedDataObserver)
        verifyNoMoreInteractions(mockedErrorObserver)
        verifyBlocking(mockedLoadingObserver) { invoke(true) }

        first.setData("first_set")
        advanceUntilIdle()
        verifyNoMoreInteractions(mockedDataObserver)
        verifyNoMoreInteractions(mockedErrorObserver)
        verifyNoMoreInteractions(mockedLoadingObserver)

        second.setData("second_set")
        advanceUntilIdle()
        verifyNoMoreInteractions(mockedDataObserver)
        verifyNoMoreInteractions(mockedErrorObserver)
        verifyNoMoreInteractions(mockedLoadingObserver)

        val error = Throwable("third")
        third.setError(error)
        advanceUntilIdle()
        verifyBlocking(mockedErrorObserver) { invoke(error) }
        verifyBlocking(mockedLoadingObserver) { invoke(false) }
        verifyNoMoreInteractions(mockedDataObserver)
    }

    @Test
    fun validateObservePostMultipleMergesWithInitialValue() = runTest {
        val mockedDataObserver: (Map<String, *>) -> Unit = mock()

        val first = mutableResponseLiveDataOf("first")
            .transformDispatcher(Dispatchers.Main)
        val second = mutableResponseLiveDataOf("second")
        val third = mutableResponseLiveDataOf("third")

        val combination = first.mergeWith("first", "second" to second, "third" to third)
        combination.observe(alwaysOnOwner) {
            data(observer = mockedDataObserver)
        }

        advanceUntilIdle()
        verifyBlocking(mockedDataObserver) {
            invoke(mapOf("first" to "first", "second" to "second", "third" to "third"))
        }

        first.setData("first_set")
        advanceUntilIdle()
        verifyBlocking(mockedDataObserver) {
            invoke(mapOf("first" to "first_set", "second" to "second", "third" to "third"))
        }

        second.setData("second_set")
        advanceUntilIdle()
        verifyBlocking(mockedDataObserver) {
            invoke(mapOf("first" to "first_set", "second" to "second_set", "third" to "third"))
        }

        third.setData("third_set")
        advanceUntilIdle()
        verifyBlocking(mockedDataObserver) {
            invoke(
                mapOf(
                    "first" to "first_set",
                    "second" to "second_set",
                    "third" to "third_set"
                )
            )
        }
    }

    @Test
    fun validateObserveOnFollowedBy() = runTest {
        val mockedDataObserver: (Pair<String, String>) -> Unit = mock()
        val mockedLoadingObserver: (Boolean) -> Unit = mock()
        val mockedErrorObserver: (Throwable) -> Unit = mock()

        val first = MutableResponseLiveData<String>()
            .transformDispatcher(Dispatchers.Main)
        val second = MutableResponseLiveData<String>()

        val combination = first.followedBy { second }
        combination.observe(alwaysOnOwner) {
            loading(observer = mockedLoadingObserver)
            error(observer = mockedErrorObserver)
            data(observer = mockedDataObserver)
        }

        advanceUntilIdle()
        verifyNoMoreInteractions(mockedDataObserver)
        verifyNoMoreInteractions(mockedErrorObserver)
        verifyBlocking(mockedLoadingObserver) { invoke(true) }

        first.setData("first_set")
        advanceUntilIdle()
        verifyNoMoreInteractions(mockedDataObserver)
        verifyNoMoreInteractions(mockedErrorObserver)
        verifyNoMoreInteractions(mockedLoadingObserver)

        second.setData("second_set")
        advanceUntilIdle()
        verifyBlocking(mockedDataObserver) { invoke("first_set" to "second_set") }
        verifyBlocking(mockedLoadingObserver) { invoke(false) }
        verifyNoMoreInteractions(mockedErrorObserver)
    }

    @Test
    fun validateObserveOnFollowedByWithConditionMet() = runTest {
        val mockedDataObserver: (Pair<String, String>) -> Unit = mock()
        val mockedLoadingObserver: (Boolean) -> Unit = mock()
        val mockedErrorObserver: (Throwable) -> Unit = mock()

        val first = MutableResponseLiveData<String>()
            .transformDispatcher(Dispatchers.Main)
        val second = MutableResponseLiveData<String>()

        val combination = first.followedBy({ second }, { it == "first_set" })
        combination.observe(alwaysOnOwner) {
            loading(observer = mockedLoadingObserver)
            error(observer = mockedErrorObserver)
            data(observer = mockedDataObserver)
        }

        advanceUntilIdle()
        verifyNoMoreInteractions(mockedDataObserver)
        verifyNoMoreInteractions(mockedErrorObserver)
        verifyBlocking(mockedLoadingObserver) { invoke(true) }

        first.setData("first_set")
        advanceUntilIdle()
        verifyNoMoreInteractions(mockedDataObserver)
        verifyNoMoreInteractions(mockedErrorObserver)
        verifyNoMoreInteractions(mockedLoadingObserver)

        second.setData("second_set")
        advanceUntilIdle()
        verifyBlocking(mockedDataObserver) { invoke("first_set" to "second_set") }
        verifyBlocking(mockedLoadingObserver) { invoke(false) }
        verifyNoMoreInteractions(mockedErrorObserver)
    }

    @Test
    fun validateObserveOnFollowedByWithConditionNotMet() = runTest {
        val mockedDataObserver: (Pair<String, String>) -> Unit = mock()
        val mockedLoadingObserver: (Boolean) -> Unit = mock()
        val mockedErrorObserver: (Throwable) -> Unit = mock()

        val first = MutableResponseLiveData<String>()
            .transformDispatcher(Dispatchers.Main)
        val second = MutableResponseLiveData<String>()

        val combination = first.followedBy({ second }, { it == "first" })
        combination.observe(alwaysOnOwner) {
            loading(observer = mockedLoadingObserver)
            error(observer = mockedErrorObserver)
            data(observer = mockedDataObserver)
        }

        advanceUntilIdle()
        verifyNoMoreInteractions(mockedDataObserver)
        verifyNoMoreInteractions(mockedErrorObserver)
        verifyBlocking(mockedLoadingObserver) { invoke(true) }

        first.setData("first_set")
        advanceUntilIdle()
        verifyNoMoreInteractions(mockedDataObserver)
        verifyBlocking(mockedLoadingObserver) { invoke(false) }
        verifyBlocking(mockedErrorObserver) { invoke(any()) }
    }

    @Test
    fun validateObserveOnFollowedByIgnoringConditionNotMet() = runTest {
        val mockedDataObserver: (Pair<String, String?>) -> Unit = mock()
        val mockedLoadingObserver: (Boolean) -> Unit = mock()
        val mockedErrorObserver: (Throwable) -> Unit = mock()

        val first = MutableResponseLiveData<String>()
            .transformDispatcher(Dispatchers.Main)
        val second = MutableResponseLiveData<String>()

        val combination = first.followedBy({ second }, { it == "first" }, true)
        combination.observe(alwaysOnOwner) {
            loading(observer = mockedLoadingObserver)
            error(observer = mockedErrorObserver)
            data(observer = mockedDataObserver)
        }

        advanceUntilIdle()
        verifyNoMoreInteractions(mockedDataObserver)
        verifyNoMoreInteractions(mockedErrorObserver)
        verifyBlocking(mockedLoadingObserver) { invoke(true) }

        first.setData("first_set")
        advanceUntilIdle()
        verifyBlocking(mockedDataObserver) { invoke("first_set" to null) }
        verifyBlocking(mockedLoadingObserver) { invoke(false) }
        verifyNoMoreInteractions(mockedErrorObserver)
    }

    @Test
    fun whenObserveSingleHideLoading_shouldBeCalledWhenStatusIsLoading() = runTest {
        val mockedObserver: () -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
        liveData.observe(alwaysOnOwner) { hideLoading(single = true, observer = mockedObserver) }

        liveData.setLoading()
        Mockito.verifyNoInteractions(mockedObserver)

        liveData.setError(IllegalStateException())
        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke() }

        liveData.setLoading()
        Mockito.verifyNoMoreInteractions(mockedObserver)

        liveData.setError(IllegalStateException())
        Mockito.verifyNoMoreInteractions(mockedObserver)

        Assert.assertFalse(liveData.hasObservers())
    }

    @Suppress("MaxLineLength")
    @Test
    fun whenObserveLoading_withData_shouldReceiveTrueWhenStatusIsLoading_andFalseOtherwise_andShouldBeCalledOnlyWithData() =
        runTest {
            val mockedObserver: (Boolean) -> Unit = mock()
            val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
            liveData.observe(alwaysOnOwner) { loading(withData = true, observer = mockedObserver) }

            liveData.setLoading()
            Mockito.verifyNoInteractions(mockedObserver)

            liveData.setLoading("data")

            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke(true) }

            liveData.setError(IllegalStateException())
            Mockito.verifyNoMoreInteractions(mockedObserver)

            liveData.setError(IllegalStateException(), "data")
            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke(false) }

            liveData.setLoading()
            Mockito.verifyNoMoreInteractions(mockedObserver)
        }

    @Test
    fun whenObserveShowLoading_withData_shouldBeCalledWhenStatusIsLoadingAndDataIsNotNull() =
        runTest {
            val mockedObserver: () -> Unit = mock()
            val liveData =
                MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
            liveData.observe(alwaysOnOwner) {
                showLoading(
                    withData = true,
                    observer = mockedObserver
                )
            }

            liveData.setLoading()
            Mockito.verifyNoInteractions(mockedObserver)

            liveData.setLoading("data")
            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke() }

            liveData.setError(IllegalStateException())
            Mockito.verifyNoMoreInteractions(mockedObserver)

            liveData.setLoading()
            Mockito.verifyNoMoreInteractions(mockedObserver)

            liveData.setLoading("data")
            advanceUntilIdle()
            verifyBlocking(mockedObserver, times(2)) { invoke() }
        }

    @Test
    fun whenObserveHideLoading_withData_shouldBeCalledWhenStatusIsLoadingAndDataIsNotNull() =
        runTest {
            val mockedObserver: () -> Unit = mock()
            val liveData =
                MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
            liveData.observe(alwaysOnOwner) {
                hideLoading(
                    withData = true,
                    observer = mockedObserver
                )
            }

            liveData.setLoading()
            Mockito.verifyNoInteractions(mockedObserver)

            liveData.setError(IllegalStateException())
            Mockito.verifyNoInteractions(mockedObserver)

            liveData.setError(IllegalStateException(), "data")
            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke() }

            liveData.setLoading()
            Mockito.verifyNoMoreInteractions(mockedObserver)
        }

    @Suppress("MaxLineLength")
    @Test
    fun whenObserveLoading_withoutData_shouldReceiveTrueWhenStatusIsLoading_andFalseOtherwise_andShouldBeCalledOnlyWithNullData() =
        runTest {
            val mockedObserver: (Boolean) -> Unit = mock()
            val liveData =
                MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
            liveData.observe(alwaysOnOwner) { loading(withData = false, observer = mockedObserver) }

            liveData.setLoading("data")
            Mockito.verifyNoInteractions(mockedObserver)

            liveData.setLoading()
            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke(true) }

            liveData.setError(IllegalStateException(), "data")
            Mockito.verifyNoMoreInteractions(mockedObserver)

            liveData.setError(IllegalStateException())
            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke(false) }

            liveData.setLoading()
            advanceUntilIdle()
            verifyBlocking(mockedObserver, times(2)) { invoke(true) }
        }

    @Test
    fun whenObserveShowLoading_withoutData_shouldBeCalledWhenStatusIsLoadingAndDataIsNull() =
        runTest {
            val mockedObserver: () -> Unit = mock()
            val liveData =
                MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
            liveData.observe(alwaysOnOwner) {
                showLoading(
                    withData = false,
                    observer = mockedObserver
                )
            }

            liveData.setLoading("data")
            Mockito.verifyNoInteractions(mockedObserver)

            liveData.setLoading()
            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke() }

            liveData.setError(IllegalStateException())
            Mockito.verifyNoMoreInteractions(mockedObserver)

            liveData.setLoading("data")
            Mockito.verifyNoMoreInteractions(mockedObserver)

            liveData.setLoading()
            advanceUntilIdle()
            verifyBlocking(mockedObserver, times(2)) { invoke() }
        }

    @Test
    fun whenObserveHideLoading_withoutData_shouldBeCalledWhenStatusIsLoadingAndDataIsNull() =
        runTest {
            val mockedObserver: () -> Unit = mock()
            val liveData =
                MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
            liveData.observe(alwaysOnOwner) {
                hideLoading(
                    withData = false,
                    observer = mockedObserver
                )
            }

            liveData.setLoading()
            Mockito.verifyNoInteractions(mockedObserver)

            liveData.setError(IllegalStateException(), "data")
            Mockito.verifyNoInteractions(mockedObserver)

            liveData.setError(IllegalStateException())
            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke() }

            liveData.setLoading()
            Mockito.verifyNoMoreInteractions(mockedObserver)
        }

    // endregion

    // region Error
    @Test
    fun whenObserveError_shouldBeCalledWhenStatusIsError() = runTest {
        val mockedObserver: () -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
        liveData.observe(alwaysOnOwner) { error(observer = mockedObserver) }

        liveData.setLoading()
        Mockito.verifyNoInteractions(mockedObserver)

        liveData.setError(IllegalStateException())
        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke() }

        liveData.setLoading()
        Mockito.verifyNoMoreInteractions(mockedObserver)

        liveData.setError(IllegalStateException())
        advanceUntilIdle()
        verifyBlocking(mockedObserver, times(2)) { invoke() }
    }

    @Test
    fun whenObserveError_withExceptionData_shouldBeCalledWhenStatusIsError() = runTest {
        val mockedObserver: (Throwable) -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
        liveData.observe(alwaysOnOwner) { error(observer = mockedObserver) }

        liveData.setLoading()
        Mockito.verifyNoInteractions(mockedObserver)

        val exception = IllegalStateException()
        liveData.setError(exception)
        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke(exception) }

        liveData.setLoading()
        Mockito.verifyNoMoreInteractions(mockedObserver)

        liveData.value = DataResult(null, null, ERROR)
        Mockito.verifyNoMoreInteractions(mockedObserver)
    }

    @Test
    fun whenObserveError_withErrorTransformer_withExceptionData_shouldBeCalledWhenStatusIsError() =
        runTest {
            val mockedTransformer: (Throwable) -> String = mock()
            val mockedObserver: (String) -> Unit = mock()
            val liveData =
                MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
            liveData.observe(alwaysOnOwner) {
                error(
                    transformer = mockedTransformer,
                    observer = mockedObserver
                )
            }

            liveData.setLoading()
            Mockito.verifyNoInteractions(mockedTransformer)
            Mockito.verifyNoInteractions(mockedObserver)

            val exception = IllegalStateException()
            Mockito.`when`(mockedTransformer.invoke(exception)).thenReturn("")
            liveData.setError(exception)
            advanceUntilIdle()
            verifyBlocking(mockedTransformer) { invoke(exception) }
            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke("") }

            liveData.setLoading()
            Mockito.verifyNoMoreInteractions(mockedTransformer)
            Mockito.verifyNoMoreInteractions(mockedObserver)

            liveData.value = DataResult(null, null, ERROR)
            Mockito.verifyNoMoreInteractions(mockedTransformer)
            Mockito.verifyNoMoreInteractions(mockedObserver)
        }

    @Test
    fun whenObserveSingleError_shouldBeCalledWhenStatusIsError() = runTest {
        val mockedObserver: () -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
        liveData.observe(alwaysOnOwner) { error(single = true, observer = mockedObserver) }

        liveData.setLoading()
        Mockito.verifyNoInteractions(mockedObserver)

        liveData.setError(IllegalStateException())
        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke() }

        liveData.setLoading()
        Mockito.verifyNoMoreInteractions(mockedObserver)

        liveData.setError(IllegalStateException())
        Mockito.verifyNoMoreInteractions(mockedObserver)

        Assert.assertFalse(liveData.hasObservers())
    }

    @Test
    fun whenObserveSingleError_withExceptionData_shouldBeCalledWhenStatusIsError() =
        runTest {
            val mockedObserver: (Throwable) -> Unit = mock()
            val liveData = MutableResponseLiveData<Any>()
                .transformDispatcher(Dispatchers.Main)
            liveData.observe(alwaysOnOwner) { error(single = true, observer = mockedObserver) }

            liveData.setLoading()
            advanceUntilIdle()
            Mockito.verifyNoInteractions(mockedObserver)

            val exception = IllegalStateException()
            liveData.setError(exception)
            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke(exception) }

            liveData.setLoading()
            advanceUntilIdle()
            Mockito.verifyNoMoreInteractions(mockedObserver)

            liveData.setError(exception)
            advanceUntilIdle()
            Mockito.verifyNoMoreInteractions(mockedObserver)

            liveData.value = DataResult(null, null, ERROR)
            advanceUntilIdle()
            Mockito.verifyNoMoreInteractions(mockedObserver)

            Assert.assertFalse(liveData.hasObservers())
        }

    @Test
    fun whenObserveSingleError_withErrorTransformer_withExceptionData_shouldBeCalledWhenStatusIsError() =
        runTest {
            val mockedTransformer: (Throwable) -> String = mock()
            val mockedObserver: (String) -> Unit = mock()
            val liveData =
                MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
            liveData.observe(alwaysOnOwner) {
                error(
                    single = true,
                    transformer = mockedTransformer,
                    observer = mockedObserver
                )
            }

            liveData.setLoading()
            Mockito.verifyNoInteractions(mockedObserver)

            val exception = IllegalStateException()
            Mockito.`when`(mockedTransformer.invoke(exception)).thenReturn("")
            liveData.setError(exception)

            advanceUntilIdle()
            verifyBlocking(mockedTransformer) { invoke(exception) }
            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke("") }

            liveData.setLoading()
            advanceUntilIdle()
            Mockito.verifyNoMoreInteractions(mockedTransformer)
            advanceUntilIdle()
            Mockito.verifyNoMoreInteractions(mockedObserver)

            liveData.setError(exception)
            advanceUntilIdle()
            Mockito.verifyNoMoreInteractions(mockedTransformer)
            advanceUntilIdle()
            Mockito.verifyNoMoreInteractions(mockedObserver)

            liveData.value = DataResult(null, null, ERROR)
            advanceUntilIdle()
            Mockito.verifyNoMoreInteractions(mockedTransformer)
            advanceUntilIdle()
            Mockito.verifyNoMoreInteractions(mockedObserver)

            Assert.assertFalse(liveData.hasObservers())
        }

    @Test
    fun whenObserveError_withoutData_shouldBeCalledWhenStatusIsErrorAndDataIsNull() =
        runTest {
            val mockedObserver: () -> Unit = mock()
            val liveData =
                MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
            liveData.observe(alwaysOnOwner) { error(withData = false, observer = mockedObserver) }

            liveData.setLoading()
            Mockito.verifyNoInteractions(mockedObserver)

            liveData.setError(IllegalStateException())
            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke() }

            liveData.setError(IllegalStateException(), "")
            Mockito.verifyNoMoreInteractions(mockedObserver)

            liveData.setLoading()
            Mockito.verifyNoMoreInteractions(mockedObserver)

            liveData.setError(IllegalStateException())
            advanceUntilIdle()
            verifyBlocking(mockedObserver, times(2)) { invoke() }
        }

    @Test
    fun whenObserveError_withData_shouldBeCalledWhenStatusIsErrorAndDataIsNonNull() =
        runTest {
            val mockedObserver: () -> Unit = mock()
            val liveData =
                MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
            liveData.observe(alwaysOnOwner) { error(withData = true, observer = mockedObserver) }

            liveData.setLoading()
            Mockito.verifyNoInteractions(mockedObserver)

            liveData.setError(IllegalStateException())
            Mockito.verifyNoInteractions(mockedObserver)

            liveData.setError(IllegalStateException(), "")
            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke() }

            liveData.setLoading()
            Mockito.verifyNoMoreInteractions(mockedObserver)

            liveData.setError(IllegalStateException())
            Mockito.verifyNoMoreInteractions(mockedObserver)

            liveData.setError(IllegalStateException(), "")
            advanceUntilIdle()
            verifyBlocking(mockedObserver, times(2)) { invoke() }
        }

    @Test
    fun whenObserveError_withExceptionData_withoutData_shouldBeCalledWhenStatusIsErrorAndDataIsNull() =
        runTest {
            val mockedObserver: (Throwable) -> Unit = mock()
            val liveData =
                MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
            liveData.observe(alwaysOnOwner) { error(withData = false, observer = mockedObserver) }

            liveData.setLoading()
            Mockito.verifyNoInteractions(mockedObserver)

            liveData.setError(IllegalStateException())
            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke(any()) }

            liveData.setError(IllegalStateException(), "")
            Mockito.verifyNoMoreInteractions(mockedObserver)

            liveData.setLoading()
            Mockito.verifyNoMoreInteractions(mockedObserver)

            liveData.setError(IllegalStateException())
            advanceUntilIdle()
            verifyBlocking(mockedObserver, times(2)) { invoke(any()) }
        }

    @Test
    fun whenObserveError_withExceptionData_withData_shouldBeCalledWhenStatusIsErrorAndDataIsNonNull() =
        runTest {
            val mockedObserver: (Throwable) -> Unit = mock()
            val liveData =
                MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
            liveData.observe(alwaysOnOwner) { error(withData = true, observer = mockedObserver) }

            liveData.setLoading()
            Mockito.verifyNoInteractions(mockedObserver)

            liveData.setError(IllegalStateException())
            Mockito.verifyNoInteractions(mockedObserver)

            liveData.setError(IllegalStateException(), "")
            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke(any()) }

            liveData.setLoading()
            Mockito.verifyNoMoreInteractions(mockedObserver)

            liveData.setError(IllegalStateException())
            Mockito.verifyNoMoreInteractions(mockedObserver)

            liveData.setError(IllegalStateException(), "")
            advanceUntilIdle()
            verifyBlocking(mockedObserver, times(2)) { invoke(any()) }
        }
    // endregion

    // region Success
    @Test
    fun whenObserveSuccess_shouldBeCalledWhenStatusIsSuccess() = runTest {
        val mockedObserver: () -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
        liveData.observe(alwaysOnOwner) { success(observer = mockedObserver) }

        liveData.setLoading()
        Mockito.verifyNoInteractions(mockedObserver)

        val data = "data"
        liveData.setData(data)
        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke() }

        liveData.setLoading()
        Mockito.verifyNoMoreInteractions(mockedObserver)
    }

    @Test
    fun whenObserveSingleSuccess_shouldBeCalledWhenStatusIsSuccess() = runTest {
        val mockedObserver: () -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
        liveData.observe(alwaysOnOwner) { success(observer = mockedObserver) }

        liveData.setLoading()
        Mockito.verifyNoInteractions(mockedObserver)

        val data = "data"
        liveData.setData(data)
        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke() }

        liveData.setLoading()
        Mockito.verifyNoMoreInteractions(mockedObserver)

        liveData.setData(data)
        Mockito.verifyNoMoreInteractions(mockedObserver)

        Assert.assertFalse(liveData.hasObservers())
    }

    @Test
    fun whenObserveSuccess_withoutData_shouldBeCalledWhenStatusIsSuccessAndDataIsNull() =
        runTest {
            val mockedObserver: () -> Unit = mock()
            val liveData =
                MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
            liveData.observe(alwaysOnOwner) {
                success(withData = false, observer = mockedObserver)
            }

            liveData.setLoading()
            Mockito.verifyNoInteractions(mockedObserver)

            val data = "data"
            liveData.setData(data)
            Mockito.verifyNoInteractions(mockedObserver)

            liveData.setSuccess()
            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke() }

            liveData.setData(data)
            Mockito.verifyNoMoreInteractions(mockedObserver)
        }

    @Test
    fun whenObserveSuccess_withData_shouldBeCalledWhenStatusIsSuccessAndDataIsNonNull() =
        runTest {
            val mockedObserver: () -> Unit = mock()
            val liveData =
                MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
            liveData.observe(alwaysOnOwner) {
                success(withData = true, observer = mockedObserver)
            }

            liveData.setLoading()
            Mockito.verifyNoInteractions(mockedObserver)

            liveData.setSuccess()
            Mockito.verifyNoInteractions(mockedObserver)

            val data = "data"
            liveData.setData(data)
            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke() }

            liveData.setSuccess()
            Mockito.verifyNoMoreInteractions(mockedObserver)
        }
    // endregion

    //region Data
    @Test
    fun whenObserveData_shouldBeCalledWhenDataIsNotNull() = runTest {
        val mockedObserver: (Any) -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
        liveData.observe(alwaysOnOwner) { data(observer = mockedObserver) }

        liveData.setLoading()
        Mockito.verifyNoInteractions(mockedObserver)

        val data = "data"
        liveData.setData(data)
        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke(data) }

        liveData.setLoading()
        Mockito.verifyNoMoreInteractions(mockedObserver)

        liveData.value = DataResult(null, null, SUCCESS)
        advanceUntilIdle()
        Mockito.verifyNoMoreInteractions(mockedObserver)

        liveData.setLoading(data)
        advanceUntilIdle()
        verifyBlocking(mockedObserver, times(2)) { invoke(data) }

        liveData.setError(IllegalStateException())
        Mockito.verifyNoMoreInteractions(mockedObserver)

        liveData.setError(IllegalStateException(), data)
        advanceUntilIdle()
        verifyBlocking(mockedObserver, times(3)) { invoke(data) }

        liveData.setLoading(null)
        Mockito.verifyNoMoreInteractions(mockedObserver)

        liveData.setError(IllegalStateException(), null)
        Mockito.verifyNoMoreInteractions(mockedObserver)
    }

    @Test
    fun whenObserveData_withTransformer_shouldBeCalledWhenStatusIsSuccess() = runTest {
        val mockedObserver: (Int) -> Unit = mock()
        val mockedTransformer: (String) -> Int = mock()
        val liveData =
            MutableResponseLiveData<String>().transformDispatcher(Dispatchers.Main)
        liveData.observe(alwaysOnOwner) {
            data(
                transformer = mockedTransformer,
                observer = mockedObserver
            )
        }

        liveData.setLoading()
        Mockito.verifyNoInteractions(mockedObserver)

        val data = "data"
        Mockito.`when`(mockedTransformer.invoke(data)).thenReturn(0)
        liveData.setData(data)
        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke(0) }
        advanceUntilIdle()
        verifyBlocking(mockedTransformer) { invoke(data) }

        liveData.setLoading()
        Mockito.verifyNoMoreInteractions(mockedObserver)
        Mockito.verifyNoMoreInteractions(mockedTransformer)

        liveData.value = DataResult(null, null, SUCCESS)
        Mockito.verifyNoMoreInteractions(mockedObserver)
        Mockito.verifyNoMoreInteractions(mockedTransformer)

        liveData.setLoading(data)
        advanceUntilIdle()
        verifyBlocking(mockedObserver, times(2)) { invoke(0) }
        advanceUntilIdle()
        verifyBlocking(mockedTransformer, times(2)) { invoke(data) }

        liveData.setError(IllegalStateException())
        Mockito.verifyNoMoreInteractions(mockedObserver)
        Mockito.verifyNoMoreInteractions(mockedTransformer)

        liveData.setError(IllegalStateException(), data)
        advanceUntilIdle()
        verifyBlocking(mockedObserver, times(3)) { invoke(0) }
        advanceUntilIdle()
        verifyBlocking(mockedTransformer, times(3)) { invoke(data) }

        liveData.setLoading(null)
        Mockito.verifyNoMoreInteractions(mockedObserver)
        Mockito.verifyNoMoreInteractions(mockedTransformer)

        liveData.setError(IllegalStateException(), null)
        Mockito.verifyNoMoreInteractions(mockedObserver)
        Mockito.verifyNoMoreInteractions(mockedTransformer)
    }

    @Test
    fun whenObserveSingleData_withTransformer_shouldBeCalledWhenStatusIsSuccess() =
        runTest {
            val mockedObserver: (Any) -> Unit = mock()
            val liveData =
                MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
            liveData.observe(alwaysOnOwner) { data(single = true, observer = mockedObserver) }

            liveData.setLoading()
            Mockito.verifyNoInteractions(mockedObserver)

            val data = "data"
            liveData.setData(data)
            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke(data) }

            liveData.setLoading()
            Mockito.verifyNoMoreInteractions(mockedObserver)

            liveData.setData(data)
            Mockito.verifyNoMoreInteractions(mockedObserver)

            liveData.value = DataResult(null, null, SUCCESS)
            Mockito.verifyNoMoreInteractions(mockedObserver)

            Assert.assertFalse(liveData.hasObservers())
        }

    @Test
    fun whenObserveSingleData_shouldBeCalledWhenStatusIsSuccess() = runTest {
        val mockedObserver: (Int) -> Unit = mock()
        val mockedTransformer: (String) -> Int = mock()
        val liveData =
            MutableResponseLiveData<String>().transformDispatcher(Dispatchers.Main)
        liveData.observe(alwaysOnOwner) {
            data(
                single = true,
                transformer = mockedTransformer,
                observer = mockedObserver
            )
        }

        liveData.setLoading()
        Mockito.verifyNoInteractions(mockedObserver)

        val data = "data"
        Mockito.`when`(mockedTransformer.invoke(data)).thenReturn(0)
        liveData.setData(data)
        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke(0) }
        advanceUntilIdle()
        verifyBlocking(mockedTransformer) { invoke(data) }

        liveData.setLoading()
        advanceUntilIdle()
        Mockito.verifyNoMoreInteractions(mockedObserver)
        advanceUntilIdle()
        Mockito.verifyNoMoreInteractions(mockedTransformer)

        liveData.setData(data)
        advanceUntilIdle()
        Mockito.verifyNoMoreInteractions(mockedObserver)
        advanceUntilIdle()
        Mockito.verifyNoMoreInteractions(mockedTransformer)

        liveData.value = DataResult(null, null, SUCCESS)
        advanceUntilIdle()
        Mockito.verifyNoMoreInteractions(mockedObserver)
        advanceUntilIdle()
        Mockito.verifyNoMoreInteractions(mockedTransformer)

        Assert.assertFalse(liveData.hasObservers())
    }
    //endregion

    // region Result
    @Test
    fun whenObserveResult_shouldBeCalledWhenResultIsPosted() = runTest {
        val mockedObserver: (DataResult<Any>) -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
        liveData.observe(alwaysOnOwner) {
            result(observer = mockedObserver)
        }

        val result = DataResult<Any>(null, null, SUCCESS)
        liveData.value = result
        advanceUntilIdle()
        advanceUntilIdle()
        verifyBlocking(mockedObserver, times(1)) { invoke(result) }

        val result2 = DataResult<Any>(null, null, ERROR)
        liveData.value = result2
        advanceUntilIdle()
        advanceUntilIdle()
        verifyBlocking(mockedObserver, times(1)) { invoke(result2) }
    }

    @Test
    fun whenObserveResult_withTransformer_shouldBeCalledWhenResultIsPostedWithTheTransformedResult() =
        runTest {
            val mockedObserver: (Int) -> Unit = mock()
            val mockedTransformer: (DataResult<Any>) -> Int = mock()
            val liveData =
                MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
            liveData.observe(alwaysOnOwner) {
                result(
                    observer = mockedObserver,
                    transformer = mockedTransformer
                )
            }

            val result = DataResult<Any>(null, null, SUCCESS)
            Mockito.`when`(mockedTransformer.invoke(result)).thenReturn(0)
            liveData.value = result
            advanceUntilIdle()
            verifyBlocking(mockedTransformer) { invoke(result) }
            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke(0) }

            val result2 = DataResult<Any>(null, null, ERROR)
            Mockito.`when`(mockedTransformer.invoke(result2)).thenReturn(1)
            liveData.value = result2
            advanceUntilIdle()
            verifyBlocking(mockedTransformer) { invoke(result2) }
            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke(1) }
        }

    @Test
    fun whenObserveSingleResult_shouldBeCalledWhenResultIsPosted() = runTest {
        val mockedObserver: (DataResult<Any>) -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
        liveData.observe(alwaysOnOwner) { result(single = true, observer = mockedObserver) }

        val result = DataResult<Any>(null, null, SUCCESS)
        liveData.value = result
        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke(result) }

        val result2 = DataResult<Any>(null, null, ERROR)
        liveData.value = result2
        Mockito.verifyNoMoreInteractions(mockedObserver)

        Assert.assertFalse(liveData.hasObservers())
    }

    @Test
    fun whenObserveSingleResult_withTransformer_shouldBeCalledWhenResultIsPostedWithTheTransformedResult() =
        runTest {
            val mockedObserver: (Int) -> Unit = mock()
            val mockedTransformer: (DataResult<Any>) -> Int = mock()
            val liveData =
                MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
            liveData.observe(alwaysOnOwner) {
                result(
                    single = true,
                    observer = mockedObserver,
                    transformer = mockedTransformer
                )
            }

            val result = DataResult<Any>(null, null, SUCCESS)
            Mockito.`when`(mockedTransformer.invoke(result)).thenReturn(0)
            liveData.value = result
            advanceUntilIdle()
            verifyBlocking(mockedTransformer) { invoke(result) }
            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke(0) }

            val result2 = DataResult<Any>(null, null, ERROR)
            Mockito.`when`(mockedTransformer.invoke(result2)).thenReturn(1)
            liveData.value = result2
            Mockito.verifyNoMoreInteractions(mockedTransformer)
            Mockito.verifyNoMoreInteractions(mockedObserver)

            Assert.assertFalse(liveData.hasObservers())
        }
    // endregion

    // region Status
    @Test
    fun whenObserveStatus_shouldBeCalledWhenResultIsPosted() = runTest {
        val mockedObserver: (DataResultStatus) -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
        liveData.observe(alwaysOnOwner) { status(observer = mockedObserver) }

        val result = DataResult<Any>(null, null, SUCCESS)
        liveData.value = result
        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke(SUCCESS) }

        val result2 = DataResult<Any>(null, null, ERROR)
        liveData.value = result2
        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke(ERROR) }
    }

    @Test
    fun whenObserveStatus_withTransformer_shouldBeCalledWhenResultIsPostedWithTheTransformedStatus() =
        runTest {
            val mockedObserver: (Int) -> Unit = mock()
            val mockedTransformer: (DataResultStatus) -> Int = mock()
            val liveData =
                MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
            liveData.observe(alwaysOnOwner) {
                status(
                    observer = mockedObserver,
                    transformer = mockedTransformer
                )
            }

            val result = DataResult<Any>(null, null, SUCCESS)
            Mockito.`when`(mockedTransformer.invoke(SUCCESS)).thenReturn(0)
            liveData.value = result

            advanceUntilIdle()
            verifyBlocking(mockedTransformer) { invoke(SUCCESS) }
            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke(0) }

            val result2 = DataResult<Any>(null, null, ERROR)
            Mockito.`when`(mockedTransformer.invoke(ERROR)).thenReturn(1)
            liveData.value = result2
            advanceUntilIdle()
            verifyBlocking(mockedTransformer) { invoke(ERROR) }
            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke(1) }
        }

    @Test
    fun whenObserveSingleStatus_shouldBeCalledWhenResultIsPosted() = runTest {
        val mockedObserver: (DataResultStatus) -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
        liveData.observe(alwaysOnOwner) { status(single = true, observer = mockedObserver) }

        val result = DataResult<Any>(null, null, SUCCESS)
        liveData.value = result
        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke(SUCCESS) }

        val result2 = DataResult<Any>(null, null, ERROR)
        liveData.value = result2
        Mockito.verifyNoMoreInteractions(mockedObserver)
        Assert.assertFalse(liveData.hasObservers())
    }

    @Test
    fun whenObserveSingleStatus_withTransformer_shouldBeCalledWhenResultIsPostedWithTheTransformedStatus() =
        runTest {
            val mockedObserver: (Int) -> Unit = mock()
            val mockedTransformer: (DataResultStatus) -> Int = mock()
            val liveData =
                MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
            liveData.observe(alwaysOnOwner) {
                status(
                    single = true,
                    observer = mockedObserver,
                    transformer = mockedTransformer
                )
            }

            val result = DataResult<Any>(null, null, SUCCESS)
            Mockito.`when`(mockedTransformer.invoke(SUCCESS)).thenReturn(0)
            liveData.value = result
            advanceUntilIdle()
            verifyBlocking(mockedTransformer) { invoke(SUCCESS) }
            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke(0) }

            val result2 = DataResult<Any>(null, null, ERROR)
            Mockito.`when`(mockedTransformer.invoke(ERROR)).thenReturn(1)
            liveData.value = result2
            Mockito.verifyNoMoreInteractions(mockedTransformer)
            Mockito.verifyNoMoreInteractions(mockedObserver)

            Assert.assertFalse(liveData.hasObservers())
        }
    // endregion

    @Test
    fun whenMap_withTransformAsync_shouldTransformDataStartingThreads() = runTest {
        val mockedTransformer: (String) -> String = mock()
        val mockedObserver: (String) -> Unit = mock()

        val liveData =
            MutableResponseLiveData<String>().transformDispatcher(Dispatchers.Main)
        val mappedLiveData = liveData.map(mockedTransformer)

        val data = "data"
        Mockito.`when`(mockedTransformer.invoke(data)).thenReturn(data)

        liveData.setData(data)
        mappedLiveData.observe(alwaysOnOwner) { data(observer = mockedObserver) }

        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke(data) }
        advanceUntilIdle()
        verifyBlocking(mockedTransformer) { invoke(data) }
    }

    @Test
    fun whenMap_withoutTransformAsync_shouldTransformDataWithoutStartingThreads() =
        runTest {
            val mockedTransformer: (String) -> String = mock()
            val mockedObserver: (String) -> Unit = mock()

            val liveData =
                MutableResponseLiveData<String>().transformDispatcher(Dispatchers.Main)
            val mappedLiveData = liveData.map(mockedTransformer)

            val data = "data"
            Mockito.`when`(mockedTransformer.invoke(data)).thenReturn(data)

            liveData.setData(data)
            mappedLiveData.observe(alwaysOnOwner) { data(observer = mockedObserver) }

            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke(data) }
            advanceUntilIdle()
            verifyBlocking(mockedTransformer) { invoke(data) }
        }

    @Test
    fun whenMapError_withTransformAsync_shouldTransformErrorStartingThreads() = runTest {
        val mockedTransformer: (Throwable) -> Throwable = mock()
        val mockedObserver: (Throwable) -> Unit = mock()

        val liveData =
            MutableResponseLiveData<String>().transformDispatcher(Dispatchers.Main)
        val mappedLiveData = liveData.mapError(mockedTransformer)

        val error = IllegalStateException("error")
        Mockito.`when`(mockedTransformer.invoke(error)).thenReturn(error)

        liveData.setError(error)
        mappedLiveData.observe(alwaysOnOwner) { error(observer = mockedObserver) }

        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke(error) }
        advanceUntilIdle()
        verifyBlocking(mockedTransformer) { invoke(error) }
    }

    @Test
    fun whenMapError_withoutTransformAsync_shouldTransformErrorWithoutStartingThreads() =
        runTest {
            val mockedTransformer: (Throwable) -> Throwable = mock()
            val mockedObserver: (Throwable) -> Unit = mock()

            val liveData =
                MutableResponseLiveData<String>().transformDispatcher(Dispatchers.Main)
            val mappedLiveData = liveData.mapError(mockedTransformer)

            val error = IllegalStateException("error")
            Mockito.`when`(mockedTransformer.invoke(error)).thenReturn(error)

            liveData.setError(error)
            mappedLiveData.observe(alwaysOnOwner) { error(observer = mockedObserver) }

            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke(error) }
            advanceUntilIdle()
            verifyBlocking(mockedTransformer) { invoke(error) }
        }

    @Test
    fun whenOnNext_withTransformAsync_shouldDeliverDataBeforeCallObserverStartingThreads() =
        runTest {
            val mockedOnNext: (String) -> Unit = mock()
            val mockedObserver: (String) -> Unit = mock()

            val liveData =
                MutableResponseLiveData<String>().transformDispatcher(Dispatchers.Main)
            val onNextLiveData = liveData.onNext(mockedOnNext)

            val data = "data"

            liveData.setData(data)
            onNextLiveData.observe(alwaysOnOwner) { data(observer = mockedObserver) }

            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke(data) }
            advanceUntilIdle()
            verifyBlocking(mockedOnNext) { invoke(data) }
        }

    @Test
    fun whenOnNext_withoutTransformAsync_shouldDeliverDataBeforeCallObserverWithoutStartingThreads() =
        runTest {
            val mockedOnNext: (String) -> Unit = mock()
            val mockedObserver: (String) -> Unit = mock()

            val liveData =
                MutableResponseLiveData<String>().transformDispatcher(Dispatchers.Main)
            val onNextLiveData = liveData.onNext(mockedOnNext)

            val data = "data"

            liveData.setData(data)
            onNextLiveData.observe(alwaysOnOwner) { data(observer = mockedObserver) }

            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke(data) }
            advanceUntilIdle()
            verifyBlocking(mockedOnNext) { invoke(data) }
        }

    @Test
    fun whenOnError_withTransformAsync_shouldDeliverErrorBeforeCallObserverStartingThreads() =
        runTest {
            val mockedOnError: (Throwable) -> Unit = mock()
            val mockedObserver: (Throwable) -> Unit = mock()

            val liveData =
                MutableResponseLiveData<String>().transformDispatcher(Dispatchers.Main)
            val onErrorLiveData = liveData.onError(mockedOnError)

            val error = IllegalStateException("error")

            liveData.setError(error)
            onErrorLiveData.observe(alwaysOnOwner) { error(observer = mockedObserver) }

            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke(error) }
            advanceUntilIdle()
            verifyBlocking(mockedOnError) { invoke(error) }
        }

    @Test
    fun whenOnError_withoutTransformAsync_shouldDeliverErrorBeforeCallObserverWithoutStartingThreads() =
        runTest {
            val mockedOnError: (Throwable) -> Unit = mock()
            val mockedObserver: (Throwable) -> Unit = mock()

            val liveData =
                MutableResponseLiveData<String>().transformDispatcher(Dispatchers.Main)
            val onErrorLiveData = liveData.onError(mockedOnError)

            val error = IllegalStateException("error")

            liveData.setError(error)
            onErrorLiveData.observe(alwaysOnOwner) { error(observer = mockedObserver) }

            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke(error) }
            advanceUntilIdle()
            verifyBlocking(mockedOnError) { invoke(error) }
        }

    @Test
    fun whenOnErrorReturn_withTransformAsync_shouldDeliverTransformedDataBeforeCallObserverStartingThreads() =
        runTest {
            val mockedOnErrorReturn: (Throwable) -> String = mock()
            val mockedObserver: (Throwable) -> Unit = mock()
            val mockedDataObserver: (String) -> Unit = mock()

            val liveData =
                MutableResponseLiveData<String>().transformDispatcher(Dispatchers.Main)
            val onErrorLiveData = liveData.onErrorReturn(mockedOnErrorReturn)

            val error = IllegalStateException("error")
            Mockito.`when`(mockedOnErrorReturn.invoke(error)).thenReturn("error")

            liveData.setError(error)
            onErrorLiveData.observe(alwaysOnOwner) { error(observer = mockedObserver) }
            onErrorLiveData.observe(alwaysOnOwner) { data(observer = mockedDataObserver) }

            Mockito.verifyNoInteractions(mockedObserver)
            advanceUntilIdle()
            verifyBlocking(mockedOnErrorReturn) { invoke(error) }
            advanceUntilIdle()
            verifyBlocking(mockedDataObserver) { invoke("error") }
        }

    @Test
    fun whenOnErrorReturn_withoutTransformAsync_shouldDeliverTransformedDataBeforeCallObserverWithoutStartingThreads() =
        runTest {
            val mockedOnErrorReturn: (Throwable) -> String = mock()
            val mockedObserver: (Throwable) -> Unit = mock()
            val mockedDataObserver: (String) -> Unit = mock()

            val liveData =
                MutableResponseLiveData<String>().transformDispatcher(Dispatchers.Main)
            val onErrorLiveData = liveData.onErrorReturn(mockedOnErrorReturn)

            val error = IllegalStateException("error")
            Mockito.`when`(mockedOnErrorReturn.invoke(error)).thenReturn("error")

            liveData.setError(error)
            onErrorLiveData.observe(alwaysOnOwner) { error(observer = mockedObserver) }
            onErrorLiveData.observe(alwaysOnOwner) { data(observer = mockedDataObserver) }

            Mockito.verifyNoInteractions(mockedObserver)
            advanceUntilIdle()
            verifyBlocking(mockedOnErrorReturn) { invoke(error) }
            advanceUntilIdle()
            verifyBlocking(mockedDataObserver) { invoke("error") }
        }

    @Test
    fun whenOnErrorReturn_whenReceiveErrorWithData_shouldNOTcallOnErrorReturnBlockAndDeliverTheOriginalData() =
        runTest {
            val mockedOnErrorReturn: (Throwable) -> String = mock()
            val mockedObserver: (Throwable) -> Unit = mock()
            val mockedDataObserver: (String) -> Unit = mock()

            val liveData =
                MutableResponseLiveData<String>().transformDispatcher(Dispatchers.Main)
            val onErrorLiveData = liveData.onErrorReturn(mockedOnErrorReturn)

            val error = IllegalStateException("error")
            liveData.setError(error, "data")

            onErrorLiveData.observe(alwaysOnOwner) { error(observer = mockedObserver) }
            onErrorLiveData.observe(alwaysOnOwner) { data(observer = mockedDataObserver) }

            Mockito.verifyNoInteractions(mockedObserver)
            Mockito.verifyNoInteractions(mockedOnErrorReturn)
            advanceUntilIdle()
            verifyBlocking(mockedDataObserver, times(1)) { invoke("data") }
        }

    @Test
    fun whenTransform_withoutAsync_shouldDeliverTransformedDataBeforeCallObserverWithoutStartingThreads() =
        runTest {
            val mockedTransformation: (DataResult<String>) -> DataResult<Int> = mock()
            val mockedDataObserver: (Int) -> Unit = mock()

            val liveData =
                MutableResponseLiveData<String>().transformDispatcher(Dispatchers.Main)
            val transformedLiveData = liveData.transform(mockedTransformation)
            transformedLiveData.observe(alwaysOnOwner) { data(observer = mockedDataObserver) }

            val data = DataResult("data", null, SUCCESS)
            val result = DataResult(0, null, SUCCESS)
            Mockito.`when`(mockedTransformation.invoke(data)).thenReturn(result)
            liveData.setData("data")

            advanceUntilIdle()
            verifyBlocking(mockedTransformation, times(1)) { invoke(data) }
            advanceUntilIdle()
            verifyBlocking(mockedDataObserver, times(1)) { invoke(0) }
        }

    @Test
    fun whenTransform_withAsync_shouldDeliverTransformedDataBeforeCallObserverWithoutStartingThreads() =
        runTest {
            val mockedTransformation: (DataResult<String>) -> DataResult<Int> = mock()
            val mockedDataObserver: (Int) -> Unit = mock()

            val liveData =
                MutableResponseLiveData<String>().transformDispatcher(Dispatchers.Main)
            val transformedLiveData = liveData.transform(mockedTransformation)
            transformedLiveData.observe(alwaysOnOwner) { data(observer = mockedDataObserver) }

            val data = DataResult("data", null, SUCCESS)
            val result = DataResult(0, null, SUCCESS)
            Mockito.`when`(mockedTransformation.invoke(data)).thenReturn(result)
            liveData.setData("data")

            advanceUntilIdle()
            verifyBlocking(mockedTransformation, times(1)) { invoke(data) }
            advanceUntilIdle()
            verifyBlocking(mockedDataObserver, times(1)) { invoke(0) }
        }

    @Test
    fun whenInitialize_withoutValue_shouldReturnAnInstanceWithEmptyValue() = runTest {
        val liveData = ResponseLiveData<Any>()
        Assert.assertNull(liveData.data)
        Assert.assertNull(liveData.status)
        Assert.assertNull(liveData.error)
    }

    @Test
    fun whenInitialize_withValue_shouldReturnAnInstanceWithADefaultValue() = runTest {
        val liveData = ResponseLiveData(
            DataResult("value", null, SUCCESS)
        )
        Assert.assertTrue(liveData.data == "value")
        Assert.assertTrue(liveData.status == SUCCESS)
        Assert.assertNull(liveData.error)
    }
}
