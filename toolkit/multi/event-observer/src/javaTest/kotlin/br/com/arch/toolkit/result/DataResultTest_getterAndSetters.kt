@file:Suppress("LongMethod")

package br.com.arch.toolkit.result

import io.mockk.mockk
import br.com.arch.toolkit.util.dataResultError
import br.com.arch.toolkit.util.dataResultLoading
import br.com.arch.toolkit.util.dataResultNone
import br.com.arch.toolkit.util.dataResultSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DataResultTest_getterAndSetters {

    private val error = IllegalStateException("error")

    // Success
    private val resultA = dataResultSuccess("data A")
    private val resultB = dataResultSuccess<String>(null)

    // Loading
    private val resultC = dataResultLoading<String>()
    private val resultD = dataResultLoading("data D")
    private val resultE = dataResultLoading<String>(null, error)
    private val resultF = dataResultLoading("data F", error)

    // Error
    private val resultG = dataResultError(error, "data G")
    private val resultH = dataResultError<String>(error)
    private val resultI = dataResultError<String>(null)

    // None
    private val resultJ = dataResultNone<String>()

    // List
    private val resultListEmpty = dataResultSuccess(listOf<String>())
    private val resultMapEmpty = dataResultSuccess(mapOf<String, String>())
    private val resultSequenceEmpty = dataResultSuccess(sequenceOf<String>())
    private val resultListNotEmpty = dataResultSuccess(listOf("a"))
    private val resultMapNotEmpty = dataResultSuccess(mapOf("a" to "a"))
    private val resultSequenceNotEmpty = dataResultSuccess(sequenceOf("a"))

    init {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun `0 - HasData`() {
        assertTrue(resultA.hasData)
        assertFalse(resultB.hasData)
        assertFalse(resultC.hasData)
        assertTrue(resultD.hasData)
        assertFalse(resultE.hasData)
        assertTrue(resultF.hasData)
        assertTrue(resultG.hasData)
        assertFalse(resultH.hasData)
        assertFalse(resultI.hasData)
        assertFalse(resultJ.hasData)
        assertTrue(resultListEmpty.hasData)
        assertTrue(resultMapEmpty.hasData)
        assertTrue(resultSequenceEmpty.hasData)
        assertTrue(resultListNotEmpty.hasData)
        assertTrue(resultMapNotEmpty.hasData)
        assertTrue(resultSequenceNotEmpty.hasData)
    }

    @Test
    fun `1 - HasError`() {
        assertFalse(resultA.hasError)
        assertFalse(resultB.hasError)
        assertFalse(resultC.hasError)
        assertFalse(resultD.hasError)
        assertTrue(resultE.hasError)
        assertTrue(resultF.hasError)
        assertTrue(resultG.hasError)
        assertTrue(resultH.hasError)
        assertFalse(resultI.hasError)
        assertFalse(resultJ.hasError)
        assertFalse(resultListEmpty.hasError)
        assertFalse(resultMapEmpty.hasError)
        assertFalse(resultSequenceEmpty.hasError)
        assertFalse(resultListNotEmpty.hasError)
        assertFalse(resultMapNotEmpty.hasError)
        assertFalse(resultSequenceNotEmpty.hasError)
    }

    @Test
    fun `2 - IsSuccess`() {
        assertTrue(resultA.isSuccess)
        assertTrue(resultB.isSuccess)
        assertFalse(resultC.isSuccess)
        assertFalse(resultD.isSuccess)
        assertFalse(resultE.isSuccess)
        assertFalse(resultF.isSuccess)
        assertFalse(resultG.isSuccess)
        assertFalse(resultH.isSuccess)
        assertFalse(resultI.isSuccess)
        assertFalse(resultJ.isSuccess)
        assertTrue(resultListEmpty.isSuccess)
        assertTrue(resultMapEmpty.isSuccess)
        assertTrue(resultSequenceEmpty.isSuccess)
        assertTrue(resultListNotEmpty.isSuccess)
        assertTrue(resultMapNotEmpty.isSuccess)
        assertTrue(resultSequenceNotEmpty.isSuccess)
    }

    @Test
    fun `3 - IsLoading`() {
        assertFalse(resultA.isLoading)
        assertFalse(resultB.isLoading)
        assertTrue(resultC.isLoading)
        assertTrue(resultD.isLoading)
        assertTrue(resultE.isLoading)
        assertTrue(resultF.isLoading)
        assertFalse(resultG.isLoading)
        assertFalse(resultH.isLoading)
        assertFalse(resultI.isLoading)
        assertFalse(resultJ.isLoading)
        assertFalse(resultListEmpty.isLoading)
        assertFalse(resultMapEmpty.isLoading)
        assertFalse(resultSequenceEmpty.isLoading)
        assertFalse(resultListNotEmpty.isLoading)
        assertFalse(resultMapNotEmpty.isLoading)
        assertFalse(resultSequenceNotEmpty.isLoading)
    }

    @Test
    fun `4 - IsError`() {
        assertFalse(resultA.isError)
        assertFalse(resultB.isError)
        assertFalse(resultC.isError)
        assertFalse(resultD.isError)
        assertFalse(resultE.isError)
        assertFalse(resultF.isError)
        assertTrue(resultG.isError)
        assertTrue(resultH.isError)
        assertTrue(resultI.isError)
        assertFalse(resultJ.isError)
        assertFalse(resultListEmpty.isError)
        assertFalse(resultMapEmpty.isError)
        assertFalse(resultSequenceEmpty.isError)
        assertFalse(resultListNotEmpty.isError)
        assertFalse(resultMapNotEmpty.isError)
        assertFalse(resultSequenceNotEmpty.isError)
    }

    @Test
    fun `5 - IsNone`() {
        assertFalse(resultA.isNone)
        assertFalse(resultB.isNone)
        assertFalse(resultC.isNone)
        assertFalse(resultD.isNone)
        assertFalse(resultE.isNone)
        assertFalse(resultF.isNone)
        assertFalse(resultG.isNone)
        assertFalse(resultH.isNone)
        assertFalse(resultI.isNone)
        assertTrue(resultJ.isNone)
        assertFalse(resultListEmpty.isNone)
        assertFalse(resultMapEmpty.isNone)
        assertFalse(resultSequenceEmpty.isNone)
        assertFalse(resultListNotEmpty.isNone)
        assertFalse(resultMapNotEmpty.isNone)
        assertFalse(resultSequenceNotEmpty.isNone)
    }

    @Test
    fun `6 - IsListType`() {
        assertFalse(resultA.isListType)
        assertFalse(resultB.isListType)
        assertFalse(resultC.isListType)
        assertFalse(resultD.isListType)
        assertFalse(resultE.isListType)
        assertFalse(resultF.isListType)
        assertFalse(resultG.isListType)
        assertFalse(resultH.isListType)
        assertFalse(resultI.isListType)
        assertFalse(resultJ.isListType)
        assertTrue(resultListEmpty.isListType)
        assertTrue(resultMapEmpty.isListType)
        assertTrue(resultSequenceEmpty.isListType)
        assertTrue(resultListNotEmpty.isListType)
        assertTrue(resultMapNotEmpty.isListType)
        assertTrue(resultSequenceNotEmpty.isListType)
    }

    @Test
    fun `7 - IsEmpty`() {
        assertFalse(resultA.isEmpty)
        assertFalse(resultB.isEmpty)
        assertFalse(resultC.isEmpty)
        assertFalse(resultD.isEmpty)
        assertFalse(resultE.isEmpty)
        assertFalse(resultF.isEmpty)
        assertFalse(resultG.isEmpty)
        assertFalse(resultH.isEmpty)
        assertFalse(resultI.isEmpty)
        assertFalse(resultJ.isEmpty)
        assertTrue(resultListEmpty.isEmpty)
        assertTrue(resultMapEmpty.isEmpty)
        assertTrue(resultSequenceEmpty.isEmpty)
        assertFalse(resultListNotEmpty.isEmpty)
        assertFalse(resultMapNotEmpty.isEmpty)
        assertFalse(resultSequenceNotEmpty.isEmpty)
    }

    @Test
    fun `8 - IsNotEmpty`() {
        assertFalse(resultA.isNotEmpty)
        assertFalse(resultB.isNotEmpty)
        assertFalse(resultC.isNotEmpty)
        assertFalse(resultD.isNotEmpty)
        assertFalse(resultE.isNotEmpty)
        assertFalse(resultF.isNotEmpty)
        assertFalse(resultG.isNotEmpty)
        assertFalse(resultH.isNotEmpty)
        assertFalse(resultI.isNotEmpty)
        assertFalse(resultJ.isNotEmpty)
        assertFalse(resultListEmpty.isNotEmpty)
        assertFalse(resultMapEmpty.isNotEmpty)
        assertFalse(resultSequenceEmpty.isNotEmpty)
        assertTrue(resultListNotEmpty.isNotEmpty)
        assertTrue(resultMapNotEmpty.isNotEmpty)
        assertTrue(resultSequenceNotEmpty.isNotEmpty)
    }
}
