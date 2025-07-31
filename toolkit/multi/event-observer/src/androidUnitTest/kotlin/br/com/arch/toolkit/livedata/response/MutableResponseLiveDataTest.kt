package br.com.arch.toolkit.livedata.response

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.arch.toolkit.MainDispatcherRule
import br.com.arch.toolkit.livedata.MutableResponseLiveData
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.result.DataResultStatus.ERROR
import br.com.arch.toolkit.result.DataResultStatus.LOADING
import br.com.arch.toolkit.result.DataResultStatus.NONE
import br.com.arch.toolkit.result.DataResultStatus.SUCCESS
import br.com.arch.toolkit.util.dataResultError
import br.com.arch.toolkit.util.dataResultLoading
import br.com.arch.toolkit.util.dataResultNone
import br.com.arch.toolkit.util.dataResultSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runners.MethodSorters

@OptIn(ExperimentalCoroutinesApi::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class MutableResponseLiveDataTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val rule = MainDispatcherRule()

    @Test
    fun `0 - init without param, should init with null value`() = runTest {
        val liveData = MutableResponseLiveData<Any>()
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
        val liveData = MutableResponseLiveData(value)

        Assert.assertEquals(value, liveData.value)
        Assert.assertNull(liveData.error)
        Assert.assertEquals("String", liveData.data)
        Assert.assertEquals(SUCCESS, liveData.status)
    }

    @Test
    fun `POST - null, null, LOADING`() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        liveData.postLoading()

        Assert.assertEquals(dataResultLoading<Any>(), liveData.value)
        Assert.assertNull(liveData.error)
        Assert.assertNull(liveData.data)
        Assert.assertEquals(LOADING, liveData.status)
    }

    @Test
    fun `POST - data, null, LOADING`() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        liveData.postLoading("data")

        Assert.assertEquals(dataResultLoading<Any>("data"), liveData.value)
        Assert.assertNull(liveData.error)
        Assert.assertEquals("data", liveData.data)
        Assert.assertEquals(LOADING, liveData.status)
    }

    @Test
    fun `POST - null, error, ERROR`() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        val error = IllegalStateException()
        liveData.postError(error)

        Assert.assertEquals(dataResultError<Any>(error), liveData.value)
        Assert.assertEquals(error, liveData.error)
        Assert.assertNull(liveData.data)
        Assert.assertEquals(ERROR, liveData.status)
    }

    @Test
    fun `POST - data, error, ERROR`() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        val error = IllegalStateException()
        liveData.postError(error, "data")

        Assert.assertEquals(dataResultError<Any>(error, "data"), liveData.value)
        Assert.assertEquals(error, liveData.error)
        Assert.assertEquals("data", liveData.data)
        Assert.assertEquals(ERROR, liveData.status)
    }

    @Test
    fun `POST - data, null, SUCCESS`() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        liveData.postData("data")

        Assert.assertEquals(dataResultSuccess<Any>("data"), liveData.value)
        Assert.assertNull(liveData.error)
        Assert.assertEquals("data", liveData.data)
        Assert.assertEquals(SUCCESS, liveData.status)
    }

    @Test
    fun `POST - null, null, SUCCESS`() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        liveData.postSuccess()

        Assert.assertEquals(dataResultSuccess<Any>(null), liveData.value)
        Assert.assertNull(liveData.error)
        Assert.assertNull(liveData.data)
        Assert.assertEquals(SUCCESS, liveData.status)
    }

    @Test
    fun `POST - null, null, NONE`() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        liveData.postNone()

        Assert.assertEquals(dataResultNone<Any>(), liveData.value)
        Assert.assertNull(liveData.error)
        Assert.assertNull(liveData.data)
        Assert.assertEquals(NONE, liveData.status)
    }

    @Test
    fun `POST - DataResult`() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        liveData.postValue(dataResultNone())

        Assert.assertEquals(dataResultNone<Any>(), liveData.value)
        Assert.assertNull(liveData.error)
        Assert.assertNull(liveData.data)
        Assert.assertEquals(NONE, liveData.status)
    }

    @Test
    fun `SET - null, null, LOADING`() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        liveData.setLoading()

        Assert.assertEquals(dataResultLoading<Any>(), liveData.value)
        Assert.assertNull(liveData.error)
        Assert.assertNull(liveData.data)
        Assert.assertEquals(LOADING, liveData.status)
    }

    @Test
    fun `SET - data, null, LOADING`() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        liveData.setLoading("data")

        Assert.assertEquals(dataResultLoading<Any>("data"), liveData.value)
        Assert.assertNull(liveData.error)
        Assert.assertEquals("data", liveData.data)
        Assert.assertEquals(LOADING, liveData.status)
    }

    @Test
    fun `SET - null, error, ERROR`() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        val error = IllegalStateException()
        liveData.setError(error)

        Assert.assertEquals(dataResultError<Any>(error), liveData.value)
        Assert.assertEquals(error, liveData.error)
        Assert.assertNull(liveData.data)
        Assert.assertEquals(ERROR, liveData.status)
    }

    @Test
    fun `SET - data, error, ERROR`() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        val error = IllegalStateException()
        liveData.setError(error, "data")

        Assert.assertEquals(dataResultError<Any>(error, "data"), liveData.value)
        Assert.assertEquals(error, liveData.error)
        Assert.assertEquals("data", liveData.data)
        Assert.assertEquals(ERROR, liveData.status)
    }

    @Test
    fun `SET - data, null, SUCCESS`() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        liveData.setData("data")

        Assert.assertEquals(dataResultSuccess<Any>("data"), liveData.value)
        Assert.assertNull(liveData.error)
        Assert.assertEquals("data", liveData.data)
        Assert.assertEquals(SUCCESS, liveData.status)
    }

    @Test
    fun `SET - null, null, SUCCESS`() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        liveData.setSuccess()

        Assert.assertEquals(dataResultSuccess<Any>(null), liveData.value)
        Assert.assertNull(liveData.error)
        Assert.assertNull(liveData.data)
        Assert.assertEquals(SUCCESS, liveData.status)
    }

    @Test
    fun `SET - null, null, NONE`() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        liveData.setNone()

        Assert.assertEquals(dataResultNone<Any>(), liveData.value)
        Assert.assertNull(liveData.error)
        Assert.assertNull(liveData.data)
        Assert.assertEquals(NONE, liveData.status)
    }

    @Test
    fun `SET - DataResult`() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        liveData.value = dataResultNone()

        Assert.assertEquals(dataResultNone<Any>(), liveData.value)
        Assert.assertNull(liveData.error)
        Assert.assertNull(liveData.data)
        Assert.assertEquals(NONE, liveData.status)
    }
}
