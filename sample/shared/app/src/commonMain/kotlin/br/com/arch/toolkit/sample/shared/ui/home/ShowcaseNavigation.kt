package br.com.arch.toolkit.sample.shared.ui.home

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.defaultPredictivePopTransitionSpec
import br.com.arch.toolkit.sample.feature.github.ui.GithubDetailRoute
import br.com.arch.toolkit.sample.feature.github.ui.GithubRoute
import br.com.arch.toolkit.sample.feature.toolkit.ToolkitRoute
import br.com.arch.toolkit.sample.feature.toolkit.ToolkitSampleRoute
import com.pedrobneto.easy.navigation.core.Navigation
import com.pedrobneto.easy.navigation.core.NavigationController
import com.pedrobneto.easy.navigation.core.model.NavigationRoute
import com.pedrobneto.easy.navigation.core.transition.DefaultTransitionSpec
import com.pedrobneto.easy.navigation.core.transition.NavigationTransitions
import com.pedrobneto.easy.navigation.core.transition.SceneTransitions

/** Tab changes fade; navigation inside a tab uses a short, bounded directional movement. */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ShowcaseNavigation(controller: NavigationController, modifier: Modifier = Modifier) {
    val direction = if (LocalLayoutDirection.current == LayoutDirection.Rtl) -1 else 1
    val distance = with(LocalDensity.current) { 48.dp.roundToPx() }
    val transitions = remember(direction, distance) {
        NavigationTransitions(ShowcaseSceneTransitions(direction, distance))
    }
    Navigation(modifier = modifier, controller = controller, transitions = transitions)
}

private class ShowcaseSceneTransitions(
    private val direction: Int,
    private val distance: Int
) : SceneTransitions {
    override val transitionSpec: DefaultTransitionSpec = transition(back = false)
    override val popTransitionSpec: DefaultTransitionSpec = transition(back = true)

    // Keep the platform gesture linked to predictive back progress.
    override val predictivePopTransitionSpec = defaultPredictivePopTransitionSpec<NavigationRoute>()

    private fun transition(back: Boolean): DefaultTransitionSpec = {
        // Easy Navigation 1.2 stores the route class name in each scene's metadata.
        val from = initialState.metadata[ROUTE_METADATA] as? String
        val to = targetState.metadata[ROUTE_METADATA] as? String
        if (from == null || to == null || from.section() != to.section()) {
            fadeIn(tween(TAB_DURATION)) togetherWith fadeOut(tween(TAB_DURATION))
        } else {
            // A direct-link toolbar return can replace the stack rather than pop it.
            val returning = back ||
                (
                    from == ToolkitSampleRoute::class.qualifiedName &&
                        to == ToolkitRoute::class.qualifiedName
                    )
            val sign = if (returning) -direction else direction
            (
                fadeIn(tween(PAGE_DURATION)) + slideInHorizontally(tween(PAGE_DURATION)) {
                    minOf(it / MOTION_WIDTH_DIVISOR, distance) * sign
                }
                ) togetherWith
                (
                    fadeOut(tween(PAGE_DURATION)) + slideOutHorizontally(tween(PAGE_DURATION)) {
                        -minOf(it / MOTION_WIDTH_DIVISOR, distance) * sign
                    }
                    )
        }
    }
}

private fun String?.section(): String? = when (this) {
    ToolkitSampleRoute::class.qualifiedName -> ToolkitRoute::class.qualifiedName
    GithubDetailRoute::class.qualifiedName -> GithubRoute::class.qualifiedName
    else -> this
}

private const val TAB_DURATION = 180
private const val PAGE_DURATION = 240
private const val ROUTE_METADATA = "route"
private const val MOTION_WIDTH_DIVISOR = 8
