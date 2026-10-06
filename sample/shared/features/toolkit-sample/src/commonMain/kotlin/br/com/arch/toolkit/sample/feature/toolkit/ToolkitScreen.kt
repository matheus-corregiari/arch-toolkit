package br.com.arch.toolkit.sample.feature.toolkit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalUriHandler
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.sample.design.AppText
import br.com.arch.toolkit.sample.design.AppTheme
import br.com.arch.toolkit.sample.design.component.AppButton
import br.com.arch.toolkit.sample.design.component.AppPage
import br.com.arch.toolkit.sample.design.component.GithubMark
import br.com.arch.toolkit.sample.design.text
import br.com.arch.toolkit.util.dataResultNone
import com.pedrobneto.easy.navigation.core.LocalNavigationController
import com.pedrobneto.easy.navigation.core.annotation.Deeplink
import com.pedrobneto.easy.navigation.core.annotation.ParentRoute
import com.pedrobneto.easy.navigation.core.annotation.Route
import com.pedrobneto.easy.navigation.core.annotation.Scope
import com.pedrobneto.easy.navigation.core.model.LaunchStrategy
import com.pedrobneto.easy.navigation.core.model.NavigationRoute
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object ToolkitRoute : NavigationRoute

@Serializable
data class ToolkitSampleRoute(val library: ToolkitLibrary) : NavigationRoute

@Route(ToolkitRoute::class)
@Scope("toolkit")
@Deeplink("/toolkit")
@Composable
fun ToolkitDestination() {
    val navigation = LocalNavigationController.current
    EcosystemContent(onOpenSample = { navigation.navigateTo(ToolkitSampleRoute(it)) })
}

@Route(ToolkitSampleRoute::class)
@Scope("toolkit")
@Deeplink("/toolkit/{library}")
@ParentRoute(ToolkitRoute::class)
@Composable
fun ToolkitSampleDestination(route: ToolkitSampleRoute) {
    val navigation = LocalNavigationController.current
    val back = {
        if (!navigation.safePopUpTo(ToolkitRoute)) {
            navigation.navigateTo(ToolkitRoute, LaunchStrategy.NewStack)
        }
        Unit
    }
    if (route.library == ToolkitLibrary.ANDROID || route.library == ToolkitLibrary.OBSERVER) {
        ToolkitContent(emptyList(), StorageDemoState(), library = route.library, onBack = back)
    } else {
        ToolkitScreen(koinViewModel(), route.library, back)
    }
}

@Composable
fun ToolkitScreen(
    model: ToolkitViewModel,
    library: ToolkitLibrary = ToolkitLibrary.LUMBER,
    onBack: () -> Unit = {}
) {
    val logs by model.logs.collectAsState()
    val state by model.storage.collectAsState()
    val request by model.splinter.request.collectAsState()
    val polling by model.splinter.polling.collectAsState()
    ToolkitContent(
        logs,
        state,
        library = library,
        onBack = onBack,
        request = request,
        polling = polling,
        actions = ToolkitActions(
            model::writeLog, model::clearLogs, model::save, model::read, model::delete,
            model.splinter::load, model.splinter::poll,
            model.splinter::cancelRequest, model.splinter::cancelPolling
        )
    )
}

/** A fixed toolbar above one scroll owner for the actions, result and expanded source. */
@Composable
fun ToolkitContent(
    logs: List<String>,
    state: StorageDemoState,
    actions: ToolkitActions = ToolkitActions(),
    onOpenRepository: (String) -> Unit = LocalUriHandler.current::openUri,
    request: DataResult<String> = dataResultNone(),
    polling: DataResult<String> = dataResultNone(),
    library: ToolkitLibrary = ToolkitLibrary.LUMBER,
    onBack: () -> Unit = {}
) {
    AppPage(
        library.title,
        maxWidth = AppTheme.dimen.readingMaxWidth,
        description = text(library.description),
        scrollable = true,
        onBack = onBack,
        backLabel = text(AppText.BACK_TO_TOOLKIT)
    ) {
        when (library) {
            ToolkitLibrary.LUMBER -> LumberDemo(logs, actions)
            ToolkitLibrary.STORAGE -> StorageDemo(state, actions)
            ToolkitLibrary.SPLINTER -> {
                SplinterRequestDemo(request, actions)
                SplinterPollingDemo(polling, actions)
            }
            ToolkitLibrary.OBSERVER -> EventObserverDemo()
            ToolkitLibrary.ANDROID -> AndroidLibraryDemo()
        }
        AppButton(
            text(AppText.OPEN_GITHUB),
            { onOpenRepository(library.githubUrl) },
            style = AppButton.Style.Secondary,
            leadingIcon = GithubMark
        )
    }
}
