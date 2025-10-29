@file:Suppress("LongMethod", "LargeClass")

package br.com.arch.toolkit.result

import br.com.arch.toolkit.util.dataResultNone
import br.com.arch.toolkit.util.dataResultSuccess
import br.com.arch.toolkit.util.orNone
import kotlin.test.Test
import kotlin.test.assertEquals

class DataResultOrNoneTest {

    @Test
    fun validate_orNone() {
        val resultNull: DataResult<String>? = null
        assertEquals(dataResultNone(), resultNull.orNone())
        val result: DataResult<String> = dataResultSuccess("A")
        assertEquals(result, result.orNone())
    }
}
