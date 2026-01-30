package br.com.arch.toolkit.sample.github.desktop

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gite
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import br.com.arch.toolkit.lumber.DebugOak
import br.com.arch.toolkit.lumber.Lumber
import br.com.arch.toolkit.sample.github.shared.designSystem.AppTheme
import br.com.arch.toolkit.sample.shared.initKoin
import br.com.arch.toolkit.sample.shared.ui.home.AppHome
import org.koin.core.context.stopKoin
import java.awt.Dimension

fun main() = application {
    Lumber.plant(DebugOak())
    initKoin()
    Window(
        title = "Github Sample",
        icon = rememberVectorPainter(image = Icons.Filled.Gite),
        state = rememberWindowState(size = DpSize(800.dp, 600.dp)),
        onCloseRequest = {
            stopKoin()
            exitApplication()
        },
    ) {
        LaunchedEffect(Unit) { window.minimumSize = Dimension(320, 480) }
        AppTheme { AppHome() }
    }
}
