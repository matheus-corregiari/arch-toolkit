@file:Suppress("UnstableApiUsage")

plugins { id("toolkit-android-library") }

android.namespace = "br.com.arch.toolkit.foldable"
android.testNamespace = "br.com.arch.toolkit.foldable.test"

android {
    testOptions {
        unitTests.isIncludeAndroidResources = false
        unitTests.all { test -> test.enabled = false }
    }
}

dependencies {
    // Other Modules
    implementation(project(":toolkit:android:delegate"))

    // Libraries
    implementation(libs.jetbrains.stdlib.jdk8)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraint)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.window)
    implementation(libs.google.material)

    // Test Libraries
    androidTestImplementation(libs.jetbrains.stdlib.jdk8)
    androidTestImplementation(libs.androidx.annotation)
    androidTestImplementation(libs.androidx.appcompat)
    androidTestImplementation(libs.androidx.constraint)
    androidTestImplementation(libs.androidx.lifecycle.runtime)
    androidTestImplementation(libs.androidx.window)
    androidTestImplementation(libs.google.material)
    androidTestImplementation(libs.androidx.test.window)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espresso.intents)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.junit.test)
}
