import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("toolkit-desktop-sample")
    alias(libs.plugins.jetbrains.compose.compiler)
    alias(libs.plugins.jetbrains.compose.kotlin)
}

dependencies {
    // Sample Modules
    implementation(project(":sample:shared:app"))

    // Arch Toolkit Dependencies
    implementation(libs.arch.lumber)
    implementation(project(":toolkit:multi:splinter"))
    implementation(project(":toolkit:multi:event-observer"))
    implementation(project(":toolkit:multi:event-observer-compose"))

    // Jetbrains Compose Tools
    implementation(compose.runtime)
    implementation(compose.desktop.currentOs)
    implementation(compose.desktop.common)
    implementation(compose.components.resources)
    implementation(compose.materialIconsExtended)

    // Other Dependencies
    implementation(libs.di.koin.core)
    implementation(libs.jetbrains.coroutines.core)
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
