package br.com.arch.toolkit.sample.github.shared.structure.repository

import br.com.arch.toolkit.sample.github.shared.structure.core.model.ContrastMode
import br.com.arch.toolkit.sample.github.shared.structure.core.model.ThemeMode
import br.com.arch.toolkit.storage.core.StorageProvider

class SettingsRepository internal constructor(
    storage: StorageProvider
) {
    val themeMode = storage.enum(key = "theme", default = ThemeMode.SYSTEM)
    val contrastMode = storage.enum(key = "contrast", default = ContrastMode.STANDARD)
}
