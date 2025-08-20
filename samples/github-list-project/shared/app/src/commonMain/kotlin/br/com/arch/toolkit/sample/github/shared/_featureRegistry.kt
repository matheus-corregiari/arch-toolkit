@file:Suppress("MatchingDeclarationName")

package br.com.arch.toolkit.sample.github.shared

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Settings
import arch_toolkit.samples.github_list_project.shared.app.generated.resources.Res
import arch_toolkit.samples.github_list_project.shared.app.generated.resources.sample_github_list_description
import arch_toolkit.samples.github_list_project.shared.app.generated.resources.sample_github_list_title
import arch_toolkit.samples.github_list_project.shared.app.generated.resources.sample_settings_description
import arch_toolkit.samples.github_list_project.shared.app.generated.resources.sample_settings_title
import br.com.arch.toolkit.sample.github.shared.structure.core.ComposeContent
import br.com.arch.toolkit.sample.github.shared.structure.core.FeatureIcon
import br.com.arch.toolkit.sample.github.shared.structure.core.FeatureRegistry
import br.com.arch.toolkit.sample.github.shared.ui.list.RepositoryListScreen
import br.com.arch.toolkit.sample.github.shared.ui.settings.SettingsScreen

internal fun repositoryList(): List<FeatureRegistry> = listOf(
    FeatureRegistry(
        id = "sample_github_list",
        version = 1,
        title = Res.string.sample_github_list_title,
        description = Res.string.sample_github_list_description,
        icon = FeatureIcon(
            selected = Icons.Filled.Code,
            unselected = Icons.Outlined.Code
        ),
        content = listOf(ComposeContent { RepositoryListScreen() })
    )
)

internal fun settings(): List<FeatureRegistry> = listOf(
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
