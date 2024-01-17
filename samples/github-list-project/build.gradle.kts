plugins {
    id("toolkit-android-sample")
    // id("toolkit-compose") --> Commented until Compose finally work on API 34 -.-"
    alias(libraries.plugins.google.ksp)
}

android.namespace = "br.com.arch.toolkit.sample.github"
android.defaultConfig.applicationId("br.com.arch.toolkit.sample.github")

android {
    defaultConfig {
        buildConfigField("String", "BASE_URL", "\"https://api.github.com/\"")
    }
}

dependencies {
    // Other Modules
    implementation(project(":toolkit:delegate"))
    implementation(project(":toolkit:event-observer"))
    implementation(project(":toolkit:recycler-adapter"))
    implementation(project(":toolkit:statemachine"))
    implementation(project(":toolkit:splinter"))

    // Libraries
    implementation(libraries.jetbrains.stdlib.jdk8)
    implementation(libraries.androidx.annotation)
    implementation(libraries.androidx.appcompat)
    implementation(libraries.androidx.recycler)
    implementation(libraries.androidx.splash)
    implementation(libraries.square.retrofit.main)
    implementation(libraries.square.retrofit.moshi)
    implementation(libraries.square.okhttp.core)
    implementation(libraries.square.moshi.kotlin)
    implementation(libraries.square.timber)
    implementation(libraries.square.okhttp.logging)

    // Doing evil things generating code!
    ksp(libraries.square.moshi.codegen)
}

