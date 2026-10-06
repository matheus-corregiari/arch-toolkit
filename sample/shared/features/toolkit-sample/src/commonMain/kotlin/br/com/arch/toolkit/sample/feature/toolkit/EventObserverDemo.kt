package br.com.arch.toolkit.sample.feature.toolkit

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.sample.design.AppText
import br.com.arch.toolkit.sample.design.component.AppButton
import br.com.arch.toolkit.sample.design.component.AppSection
import br.com.arch.toolkit.sample.design.text
import br.com.arch.toolkit.util.dataResultError
import br.com.arch.toolkit.util.dataResultLoading
import br.com.arch.toolkit.util.dataResultNone
import br.com.arch.toolkit.util.dataResultSuccess

@Composable
internal fun EventObserverDemo() {
    var result by remember { mutableStateOf<DataResult<String>>(dataResultNone()) }
    AppSection("DataResult", description = text(AppText.EVENT_OBSERVER_DESCRIPTION)) {
        AppButton(text(AppText.LOADING), { result = dataResultLoading() })
        AppButton(text(AppText.SUCCEEDED), { result = dataResultSuccess("Arch Toolkit") })
        AppButton(text(AppText.SIMULATE_FAILURE), {
            result = dataResultError(IllegalStateException("Sample failure"))
        }, style = AppButton.Style.Secondary)
        val status = when {
            result.isLoading -> AppText.LOADING
            result.isSuccess -> AppText.SUCCEEDED
            result.isError -> AppText.FAILED
            else -> AppText.READY
        }
        CodeBlock(
            text(AppText.RESULT),
            listOfNotNull(text(status), result.data, result.error?.message).joinToString("\n")
        )
        CodeBlock(
            text(AppText.SNIPPET),
            """
            dataResultLoading<String>()
            dataResultSuccess("Arch Toolkit")
            dataResultError<String>(IllegalStateException("Sample failure"))
            """.trimIndent()
        )
    }
}

@Composable
internal expect fun AndroidLibraryDemo()

@Composable
internal fun AndroidUnavailableDemo() {
    AppSection("ContextProvider") {
        Text(text(AppText.ANDROID_ONLY))
        CodeBlock(text(AppText.SNIPPET), "ContextProvider.current?.javaClass?.simpleName")
    }
}
