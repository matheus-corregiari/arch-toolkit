plugins { id("toolkit-multiplatform-sample") }

android.namespace = "br.com.arch.toolkit.sample.http"
android.androidResources.enable = false
android.buildFeatures.buildConfig = false

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.ktor.content.negotiation)
                implementation(libs.ktor.serialization.json)
                implementation(libs.ktor.logging)
                implementation(libs.arch.lumber)
            }
        }
        javaMain { dependencies { implementation(libs.ktor.client.okhttp) } }
        appleMain { dependencies { implementation(libs.ktor.client.darwin) } }
    }
}
