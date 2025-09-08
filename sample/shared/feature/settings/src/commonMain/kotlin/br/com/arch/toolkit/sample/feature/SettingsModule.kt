@file:Suppress("MatchingDeclarationName")

package br.com.arch.toolkit.sample.feature

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Settings
import arch_toolkit.sample.shared.feature.settings.generated.resources.Res
import arch_toolkit.sample.shared.feature.settings.generated.resources.sample_settings_description
import arch_toolkit.sample.shared.feature.settings.generated.resources.sample_settings_title
import br.com.arch.toolkit.sample.feature.settings.ui.SettingsScreen
import br.com.arch.toolkit.sample.feature.settings.ui.SettingsViewModel
import br.com.arch.toolkit.sample.github.shared.structure.core.ComposeContent
import br.com.arch.toolkit.sample.github.shared.structure.core.FeatureIcon
import br.com.arch.toolkit.sample.github.shared.structure.core.FeatureRegistry
import br.com.arch.toolkit.sample.github.shared.structure.core.featureRegistry
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

object SettingsModule {
    val module = module {
        // ViewModels
        viewModelOf(::SettingsViewModel)

        // Features
        featureRegistry("settings-home") { settings() }
    }

    private fun settings(): List<FeatureRegistry> = listOf(
        FeatureRegistry(
            id = "sample_settings_title",
            version = 1,
            title = Res.string.sample_settings_title,
            description = Res.string.sample_settings_description,
            icon = FeatureIcon(
                selected = Icons.Filled.Settings,
                unselected = Icons.Outlined.Settings
            ),
            content = listOf(ComposeContent { SettingsScreen() })
        )
    )
}

