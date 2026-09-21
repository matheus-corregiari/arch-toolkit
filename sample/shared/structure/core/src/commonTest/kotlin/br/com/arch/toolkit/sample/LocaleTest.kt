package br.com.arch.toolkit.sample

import br.com.arch.toolkit.sample.github.shared.structure.core.extension.localized
import br.com.arch.toolkit.sample.github.shared.structure.core.model.AppLanguage
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class LocaleTest {
    @Test
    fun formatsBySelectedLanguage() {
        assertEquals("1.234.567", 1234567L.localized(AppLanguage.PORTUGUESE_BRAZIL))
        assertEquals("-1,234", (-1234L).localized(AppLanguage.ENGLISH))
        val date = LocalDateTime(2026, 9, 20, 8, 5)
        assertEquals("20/09/2026 08:05", date.localized(AppLanguage.PORTUGUESE_BRAZIL))
        assertEquals("09/20/2026 08:05", date.localized(AppLanguage.ENGLISH))
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromTag("unknown"))
        assertEquals(AppLanguage.PORTUGUESE_BRAZIL, AppLanguage.fromTag("pt-BR"))
    }
}
