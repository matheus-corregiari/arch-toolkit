plugins {
    id("toolkit-android-sample")
    // id("toolkit-compose") --> Commented until Compose finally work on API 34 -.-"
    alias(libs.plugins.google.ksp)
}

android.namespace = "br.com.arch.toolkit.sample.github"
android.defaultConfig.applicationId("br.com.arch.toolkit.sample.github")

android {
    defaultConfig {
        buildConfigField("String", "BASE_URL", "\"https://api.github.com/\"")
    }
}

androidComponents.beforeVariants {
    it.enable = it.name == "debug"
}

dependencies {
    // Other Modules
    implementation(project(":toolkit:delegate"))
    implementation(project(":toolkit:event-observer"))
    implementation(project(":toolkit:recycler-adapter"))
    implementation(project(":toolkit:statemachine"))
    implementation(project(":toolkit:splinter"))

    // Libraries
    implementation(libs.jetbrains.stdlib.jdk8)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.recycler)
    implementation(libs.androidx.splash)
    implementation(libs.square.retrofit.main)
    implementation(libs.square.retrofit.moshi)
    implementation(libs.square.okhttp.core)
    implementation(libs.square.moshi.kotlin)
    implementation(libs.square.timber)
    implementation(libs.square.okhttp.logging)

    // Doing evil things generating code!
    ksp(libs.square.moshi.codegen)
}

