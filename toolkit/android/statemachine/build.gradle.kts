plugins {
    id("toolkit-android-library")
    id("toolkit-android-publish")
}

android.namespace = "br.com.arch.toolkit.statemachine"
android.testNamespace = "br.com.arch.toolkit.statemachine.test"

android {
    testOptions {
        unitTests {
            all { test ->
                test.systemProperty("robolectric.logging.enabled", "true")
            }
        }
    }
}

dependencies {
    // Libraries
    compileOnly(libs.jetbrains.stdlib)
    compileOnly(libs.androidx.annotation)

    // Test Libraries
    androidTestImplementation(libs.androidx.appcompat)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espresso.intents)
    androidTestImplementation(libs.junit.test)
    androidTestImplementation(libs.jetbrains.stdlib)

    // Test Libraries
    testImplementation(libs.jetbrains.stdlib)
    testImplementation(libs.junit.test)
    testImplementation(libs.mockk.test.android)
    testImplementation(libs.mockk.test.agent)
    testImplementation(libs.robolectric.test)
}

publishing.publications.withType(MavenPublication::class.java) {
    pom {
        distributionManagement {
            relocation {
                message.set("This library was moved from another repository")
                groupId.set("io.github.matheus-corregiari")
                artifactId.set("arch-android")
                version.set("1.0.0")
            }
        }
    }
}
