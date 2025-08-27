import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import br.com.arch.toolkit.lumber.DebugTree
import br.com.arch.toolkit.lumber.Lumber
import br.com.arch.toolkit.sample.github.shared.designSystem.AppTheme
import br.com.arch.toolkit.sample.github.shared.initKoin
import br.com.arch.toolkit.sample.github.shared.ui.home.AppHome

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    Lumber.plant(DebugTree())
    initKoin()
    ComposeViewport(viewportContainerId = "bacate") {
        AppTheme { AppHome() }
    }
}
