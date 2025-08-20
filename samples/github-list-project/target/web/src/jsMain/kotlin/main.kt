import org.jetbrains.compose.web.renderComposable
import org.jetbrains.skiko.wasm.onWasmReady

fun main() {
    Lumber.plant(DebugTree())
    initKoin()
    onWasmReady {
        renderComposable(rootElementId = "bacate") {
            AppTheme { AppHome() }
        }
    }
}
