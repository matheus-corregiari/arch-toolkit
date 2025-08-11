package br.com.arch.toolkit.sample.github.desktop

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import br.com.arch.toolkit.lumber.DebugTree
import br.com.arch.toolkit.lumber.Lumber
import br.com.arch.toolkit.sample.github.shared.designSystem.AppTheme
import br.com.arch.toolkit.sample.github.shared.initKoin
import br.com.arch.toolkit.sample.github.shared.ui.home.AppHome
import org.koin.core.context.stopKoin
import java.awt.Dimension

fun main() = application {
    Lumber.plant(DebugTree())
    initKoin()
    Window(
        title = "Github Sample",
        icon = rememberVectorPainter(image = Icons.Filled.Coffee),
        onCloseRequest = {
            stopKoin()
            exitApplication()
        },
        state = rememberWindowState(),
    ) {
        window.minimumSize = Dimension(320, 480)
        AppTheme { AppHome() }
    }
}
