package br.com.arch.toolkit.sample.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import br.com.arch.toolkit.sample.design.AppTheme
import br.com.arch.toolkit.sample.design.LocalAppLanguage
import br.com.arch.toolkit.sample.repository.SettingsRepository
import br.com.arch.toolkit.sample.shared.ui.home.AppHome
import org.koin.compose.koinInject

@Composable
fun ShowcaseApp(deepLink: String? = null, onDeepLinkHandled: () -> Unit = {}) {
    val settings: SettingsRepository = koinInject()
    val language by settings.language.get().collectAsState(initial = null)
    val activeLanguage = language ?: return
    val theme by settings.themeMode.state()
    val contrast by settings.contrastMode.state()
    CompositionLocalProvider(LocalAppLanguage provides activeLanguage) {
        AppTheme(theme = theme, contrast = contrast) { AppHome(deepLink, onDeepLinkHandled) }
    }
}
