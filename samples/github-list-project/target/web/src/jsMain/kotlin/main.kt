@file:OptIn(ExperimentalComposeUiApi::class)

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.Text

fun main() {
    renderComposable(rootElementId = "bacate") {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Hello World")
        }
    }
}
