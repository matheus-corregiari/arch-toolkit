package br.com.arch.toolkit.sample.feature.design

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.arch.toolkit.sample.design.AppText
import br.com.arch.toolkit.sample.design.text
import br.com.arch.toolkit.sample.github.shared.designSystem.AppTheme
import br.com.arch.toolkit.sample.github.shared.designSystem.component.AppButton
import br.com.arch.toolkit.sample.github.shared.designSystem.component.EmptyState
import br.com.arch.toolkit.sample.github.shared.designSystem.component.ErrorState
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
    Column(
        Modifier.fillMaxSize().verticalScroll(
            rememberScrollState()
        ).padding(AppTheme.dimen.spacingM),
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingM)
    ) {
        Text(text(AppText.DESIGN), style = AppTheme.textStyle.titleXLRegular)
        Text(text(AppText.TYPOGRAPHY), style = AppTheme.textStyle.title)
        Text("Arch Toolkit", style = AppTheme.textStyle.paragraphCaptionS)
        DesignTokens()
        Text(text(AppText.WIDGETS))
        AppButton.Style.entries.forEach { style ->
            AppButton(style.name, {
                selected = !selected
            }, style = style)
        }
        AppButton(text(AppText.DISABLED), {}, enabled = false)
        CircularProgressIndicator()
        EmptyState(Modifier, text(AppText.EMPTY))
        ErrorState(
            Modifier,
            text(
                AppText.CONNECTION_ERROR
            ),
            retryLabel = text(AppText.RETRY),
            retry = {
                selected =
                    !selected
            }
        )
        FilterChip(selected, { selected = !selected }, label = { Text(text(AppText.SELECTED)) })
    }
}

@Composable
private fun DesignTokens() {
    Text(text(AppText.COLORS))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingM)) {
        listOf(
            "textTitle" to AppTheme.color.textTitle,
            "backgroundBrandPrimary" to AppTheme.color.backgroundBrandPrimary,
            "fillSecondary" to AppTheme.color.fillSecondary
        ).forEach { (name, color) ->
            Column {
                Box(Modifier.size(48.dp).background(color))
                Text(name, style = AppTheme.textStyle.paragraphCaptionXs)
            }
        }
    }
    Text(text(AppText.SPACING))
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
