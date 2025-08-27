@file:Suppress("MatchingDeclarationName")

package br.com.arch.toolkit.sample.shared

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import arch_toolkit.sample.shared.app.generated.resources.Res
import arch_toolkit.sample.shared.app.generated.resources.sample_main_description
import arch_toolkit.sample.shared.app.generated.resources.sample_main_title
import br.com.arch.toolkit.sample.github.shared.structure.core.ComposeContent
import br.com.arch.toolkit.sample.github.shared.structure.core.FeatureIcon
import br.com.arch.toolkit.sample.github.shared.structure.core.FeatureRegistry
import br.com.arch.toolkit.sample.shared.ui.home.AppHome

internal fun mainRegistry(): List<FeatureRegistry> = listOf(
    FeatureRegistry(
        id = "main",
        version = 1,
        title = Res.string.sample_main_title,
        description = Res.string.sample_main_description,
        icon = FeatureIcon(
            selected = Icons.Filled.Home,
            unselected = Icons.Outlined.Home
        ),
        content = listOf(ComposeContent { AppHome() })
    )
)
