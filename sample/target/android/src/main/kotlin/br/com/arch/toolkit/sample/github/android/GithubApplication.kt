package br.com.arch.toolkit.sample.github.android

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import br.com.arch.toolkit.lumber.DebugOak
import br.com.arch.toolkit.lumber.Lumber
import br.com.arch.toolkit.sample.github.shared.structure.core.model.ThemeMode
import br.com.arch.toolkit.sample.github.shared.structure.repository.SettingsRepository
import br.com.arch.toolkit.sample.shared.initKoin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.context.stopKoin

internal class GithubApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Lumber.plant(DebugOak())
        initKoin()
        ProcessLifecycleOwner.get().lifecycleScope.launch(Dispatchers.Main) {
            val settings by inject<SettingsRepository>()
            var count = 0
            settings.themeMode.get().collectLatest {
                if (count != 0) delay(400L) // <-- To avoid flaky transition
                AppCompatDelegate.setDefaultNightMode(it.toAndroidMode)
                count++
            }
        }
    }

    override fun onTerminate() {
        stopKoin()
        super.onTerminate()
    }

    private val ThemeMode.toAndroidMode: Int
        get() = when (this) {
            ThemeMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            ThemeMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            ThemeMode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
}
