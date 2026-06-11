package com.toolkit.plugin.multiplatform

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import com.toolkit.plugin.util.applyPlugins
import com.toolkit.plugin.util.libs
import com.toolkit.plugin.util.multiplatform
import com.toolkit.plugin.util.version
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

@OptIn(ExperimentalWasmDsl::class)
internal class ToolkitLibraryPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.applyPlugins("android-kmp-library", "jetbrains-kover")
        target.plugins.apply("toolkit-multiplatform-base")

        val android = target.multiplatform.targets.getByName("android")
            as KotlinMultiplatformAndroidLibraryTarget
        with(android) {
            namespace = "br.com.arch.toolkit.${target.name.replace("-", "")}"
            compileSdk = target.libs.version("build-sdk-compile").toInt()
            minSdk = target.libs.version("build-sdk-min-toolkit").toInt()
            buildToolsVersion = target.libs.version("build-tools")
            androidResources { enable = false }
            withHostTest {
                enableCoverage = true
                isIncludeAndroidResources = true
                isReturnDefaultValues = true
            }
            lint {
                checkReleaseBuilds = true
                abortOnError = true
                ignoreWarnings = false
                absolutePaths = false
                warningsAsErrors = false
            }
            testCoverage { jacocoVersion = target.libs.version("jacoco") }
            target.file("consumer-proguard-rules.pro")
                .takeIf { it.exists() }
                ?.let(optimization.consumerKeepRules::file)
        }

        with(target.multiplatform) {
            jvm {}

            // Web Targets
            wasmJs { wasm ->
                wasm.browser { testTask { it.useKarma { useChromeHeadless() } } }
                wasm.binaries.library()
            }
            js {
                browser { testTask { it.useKarma { useChromeHeadless() } } }
                binaries.library()
            }

            // iOS Targets
            val exportName = target.name.split("-").joinToString("") {
                it.replaceFirstChar(Char::uppercase)
            }
            listOf(
                iosArm64(),
                iosSimulatorArm64(),
            ).forEach { target ->
                target.binaries.framework {
                    baseName = "${exportName}Kit"
                    isStatic = true
                }
            }
        }

        target.plugins.apply("toolkit-lint")
        target.plugins.apply("toolkit-test")
        target.plugins.apply("toolkit-optimize")
    }
}
