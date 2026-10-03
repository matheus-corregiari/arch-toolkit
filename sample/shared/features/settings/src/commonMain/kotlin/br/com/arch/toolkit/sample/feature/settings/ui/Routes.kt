package br.com.arch.toolkit.sample.feature.settings.ui

import androidx.compose.runtime.Composable
import com.pedrobneto.easy.navigation.core.annotation.Deeplink
import com.pedrobneto.easy.navigation.core.annotation.Route
import com.pedrobneto.easy.navigation.core.annotation.Scope
import com.pedrobneto.easy.navigation.core.model.NavigationRoute
import kotlinx.serialization.Serializable

@Serializable
data object SettingsRoute : NavigationRoute

@Route(SettingsRoute::class)
@Scope("settings")
@Deeplink("/settings")
@Composable
fun SettingsDestination() {
    SettingsScreen()
}
