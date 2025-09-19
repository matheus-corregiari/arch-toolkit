package com.toolkit.plugin.multiplatform

import com.android.build.api.variant.impl.capitalizeFirstChar
import com.toolkit.plugin.android.setupAndroidLibraryModule
import com.toolkit.plugin.util.android
import com.toolkit.plugin.util.applyPlugins
import com.toolkit.plugin.util.multiplatform
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

@OptIn(ExperimentalWasmDsl::class)
internal class ToolkitLibraryPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.applyPlugins("android-library")
        target.setupAndroidLibraryModule()

        target.plugins.apply("toolkit-multiplatform-base")

        with(target.multiplatform) {

            // Java Targets
            androidTarget {}
            jvm {}

            // Web Targets
            wasmJs { wasm ->
                wasm.browser { testTask { it.useKarma { useChromeHeadless() } } }
                wasm.binaries.library()
            }
            js(IR) {
                browser { testTask { it.useKarma { useChromeHeadless() } } }
                binaries.library()
            }

            // iOS Targets
//            val exportName = target.name.split("-").joinToString(
//                separator = "",
//                transform = String::capitalizeFirstChar
//            )
//            val exportId = (target.android.namespace ?: "").trim()
//            listOf(
//                iosArm64(),
//                iosX64(),
//                iosSimulatorArm64(),
//            ).forEach { target ->
//                target.binaries.framework {
//                    baseName = "${exportName}Kit"
//                    isStatic = true
//                    freeCompilerArgs += listOf("-bundle-id", exportId)
//                }
//            }
        }

        target.plugins.apply("toolkit-lint")
        target.plugins.apply("toolkit-test")
        target.plugins.apply("toolkit-optimize")
    }
}
