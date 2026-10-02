import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import br.com.arch.toolkit.lumber.DebugOak
import br.com.arch.toolkit.lumber.Lumber
import br.com.arch.toolkit.sample.shared.ShowcaseApp
import br.com.arch.toolkit.sample.shared.initKoin
import br.com.arch.toolkit.sample.shared.ui.home.AppHome

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    Lumber.plant(DebugOak())
    initKoin()
    ComposeViewport(viewportContainerId = "bacate") {
        ShowcaseApp()
    }
}
