@file:Suppress("UnstableApiUsage")

plugins { id("toolkit-library") }

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
    implementation(project(":toolkit:delegate"))

    // Libraries
    implementation(libraries.jetbrains.stdlib.jdk8)
    implementation(libraries.androidx.annotation)
    implementation(libraries.androidx.appcompat)
    implementation(libraries.androidx.constraint)
    implementation(libraries.androidx.lifecycle.runtime)
    implementation(libraries.androidx.window)
    implementation(libraries.google.material)

    // Test Libraries
    androidTestImplementation(libraries.jetbrains.stdlib.jdk8)
    androidTestImplementation(libraries.androidx.annotation)
    androidTestImplementation(libraries.androidx.appcompat)
    androidTestImplementation(libraries.androidx.constraint)
    androidTestImplementation(libraries.androidx.lifecycle.runtime)
    androidTestImplementation(libraries.androidx.window)
    androidTestImplementation(libraries.google.material)
    androidTestImplementation(libraries.androidx.test.window)
    androidTestImplementation(libraries.androidx.test.core)
    androidTestImplementation(libraries.androidx.test.junit)
    androidTestImplementation(libraries.androidx.test.espresso.intents)
    androidTestImplementation(libraries.androidx.test.espresso.core)
    androidTestImplementation(libraries.junit.test)
}
