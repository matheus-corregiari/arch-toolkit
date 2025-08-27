package com.toolkit.plugin.multiplatform

import com.toolkit.plugin.android.setupAndroidLibraryModule
import com.toolkit.plugin.util.applyPlugins
import com.toolkit.plugin.util.multiplatform
import com.toolkit.plugin.util.projectJavaVersionCode
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

@ExperimentalWasmDsl
@OptIn(ExperimentalKotlinGradlePluginApi::class)
internal class ToolkitSamplePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.applyPlugins("android-library")
        target.setupAndroidLibraryModule()

        target.applyPlugins("jetbrains-multiplatform")
        target.kotlinExtension.jvmToolchain(projectJavaVersionCode)

        with(target.multiplatform) {
            applyDefaultHierarchyTemplate {
                common {
                    group("java") {
                        withJvm()
                        withAndroidTarget()
                    }
                    group("web") {
                        withJs()
                        withWasmJs()
                    }
                }
            }
            androidTarget {}
            jvm {}
            wasmJs { wasm -> wasm.browser() }
            js(IR) { browser() }
            listOf(
                iosArm64(),
                iosX64(),
                iosSimulatorArm64(),
            ).forEach {
                it.binaries.framework {
                    baseName = "BacateKit"
                    isStatic = true
                }
            }
        }

        target.plugins.apply("toolkit-optimize")
    }
}
