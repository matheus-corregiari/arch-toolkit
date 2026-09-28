package br.com.arch.toolkit.sample.feature.toolkit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
    var key by rememberSaveable { mutableStateOf("hello") }
    var value by rememberSaveable { mutableStateOf("Arch Toolkit") }
    LazyColumn(
        Modifier.fillMaxSize().padding(AppTheme.dimen.spacingM),
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingM),
        state = listState
    ) {
        item {
            Text("Lumber", style = AppTheme.textStyle.titleXLRegular)
            Text(text(AppText.LUMBER_DESCRIPTION))
            Row {
                AppButton(text(AppText.LOG), actions.writeLog, modifier = Modifier.weight(1f))
                TextButton(onClick = actions.clearLogs) { Text(text(AppText.CLEAR)) }
            }
            Text(logs.joinToString("\n"), fontFamily = FontFamily.Monospace)
            Text(DemoSnippets.lumber, fontFamily = FontFamily.Monospace)
        }
        item {
            Text("Storage", style = AppTheme.textStyle.titleXLRegular)
            Text(text(AppText.STORAGE_DESCRIPTION))
            OutlinedTextField(
                key,
                { key = it },
                label = { Text(text(AppText.KEY)) },
                singleLine = true
            )
            OutlinedTextField(value, { value = it }, label = { Text(text(AppText.VALUE)) })
            Row {
                TextButton(
                    onClick = { actions.save(key, value) },
                    enabled = !state.busy
                ) { Text(text(AppText.SAVE)) }
                TextButton(
                    onClick = { actions.read(key) },
                    enabled = !state.busy
                ) { Text(text(AppText.READ)) }
                TextButton(
                    onClick = { actions.delete(key) },
                    enabled = !state.busy
                ) { Text(text(AppText.DELETE)) }
            }
            state.message?.let { Text(text(it)) }
            Text(state.value ?: text(AppText.MISSING_VALUE))
            Text(DemoSnippets.storage, fontFamily = FontFamily.Monospace)
        }
        item { EcosystemCatalogue() }
    }
}

@Composable
private fun EcosystemCatalogue() {
    val uriHandler = LocalUriHandler.current
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
        Column {
            Text(title)
            Text(text(description))
            TextButton(
                onClick = {
                    uriHandler.openUri(
                        "https://github.com/matheus-corregiari/$repo"
                    )
                }
            ) {
                Text(text(AppText.DOCUMENTATION))
            }
        }
    }
}
