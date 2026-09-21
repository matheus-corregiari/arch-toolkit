package br.com.arch.toolkit.sample.github.shared

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.window.ComposeUIViewController
import br.com.arch.toolkit.lumber.DebugOak
import br.com.arch.toolkit.lumber.Lumber
import br.com.arch.toolkit.sample.shared.ShowcaseApp
import br.com.arch.toolkit.sample.shared.initKoin
import platform.UIKit.UIViewController

private var initialized = false
private val deepLink = mutableStateOf<String?>(null)

fun openShowcaseLink(value: String) {
    deepLink.value = value
}

fun createController(): UIViewController {
    if (!initialized) {
        Lumber.plant(DebugOak())
        initKoin()
        initialized = true
    }
    return ComposeUIViewController {
        ShowcaseApp(deepLink.value) { deepLink.value = null }
    }
}
