@file:Suppress("FunctionNaming")
@file:OptIn(androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi::class)

package br.com.arch.toolkit.sample.shared.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRailItemColors
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import br.com.arch.toolkit.sample.github.shared.designSystem.AppTheme
import br.com.arch.toolkit.sample.github.shared.structure.core.FeatureRegistry
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppHome(
    viewModel: HomeViewModel = koinViewModel()
) {
    val itemModifier = Modifier.padding(horizontal = AppTheme.dimen.spacingXs)
    val itemColors = NavigationSuiteItemColors(
        navigationBarItemColors = NavigationBarItemColors(
            selectedIconColor = AppTheme.color.iconPrimary,
            selectedTextColor = AppTheme.color.textTitle,
            selectedIndicatorColor = AppTheme.color.fillSecondary,
            unselectedIconColor = AppTheme.color.iconSecondary,
            unselectedTextColor = AppTheme.color.textParagraph,
            disabledIconColor = AppTheme.color.iconDisabled,
            disabledTextColor = AppTheme.color.textDisabled
        ),
        navigationRailItemColors = NavigationRailItemColors(
            selectedIconColor = AppTheme.color.iconPrimary,
            selectedTextColor = AppTheme.color.textTitle,
            selectedIndicatorColor = AppTheme.color.backgroundSurfaceTertiary,
            unselectedIconColor = AppTheme.color.iconSecondary,
            unselectedTextColor = AppTheme.color.textParagraph,
            disabledIconColor = AppTheme.color.iconDisabled,
            disabledTextColor = AppTheme.color.textDisabled
        ),
        navigationDrawerItemColors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = AppTheme.color.backgroundSurfaceTertiary,
            unselectedContainerColor = Color.Unspecified,
            selectedIconColor = AppTheme.color.iconPrimary,
            unselectedIconColor = AppTheme.color.iconSecondary,
            selectedTextColor = AppTheme.color.textTitle,
            unselectedTextColor = AppTheme.color.textParagraph,
            selectedBadgeColor = AppTheme.color.backgroundBrandPrimary,
            unselectedBadgeColor = AppTheme.color.backgroundSurfaceTertiaryDisabled
        )
    )

    val registries = androidx.compose.runtime.remember {
        listOf(
            com.pedrobneto.easy.navigation.registry.GithubDirectionRegistry,
            com.pedrobneto.easy.navigation.registry.SettingsDirectionRegistry
        )
    }
    val navigation = com.pedrobneto.easy.navigation.core.rememberNavigationController(
        initialRoute = br.com.arch.toolkit.sample.feature.githubSample.ui.GithubRoute,
        directionRegistries = registries
    )
    val items by viewModel.featureFlow.collectAsState()
    val selectedItem = viewModel.itemById(
        if (navigation.currentRoute is br.com.arch.toolkit.sample.feature.settings.ui.SettingsRoute) "settings" else "repository-list-home"
    )
    NavigationSuiteScaffold(
        layoutType = AppTheme.screen.navigationSuiteType,
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContainerColor = AppTheme.color.backgroundSurfaceTertiary,
            navigationBarContentColor = AppTheme.color.backgroundSurfaceDefault,
            navigationRailContainerColor = AppTheme.color.backgroundSurfaceSecondary,
            navigationRailContentColor = AppTheme.color.backgroundSurfaceDefault,
            navigationDrawerContainerColor = AppTheme.color.backgroundSurfaceSecondary,
            navigationDrawerContentColor = AppTheme.color.backgroundSurfaceDefault
        ),
        containerColor = AppTheme.color.backgroundSurfaceDefault,
        contentColor = AppTheme.color.backgroundSurfaceSecondary,
        navigationSuiteItems = {
            addItems(itemModifier, selectedItem, items, itemColors) { item ->
                val route = if (item.id == "settings") {
                    br.com.arch.toolkit.sample.feature.settings.ui.SettingsRoute
                } else {
                    br.com.arch.toolkit.sample.feature.githubSample.ui.GithubRoute
                }
                navigation.navigateTo(route, com.pedrobneto.easy.navigation.core.model.LaunchStrategy.NewStack)
            }
        },
        content = {
            com.pedrobneto.easy.navigation.core.Navigation(
                modifier = Modifier,
                initialRoute = br.com.arch.toolkit.sample.feature.githubSample.ui.GithubRoute,
                directionRegistries = registries,
                controller = navigation
            )
        }
    )
}

@Suppress("LongParameterList")
private fun NavigationSuiteScope.addItems(
    modifier: Modifier,
    selected: FeatureRegistry,
    items: List<FeatureRegistry>,
    colors: NavigationSuiteItemColors,
    onMenuSelected: (FeatureRegistry) -> Unit
) = items.forEachIndexed { index, option ->
    item(
        colors = colors,
        modifier = modifier,
        selected = selected.id == option.id,
        onClick = { onMenuSelected(option) },
        label = {
            Text(
                text = stringResource(option.title),
                textAlign = TextAlign.Center,
                style = AppTheme.textStyle.paragraphCaptionS
            )
        },
        icon = {
            Icon(
                imageVector = option.icon.getIcon(selected.id == option.id),
                contentDescription = stringResource(option.title)
            )
        },
        alwaysShowLabel = true
    )
}
