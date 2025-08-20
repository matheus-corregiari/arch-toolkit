package br.com.arch.toolkit.sample.github.shared.designSystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import br.com.arch.toolkit.sample.github.shared.designSystem.AppTheme

@Composable
fun EmptyState(
    modifier: Modifier,
    title: String,
    description: String? = null,
    icon: ImageVector = Icons.Filled.Search,
    titleColor: Color = AppTheme.color.textTitle,
    descriptionColor: Color = AppTheme.color.textParagraph,
    iconColor: Color = AppTheme.color.iconPrimary,
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
}
