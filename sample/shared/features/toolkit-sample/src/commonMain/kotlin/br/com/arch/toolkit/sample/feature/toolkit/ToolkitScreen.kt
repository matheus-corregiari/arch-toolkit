package br.com.arch.toolkit.sample.feature.toolkit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontFamily
import br.com.arch.toolkit.sample.design.AppText
import br.com.arch.toolkit.sample.design.text
import br.com.arch.toolkit.sample.github.shared.designSystem.AppTheme
import br.com.arch.toolkit.sample.github.shared.designSystem.component.AppButton
import br.com.arch.toolkit.sample.github.shared.designSystem.component.AppPage
import br.com.arch.toolkit.sample.github.shared.designSystem.component.AppSection
import br.com.arch.toolkit.sample.github.shared.designSystem.component.AppTextField
import br.com.arch.toolkit.sample.github.shared.designSystem.component.containerRadiusXs
import com.pedrobneto.easy.navigation.core.annotation.Deeplink
import com.pedrobneto.easy.navigation.core.annotation.Route
import com.pedrobneto.easy.navigation.core.annotation.Scope
import com.pedrobneto.easy.navigation.core.model.NavigationRoute
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object ToolkitRoute : NavigationRoute

@Route(ToolkitRoute::class)
@Scope("toolkit")
@Deeplink("/toolkit")
@Composable
fun ToolkitDestination() {
    val model: ToolkitViewModel = koinViewModel()
    ToolkitScreen(model)
}

@Composable
fun ToolkitScreen(model: ToolkitViewModel) {
    val logs by model.logs.collectAsState()
    val state by model.storage.collectAsState()
    ToolkitContent(
        logs = logs,
        state = state,
        actions = ToolkitActions(
            model::writeLog,
            model::clearLogs,
            model::save,
            model::read,
            model::delete
        )
    )
}

data class ToolkitActions(
    val writeLog: () -> Unit = {},
    val clearLogs: () -> Unit = {},
    val save: (String, String) -> Unit = { _, _ -> },
    val read: (String) -> Unit = {},
    val delete: (String) -> Unit = {}
)

@Composable
fun ToolkitContent(
    logs: List<String>,
    state: StorageDemoState,
    listState: LazyListState = rememberLazyListState(),
    actions: ToolkitActions = ToolkitActions()
) {
    AppPage(text(AppText.TOOLKIT)) {
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(AppTheme.dimen.spacingM),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingM),
            state = listState
        ) {
            item { LumberDemo(logs, actions) }
            item { StorageDemo(state, actions) }
            item { EcosystemCatalogue() }
        }
    }
}

@Composable
private fun LumberDemo(logs: List<String>, actions: ToolkitActions) {
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
        CodeBlock(text(AppText.SNIPPET), DemoSnippets.lumber)
    }
}

@Composable
private fun StorageDemo(state: StorageDemoState, actions: ToolkitActions) {
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
        CodeBlock(text(AppText.SNIPPET), DemoSnippets.storage)
    }
}

@Composable
private fun CodeBlock(title: String, code: String) {
    Text(title, style = AppTheme.textStyle.paragraphCaptionS, color = AppTheme.color.textSubtitle)
    Text(
        code,
        modifier = Modifier.fillMaxWidth().containerRadiusXs().padding(AppTheme.dimen.spacingS),
        fontFamily = FontFamily.Monospace,
        style = AppTheme.textStyle.paragraphCaptionS
    )
}

@Composable
private fun EcosystemCatalogue() {
    val uriHandler = LocalUriHandler.current
    Column(verticalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingM)) {
        Text(text(AppText.ECOSYSTEM), style = AppTheme.textStyle.subtitleXBold)
        listOf(
            Triple("Arch Android / Android", "arch-android", AppText.ARCH_ANDROID_DESCRIPTION),
            Triple(
                "Event Observer / Android, iOS, JVM, JS, Wasm",
                "arch-event-observer",
                AppText.EVENT_OBSERVER_DESCRIPTION
            ),
            Triple(
                "Splinter / Android, iOS, JVM, JS, Wasm",
                "arch-toolkit",
                AppText.SPLINTER_DESCRIPTION
            )
        ).forEach { (title, repo, description) ->
            AppSection(title.substringBefore(" / "), description = text(description)) {
                Text(
                    title.substringAfter(" / "),
                    style = AppTheme.textStyle.paragraphCaptionS,
                    color = AppTheme.color.textSubtitle
                )
                AppButton(
                    text(AppText.DOCUMENTATION),
                    onClick = {
                        uriHandler.openUri(
                            "https://github.com/matheus-corregiari/$repo"
                        )
                    },
                    style = AppButton.Style.Link,
                    size = AppButton.Size.Small
                )
            }
        }
    }
}
