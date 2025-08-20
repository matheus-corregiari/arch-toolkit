@file:OptIn(ExperimentalMaterial3Api::class)

package br.com.arch.toolkit.sample.github.shared.designSystem.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import br.com.arch.toolkit.sample.github.shared.designSystem.AppTheme
import br.com.arch.toolkit.sample.github.shared.structure.core.model.WindowSize

@Composable
fun ScreenTitle(modifier: Modifier, text: String) {
    val colors = TopAppBarDefaults.topAppBarColors(
        containerColor = Color.Transparent,
        scrolledContainerColor = Color.Transparent,
    )
    val style = when (AppTheme.screen.size) {
        WindowSize.SMALL -> AppTheme.textStyle.titleXLMedium
        WindowSize.MEDIUM -> AppTheme.textStyle.titleXXXLMedium
        WindowSize.LARGE -> AppTheme.textStyle.titleHMedium
    }
    TopAppBar(
        modifier = modifier,
        colors = colors,
        title = {
            Text(
                text = text,
                style = style,
                color = AppTheme.color.textSubtitle
            )
        }
    )
}
