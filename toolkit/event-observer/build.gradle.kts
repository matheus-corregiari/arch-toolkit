import com.toolkit.plugin.multiplatform.androidMain
import com.toolkit.plugin.multiplatform.androidUnitTest

plugins {
    id("toolkit-multiplatform-library")
    id("toolkit-publish")
}

android.namespace = "br.com.arch.toolkit.livedata"

kotlin {
    sourceSets {

        // Libraries
        androidMain {
            dependencies {
                implementation(libraries.jetbrains.stdlib.jdk8)
                implementation(libraries.jetbrains.coroutines.core)
                implementation(libraries.jetbrains.coroutines.android)
                implementation(libraries.androidx.lifecycle.livedata)
            }
        }

        // Test Libraries
        androidUnitTest {
            dependencies {
                implementation(libraries.androidx.lifecycle.livedata)
                implementation(libraries.androidx.lifecycle.runtime)
                implementation(libraries.androidx.test.core)
                implementation(libraries.jetbrains.test.coroutines)
                implementation(libraries.mockito.test.core)
                implementation(libraries.mockito.test.kotlin)
            }
        }
    }
}
