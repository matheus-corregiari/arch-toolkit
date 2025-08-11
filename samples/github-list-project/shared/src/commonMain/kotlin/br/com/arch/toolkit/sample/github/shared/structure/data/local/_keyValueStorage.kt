@file:Suppress("UnrememberedMutableState")

package br.com.arch.toolkit.sample.github.shared.structure.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

internal const val DATASTORE_FILENAME = "batata.preferences_pb"
typealias PrefsDataStore = DataStore<Preferences>

internal fun createDataStore(producePath: () -> String): PrefsDataStore =
    PreferenceDataStoreFactory.createWithPath(produceFile = { producePath().toPath() })

internal fun defaultKeyValueDataStore(): PrefsDataStore = createDataStore { defaultKeyValuePath() }

expect fun defaultKeyValuePath(): String
