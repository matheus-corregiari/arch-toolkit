plugins {
    id("jacoco")

    alias(pluginLibraries.plugins.google.ksp) apply false
    alias(pluginLibraries.plugins.jetbrains.serialization) apply false
    alias(pluginLibraries.plugins.dexcount) apply false
}
