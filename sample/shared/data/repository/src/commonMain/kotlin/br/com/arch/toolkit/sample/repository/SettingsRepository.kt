package br.com.arch.toolkit.sample.repository

import br.com.arch.toolkit.sample.core.model.AppLanguage
import br.com.arch.toolkit.sample.core.model.ContrastMode
import br.com.arch.toolkit.sample.core.model.ThemeMode
import br.com.arch.toolkit.storage.core.StorageProvider

class SettingsRepository(
    storage: StorageProvider
) {
    val language = storage.enum(key = "language", default = AppLanguage.ENGLISH)
    val themeMode = storage.enum(key = "theme", default = ThemeMode.SYSTEM)
    val contrastMode = storage.enum(key = "contrast", default = ContrastMode.STANDARD)
}
