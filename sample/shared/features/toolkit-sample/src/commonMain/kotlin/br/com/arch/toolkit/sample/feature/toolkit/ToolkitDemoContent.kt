package br.com.arch.toolkit.sample.feature.toolkit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import br.com.arch.toolkit.sample.design.AppText
import br.com.arch.toolkit.sample.design.AppTheme
import br.com.arch.toolkit.sample.design.component.AppButton
import br.com.arch.toolkit.sample.design.component.AppDisclosure
import br.com.arch.toolkit.sample.design.component.AppSection
import br.com.arch.toolkit.sample.design.component.AppTextField
import br.com.arch.toolkit.sample.design.component.containerRadiusXs
import br.com.arch.toolkit.sample.design.text

data class ToolkitActions(
    val writeLog: () -> Unit = {},
    val clearLogs: () -> Unit = {},
    val save: (String, String) -> Unit = { _, _ -> },
    val read: (String) -> Unit = {},
    val delete: (String) -> Unit = {},
    val load: (Boolean) -> Unit = {},
    val poll: () -> Unit = {},
    val cancelRequest: () -> Unit = {},
    val cancelPolling: () -> Unit = {}
)

@Composable
internal fun LumberDemo(logs: List<String>, actions: ToolkitActions) {
    AppSection("Lumber", description = text(AppText.LUMBER_DESCRIPTION)) {
        Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingXs)) {
            AppButton(
                text(AppText.LOG),
                actions.writeLog,
                modifier = Modifier.weight(1f)
            )
            AppButton(
                text(AppText.CLEAR),
                actions.clearLogs,
                modifier = Modifier.weight(1f),
                style = AppButton.Style.Secondary
            )
        }
        CodeBlock(
            text(AppText.RESULT),
            logs.joinToString("\n").ifEmpty { text(AppText.EMPTY_LOGS) }
        )
        DemoCode(DemoSource.Lumber)
    }
}

@Composable
internal fun StorageDemo(state: StorageDemoState, actions: ToolkitActions) {
    var key by rememberSaveable { mutableStateOf("hello") }
    var value by rememberSaveable { mutableStateOf("Arch Toolkit") }
    AppSection("Storage", description = text(AppText.STORAGE_DESCRIPTION)) {
        AppTextField(key, { key = it }, text(AppText.KEY), singleLine = true)
        AppTextField(value, { value = it }, text(AppText.VALUE))
        Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingXs)) {
            AppButton(
                text(AppText.SAVE),
                { actions.save(key, value) },
                modifier = Modifier.weight(1f),
                size = AppButton.Size.Small,
                enabled = !state.busy
            )
            AppButton(
                text(AppText.READ),
                { actions.read(key) },
                modifier = Modifier.weight(1f),
                style = AppButton.Style.Secondary,
                size = AppButton.Size.Small,
                enabled = !state.busy
            )
        }
        AppButton(
            text(AppText.DELETE),
            { actions.delete(key) },
            modifier = Modifier.fillMaxWidth(),
            style = AppButton.Style.Destructive,
            size = AppButton.Size.Small,
            enabled = !state.busy
        )
        state.message?.let { Text(text(it), color = AppTheme.color.textSubtitle) }
        CodeBlock(text(AppText.RESULT), state.value ?: text(AppText.MISSING_VALUE))
        DemoCode(DemoSource.Storage)
    }
}

@Composable
fun DemoCode(source: DemoSource, initiallyExpanded: Boolean = false) {
    val code = when (source) {
        DemoSource.Lumber -> DemoSnippets.lumber
        DemoSource.Storage -> DemoSnippets.storage
        DemoSource.OneShot -> DemoSnippets.oneShot
        DemoSource.Polling -> DemoSnippets.polling
    }
    AppDisclosure(text(AppText.SHOW_CODE), text(AppText.HIDE_CODE), initiallyExpanded) {
        CodeBlock(text(AppText.SNIPPET), code)
    }
}

@Composable
internal fun CodeBlock(title: String, code: String) {
    Text(title, style = AppTheme.textStyle.paragraphCaptionS, color = AppTheme.color.textSubtitle)
    Text(
        code,
        modifier = Modifier.fillMaxWidth().containerRadiusXs().padding(AppTheme.dimen.spacingS),
        fontFamily = FontFamily.Monospace,
        style = AppTheme.textStyle.code
    )
}

enum class DemoSource { Lumber, Storage, OneShot, Polling }
