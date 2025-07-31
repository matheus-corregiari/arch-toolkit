package br.com.arch.toolkit.sample.github.desktop

import androidx.compose.material3.Text
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.awt.Dimension

fun main() = application {
    Window(
        title = "Github Sample",
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(),
    ) {
        window.minimumSize = Dimension(320, 480)
        Text("Hello World")
    }
}
