plugins {
    id("jacoco")

    alias(pluginLibraries.plugins.android.application) apply false
    alias(pluginLibraries.plugins.android.library) apply false
    alias(pluginLibraries.plugins.google.ksp) apply false
    alias(pluginLibraries.plugins.jetbrains.kotlin) apply false
    alias(pluginLibraries.plugins.jetbrains.multiplatform) apply false
    alias(pluginLibraries.plugins.jetbrains.serialization) apply false
    alias(pluginLibraries.plugins.dexcount) apply false
    alias(pluginLibraries.plugins.detekt) apply false
    // TODO Pedrinho, help me
//    alias(pluginLibraries.plugins.pedrinho_publish) apply false
}
