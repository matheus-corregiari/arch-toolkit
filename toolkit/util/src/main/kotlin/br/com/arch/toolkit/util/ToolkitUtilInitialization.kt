package br.com.arch.toolkit.util

import android.content.Context
import androidx.startup.Initializer

internal class ToolkitUtilInitialization : Initializer<Unit> {
    override fun create(context: Context) = ContextProvider.init(context)
    override fun dependencies() = mutableListOf<Class<out Initializer<*>>>()
}
