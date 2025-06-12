plugins {
    id("toolkit-android-library")
    id("toolkit-android-publish")
}

android.namespace = "br.com.arch.toolkit.statemachine"
android.testNamespace = "br.com.arch.toolkit.statemachine.test"

android.testOptions.unitTests { isIncludeAndroidResources = true }

dependencies {
    // Libraries
    compileOnly(libs.jetbrains.stdlib.jdk8)
    compileOnly(libs.androidx.annotation)

    // Test Libraries
    androidTestImplementation(libs.androidx.appcompat)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espresso.intents)
    androidTestImplementation(libs.junit.test)
    androidTestImplementation(libs.jetbrains.stdlib.jdk8)

    // Test Libraries
    testImplementation(libs.jetbrains.stdlib.jdk8)
    testImplementation(libs.junit.test)
    testImplementation(libs.mockito.test.core)
    testImplementation(libs.mockito.test.kotlin)
    testImplementation(libs.robolectric.test)
}
