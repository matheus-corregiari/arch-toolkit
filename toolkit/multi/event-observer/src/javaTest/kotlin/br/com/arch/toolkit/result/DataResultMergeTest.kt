@file:Suppress("LongMethod", "LargeClass")

package br.com.arch.toolkit.result

import io.mockk.mockk
import br.com.arch.toolkit.result.DataResultStatus.ERROR
import br.com.arch.toolkit.result.DataResultStatus.LOADING
import br.com.arch.toolkit.result.DataResultStatus.NONE
import br.com.arch.toolkit.result.DataResultStatus.SUCCESS
import br.com.arch.toolkit.util.dataResultError
import br.com.arch.toolkit.util.dataResultLoading
import br.com.arch.toolkit.util.dataResultNone
import br.com.arch.toolkit.util.dataResultSuccess
import br.com.arch.toolkit.util.merge
import br.com.arch.toolkit.util.mergeAll
import br.com.arch.toolkit.util.mergeNotNull
import br.com.arch.toolkit.util.plus
import kotlin.test.Test
import kotlin.test.assertEquals

class DataResultMergeTest {

    private val error = IllegalStateException("error")
    private val resultA = dataResultSuccess("data A")
    private val resultB = dataResultLoading(123)
    private val resultC = dataResultError<Boolean>(error)
    private val resultD: DataResult<Float>? = null
    private val resultE = dataResultNone<Double>()

    @Test
    fun validate_merge() {
        /* region -------------------- A -------------------- */
        // A + A
        assertEquals(
            dataResultSuccess<Pair<String?, String?>>("data A" to "data A"),
            resultA + resultA
        )
        // A + B
        assertEquals(
            dataResultLoading<Pair<String?, Int?>>("data A" to 123),
            resultA + resultB
        )
        // A + C
        assertEquals(
            dataResultError<Pair<String?, Boolean?>>(error, ("data A" to null)),
            resultA + resultC
        )
        // A + D
        assertEquals(
            dataResultSuccess<Pair<String?, Float?>>(("data A" to null)),
            resultA + resultD
        )
        // A + E
        assertEquals(
            dataResultSuccess<Pair<String?, Double?>>(("data A" to null)),
            resultA + resultE
        )
        /* endregion */

        /* region -------------------- B -------------------- */
        // B + A
        assertEquals(
            dataResultLoading<Pair<Int?, String?>>(123 to "data A"),
            resultB.merge(resultA)
        )
        // B + B
        assertEquals(
            dataResultLoading<Pair<Int?, Int?>>(123 to 123),
            resultB.merge(resultB)
        )
        // B + C
        assertEquals(
            dataResultError<Pair<Int?, Boolean?>>(error, (123 to null)),
            resultB.merge(resultC)
        )
        // B + D
        assertEquals(
            dataResultLoading<Pair<Int?, Float?>>((123 to null)),
            resultB.merge(resultD)
        )
        // B + E
        assertEquals(
            dataResultLoading<Pair<Int?, Double?>>((123 to null)),
            resultB.merge(resultE)
        )
        /* endregion */

        /* region -------------------- C -------------------- */
        // C + A
        assertEquals(
            dataResultError<Pair<Boolean?, String?>>(error, (null to "data A")),
            resultC.merge(resultA)
        )
        // C + B
        assertEquals(
            dataResultError<Pair<Boolean?, Int?>>(error, (null to 123)),
            resultC.merge(resultB)
        )
        // C + C
        assertEquals(
            dataResultError(error),
            resultC.merge(resultC)
        )
        // C + D
        assertEquals(
            dataResultError(error),
            resultC.merge(resultD)
        )
        // C + E
        assertEquals(
            dataResultError(error),
            resultC.merge(resultE)
        )
        /* endregion */

        /* region -------------------- D -------------------- */
        // D + A
        assertEquals(
            dataResultSuccess<Pair<Float?, String?>>(null to "data A"),
            resultD.merge(resultA)
        )
        // D + B
        assertEquals(
            dataResultLoading<Pair<Float?, Int?>>(null to 123),
            resultD.merge(resultB)
        )
        // D + C
        assertEquals(
            dataResultError(error),
            resultD.merge(resultC)
        )
        // D + D
        assertEquals(
            dataResultNone(),
            resultD.merge(resultD)
        )
        // D + E
        assertEquals(
            dataResultNone(),
            resultD.merge(resultE)
        )
        /* endregion */

        /* region -------------------- E -------------------- */
        // E + A
        assertEquals(
            dataResultSuccess<Pair<Double?, String?>>(null to "data A"),
            resultE.merge(resultA)
        )
        // E + B
        assertEquals(
            dataResultLoading<Pair<Double?, Int?>>(null to 123),
            resultE.merge(resultB)
        )
        // E + C
        assertEquals(
            dataResultError(error),
            resultE.merge(resultC)
        )
        // E + D
        assertEquals(
            dataResultNone(),
            resultE.merge(resultD)
        )
        // E + E
        assertEquals(
            dataResultNone(),
            resultE.merge(resultE)
        )
        /* endregion */
    }

    @Test
    fun validate_mergeNotNull() {
        /* region -------------------- A -------------------- */
        // A + A
        assertEquals(
            dataResultSuccess("data A" to "data A"),
            resultA.mergeNotNull(resultA)
        )
        // A + B
        assertEquals(
            dataResultLoading("data A" to 123),
            resultA.mergeNotNull(resultB)
        )
        // A + C
        assertEquals(
            dataResultError(error),
            resultA.mergeNotNull(resultC)
        )
        // A + D
        assertEquals(
            dataResultSuccess<Pair<String, Float>>(null),
            resultA.mergeNotNull(resultD)
        )
        // A + E
        assertEquals(
            dataResultSuccess<Pair<String, Double>>(null),
            resultA.mergeNotNull(resultE)
        )
        /* endregion */

        /* region -------------------- B -------------------- */
        // B + A
        assertEquals(
            dataResultLoading(123 to "data A"),
            resultB.mergeNotNull(resultA)
        )
        // B + B
        assertEquals(
            dataResultLoading(123 to 123),
            resultB.mergeNotNull(resultB)
        )
        // B + C
        assertEquals(
            dataResultError(error),
            resultB.mergeNotNull(resultC)
        )
        // B + D
        assertEquals(
            dataResultLoading(),
            resultB.mergeNotNull(resultD)
        )
        // B + E
        assertEquals(
            dataResultLoading(),
            resultB.mergeNotNull(resultE)
        )
        /* endregion */

        /* region -------------------- C -------------------- */
        // C + A
        assertEquals(
            dataResultError(error),
            resultC.mergeNotNull(resultA)
        )
        // C + B
        assertEquals(
            dataResultError(error),
            resultC.mergeNotNull(resultB)
        )
        // C + C
        assertEquals(
            dataResultError(error),
            resultC.mergeNotNull(resultC)
        )
        // C + D
        assertEquals(
            dataResultError(error),
            resultC.mergeNotNull(resultD)
        )
        // C + E
        assertEquals(
            dataResultError(error),
            resultC.mergeNotNull(resultE)
        )
        /* endregion */

        /* region -------------------- D -------------------- */
        // D + A
        assertEquals(
            dataResultSuccess<Pair<Float, String>>(null),
            resultD.mergeNotNull(resultA)
        )
        // D + B
        assertEquals(
            dataResultLoading(),
            resultD.mergeNotNull(resultB)
        )
        // D + C
        assertEquals(
            dataResultError(error),
            resultD.mergeNotNull(resultC)
        )
        // D + D
        assertEquals(
            dataResultNone(),
            resultD.mergeNotNull(resultD)
        )
        // D + E
        assertEquals(
            dataResultNone(),
            resultD.mergeNotNull(resultE)
        )
        /* endregion */

        /* region -------------------- E -------------------- */
        // E + A
        assertEquals(
            dataResultSuccess<Pair<Double, String>>(null),
            resultE.mergeNotNull(resultA)
        )
        // E + B
        assertEquals(
            dataResultLoading(),
            resultE.mergeNotNull(resultB)
        )
        // E + C
        assertEquals(
            dataResultError(error),
            resultE.mergeNotNull(resultC)
        )
        // E + D
        assertEquals(
            dataResultNone(),
            resultE.mergeNotNull(resultD)
        )
        // E + E
        assertEquals(
            dataResultNone(),
            resultE.mergeNotNull(resultE)
        )
        /* endregion */
    }

    @Test
    fun validate_mergeAll() {
        val pairA = "success" to resultA
        val pairB = "loading" to resultB
        val pairC = "error" to resultC
        val pairD = "null" to resultD
        val pairE = "none" to resultE

        //region Single Assert
        assertEquals(
            listOf(pairA).mergeAll(),
            DataResult<Map<String, *>>(
                data = mapOf("success" to "data A"),
                error = null,
                status = SUCCESS,
            )
        )
        assertEquals(
            listOf(pairB).mergeAll(),
            DataResult<Map<String, *>>(
                data = mapOf("loading" to 123),
                error = null,
                status = LOADING,
            )
        )
        assertEquals(
            listOf(pairC).mergeAll(),
            DataResult<Map<String, *>>(
                data = null,
                error = error,
                status = ERROR,
            )
        )
        assertEquals(
            listOf(pairD).mergeAll(),
            DataResult<Map<String, *>>(
                data = null,
                error = null,
                status = NONE,
            )
        )
        assertEquals(
            listOf(pairE).mergeAll(),
            DataResult<Map<String, *>>(
                data = null,
                error = null,
                status = NONE,
            )
        )
        //endregion

        assertEquals(
            listOf(pairA, pairB).mergeAll(),
            DataResult<Map<String, *>>(
                data = mapOf(
                    "success" to "data A",
                    "loading" to 123,
                ),
                error = null,
                status = LOADING,
            )
        )
        assertEquals(
            listOf(pairA, pairB, pairC).mergeAll(),
            DataResult<Map<String, *>>(
                data = mapOf(
                    "success" to "data A",
                    "loading" to 123,
                    "error" to null,
                ),
                error = error,
                status = ERROR,
            )
        )
        assertEquals(
            listOf(pairA, pairB, pairD).mergeAll(),
            DataResult<Map<String, *>>(
                data = mapOf(
                    "success" to "data A",
                    "loading" to 123,
                    "null" to null,
                ),
                error = null,
                status = LOADING,
            )
        )
        assertEquals(
            listOf(pairA, pairB, pairE).mergeAll(),
            DataResult<Map<String, *>>(
                data = mapOf(
                    "success" to "data A",
                    "loading" to 123,
                    "none" to null
                ),
                error = null,
                status = LOADING,
            )
        )
        assertEquals(
            listOf(pairA, pairB, pairC, pairD).mergeAll(),
            DataResult<Map<String, *>>(
                data = mapOf(
                    "success" to "data A",
                    "loading" to 123,
                    "error" to null,
                    "null" to null,
                ),
                error = error,
                status = ERROR,
            )
        )
        assertEquals(
            listOf(pairA, pairB, pairC, pairE).mergeAll(),
            DataResult<Map<String, *>>(
                data = mapOf(
                    "success" to "data A",
                    "loading" to 123,
                    "error" to null,
                    "none" to null
                ),
                error = error,
                status = ERROR,
            )
        )
        assertEquals(
            listOf(pairA, pairB, pairC, pairD, pairE).mergeAll(),
            DataResult<Map<String, *>>(
                data = mapOf(
                    "success" to "data A",
                    "loading" to 123,
                    "error" to null,
                    "null" to null,
                    "none" to null
                ),
                error = error,
                status = ERROR,
            )
        )

        assertEquals(
            listOf(pairA, pairC).mergeAll(),
            DataResult<Map<String, *>>(
                data = mapOf(
                    "success" to "data A",
                    "error" to null,
                ),
                error = error,
                status = ERROR,
            )
        )
        assertEquals(
            listOf(pairA, pairC, pairB).mergeAll(),
            DataResult<Map<String, *>>(
                data = mapOf(
                    "success" to "data A",
                    "error" to null,
                    "loading" to 123,
                ),
                error = error,
                status = ERROR,
            )
        )
        assertEquals(
            listOf(pairA, pairC, pairD).mergeAll(),
            DataResult<Map<String, *>>(
                data = mapOf(
                    "success" to "data A",
                    "error" to null,
                    "null" to null,
                ),
                error = error,
                status = ERROR,
            )
        )
        assertEquals(
            listOf(pairA, pairC, pairE).mergeAll(),
            DataResult<Map<String, *>>(
                data = mapOf(
                    "success" to "data A",
                    "error" to null,
                    "none" to null
                ),
                error = error,
                status = ERROR,
            )
        )
        assertEquals(
            listOf(pairA, pairC, pairB, pairD).mergeAll(),
            DataResult<Map<String, *>>(
                data = mapOf(
                    "success" to "data A",
                    "error" to null,
                    "loading" to 123,
                    "null" to null,
                ),
                error = error,
                status = ERROR,
            )
        )
        assertEquals(
            listOf(pairA, pairC, pairB, pairE).mergeAll(),
            DataResult<Map<String, *>>(
                data = mapOf(
                    "success" to "data A",
                    "error" to null,
                    "loading" to 123,
                    "none" to null
                ),
                error = error,
                status = ERROR,
            )
        )
        assertEquals(
            listOf(pairA, pairC, pairB, pairD, pairE).mergeAll(),
            DataResult<Map<String, *>>(
                data = mapOf(
                    "success" to "data A",
                    "error" to null,
                    "loading" to 123,
                    "null" to null,
                    "none" to null
                ),
                error = error,
                status = ERROR,
            )
        )

        assertEquals(
            listOf(pairA, pairD).mergeAll(),
            DataResult<Map<String, *>>(
                data = mapOf(
                    "success" to "data A",
                    "null" to null,
                ),
                error = null,
                status = SUCCESS,
            )
        )
        assertEquals(
            listOf(pairA, pairD, pairC).mergeAll(),
            DataResult<Map<String, *>>(
                data = mapOf(
                    "success" to "data A",
                    "null" to null,
                    "error" to null,
                ),
                error = error,
                status = ERROR,
            )
        )
        assertEquals(
            listOf(pairA, pairD, pairB).mergeAll(),
            DataResult<Map<String, *>>(
                data = mapOf(
                    "success" to "data A",
                    "null" to null,
                    "loading" to 123,
                ),
                error = null,
                status = LOADING,
            )
        )
        assertEquals(
            listOf(pairA, pairD, pairE).mergeAll(),
            DataResult<Map<String, *>>(
                data = mapOf(
                    "success" to "data A",
                    "null" to null,
                    "none" to null
                ),
                error = null,
                status = SUCCESS,
            )
        )
        assertEquals(
            listOf(pairA, pairD, pairC, pairB).mergeAll(),
            DataResult<Map<String, *>>(
                data = mapOf(
                    "success" to "data A",
                    "null" to null,
                    "error" to null,
                    "loading" to 123,
                ),
                error = error,
                status = ERROR,
            )
        )
        assertEquals(
            listOf(pairA, pairD, pairC, pairE).mergeAll(),
            DataResult<Map<String, *>>(
                data = mapOf(
                    "success" to "data A",
                    "null" to null,
                    "error" to null,
                    "none" to null
                ),
                error = error,
                status = ERROR,
            )
        )
        assertEquals(
            listOf(pairA, pairD, pairC, pairB, pairE).mergeAll(),
            DataResult<Map<String, *>>(
                data = mapOf(
                    "success" to "data A",
                    "null" to null,
                    "error" to null,
                    "loading" to 123,
                    "none" to null
                ),
                error = error,
                status = ERROR,
            )
        )

        assertEquals(
            listOf(pairA, pairE).mergeAll(),
            DataResult<Map<String, *>>(
                data = mapOf(
                    "success" to "data A",
                    "none" to null
                ),
                error = null,
                status = SUCCESS,
            )
        )
        assertEquals(
            listOf(pairA, pairE, pairC).mergeAll(),
            DataResult<Map<String, *>>(
                data = mapOf(
                    "success" to "data A",
                    "none" to null,
                    "error" to null,
                ),
                error = error,
                status = ERROR,
            )
        )
        assertEquals(
            listOf(pairA, pairE, pairD).mergeAll(),
            DataResult<Map<String, *>>(
                data = mapOf(
                    "success" to "data A",
                    "none" to null,
                    "null" to null,
                ),
                error = null,
                status = SUCCESS,
            )
        )
        assertEquals(
            listOf(pairA, pairE, pairB).mergeAll(),
            DataResult<Map<String, *>>(
                data = mapOf(
                    "success" to "data A",
                    "none" to null,
                    "loading" to 123,
                ),
                error = null,
                status = LOADING,
            )
        )
        assertEquals(
            listOf(pairA, pairE, pairC, pairD).mergeAll(),
            DataResult<Map<String, *>>(
                data = mapOf(
                    "success" to "data A",
                    "none" to null,
                    "error" to null,
                    "null" to null,
                ),
                error = error,
                status = ERROR,
            )
        )
        assertEquals(
            listOf(pairA, pairE, pairC, pairB).mergeAll(),
            DataResult<Map<String, *>>(
                data = mapOf(
                    "success" to "data A",
                    "none" to null,
                    "error" to null,
                    "loading" to 123,
                ),
                error = error,
                status = ERROR,
            )
        )
        assertEquals(
            listOf(pairA, pairE, pairC, pairD, pairB).mergeAll(),
            DataResult<Map<String, *>>(
                data = mapOf(
                    "success" to "data A",
                    "none" to null,
                    "error" to null,
                    "null" to null,
                    "loading" to 123,
                ),
                error = error,
                status = ERROR,
            )
        )
    }
}
