plugins {
    id("toolkit-android-sample")
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
    implementation(project(":toolkit:multi:event-observer"))
    implementation(project(":toolkit:multi:splinter"))
    implementation(project(":toolkit:multi:lumber"))
    implementation(project(":toolkit:android:delegate"))
    implementation(project(":toolkit:android:recycler-adapter"))
    implementation(project(":toolkit:android:statemachine"))

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
    implementation(libs.square.okhttp.logging)

    // Doing evil things generating code!
    ksp(libs.square.moshi.codegen)
}

