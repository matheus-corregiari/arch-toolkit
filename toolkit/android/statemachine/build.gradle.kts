plugins {
    id("toolkit-android-library")
    id("toolkit-android-publish")
}

android.namespace = "br.com.arch.toolkit.statemachine"
android.testNamespace = "br.com.arch.toolkit.statemachine.test"

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

    // Test Libraries
    testImplementation(libs.junit.test)
    testImplementation(libs.mockito.test.core)
    testImplementation(libs.mockito.test.kotlin)
    testImplementation(libs.robolectric.test)
}
