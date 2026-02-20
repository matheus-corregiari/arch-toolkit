package br.com.arch.toolkit.storage

import android.content.Context
import androidx.startup.Initializer

/** AndroidX Startup initializer that configures default storage instances. */
class StorageInitializer internal constructor() : Initializer<Unit> {
    override fun create(context: Context) = Storage.KeyValue.init(context)
    override fun dependencies() = mutableListOf<Class<out Initializer<*>>>()
}
