import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("toolkit-desktop-sample")
    alias(libs.plugins.jetbrains.compose.compiler)
    alias(libs.plugins.jetbrains.compose.kotlin)
}

dependencies {
    // Regular Dependencies
    implementation(compose.runtime)
    implementation(compose.ui)
    implementation(compose.material3)
    implementation(compose.desktop.currentOs)
    implementation(compose.desktop.common)
}

compose.desktop {
    application {
        mainClass = "br.com.arch.toolkit.sample.github.desktop.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Exe)
            packageName = "Github Sample"
            packageVersion = "1.0.0"
        }
    }
}
