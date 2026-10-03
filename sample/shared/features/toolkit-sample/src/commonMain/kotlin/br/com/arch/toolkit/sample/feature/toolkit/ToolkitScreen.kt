package br.com.arch.toolkit.sample.feature.toolkit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.sample.design.AppText
import br.com.arch.toolkit.sample.design.AppTheme
import br.com.arch.toolkit.sample.design.component.AppButton
import br.com.arch.toolkit.sample.design.component.AppDisclosure
import br.com.arch.toolkit.sample.design.component.AppPage
import br.com.arch.toolkit.sample.design.component.AppSection
import br.com.arch.toolkit.sample.design.component.AppSectionGrid
import br.com.arch.toolkit.sample.design.component.AppTextField
import br.com.arch.toolkit.sample.design.component.GithubMark
import br.com.arch.toolkit.sample.design.component.containerRadiusXs
import br.com.arch.toolkit.sample.design.text
import br.com.arch.toolkit.util.dataResultNone
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
    val request by model.splinter.request.collectAsState()
    val polling by model.splinter.polling.collectAsState()
    ToolkitContent(
        logs = logs,
        state = state,
        request = request,
        polling = polling,
        actions = ToolkitActions(
            model::writeLog,
            model::clearLogs,
            model::save,
            model::read,
            model::delete,
            model.splinter::load,
            model.splinter::poll,
            model.splinter::cancelRequest,
            model.splinter::cancelPolling
        )
    )
}

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
fun ToolkitContent(
    logs: List<String>,
    state: StorageDemoState,
    gridState: LazyGridState = rememberLazyGridState(),
    actions: ToolkitActions = ToolkitActions(),
    onOpenRepository: (String) -> Unit = LocalUriHandler.current::openUri,
    request: DataResult<String> = dataResultNone(),
    polling: DataResult<String> = dataResultNone()
) {
    AppPage(text(AppText.TOOLKIT), description = text(AppText.TOOLKIT_INTRO)) {
        AppSectionGrid(state = gridState) {
            item(key = "lumber") { LumberDemo(logs, actions) }
            item(key = "storage") { StorageDemo(state, actions) }
            item(key = "splinter-request") { SplinterRequestDemo(request, actions) }
            item(key = "splinter-polling") { SplinterPollingDemo(polling, actions) }
            ecosystemCatalogue(onOpenRepository)
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

/** The same repository cards used in Toolkit, isolated for previews and interaction checks. */
@Composable
fun EcosystemContent(onOpenRepository: (String) -> Unit = LocalUriHandler.current::openUri) {
    AppPage(text(AppText.ECOSYSTEM), description = text(AppText.ECOSYSTEM_INTRO)) {
        AppSectionGrid {
            ecosystemRepositoryCards(onOpenRepository)
        }
    }
}

private fun LazyGridScope.ecosystemCatalogue(onOpenRepository: (String) -> Unit) {
    item(key = "ecosystem", span = { GridItemSpan(maxLineSpan) }) {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingXs)) {
            Text(text(AppText.ECOSYSTEM), style = AppTheme.textStyle.sectionHeading)
            Text(text(AppText.ECOSYSTEM_INTRO), style = AppTheme.textStyle.body)
        }
    }
    ecosystemRepositoryCards(onOpenRepository)
}

private fun LazyGridScope.ecosystemRepositoryCards(onOpenRepository: (String) -> Unit) {
    items(ecosystemItems, key = { it.repository }) { item ->
        EcosystemCard(item, onOpenRepository)
    }
}

@Composable
private fun EcosystemCard(item: EcosystemItem, onOpenRepository: (String) -> Unit) {
    val label = text(AppText.OPEN_GITHUB)
    AppSection(item.title, description = text(item.description)) {
        Text(text(item.usage), style = AppTheme.textStyle.body)
        Text(
            "matheus-corregiari/${item.repository}",
            style = AppTheme.textStyle.metadata,
            color = AppTheme.color.textSubtitle
        )
        AppButton(
            label,
            onClick = {
                onOpenRepository(
                    "https://github.com/matheus-corregiari/${item.repository}"
                )
            },
            modifier = Modifier.semantics { contentDescription = "$label: ${item.title}" },
            style = AppButton.Style.Secondary,
            size = AppButton.Size.Small,
            leadingIcon = GithubMark
        )
    }
}

private data class EcosystemItem(
    val title: String,
    val repository: String,
    val description: AppText,
    val usage: AppText
)

private val ecosystemItems = listOf(
    EcosystemItem(
        "Arch Toolkit",
        "arch-toolkit",
        AppText.TOOLKIT_REPOSITORY_DESCRIPTION,
        AppText.TOOLKIT_REPOSITORY_USAGE
    ),
    EcosystemItem(
        "Arch Android",
        "arch-android",
        AppText.ANDROID_REPOSITORY_DESCRIPTION,
        AppText.ANDROID_REPOSITORY_USAGE
    ),
    EcosystemItem(
        "Event Observer",
        "arch-event-observer",
        AppText.OBSERVER_REPOSITORY_DESCRIPTION,
        AppText.OBSERVER_REPOSITORY_USAGE
    ),
    EcosystemItem(
        "Lumber",
        "arch-lumber",
        AppText.LUMBER_REPOSITORY_DESCRIPTION,
        AppText.LUMBER_REPOSITORY_USAGE
    ),
    EcosystemItem(
        "Storage",
        "arch-storage",
        AppText.STORAGE_REPOSITORY_DESCRIPTION,
        AppText.STORAGE_REPOSITORY_USAGE
    )
)

enum class DemoSource { Lumber, Storage, OneShot, Polling }
