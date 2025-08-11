package br.com.arch.toolkit.sample.github.shared.structure.data.local

import br.com.arch.toolkit.util.ContextProvider

actual fun defaultKeyValuePath(): String =
    requireNotNull(ContextProvider.current).filesDir.resolve(DATASTORE_FILENAME).absolutePath
