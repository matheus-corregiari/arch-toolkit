package br.com.arch.toolkit.sample.feature.design

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import br.com.arch.toolkit.sample.design.AppText
import br.com.arch.toolkit.sample.design.AppTheme
import br.com.arch.toolkit.sample.design.component.AppButton
import br.com.arch.toolkit.sample.design.component.AppChoiceGroup
import br.com.arch.toolkit.sample.design.component.AppPage
import br.com.arch.toolkit.sample.design.component.AppSection
import br.com.arch.toolkit.sample.design.component.AppSectionGrid
import br.com.arch.toolkit.sample.design.component.EmptyState
import br.com.arch.toolkit.sample.design.component.ErrorState
import br.com.arch.toolkit.sample.design.text
import com.pedrobneto.easy.navigation.core.annotation.Deeplink
import com.pedrobneto.easy.navigation.core.annotation.Route
import com.pedrobneto.easy.navigation.core.annotation.Scope
import com.pedrobneto.easy.navigation.core.model.NavigationRoute
import kotlinx.serialization.Serializable

@Serializable
data object DesignRoute : NavigationRoute

@Route(DesignRoute::class)
@Scope("design")
@Deeplink("/design")
@Composable
fun DesignDestination() {
    var selected by rememberSaveable { mutableStateOf(false) }
    DesignContent(selected, onSelect = { selected = !selected })
}

@Composable
fun DesignContent(
    selected: Boolean = false,
    gridState: LazyGridState = rememberLazyGridState(),
    onSelect: () -> Unit = {}
) {
    AppPage(text(AppText.DESIGN), description = text(AppText.DESIGN_INTRO)) {
        AppSectionGrid(state = gridState) {
            item(key = "typography") {
                AppSection(text(AppText.TYPOGRAPHY)) {
                    Text("Arch Toolkit", style = AppTheme.textStyle.pageHeading)
                    Text(text(AppText.TOKENS), style = AppTheme.textStyle.body)
                    Text(text(AppText.DESCRIPTION), style = AppTheme.textStyle.paragraphCaptionS)
                }
            }
            item(key = "colors") {
                AppSection(text(AppText.COLORS)) { DesignTokens() }
            }
            item(key = "spacing") {
                AppSection(text(AppText.SPACING)) { SpacingTokens() }
            }
            item(key = "feedback") {
                AppSection(text(AppText.FEEDBACK)) {
                    CircularProgressIndicator()
                    EmptyState(Modifier.fillMaxWidth(), text(AppText.EMPTY))
                    ErrorState(
                        Modifier.fillMaxWidth(),
                        text(AppText.CONNECTION_ERROR),
                        retryLabel = text(AppText.RETRY),
                        retry = onSelect
                    )
                }
            }
            item(key = "widgets") {
                AppSection(text(AppText.WIDGETS)) {
                    AppButton.Style.entries.forEach { style ->
                        AppButton(
                            buttonLabel(style),
                            onSelect,
                            modifier = Modifier.fillMaxWidth(),
                            style = style
                        )
                    }
                    AppButton(
                        text(AppText.DISABLED),
                        {},
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false
                    )
                    AppChoiceGroup(
                        listOf(false, true),
                        selected,
                        { if (it != selected) onSelect() },
                        label = { text(if (it) AppText.SELECTED else AppText.ENABLED) }
                    )
                }
            }
        }
    }
}

@Composable
private fun buttonLabel(style: AppButton.Style) = when (style) {
    AppButton.Style.Primary -> text(AppText.SAVE)
    AppButton.Style.Secondary -> text(AppText.READ)
    AppButton.Style.Link -> text(AppText.DOCUMENTATION)
    AppButton.Style.Destructive -> text(AppText.DELETE)
}

@Composable
private fun DesignTokens() {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingM)) {
        listOf(
            text(AppText.BRAND) to AppTheme.color.backgroundBrandPrimary,
            text(AppText.SURFACE) to AppTheme.color.backgroundSurfaceTertiary,
            text(AppText.TEXT) to AppTheme.color.textTitle
        ).forEach { (name, color) ->
            Column {
                Box(
                    Modifier.size(
                        AppTheme.dimen.spacingH
                    ).background(
                        color,
                        AppTheme.dimen.shapes().small
                    ).border(
                        AppTheme.dimen.borderWidthS,
                        AppTheme.color.surfaceOutline,
                        AppTheme.dimen.shapes().small
                    )
                )
                Text(name, style = AppTheme.textStyle.paragraphCaptionXs)
            }
        }
    }
}

@Composable
private fun SpacingTokens() {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingS)) {
        listOf(
            "XS" to AppTheme.dimen.spacingXs,
            "S" to AppTheme.dimen.spacingS,
            "M" to AppTheme.dimen.spacingM,
            "L" to AppTheme.dimen.spacingL
        ).forEach { (name, spacing) ->
            Column {
                Box(Modifier.size(spacing).background(AppTheme.color.textTitle))
                Text("$name: $spacing", style = AppTheme.textStyle.paragraphCaptionXs)
            }
        }
    }
}
