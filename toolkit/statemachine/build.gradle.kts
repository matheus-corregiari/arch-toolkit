plugins {
    id("toolkit-library")
    id("toolkit-publish")
}

android.namespace = "br.com.arch.toolkit.statemachine"
android.testNamespace = "br.com.arch.toolkit.statemachine.test"

dependencies {
    // Libraries
    compileOnly(libraries.jetbrains.stdlib.jdk8)
    compileOnly(libraries.androidx.annotation)

    // Test Libraries
    androidTestImplementation(libraries.androidx.appcompat)
    androidTestImplementation(libraries.androidx.test.core)
    androidTestImplementation(libraries.androidx.test.junit)
    androidTestImplementation(libraries.androidx.test.espresso.intents)
    androidTestImplementation(libraries.junit.test)

    // Test Libraries
    testImplementation(libraries.junit.test)
    testImplementation(libraries.mockito.test.core)
    testImplementation(libraries.mockito.test.kotlin)
    testImplementation(libraries.robolectric.test)
}