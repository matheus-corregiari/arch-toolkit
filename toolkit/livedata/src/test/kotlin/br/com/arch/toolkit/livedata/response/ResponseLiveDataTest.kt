package br.com.arch.toolkit.livedata.response

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.LiveData
import br.com.arch.toolkit.common.DataResult
import br.com.arch.toolkit.common.DataResultStatus
import br.com.arch.toolkit.livedata.extention.mutableResponseLiveDataOf
import br.com.arch.toolkit.livedata.extention.responseLiveDataOf
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verifyBlocking
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
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

@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("LargeClass")
class ResponseLiveDataTest {

    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private var owner = object : LifecycleOwner {
        private val registry = LifecycleRegistry(this)
        override fun getLifecycle(): Lifecycle {
            registry.currentState = Lifecycle.State.RESUMED
            return registry
        }
    }

    init {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun validateNullValueScenarios() = runTest {
        val mockedObserver: (Any) -> Unit = mock()
        val errorObserver: (Throwable) -> Unit = mock()
        val emptyObserver: () -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
        liveData.observeSingleShowLoading(owner, emptyObserver)
        liveData.observeSingleHideLoading(owner, emptyObserver)
        liveData.observeSingleError(owner, emptyObserver)
        liveData.observeSingleData(owner, mockedObserver)
        liveData.observeSingleSuccess(owner, emptyObserver)

        val nullObject: DataResult<Any>? = null

        LiveData::class.java.declaredMethods.find { it.name == "setValue" }?.let {
            it.isAccessible = true
            it.invoke(liveData, nullObject)
        }

        Mockito.verifyNoInteractions(mockedObserver)
        Mockito.verifyNoInteractions(errorObserver)
        Mockito.verifyNoInteractions(emptyObserver)

        Assert.assertNull(liveData.value)
        Assert.assertNull(liveData.data)
        Assert.assertNull(liveData.status)
        Assert.assertNull(liveData.error)
    }

    // region Loading
    @Test
    fun whenObserveLoading_shouldReceiveTrueWhenStatusIsLoading_andFalseOtherwise() = runTest {
        val mockedObserver: (Boolean) -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
        liveData.observeLoading(owner, mockedObserver)

        liveData.setLoading()
        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke(true) }

        liveData.setError(IllegalStateException())
        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke(false) }

        liveData.setLoading()
        advanceUntilIdle()
        verifyBlocking(mockedObserver, times(2)) { invoke(true) }
    }

    @Test
    fun whenObserveShowLoading_shouldBeCalledWhenStatusIsLoading() = runTest {
        val mockedObserver: () -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
        liveData.observeShowLoading(owner, mockedObserver)

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
        liveData.observeHideLoading(owner, mockedObserver)

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
            liveData.observeSingleLoading(owner, mockedObserver)

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
        liveData.observeSingleShowLoading(owner, mockedObserver)

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
        Assert.assertTrue(combination.status == DataResultStatus.LOADING)
    }

    @Test
    fun validateMergeSingleSecondValue() = runTest {
        val first = responseLiveDataOf("first")
        val second = MutableResponseLiveData<String>()
        val combination = first.mergeWith(second)

        Assert.assertNull(combination.data)
        Assert.assertTrue(combination.status == DataResultStatus.LOADING)
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
        combination.observe(owner) {
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
        combination.observe(owner) {
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
        secondCombination.observe(owner) {
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
        combination.observe(owner) {
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
            invoke(mapOf("first" to "first_set", "second" to "second_set", "third" to "third_set"))
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
        combination.observe(owner) {
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
        combination.observe(owner) {
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
            invoke(mapOf("first" to "first_set", "second" to "second_set", "third" to "third_set"))
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
        combination.observe(owner) {
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
        combination.observe(owner) {
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
        combination.observe(owner) {
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
        combination.observe(owner) {
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
        liveData.observeSingleHideLoading(owner, mockedObserver)

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
            liveData.observe(owner) { loading(withData = true, observer = mockedObserver) }

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
            val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
            liveData.observe(owner) { showLoading(withData = true, observer = mockedObserver) }

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
            val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
            liveData.observe(owner) { hideLoading(withData = true, observer = mockedObserver) }

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
            val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
            liveData.observe(owner) { loading(withData = false, observer = mockedObserver) }

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
            val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
            liveData.observe(owner) { showLoading(withData = false, observer = mockedObserver) }

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
            val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
            liveData.observe(owner) { hideLoading(withData = false, observer = mockedObserver) }

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
        liveData.observeError(owner, mockedObserver)

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
        liveData.observeError(owner, mockedObserver)

        liveData.setLoading()
        Mockito.verifyNoInteractions(mockedObserver)

        val exception = IllegalStateException()
        liveData.setError(exception)
        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke(exception) }

        liveData.setLoading()
        Mockito.verifyNoMoreInteractions(mockedObserver)

        liveData.value = DataResult(null, null, DataResultStatus.ERROR)
        Mockito.verifyNoMoreInteractions(mockedObserver)
    }

    @Test
    fun whenObserveError_withErrorTransformer_withExceptionData_shouldBeCalledWhenStatusIsError() =
        runTest {
            val mockedTransformer: (Throwable) -> String = mock()
            val mockedObserver: (String) -> Unit = mock()
            val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
            liveData.observeError(owner, mockedTransformer, mockedObserver)

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

            liveData.value = DataResult(null, null, DataResultStatus.ERROR)
            Mockito.verifyNoMoreInteractions(mockedTransformer)
            Mockito.verifyNoMoreInteractions(mockedObserver)
        }

    @Test
    fun whenObserveSingleError_shouldBeCalledWhenStatusIsError() = runTest {
        val mockedObserver: () -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
        liveData.observeSingleError(owner, mockedObserver)

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
    fun whenObserveSingleError_withExceptionData_shouldBeCalledWhenStatusIsError() = runTest {
        val mockedObserver: (Throwable) -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>()
            .transformDispatcher(Dispatchers.Main)
        liveData.observeSingleError(owner, mockedObserver)

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

        liveData.value = DataResult(null, null, DataResultStatus.ERROR)
        advanceUntilIdle()
        Mockito.verifyNoMoreInteractions(mockedObserver)

        Assert.assertFalse(liveData.hasObservers())
    }

    @Test
    fun whenObserveSingleError_withErrorTransformer_withExceptionData_shouldBeCalledWhenStatusIsError() =
        runTest {
            val mockedTransformer: (Throwable) -> String = mock()
            val mockedObserver: (String) -> Unit = mock()
            val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
            liveData.observeSingleError(owner, mockedTransformer, mockedObserver)

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

            liveData.value = DataResult(null, null, DataResultStatus.ERROR)
            advanceUntilIdle()
            Mockito.verifyNoMoreInteractions(mockedTransformer)
            advanceUntilIdle()
            Mockito.verifyNoMoreInteractions(mockedObserver)

            Assert.assertFalse(liveData.hasObservers())
        }

    @Test
    fun whenObserveError_withoutData_shouldBeCalledWhenStatusIsErrorAndDataIsNull() = runTest {
        val mockedObserver: () -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
        liveData.observe(owner) { error(withData = false, observer = mockedObserver) }

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
    fun whenObserveError_withData_shouldBeCalledWhenStatusIsErrorAndDataIsNonNull() = runTest {
        val mockedObserver: () -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
        liveData.observe(owner) { error(withData = true, observer = mockedObserver) }

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
            val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
            liveData.observe(owner) { error(withData = false, observer = mockedObserver) }

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
            val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
            liveData.observe(owner) { error(withData = true, observer = mockedObserver) }

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
        liveData.observeSuccess(owner, mockedObserver)

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
        liveData.observeSingleSuccess(owner, mockedObserver)

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
    fun whenObserveSuccess_withoutData_shouldBeCalledWhenStatusIsSuccessAndDataIsNull() = runTest {
        val mockedObserver: () -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
        liveData.observe(owner) {
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
    fun whenObserveSuccess_withData_shouldBeCalledWhenStatusIsSuccessAndDataIsNonNull() = runTest {
        val mockedObserver: () -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
        liveData.observe(owner) {
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
        liveData.observeData(owner, mockedObserver)

        liveData.setLoading()
        Mockito.verifyNoInteractions(mockedObserver)

        val data = "data"
        liveData.setData(data)
        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke(data) }

        liveData.setLoading()
        Mockito.verifyNoMoreInteractions(mockedObserver)

        liveData.value = DataResult(null, null, DataResultStatus.SUCCESS)
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
        val liveData = MutableResponseLiveData<String>().transformDispatcher(Dispatchers.Main)
        liveData.observeData(owner, mockedTransformer, mockedObserver)

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

        liveData.value = DataResult(null, null, DataResultStatus.SUCCESS)
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
    fun whenObserveSingleData_withTransformer_shouldBeCalledWhenStatusIsSuccess() = runTest {
        val mockedObserver: (Any) -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
        liveData.observeSingleData(owner, mockedObserver)

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

        liveData.value = DataResult(null, null, DataResultStatus.SUCCESS)
        Mockito.verifyNoMoreInteractions(mockedObserver)

        Assert.assertFalse(liveData.hasObservers())
    }

    @Test
    fun whenObserveSingleData_shouldBeCalledWhenStatusIsSuccess() = runTest {
        val mockedObserver: (Int) -> Unit = mock()
        val mockedTransformer: (String) -> Int = mock()
        val liveData = MutableResponseLiveData<String>().transformDispatcher(Dispatchers.Main)
        liveData.observeSingleData(owner, mockedTransformer, mockedObserver)

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

        liveData.value = DataResult(null, null, DataResultStatus.SUCCESS)
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
        liveData.observe(owner) {
            result(observer = mockedObserver)
        }

        val result = DataResult<Any>(null, null, DataResultStatus.SUCCESS)
        liveData.value = result
        advanceUntilIdle()
        advanceUntilIdle()
        verifyBlocking(mockedObserver, times(1)) { invoke(result) }

        val result2 = DataResult<Any>(null, null, DataResultStatus.ERROR)
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
            val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
            liveData.observe(owner) {
                result(
                    observer = mockedObserver,
                    transformer = mockedTransformer
                )
            }

            val result = DataResult<Any>(null, null, DataResultStatus.SUCCESS)
            Mockito.`when`(mockedTransformer.invoke(result)).thenReturn(0)
            liveData.value = result
            advanceUntilIdle()
            verifyBlocking(mockedTransformer) { invoke(result) }
            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke(0) }

            val result2 = DataResult<Any>(null, null, DataResultStatus.ERROR)
            Mockito.`when`(mockedTransformer.invoke(result2)).thenReturn(1)
            liveData.value = result2
            advanceUntilIdle()
            verifyBlocking(mockedTransformer) { invoke(result2) }
            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke(1) }
        }

    @Test
    fun whenObserveResult_shouldBeCalledWhenResultIsPosted_withoutArguments() = runTest {
        val mockedObserver: () -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
        liveData.observe(owner) { result(observer = mockedObserver) }

        val result = DataResult<Any>(null, null, DataResultStatus.SUCCESS)
        liveData.value = result
        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke() }

        val result2 = DataResult<Any>(null, null, DataResultStatus.ERROR)
        liveData.value = result2
        advanceUntilIdle()
        verifyBlocking(mockedObserver, times(2)) { invoke() }
    }

    @Test
    fun whenObserveSingleResult_shouldBeCalledWhenResultIsPosted() = runTest {
        val mockedObserver: (DataResult<Any>) -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
        liveData.observe(owner) { result(single = true, observer = mockedObserver) }

        val result = DataResult<Any>(null, null, DataResultStatus.SUCCESS)
        liveData.value = result
        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke(result) }

        val result2 = DataResult<Any>(null, null, DataResultStatus.ERROR)
        liveData.value = result2
        Mockito.verifyNoMoreInteractions(mockedObserver)

        Assert.assertFalse(liveData.hasObservers())
    }

    @Test
    fun whenObserveSingleResult_withTransformer_shouldBeCalledWhenResultIsPostedWithTheTransformedResult() =
        runTest {
            val mockedObserver: (Int) -> Unit = mock()
            val mockedTransformer: (DataResult<Any>) -> Int = mock()
            val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
            liveData.observe(owner) {
                result(
                    single = true,
                    observer = mockedObserver,
                    transformer = mockedTransformer
                )
            }

            val result = DataResult<Any>(null, null, DataResultStatus.SUCCESS)
            Mockito.`when`(mockedTransformer.invoke(result)).thenReturn(0)
            liveData.value = result
            advanceUntilIdle()
            verifyBlocking(mockedTransformer) { invoke(result) }
            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke(0) }

            val result2 = DataResult<Any>(null, null, DataResultStatus.ERROR)
            Mockito.`when`(mockedTransformer.invoke(result2)).thenReturn(1)
            liveData.value = result2
            Mockito.verifyNoMoreInteractions(mockedTransformer)
            Mockito.verifyNoMoreInteractions(mockedObserver)

            Assert.assertFalse(liveData.hasObservers())
        }

    @Test
    fun whenObserveSingleResult_shouldBeCalledWhenResultIsPosted_withoutArguments() = runTest {
        val mockedObserver: () -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
        liveData.observe(owner) { result(single = true, observer = mockedObserver) }

        val result = DataResult<Any>(null, null, DataResultStatus.SUCCESS)
        liveData.value = result
        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke() }

        val result2 = DataResult<Any>(null, null, DataResultStatus.ERROR)
        liveData.value = result2
        Mockito.verifyNoMoreInteractions(mockedObserver)

        Assert.assertFalse(liveData.hasObservers())
    }
    // endregion

    // region Status
    @Test
    fun whenObserveStatus_shouldBeCalledWhenResultIsPosted() = runTest {
        val mockedObserver: (DataResultStatus) -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
        liveData.observe(owner) { status(observer = mockedObserver) }

        val result = DataResult<Any>(null, null, DataResultStatus.SUCCESS)
        liveData.value = result
        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke(DataResultStatus.SUCCESS) }

        val result2 = DataResult<Any>(null, null, DataResultStatus.ERROR)
        liveData.value = result2
        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke(DataResultStatus.ERROR) }
    }

    @Test
    fun whenObserveStatus_withTransformer_shouldBeCalledWhenResultIsPostedWithTheTransformedStatus() =
        runTest {
            val mockedObserver: (Int) -> Unit = mock()
            val mockedTransformer: (DataResultStatus) -> Int = mock()
            val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
            liveData.observe(owner) {
                status(
                    observer = mockedObserver,
                    transformer = mockedTransformer
                )
            }

            val result = DataResult<Any>(null, null, DataResultStatus.SUCCESS)
            Mockito.`when`(mockedTransformer.invoke(DataResultStatus.SUCCESS)).thenReturn(0)
            liveData.value = result

            advanceUntilIdle()
            verifyBlocking(mockedTransformer) { invoke(DataResultStatus.SUCCESS) }
            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke(0) }

            val result2 = DataResult<Any>(null, null, DataResultStatus.ERROR)
            Mockito.`when`(mockedTransformer.invoke(DataResultStatus.ERROR)).thenReturn(1)
            liveData.value = result2
            advanceUntilIdle()
            verifyBlocking(mockedTransformer) { invoke(DataResultStatus.ERROR) }
            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke(1) }
        }

    @Test
    fun whenObserveSingleStatus_shouldBeCalledWhenResultIsPosted() = runTest {
        val mockedObserver: (DataResultStatus) -> Unit = mock()
        val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
        liveData.observe(owner) { status(single = true, observer = mockedObserver) }

        val result = DataResult<Any>(null, null, DataResultStatus.SUCCESS)
        liveData.value = result
        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke(DataResultStatus.SUCCESS) }

        val result2 = DataResult<Any>(null, null, DataResultStatus.ERROR)
        liveData.value = result2
        Mockito.verifyNoMoreInteractions(mockedObserver)
        Assert.assertFalse(liveData.hasObservers())
    }

    @Test
    fun whenObserveSingleStatus_withTransformer_shouldBeCalledWhenResultIsPostedWithTheTransformedStatus() =
        runTest {
            val mockedObserver: (Int) -> Unit = mock()
            val mockedTransformer: (DataResultStatus) -> Int = mock()
            val liveData = MutableResponseLiveData<Any>().transformDispatcher(Dispatchers.Main)
            liveData.observe(owner) {
                status(
                    single = true,
                    observer = mockedObserver,
                    transformer = mockedTransformer
                )
            }

            val result = DataResult<Any>(null, null, DataResultStatus.SUCCESS)
            Mockito.`when`(mockedTransformer.invoke(DataResultStatus.SUCCESS)).thenReturn(0)
            liveData.value = result
            advanceUntilIdle()
            verifyBlocking(mockedTransformer) { invoke(DataResultStatus.SUCCESS) }
            advanceUntilIdle()
            verifyBlocking(mockedObserver) { invoke(0) }

            val result2 = DataResult<Any>(null, null, DataResultStatus.ERROR)
            Mockito.`when`(mockedTransformer.invoke(DataResultStatus.ERROR)).thenReturn(1)
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

        val liveData = MutableResponseLiveData<String>().transformDispatcher(Dispatchers.Main)
        val mappedLiveData = liveData.map(mockedTransformer)

        val data = "data"
        Mockito.`when`(mockedTransformer.invoke(data)).thenReturn(data)

        liveData.setData(data)
        mappedLiveData.observeData(owner, mockedObserver)

        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke(data) }
        advanceUntilIdle()
        verifyBlocking(mockedTransformer) { invoke(data) }
    }

    @Test
    fun whenMap_withoutTransformAsync_shouldTransformDataWithoutStartingThreads() = runTest {

        val mockedTransformer: (String) -> String = mock()
        val mockedObserver: (String) -> Unit = mock()

        val liveData = MutableResponseLiveData<String>().transformDispatcher(Dispatchers.Main)
        val mappedLiveData = liveData.map(mockedTransformer)

        val data = "data"
        Mockito.`when`(mockedTransformer.invoke(data)).thenReturn(data)

        liveData.setData(data)
        mappedLiveData.observeData(owner, mockedObserver)

        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke(data) }
        advanceUntilIdle()
        verifyBlocking(mockedTransformer) { invoke(data) }
    }

    @Test
    fun whenMapError_withTransformAsync_shouldTransformErrorStartingThreads() = runTest {

        val mockedTransformer: (Throwable) -> Throwable = mock()
        val mockedObserver: (Throwable) -> Unit = mock()

        val liveData = MutableResponseLiveData<String>().transformDispatcher(Dispatchers.Main)
        val mappedLiveData = liveData.mapError(mockedTransformer)

        val error = IllegalStateException("error")
        Mockito.`when`(mockedTransformer.invoke(error)).thenReturn(error)

        liveData.setError(error)
        mappedLiveData.observeError(owner, mockedObserver)

        advanceUntilIdle()
        verifyBlocking(mockedObserver) { invoke(error) }
        advanceUntilIdle()
        verifyBlocking(mockedTransformer) { invoke(error) }
    }

    @Test
    fun whenMapError_withoutTransformAsync_shouldTransformErrorWithoutStartingThreads() = runTest {

        val mockedTransformer: (Throwable) -> Throwable = mock()
        val mockedObserver: (Throwable) -> Unit = mock()

        val liveData = MutableResponseLiveData<String>().transformDispatcher(Dispatchers.Main)
        val mappedLiveData = liveData.mapError(mockedTransformer)

        val error = IllegalStateException("error")
        Mockito.`when`(mockedTransformer.invoke(error)).thenReturn(error)

        liveData.setError(error)
        mappedLiveData.observeError(owner, mockedObserver)

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

            val liveData = MutableResponseLiveData<String>().transformDispatcher(Dispatchers.Main)
            val onNextLiveData = liveData.onNext(mockedOnNext)

            val data = "data"

            liveData.setData(data)
            onNextLiveData.observeData(owner, mockedObserver)

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

            val liveData = MutableResponseLiveData<String>().transformDispatcher(Dispatchers.Main)
            val onNextLiveData = liveData.onNext(mockedOnNext)

            val data = "data"

            liveData.setData(data)
            onNextLiveData.observeData(owner, mockedObserver)

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

            val liveData = MutableResponseLiveData<String>().transformDispatcher(Dispatchers.Main)
            val onErrorLiveData = liveData.onError(mockedOnError)

            val error = IllegalStateException("error")

            liveData.setError(error)
            onErrorLiveData.observeError(owner, mockedObserver)

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

            val liveData = MutableResponseLiveData<String>().transformDispatcher(Dispatchers.Main)
            val onErrorLiveData = liveData.onError(mockedOnError)

            val error = IllegalStateException("error")

            liveData.setError(error)
            onErrorLiveData.observeError(owner, mockedObserver)

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

            val liveData = MutableResponseLiveData<String>().transformDispatcher(Dispatchers.Main)
            val onErrorLiveData = liveData.onErrorReturn(mockedOnErrorReturn)

            val error = IllegalStateException("error")
            Mockito.`when`(mockedOnErrorReturn.invoke(error)).thenReturn("error")

            liveData.setError(error)
            onErrorLiveData.observeError(owner, mockedObserver)
            onErrorLiveData.observeData(owner, mockedDataObserver)

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

            val liveData = MutableResponseLiveData<String>().transformDispatcher(Dispatchers.Main)
            val onErrorLiveData = liveData.onErrorReturn(mockedOnErrorReturn)

            val error = IllegalStateException("error")
            Mockito.`when`(mockedOnErrorReturn.invoke(error)).thenReturn("error")

            liveData.setError(error)
            onErrorLiveData.observeError(owner, mockedObserver)
            onErrorLiveData.observeData(owner, mockedDataObserver)

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

            val liveData = MutableResponseLiveData<String>().transformDispatcher(Dispatchers.Main)
            val onErrorLiveData = liveData.onErrorReturn(mockedOnErrorReturn)

            val error = IllegalStateException("error")
            liveData.setError(error, "data")

            onErrorLiveData.observeError(owner, mockedObserver)
            onErrorLiveData.observeData(owner, mockedDataObserver)

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

            val liveData = MutableResponseLiveData<String>().transformDispatcher(Dispatchers.Main)
            val transformedLiveData = liveData.transform(mockedTransformation)
            transformedLiveData.observeData(owner, mockedDataObserver)

            val data = DataResult("data", null, DataResultStatus.SUCCESS)
            val result = DataResult(0, null, DataResultStatus.SUCCESS)
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

            val liveData = MutableResponseLiveData<String>().transformDispatcher(Dispatchers.Main)
            val transformedLiveData = liveData.transform(mockedTransformation)
            transformedLiveData.observeData(owner, mockedDataObserver)

            val data = DataResult("data", null, DataResultStatus.SUCCESS)
            val result = DataResult(0, null, DataResultStatus.SUCCESS)
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
            DataResult("value", null, DataResultStatus.SUCCESS)
        )
        Assert.assertTrue(liveData.data == "value")
        Assert.assertTrue(liveData.status == DataResultStatus.SUCCESS)
        Assert.assertNull(liveData.error)
    }
}