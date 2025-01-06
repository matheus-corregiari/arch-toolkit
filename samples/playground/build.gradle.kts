plugins {
    id("toolkit-android-sample")
    alias(libs.plugins.jetbrains.serialization)
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
    implementation(libs.jetbrains.stdlib.jdk8)
    implementation(libs.jetbrains.serialization)
    implementation(libs.androidx.lifecycle.livedata)
    implementation(libs.jetbrains.coroutines.android)
    implementation(libs.jetbrains.coroutines.core)
}
