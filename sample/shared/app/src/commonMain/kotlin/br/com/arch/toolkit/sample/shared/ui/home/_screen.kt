@file:Suppress("FunctionNaming")
@file:OptIn(androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi::class)

package br.com.arch.toolkit.sample.shared.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import br.com.arch.toolkit.sample.design.AppText
import br.com.arch.toolkit.sample.design.text
import br.com.arch.toolkit.sample.feature.design.DesignRoute
import br.com.arch.toolkit.sample.feature.githubSample.ui.GithubRoute
import br.com.arch.toolkit.sample.feature.settings.ui.SettingsRoute
import br.com.arch.toolkit.sample.feature.toolkit.ToolkitRoute
import br.com.arch.toolkit.sample.github.shared.designSystem.AppTheme
import br.com.arch.toolkit.sample.github.shared.designSystem.component.AppNavigationMenu
import br.com.arch.toolkit.sample.github.shared.structure.core.model.WindowSize
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
    AppHomeContent(
        currentRoute = navigation.currentRoute,
        onNavigate = { navigation.navigateTo(it, LaunchStrategy.NewStack) }
    ) {
        Navigation(
            modifier = Modifier,
            initialRoute = GithubRoute,
            directionRegistries = registries,
            controller = navigation
        )
    }
}

@Composable
fun AppHomeContent(
    currentRoute: NavigationRoute = GithubRoute,
    onNavigate: (NavigationRoute) -> Unit = {},
    content: @Composable () -> Unit
) {
    val itemModifier = Modifier.padding(horizontal = AppTheme.dimen.spacingXs)
    val itemColors = navigationItemColors()
    val items = listOf(
        NavigationItem(GithubRoute, AppText.GITHUB, Icons.Default.Code),
        NavigationItem(ToolkitRoute, AppText.TOOLKIT, Icons.Default.Build),
        NavigationItem(DesignRoute, AppText.DESIGN, Icons.Default.Palette),
        NavigationItem(SettingsRoute, AppText.SETTINGS_TAB, Icons.Default.Settings)
    )
    val selectedItem =
        items.firstOrNull { it.route::class == currentRoute::class } ?: items.first()
    if (AppTheme.screen.windowSize == WindowSize.SMALL &&
        LocalDensity.current.fontScale > NAVIGATION_MENU_FONT_SCALE
    ) {
        Column(
            Modifier.fillMaxSize().background(AppTheme.color.backgroundSurfaceDefault)
                .safeDrawingPadding()
        ) {
            AppNavigationMenu(
                labels = items.map { text(it.label) },
                selected = text(selectedItem.label),
                menuLabel = text(AppText.NAVIGATION),
                onSelect = { onNavigate(items[it].route) }
            )
            Box(Modifier.weight(1f).fillMaxWidth()) { content() }
        }
        return
    }
    NavigationSuiteScaffold(
        layoutType = AppTheme.screen.navigationSuiteType,
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContainerColor = AppTheme.color.backgroundSurfaceTertiary,
            navigationBarContentColor = AppTheme.color.textParagraph,
            navigationRailContainerColor = AppTheme.color.backgroundSurfaceSecondary,
            navigationRailContentColor = AppTheme.color.textParagraph,
            navigationDrawerContainerColor = AppTheme.color.backgroundSurfaceSecondary,
            navigationDrawerContentColor = AppTheme.color.textParagraph
        ),
        containerColor = AppTheme.color.backgroundSurfaceDefault,
        contentColor = AppTheme.color.textParagraph,
        navigationSuiteItems = {
            addItems(itemModifier, selectedItem, items, itemColors) { item ->
                onNavigate(item.route)
            }
        },
        content = content
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
                color = LocalContentColor.current,
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

private const val NAVIGATION_MENU_FONT_SCALE = 1.3f

@Composable
private fun navigationItemColors() = NavigationSuiteItemColors(
    navigationBarItemColors = NavigationBarItemColors(
        selectedIconColor = AppTheme.color.selectedContent,
        selectedTextColor = AppTheme.color.textTitle,
        selectedIndicatorColor = AppTheme.color.selectedSurface,
        unselectedIconColor = AppTheme.color.iconSecondary,
        unselectedTextColor = AppTheme.color.textParagraph,
        disabledIconColor = AppTheme.color.iconDisabled,
        disabledTextColor = AppTheme.color.textDisabled
    ),
    navigationRailItemColors = NavigationRailItemColors(
        selectedIconColor = AppTheme.color.selectedContent,
        selectedTextColor = AppTheme.color.textTitle,
        selectedIndicatorColor = AppTheme.color.selectedSurface,
        unselectedIconColor = AppTheme.color.iconSecondary,
        unselectedTextColor = AppTheme.color.textParagraph,
        disabledIconColor = AppTheme.color.iconDisabled,
        disabledTextColor = AppTheme.color.textDisabled
    ),
    navigationDrawerItemColors = NavigationDrawerItemDefaults.colors(
        selectedContainerColor = AppTheme.color.selectedSurface,
        unselectedContainerColor = Color.Unspecified,
        selectedIconColor = AppTheme.color.selectedContent,
        unselectedIconColor = AppTheme.color.iconSecondary,
        selectedTextColor = AppTheme.color.selectedContent,
        unselectedTextColor = AppTheme.color.textParagraph,
        selectedBadgeColor = AppTheme.color.backgroundBrandPrimary,
        unselectedBadgeColor = AppTheme.color.backgroundSurfaceTertiaryDisabled
    )
)
