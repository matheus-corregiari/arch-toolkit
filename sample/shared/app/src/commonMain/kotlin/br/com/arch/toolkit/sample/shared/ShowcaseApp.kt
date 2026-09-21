package br.com.arch.toolkit.sample.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import br.com.arch.toolkit.sample.design.LocalAppLanguage
import br.com.arch.toolkit.sample.github.shared.designSystem.AppTheme
import br.com.arch.toolkit.sample.github.shared.structure.repository.SettingsRepository
import br.com.arch.toolkit.sample.shared.ui.home.AppHome
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import io.ktor.client.HttpClient
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

@Composable
fun ShowcaseApp(deepLink: String? = null, onDeepLinkHandled: () -> Unit = {}) {
    val settings: SettingsRepository = koinInject()
    val language by settings.language.get().collectAsState(initial = null)
    val activeLanguage = language ?: return
    val theme by settings.themeMode.state()
    val contrast by settings.contrastMode.state()
    val client: HttpClient = koinInject(named("image-client"))
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components { add(KtorNetworkFetcherFactory(client)) }
            .build()
    }
    CompositionLocalProvider(LocalAppLanguage provides activeLanguage) {
        AppTheme(theme = theme, contrast = contrast) { AppHome(deepLink, onDeepLinkHandled) }
    }
}
