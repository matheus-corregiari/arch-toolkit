package br.com.arch.toolkit.sample.github.shared

import androidx.compose.ui.window.ComposeUIViewController
import br.com.arch.toolkit.lumber.DebugOak
import br.com.arch.toolkit.lumber.Lumber
import br.com.arch.toolkit.sample.shared.ShowcaseApp
import br.com.arch.toolkit.sample.shared.initKoin
import platform.UIKit.UIViewController

fun createController(): UIViewController = ComposeUIViewController {
    Lumber.plant(DebugOak())
    initKoin()
    ShowcaseApp()
}
