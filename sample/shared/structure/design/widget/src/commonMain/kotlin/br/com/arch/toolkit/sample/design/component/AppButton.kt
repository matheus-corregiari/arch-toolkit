package br.com.arch.toolkit.sample.design.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.com.arch.toolkit.sample.design.AppTheme

@Immutable
object AppButton {
    enum class Size {
        Regular,
        Small;

        @Composable
        internal fun shape() = when (this) {
            Regular -> RoundedCornerShape(AppTheme.dimen.radiusM)
            Small -> RoundedCornerShape(AppTheme.dimen.radiusS)
        }

        @Composable
        internal fun padding() = when (this) {
            Regular -> PaddingValues(AppTheme.dimen.spacingL, AppTheme.dimen.spacingM)
            Small -> PaddingValues(AppTheme.dimen.spacingS, AppTheme.dimen.spacingXs)
        }

        @Composable
        internal fun minHeight() = when (this) {
            Regular -> 56.dp
            Small -> 48.dp
        }

        @Composable
        internal fun textStyle() = when (this) {
            Regular -> AppTheme.textStyle.actionG
            Small -> AppTheme.textStyle.actionM
        }
    }

    enum class Style {
        Primary,
        Secondary,
        Link,
        Destructive;

        @Composable
        internal fun border() = when (this) {
            Secondary -> BorderStroke(
                width = AppTheme.dimen.borderWidthS,
                color = AppTheme.color.buttonSecondaryStrokeEnable
            )

            Primary -> null
            Link -> null
            Destructive -> null
        }

        @Composable
        internal fun textColor() = when (this) {
            Primary -> AppTheme.color.onBrand
            Secondary -> AppTheme.color.textTitle
            Link -> AppTheme.color.textLink
            Destructive -> AppTheme.color.textNegative
        }

        @Composable
        internal fun colors() = when (this) {
            Primary -> ButtonDefaults.buttonColors(
                containerColor = AppTheme.color.backgroundBrandPrimary,
                disabledContainerColor = AppTheme.color.buttonBackgroundDisabled
            )

            Secondary -> ButtonDefaults.buttonColors(
                containerColor = AppTheme.color.buttonSecondaryBackground,
                disabledContainerColor = AppTheme.color.buttonBackgroundDisabled
            )

            Link -> ButtonDefaults.buttonColors(
                containerColor = AppTheme.color.buttonPrimarylinkBackground,
                disabledContainerColor = AppTheme.color.buttonBackgroundDisabled
            )

            Destructive -> ButtonDefaults.buttonColors(
                containerColor = AppTheme.color.buttonDestructiveBackground,
                disabledContainerColor = AppTheme.color.buttonBackgroundDisabled
            )
        }
    }

    @Composable
    @Suppress("LongParameterList")
    operator fun invoke(
        text: CharSequence,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        style: Style = Style.Primary,
        size: Size = Size.Regular,
        enabled: Boolean = true,
        contentPadding: PaddingValues? = null,
        leadingIcon: ImageVector? = null
    ) {
        val padding = contentPadding ?: size.padding()
        val minHeight = padding.calculateTopPadding().takeIf { it == 0.dp } ?: size.minHeight()

        Button(
            modifier = modifier.defaultMinSize(minHeight = minHeight)
                .semantics { role = Role.Button },
            shape = size.shape(),
            colors = style.colors(),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp
            ),
            enabled = enabled,
            onClick = { onClick() },
            border = if (enabled) style.border() else null,
            contentPadding = padding
        ) {
            val textColor = if (enabled) style.textColor() else AppTheme.color.textDisabled
            if (leadingIcon == null) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = text.toString(),
                    color = textColor,
                    style = size.textStyle(),
                    textAlign = TextAlign.Center
                )
            } else {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        leadingIcon,
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(AppTheme.dimen.spacingXs))
                    Text(
                        text.toString(),
                        color = textColor,
                        style = size.textStyle(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
