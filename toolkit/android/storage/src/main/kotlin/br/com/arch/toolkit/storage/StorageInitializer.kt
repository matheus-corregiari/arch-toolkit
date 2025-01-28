package br.com.arch.toolkit.storage

import android.content.Context
import androidx.startup.Initializer

class StorageInitializer internal constructor() : Initializer<Unit> {
    override fun create(context: Context) = Storage.KeyValue.init(context)
    override fun dependencies() = mutableListOf<Class<out Initializer<*>>>()
}
