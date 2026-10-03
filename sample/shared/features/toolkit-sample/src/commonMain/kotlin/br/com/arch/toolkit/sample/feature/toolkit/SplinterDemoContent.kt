package br.com.arch.toolkit.sample.feature.toolkit

import androidx.compose.runtime.Composable
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.sample.design.AppText
import br.com.arch.toolkit.sample.design.component.AppButton
import br.com.arch.toolkit.sample.design.component.AppSection
import br.com.arch.toolkit.sample.design.text

@Composable
internal fun SplinterRequestDemo(result: DataResult<String>, actions: ToolkitActions) {
    AppSection("Splinter · OneShot", description = text(AppText.SPLINTER_ONESHOT)) {
        AppButton(text(AppText.RUN_TASK), { actions.load(false) }, enabled = !result.isLoading)
        AppButton(
            text(AppText.SIMULATE_FAILURE),
            { actions.load(true) },
            style = AppButton.Style.Secondary,
            enabled = !result.isLoading
        )
        AppButton(
            text(AppText.CANCEL_TASK),
            actions.cancelRequest,
            style = AppButton.Style.Secondary,
            enabled = result.isLoading
        )
        SplinterResult(result)
        DemoCode(DemoSource.OneShot)
    }
}

@Composable
internal fun SplinterPollingDemo(result: DataResult<String>, actions: ToolkitActions) {
    AppSection("Splinter · Polling", description = text(AppText.SPLINTER_POLLING)) {
        AppButton(text(AppText.START_POLLING), actions.poll, enabled = !result.isLoading)
        AppButton(
            text(AppText.CANCEL_TASK),
            actions.cancelPolling,
            style = AppButton.Style.Secondary,
            enabled = result.isLoading
        )
        SplinterResult(result)
        DemoCode(DemoSource.Polling)
    }
}

@Composable
private fun SplinterResult(result: DataResult<String>) {
    val status = when {
        result.isLoading -> AppText.LOADING
        result.isSuccess -> AppText.SUCCEEDED
        result.isError -> AppText.FAILED
        else -> AppText.READY
    }
    CodeBlock(text(AppText.RESULT), listOfNotNull(text(status), result.data).joinToString("\n"))
}
