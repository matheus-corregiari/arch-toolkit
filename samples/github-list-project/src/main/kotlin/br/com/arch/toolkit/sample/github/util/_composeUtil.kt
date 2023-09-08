package br.com.arch.toolkit.sample.github.util

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview

internal fun AppCompatActivity.composeContent(setupView: @Composable () -> Unit) {
    setContentView(ComposeView(this).apply {
        setContent {
            MaterialTheme(content = setupView)
        }
    })
}

@Preview(
    name = "Light Mode",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showSystemUi = false,
    showBackground = false
)
//@Preview(
//    name = "Dark Mode",
//    uiMode = Configuration.UI_MODE_NIGHT_YES,
//    showSystemUi = false,
//    showBackground = true
//)
internal annotation class DefaultComposePreview