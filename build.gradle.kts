plugins {
    id("jacoco")

    alias(libs.plugins.google.ksp) apply false
    alias(libs.plugins.jetbrains.serialization) apply false
    alias(libs.plugins.dexcount) apply false
}
