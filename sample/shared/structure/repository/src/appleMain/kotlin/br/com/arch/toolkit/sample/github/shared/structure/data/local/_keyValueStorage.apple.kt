package br.com.arch.toolkit.sample.github.shared.structure.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

internal const val DATASTORE_FILENAME = "batata.preferences_pb"
typealias PrefsDataStore = DataStore<Preferences>

internal fun createDataStore(producePath: () -> String): PrefsDataStore =
    PreferenceDataStoreFactory.createWithPath(produceFile = { producePath().toPath() })

internal fun defaultKeyValueDataStore(): PrefsDataStore = createDataStore { defaultKeyValuePath() }

@OptIn(ExperimentalForeignApi::class)
fun defaultKeyValuePath(): String {
    val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory).path + "/$DATASTORE_FILENAME"
}
