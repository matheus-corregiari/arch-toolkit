@file:Suppress("FunctionNaming")
@file:OptIn(androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi::class)

package br.com.arch.toolkit.sample.shared.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import br.com.arch.toolkit.sample.design.AppText
import br.com.arch.toolkit.sample.design.text
import br.com.arch.toolkit.sample.feature.design.DesignRoute
import br.com.arch.toolkit.sample.feature.githubSample.ui.GithubRoute
import br.com.arch.toolkit.sample.feature.settings.ui.SettingsRoute
import br.com.arch.toolkit.sample.feature.toolkit.ToolkitRoute
import br.com.arch.toolkit.sample.github.shared.designSystem.AppTheme
import com.pedrobneto.easy.navigation.core.Navigation
import com.pedrobneto.easy.navigation.core.model.LaunchStrategy
import com.pedrobneto.easy.navigation.core.model.NavigationRoute
import com.pedrobneto.easy.navigation.core.rememberNavigationController
import com.pedrobneto.easy.navigation.registry.DesignDirectionRegistry
import com.pedrobneto.easy.navigation.registry.GithubDirectionRegistry
import com.pedrobneto.easy.navigation.registry.SettingsDirectionRegistry
import com.pedrobneto.easy.navigation.registry.ToolkitDirectionRegistry

@Composable
fun AppHome(deepLink: String? = null, onDeepLinkHandled: () -> Unit = {}) {
    val itemModifier = Modifier.padding(horizontal = AppTheme.dimen.spacingXs)
    val itemColors = navigationItemColors()

    val registries = remember {
        listOf(
            GithubDirectionRegistry,
            SettingsDirectionRegistry,
            ToolkitDirectionRegistry,
            DesignDirectionRegistry
        )
    }
    val navigation = rememberNavigationController(
        initialRoute = GithubRoute,
        directionRegistries = registries
    )
    LaunchedEffect(deepLink) {
        deepLink?.let { link ->
            showcaseLinkPath(link)?.let { navigation.safeNavigateTo(it) }
            onDeepLinkHandled()
        }
    }
    val items = listOf(
        NavigationItem(GithubRoute, AppText.GITHUB, Icons.Default.Code),
        NavigationItem(ToolkitRoute, AppText.TOOLKIT, Icons.Default.Build),
        NavigationItem(DesignRoute, AppText.DESIGN, Icons.Default.Palette),
        NavigationItem(SettingsRoute, AppText.SETTINGS, Icons.Default.Settings)
    )
    val selectedItem =
        items.firstOrNull { it.route::class == navigation.currentRoute::class } ?: items.first()
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
        contentColor = AppTheme.color.textParagraph,
        navigationSuiteItems = {
            addItems(itemModifier, selectedItem, items, itemColors) { item ->
                navigation.navigateTo(
                    item.route,
                    LaunchStrategy.NewStack
                )
            }
        },
        content = {
            Navigation(
                modifier = Modifier,
                initialRoute = GithubRoute,
                directionRegistries = registries,
                controller = navigation
            )
        }
    )
}

@Suppress("LongParameterList")
private fun NavigationSuiteScope.addItems(
    modifier: Modifier,
    selected: NavigationItem,
    items: List<NavigationItem>,
    colors: NavigationSuiteItemColors,
    onMenuSelected: (NavigationItem) -> Unit
) = items.forEach { option ->
    item(
        colors = colors,
        modifier = modifier,
        selected = selected.route == option.route,
        onClick = { onMenuSelected(option) },
        label = {
            Text(
                text = text(option.label),
                textAlign = TextAlign.Center,
                style = AppTheme.textStyle.paragraphCaptionS
            )
        },
        icon = {
            Icon(
                imageVector = option.icon,
                contentDescription = text(option.label)
            )
        },
        alwaysShowLabel = true
    )
}

private data class NavigationItem(
    val route: NavigationRoute,
    val label: AppText,
    val icon: ImageVector
)

@Composable
private fun navigationItemColors() = NavigationSuiteItemColors(
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
