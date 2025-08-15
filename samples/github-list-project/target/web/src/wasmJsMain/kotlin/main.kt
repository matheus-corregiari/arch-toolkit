@file:OptIn(ExperimentalComposeUiApi::class)

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.CanvasBasedWindow
import br.com.arch.toolkit.lumber.DebugTree
import br.com.arch.toolkit.lumber.Lumber

fun main() {
    Lumber.plant(DebugTree())
    CanvasBasedWindow(canvasElementId = "bacate") {
        MaterialTheme {
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Hello World", color = Color.Red)
                Button(onClick = {
                    Lumber.warn("Warn")
                }) {
                    Text("Warn", color = Color.Red)
                }
            }
        }
    }
}
