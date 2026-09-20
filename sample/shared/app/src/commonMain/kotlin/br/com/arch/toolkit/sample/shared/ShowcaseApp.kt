package br.com.arch.toolkit.sample.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
fun ShowcaseApp() {
    val settings: SettingsRepository = koinInject()
    val theme by settings.themeMode.state()
    val contrast by settings.contrastMode.state()
    val client: HttpClient = koinInject(named("image-client"))
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components { add(KtorNetworkFetcherFactory(client)) }
            .build()
    }
    AppTheme(theme = theme, contrast = contrast) { AppHome() }
}
