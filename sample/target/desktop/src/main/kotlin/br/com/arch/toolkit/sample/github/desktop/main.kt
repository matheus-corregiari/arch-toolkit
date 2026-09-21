package br.com.arch.toolkit.sample.github.desktop

import androidx.compose.runtime.remember

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gite
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import br.com.arch.toolkit.lumber.DebugOak
import br.com.arch.toolkit.lumber.Lumber
import br.com.arch.toolkit.sample.shared.ShowcaseApp
import br.com.arch.toolkit.sample.shared.initKoin
import org.koin.core.context.stopKoin
import java.awt.Dimension

fun main(args: Array<String>) {
    Lumber.plant(DebugOak())
    initKoin()
    application {
    var deepLink by remember { androidx.compose.runtime.mutableStateOf(args.firstOrNull()) }
    Window(
        title = "Arch Toolkit Showcase",
        icon = rememberVectorPainter(image = Icons.Filled.Gite),
        state = rememberWindowState(size = DpSize(800.dp, 600.dp)),
        onCloseRequest = {
            stopKoin()
            exitApplication()
        },
    ) {
        LaunchedEffect(Unit) { window.minimumSize = Dimension(320, 480) }
        ShowcaseApp(deepLink) { deepLink = null }
    }
}

}
