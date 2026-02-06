package br.com.arch.toolkit.sample.github.shared

import androidx.compose.ui.window.ComposeUIViewController
import br.com.arch.toolkit.lumber.DebugOak
import br.com.arch.toolkit.lumber.Lumber
import br.com.arch.toolkit.sample.github.shared.designSystem.AppTheme
import br.com.arch.toolkit.sample.shared.initKoin
import br.com.arch.toolkit.sample.shared.ui.home.AppHome
import platform.UIKit.UIViewController

fun Controller(): UIViewController = ComposeUIViewController {
    Lumber.plant(DebugOak())
    initKoin()
    AppTheme { AppHome() }
}
