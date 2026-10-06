package br.com.arch.toolkit.sample.design.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import br.com.arch.toolkit.sample.design.AppTheme

/** Persistent navigation uses the same colors and typography as the page content. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppToolbar(title: String, backLabel: String, onBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                title,
                modifier = Modifier.semantics { heading() },
                style = AppTheme.textStyle.sectionHeading,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = backLabel)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = AppTheme.color.backgroundSurfaceDefault,
            titleContentColor = AppTheme.color.textTitle,
            navigationIconContentColor = AppTheme.color.iconSecondary
        )
    )
}
