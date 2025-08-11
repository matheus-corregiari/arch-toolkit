package br.com.arch.toolkit.sample.github.shared.structure.data.local

import java.io.File

actual fun defaultKeyValuePath(): String {
    val userHome = System.getProperty("user.home")
    val secureDir = File(userHome, ".myapp_secure_files")
    if (!secureDir.exists()) secureDir.mkdirs()
    return secureDir.resolve(DATASTORE_FILENAME).absolutePath
}
