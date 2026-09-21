package br.com.arch.toolkit.sample.shared

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import br.com.arch.toolkit.sample.github.shared.structure.core.model.ThemeMode
import br.com.arch.toolkit.sample.github.shared.structure.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

private const val THEME_TRANSITION_DELAY_MILLIS = 400L

fun observeAndroidTheme() {
    ProcessLifecycleOwner.get().lifecycleScope.launch(Dispatchers.Main) {
        val settings = GlobalContext.get().get<SettingsRepository>()
        var count = 0
        settings.themeMode.get().collectLatest {
            // Avoid a flaky transition after the initial theme.
            if (count != 0) delay(THEME_TRANSITION_DELAY_MILLIS)
            AppCompatDelegate.setDefaultNightMode(it.toAndroidMode)
            count++
        }
    }
}

private val ThemeMode.toAndroidMode: Int
    get() = when (this) {
        ThemeMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
        ThemeMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
        ThemeMode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }
