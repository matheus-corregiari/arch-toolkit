package br.com.arch.toolkit.sample.github.android

import android.app.Application
import br.com.arch.toolkit.lumber.DebugOak
import br.com.arch.toolkit.lumber.Lumber
import br.com.arch.toolkit.sample.shared.initKoin
import br.com.arch.toolkit.sample.shared.observeAndroidTheme
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.stopKoin

internal class GithubApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Lumber.plant(DebugOak())
        initKoin { androidContext(this@GithubApplication) }
        observeAndroidTheme()
    }
    override fun onTerminate() {
        stopKoin()
        super.onTerminate()
    }
}
