package br.com.arch.toolkit.sample.github.shared.structure.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import java.io.File

internal const val DATASTORE_FILENAME = "batata.preferences_pb"
typealias PrefsDataStore = DataStore<Preferences>

internal fun createDataStore(producePath: () -> String): PrefsDataStore =
    PreferenceDataStoreFactory.createWithPath(produceFile = { producePath().toPath() })

internal fun defaultKeyValueDataStore(): PrefsDataStore = createDataStore { defaultKeyValuePath() }

fun defaultKeyValuePath(): String {
    val userHome = System.getProperty("user.home")
    val secureDir = File(userHome, ".myapp_secure_files")
    if (!secureDir.exists()) secureDir.mkdirs()
    return secureDir.resolve(DATASTORE_FILENAME).absolutePath
}
