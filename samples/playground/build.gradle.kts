plugins {
    id("toolkit-android-sample")
    alias(pluginLibraries.plugins.jetbrains.serialization)
}

android.namespace = "br.com.arch.toolkit.sample.livedata"
android.defaultConfig.applicationId("br.com.arch.toolkit.sample.livedata")

androidComponents.beforeVariants {
    it.enable = it.name == "debug"
}

dependencies {
    // Other Modules
    implementation(project(":toolkit:event-observer"))
    implementation(project(":toolkit:storage"))

    // Libraries
    implementation(libraries.jetbrains.stdlib.jdk8)
    implementation(libraries.jetbrains.serialization)
    implementation(libraries.androidx.lifecycle.livedata)
    implementation(libraries.jetbrains.coroutines.android)
    implementation(libraries.jetbrains.coroutines.core)
}
