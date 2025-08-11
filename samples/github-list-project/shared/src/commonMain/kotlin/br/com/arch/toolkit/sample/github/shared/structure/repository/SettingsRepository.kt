package br.com.arch.toolkit.sample.github.shared.structure.repository

import br.com.arch.toolkit.sample.github.shared.structure.core.model.ContrastMode
import br.com.arch.toolkit.sample.github.shared.structure.core.model.ThemeMode
import br.com.arch.toolkit.sample.github.shared.structure.data.local.EnumKeyValue
import br.com.arch.toolkit.sample.github.shared.structure.data.local.PrefsDataStore

class SettingsRepository internal constructor(
    storage: PrefsDataStore
) {

    val themeMode = EnumKeyValue(
        key = "theme",
        store = storage,
        default = ThemeMode.SYSTEM,
        all = ThemeMode.entries
    )

    val contrastMode = EnumKeyValue(
        key = "contrast",
        store = storage,
        default = ContrastMode.STANDARD,
        all = ContrastMode.entries
    )
}
