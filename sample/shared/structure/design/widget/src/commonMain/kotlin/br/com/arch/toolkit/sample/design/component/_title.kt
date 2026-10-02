package br.com.arch.toolkit.sample.design.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import br.com.arch.toolkit.sample.design.AppTheme

@Composable
fun ScreenTitle(modifier: Modifier, text: String, description: String? = null) {
    Column(
        modifier.padding(vertical = AppTheme.dimen.spacingM),
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingXs)
    ) {
        Text(
            text,
            modifier = Modifier.semantics { heading() },
            style = AppTheme.textStyle.pageHeading,
            color = AppTheme.color.textTitle
        )
        description?.let {
            Text(
                it,
                style = AppTheme.textStyle.body,
                color = AppTheme.color.textSubtitle
            )
        }
    }
}
