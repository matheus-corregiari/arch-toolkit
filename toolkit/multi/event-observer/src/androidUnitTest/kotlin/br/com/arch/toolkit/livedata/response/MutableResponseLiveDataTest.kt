package br.com.arch.toolkit.livedata.response

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MutableResponseLiveDataTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    init {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun `0 - init without param, should init with null value`() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        liveData.scope(this)
        liveData.transformDispatcher(Dispatchers.Main.immediate)

        assertNull(liveData.value)
        assertNull(liveData.error)
        assertNull(liveData.data)
        assertNull(liveData.status)
    }

    @Test
    fun `0 - init with param, should init with param value`() = runTest {
        val value = DataResult("String", null, SUCCESS)
        val liveData = MutableResponseLiveData(value)

        assertEquals(value, liveData.value)
        assertNull(liveData.error)
        assertEquals("String", liveData.data)
        assertEquals(SUCCESS, liveData.status)
    }

    @Test
    fun `POST - null - null - LOADING`() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        liveData.postLoading()

        assertEquals(dataResultLoading<Any>(), liveData.value)
        assertNull(liveData.error)
        assertNull(liveData.data)
        assertEquals(LOADING, liveData.status)
    }

    @Test
    fun `POST - data - null - LOADING`() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        liveData.postLoading("data")

        assertEquals(dataResultLoading<Any>("data"), liveData.value)
        assertNull(liveData.error)
        assertEquals("data", liveData.data)
        assertEquals(LOADING, liveData.status)
    }

    @Test
    fun `POST - null - error - ERROR`() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        val error = IllegalStateException()
        liveData.postError(error)

        assertEquals(dataResultError<Any>(error), liveData.value)
        assertEquals(error, liveData.error)
        assertNull(liveData.data)
        assertEquals(ERROR, liveData.status)
    }

    @Test
    fun `POST - data - error - ERROR`() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        val error = IllegalStateException()
        liveData.postError(error, "data")

        assertEquals(dataResultError<Any>(error, "data"), liveData.value)
        assertEquals(error, liveData.error)
        assertEquals("data", liveData.data)
        assertEquals(ERROR, liveData.status)
    }

    @Test
    fun `POST - data - null - SUCCESS`() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        liveData.postData("data")

        assertEquals(dataResultSuccess<Any>("data"), liveData.value)
        assertNull(liveData.error)
        assertEquals("data", liveData.data)
        assertEquals(SUCCESS, liveData.status)
    }

    @Test
    fun `POST - null - null - SUCCESS`() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        liveData.postSuccess()

        assertEquals(dataResultSuccess<Any>(null), liveData.value)
        assertNull(liveData.error)
        assertNull(liveData.data)
        assertEquals(SUCCESS, liveData.status)
    }

    @Test
    fun `POST - null - null - NONE`() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        liveData.postNone()

        assertEquals(dataResultNone<Any>(), liveData.value)
        assertNull(liveData.error)
        assertNull(liveData.data)
        assertEquals(NONE, liveData.status)
    }

    @Test
    fun `POST - DataResult`() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        liveData.postValue(dataResultNone())

        assertEquals(dataResultNone<Any>(), liveData.value)
        assertNull(liveData.error)
        assertNull(liveData.data)
        assertEquals(NONE, liveData.status)
    }

    @Test
    fun `SET - null - null - LOADING`() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        liveData.setLoading()

        assertEquals(dataResultLoading<Any>(), liveData.value)
        assertNull(liveData.error)
        assertNull(liveData.data)
        assertEquals(LOADING, liveData.status)
    }

    @Test
    fun `SET - data - null - LOADING`() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        liveData.setLoading("data")

        assertEquals(dataResultLoading<Any>("data"), liveData.value)
        assertNull(liveData.error)
        assertEquals("data", liveData.data)
        assertEquals(LOADING, liveData.status)
    }

    @Test
    fun `SET - null - error - ERROR`() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        val error = IllegalStateException()
        liveData.setError(error)

        assertEquals(dataResultError<Any>(error), liveData.value)
        assertEquals(error, liveData.error)
        assertNull(liveData.data)
        assertEquals(ERROR, liveData.status)
    }

    @Test
    fun `SET - data - error - ERROR`() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        val error = IllegalStateException()
        liveData.setError(error, "data")

        assertEquals(dataResultError<Any>(error, "data"), liveData.value)
        assertEquals(error, liveData.error)
        assertEquals("data", liveData.data)
        assertEquals(ERROR, liveData.status)
    }

    @Test
    fun `SET - data - null - SUCCESS`() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        liveData.setData("data")

        assertEquals(dataResultSuccess<Any>("data"), liveData.value)
        assertNull(liveData.error)
        assertEquals("data", liveData.data)
        assertEquals(SUCCESS, liveData.status)
    }

    @Test
    fun `SET - null - null - SUCCESS`() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        liveData.setSuccess()

        assertEquals(dataResultSuccess<Any>(null), liveData.value)
        assertNull(liveData.error)
        assertNull(liveData.data)
        assertEquals(SUCCESS, liveData.status)
    }

    @Test
    fun `SET - null - null - NONE`() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        liveData.setNone()

        assertEquals(dataResultNone<Any>(), liveData.value)
        assertNull(liveData.error)
        assertNull(liveData.data)
        assertEquals(NONE, liveData.status)
    }

    @Test
    fun `SET - DataResult`() = runTest {
        val liveData = MutableResponseLiveData<Any>()
        liveData.value = dataResultNone()

        assertEquals(dataResultNone<Any>(), liveData.value)
        assertNull(liveData.error)
        assertNull(liveData.data)
        assertEquals(NONE, liveData.status)
    }
}
