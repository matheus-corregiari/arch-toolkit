package br.com.arch.toolkit.livedata.response

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.arch.toolkit.livedata.MutableResponseLiveData
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MutableResponseLiveDataTest {

    @Rule
    @get:Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    init {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun whenPostLoading_shouldHaveLoadingStatus_nullError_nullData() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        liveData.postLoading()

        Assert.assertNotNull(liveData.value)
        Assert.assertNull(liveData.error)
        Assert.assertNull(liveData.data)
        Assert.assertEquals(DataResultStatus.LOADING, liveData.status)
    }

    @Test
    fun whenPostError_shouldHaveErrorStatus_notNullError_nullData() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        val error = IllegalStateException()
        liveData.postError(error)

        Assert.assertNotNull(liveData.value)
        Assert.assertEquals(error, liveData.error)
        Assert.assertNull(liveData.data)
        Assert.assertEquals(DataResultStatus.ERROR, liveData.status)
    }

    @Test
    fun whenPostData_shouldHaveSuccessStatus_nullError_notNullData() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        val data = "data"
        liveData.postData(data)

        Assert.assertNotNull(liveData.value)
        Assert.assertNull(liveData.error)
        Assert.assertEquals(data, liveData.data)
        Assert.assertEquals(DataResultStatus.SUCCESS, liveData.status)
    }

    @Test
    fun whenPostSuccess_shouldHaveSuccessStatus_nullError_nullData() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        liveData.postSuccess()

        Assert.assertNotNull(liveData.value)
        Assert.assertNull(liveData.error)
        Assert.assertNull(liveData.data)
        Assert.assertEquals(DataResultStatus.SUCCESS, liveData.status)
    }

    @Test
    fun whenSetLoading_shouldHaveLoadingStatus_nullError_nullData() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        liveData.setLoading()

        Assert.assertNotNull(liveData.value)
        Assert.assertNull(liveData.error)
        Assert.assertNull(liveData.data)
        Assert.assertEquals(DataResultStatus.LOADING, liveData.status)
    }

    @Test
    fun whenSetError_shouldHaveErrorStatus_notNullError_nullData() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        val error = IllegalStateException()
        liveData.setError(error)

        Assert.assertNotNull(liveData.value)
        Assert.assertEquals(error, liveData.error)
        Assert.assertNull(liveData.data)
        Assert.assertEquals(DataResultStatus.ERROR, liveData.status)
    }

    @Test
    fun whenSetData_shouldHaveSuccessStatus_nullError_notNullData() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        val data = "data"
        liveData.setData(data)

        Assert.assertNotNull(liveData.value)
        Assert.assertNull(liveData.error)
        Assert.assertEquals(data, liveData.data)
        Assert.assertEquals(DataResultStatus.SUCCESS, liveData.status)
    }

    @Test
    fun whenSetSuccess_shouldHaveSuccessStatus_nullError_nullData() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        liveData.setSuccess()

        Assert.assertNotNull(liveData.value)
        Assert.assertNull(liveData.error)
        Assert.assertNull(liveData.data)
        Assert.assertEquals(DataResultStatus.SUCCESS, liveData.status)
    }

    @Test
    fun whenInitialize_withoutValue_shouldReturnAnInstanceWithEmptyValue() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        Assert.assertNull(liveData.data)
        Assert.assertEquals(liveData.status, DataResultStatus.NONE)
        Assert.assertNull(liveData.error)
    }

    @Test
    fun whenInitialize_withValue_shouldReturnAnInstanceWithADefaultValue() = runTest {
        val liveData = MutableResponseLiveData(
            DataResult("value", null, DataResultStatus.SUCCESS)
        )
        Assert.assertTrue(liveData.data == "value")
        Assert.assertTrue(liveData.status == DataResultStatus.SUCCESS)
        Assert.assertNull(liveData.error)
    }
}