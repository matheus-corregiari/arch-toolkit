package br.com.arch.toolkit.sample.github.shared.designSystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import br.com.arch.toolkit.sample.github.shared.designSystem.AppTheme

@Composable
fun ErrorState(
    modifier: Modifier,
    title: String,
    description: String? = null,
    error: Throwable? = null,
    icon: ImageVector = Icons.Filled.Error,
    titleColor: Color = AppTheme.color.textTitle,
    descriptionColor: Color = AppTheme.color.textParagraph,
    errorColor: Color = AppTheme.color.textNegative,
    iconColor: Color = AppTheme.color.iconPrimary,
    retry: (() -> Unit)? = null
) = Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingXxs)
) {
    Icon(
        modifier = Modifier.size(AppTheme.dimen.iconXl),
        imageVector = icon,
        tint = iconColor,
        contentDescription = null
    )

    Text(
        text = title,
        style = AppTheme.textStyle.subtitleXMedium,
        color = titleColor,
        textAlign = TextAlign.Center
    )

    description?.let {
        Text(
            text = it,
            style = AppTheme.textStyle.paragraphM,
            color = descriptionColor,
            textAlign = TextAlign.Center
        )
    }

    error?.let {
        val lineInDp = with(LocalDensity.current) { AppTheme.dimen.fontLineHeightM.toDp() }
        Column(
            modifier = Modifier.padding(top = AppTheme.dimen.spacingM).containerNegativeRadiusM()
                .heightIn(
                    min = (lineInDp * 3) + AppTheme.dimen.spacingL,
                    max = (lineInDp * 5) + AppTheme.dimen.spacingL
                ).verticalScroll(rememberScrollState())
        ) {
            Text(
                modifier = Modifier.fillAdjustableSize().padding(
                    vertical = AppTheme.dimen.spacingS,
                    horizontal = AppTheme.dimen.spacingM,
                ),
                text = it.stackTraceToString(),
                style = AppTheme.textStyle.paragraphM,
                color = errorColor
            )
        }
    }

    retry?.let {
        AppButton(
            modifier = Modifier.padding(top = AppTheme.dimen.spacingM),
            text = "Try Again",
            onClick = it,
            style = AppButton.Style.Secondary,
            size = AppButton.Size.Small
        )
    }

}
