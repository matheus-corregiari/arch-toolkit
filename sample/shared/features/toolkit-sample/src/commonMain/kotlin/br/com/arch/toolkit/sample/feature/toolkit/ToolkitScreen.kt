package br.com.arch.toolkit.sample.feature.toolkit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
import br.com.arch.toolkit.sample.design.AppTheme
import br.com.arch.toolkit.sample.design.component.AppButton
import br.com.arch.toolkit.sample.design.component.AppDisclosure
import br.com.arch.toolkit.sample.design.component.AppPage
import br.com.arch.toolkit.sample.design.component.AppSection
import br.com.arch.toolkit.sample.design.component.AppSectionGrid
import br.com.arch.toolkit.sample.design.component.AppTextField
import br.com.arch.toolkit.sample.design.component.containerRadiusXs
import br.com.arch.toolkit.sample.design.text
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
    gridState: LazyGridState = rememberLazyGridState(),
    actions: ToolkitActions = ToolkitActions()
) {
    AppPage(text(AppText.TOOLKIT), description = text(AppText.TOOLKIT_INTRO)) {
        AppSectionGrid(state = gridState) {
            item(key = "lumber") { LumberDemo(logs, actions) }
            item(key = "storage") { StorageDemo(state, actions) }
            item(key = "ecosystem", span = { GridItemSpan(maxLineSpan) }) {
                EcosystemCatalogue()
            }
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
        DemoCode(DemoSource.Lumber)
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
        DemoCode(DemoSource.Storage)
    }
}

@Composable
fun DemoCode(source: DemoSource, initiallyExpanded: Boolean = false) {
    val code = when (source) {
        DemoSource.Lumber -> DemoSnippets.lumber
        DemoSource.Storage -> DemoSnippets.storage
    }
    AppDisclosure(text(AppText.SHOW_CODE), text(AppText.HIDE_CODE), initiallyExpanded) {
        CodeBlock(text(AppText.SNIPPET), code)
    }
}

@Composable
private fun CodeBlock(title: String, code: String) {
    Text(title, style = AppTheme.textStyle.paragraphCaptionS, color = AppTheme.color.textSubtitle)
    Text(
        code,
        modifier = Modifier.fillMaxWidth().containerRadiusXs().padding(AppTheme.dimen.spacingS),
        fontFamily = FontFamily.Monospace,
        style = AppTheme.textStyle.code
    )
}

@Composable
private fun EcosystemCatalogue() {
    val uriHandler = LocalUriHandler.current
    Column(verticalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingM)) {
        Text(text(AppText.ECOSYSTEM), style = AppTheme.textStyle.subtitleXBold)
        ecosystemItems.forEach { item ->
            AppSection(item.title, description = text(item.description)) {
                Text(
                    item.platforms,
                    style = AppTheme.textStyle.paragraphCaptionS,
                    color = AppTheme.color.textSubtitle
                )
                AppButton(
                    text(AppText.DOCUMENTATION),
                    onClick = {
                        uriHandler.openUri(
                            "https://github.com/matheus-corregiari/${item.repository}"
                        )
                    },
                    style = AppButton.Style.Link,
                    size = AppButton.Size.Small
                )
            }
        }
    }
}

private data class EcosystemItem(
    val title: String,
    val repository: String,
    val platforms: String,
    val description: AppText
)

private val ecosystemItems = listOf(
    EcosystemItem("Arch Android", "arch-android", "Android", AppText.ARCH_ANDROID_DESCRIPTION),
    EcosystemItem(
        "Event Observer",
        "arch-event-observer",
        "Android, iOS, JVM, JS, Wasm",
        AppText.EVENT_OBSERVER_DESCRIPTION
    ),
    EcosystemItem(
        "Splinter",
        "arch-toolkit",
        "Android, iOS, JVM, JS, Wasm",
        AppText.SPLINTER_DESCRIPTION
    )
)

enum class DemoSource { Lumber, Storage }
