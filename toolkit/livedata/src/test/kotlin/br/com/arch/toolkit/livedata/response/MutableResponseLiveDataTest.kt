package br.com.arch.toolkit.livedata.response

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.arch.toolkit.common.DataResultStatus
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class MutableResponseLiveDataTest {

    @Rule
    @get:Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun whenPostLoading_shouldHaveLoadingStatus_nullError_nullData() {
        val liveData = MutableResponseLiveData<Any>()
        liveData.postLoading()

        Assert.assertNotNull(liveData.value)
        Assert.assertNull(liveData.error)
        Assert.assertNull(liveData.data)
        Assert.assertEquals(DataResultStatus.LOADING, liveData.status)
    }

    @Test
    fun whenPostError_shouldHaveErrorStatus_notNullError_nullData() {
        val liveData = MutableResponseLiveData<Any>()
        val error = IllegalStateException()
        liveData.postError(error)

        Assert.assertNotNull(liveData.value)
        Assert.assertEquals(error, liveData.error)
        Assert.assertNull(liveData.data)
        Assert.assertEquals(DataResultStatus.ERROR, liveData.status)
    }

    @Test
    fun whenPostData_shouldHaveSuccessStatus_nullError_notNullData() {
        val liveData = MutableResponseLiveData<Any>()
        val data = "data"
        liveData.postData(data)

        Assert.assertNotNull(liveData.value)
        Assert.assertNull(liveData.error)
        Assert.assertEquals(data, liveData.data)
        Assert.assertEquals(DataResultStatus.SUCCESS, liveData.status)
    }

    @Test
    fun whenPostSuccess_shouldHaveSuccessStatus_nullError_nullData() {
        val liveData = MutableResponseLiveData<Any>()
        liveData.postSuccess()

        Assert.assertNotNull(liveData.value)
        Assert.assertNull(liveData.error)
        Assert.assertNull(liveData.data)
        Assert.assertEquals(DataResultStatus.SUCCESS, liveData.status)
    }

    @Test
    fun whenSetLoading_shouldHaveLoadingStatus_nullError_nullData() {
        val liveData = MutableResponseLiveData<Any>()
        liveData.setLoading()

        Assert.assertNotNull(liveData.value)
        Assert.assertNull(liveData.error)
        Assert.assertNull(liveData.data)
        Assert.assertEquals(DataResultStatus.LOADING, liveData.status)
    }

    @Test
    fun whenSetError_shouldHaveErrorStatus_notNullError_nullData() {
        val liveData = MutableResponseLiveData<Any>()
        val error = IllegalStateException()
        liveData.setError(error)

        Assert.assertNotNull(liveData.value)
        Assert.assertEquals(error, liveData.error)
        Assert.assertNull(liveData.data)
        Assert.assertEquals(DataResultStatus.ERROR, liveData.status)
    }

    @Test
    fun whenSetData_shouldHaveSuccessStatus_nullError_notNullData() {
        val liveData = MutableResponseLiveData<Any>()
        val data = "data"
        liveData.setData(data)

        Assert.assertNotNull(liveData.value)
        Assert.assertNull(liveData.error)
        Assert.assertEquals(data, liveData.data)
        Assert.assertEquals(DataResultStatus.SUCCESS, liveData.status)
    }

    @Test
    fun whenSetSuccess_shouldHaveSuccessStatus_nullError_nullData() {
        val liveData = MutableResponseLiveData<Any>()
        liveData.setSuccess()

        Assert.assertNotNull(liveData.value)
        Assert.assertNull(liveData.error)
        Assert.assertNull(liveData.data)
        Assert.assertEquals(DataResultStatus.SUCCESS, liveData.status)
    }
}