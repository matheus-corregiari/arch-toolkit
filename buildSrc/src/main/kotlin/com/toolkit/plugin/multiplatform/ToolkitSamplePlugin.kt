package com.toolkit.plugin.multiplatform

import com.toolkit.plugin.android.setupAndroidLibraryModule
import com.toolkit.plugin.util.applyPlugins
import com.toolkit.plugin.util.multiplatform
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

@ExperimentalWasmDsl
@OptIn(ExperimentalKotlinGradlePluginApi::class)
internal class ToolkitSamplePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.applyPlugins("android-library")
        target.setupAndroidLibraryModule()

        target.plugins.apply("toolkit-multiplatform-base")

        with(target.multiplatform) {
            androidTarget {}
            jvm {}
            //wasmJs { wasm -> wasm.browser() }
            //js(IR) { browser() }
        }

        target.plugins.apply("toolkit-lint")
        target.plugins.apply("toolkit-test")
        target.plugins.apply("toolkit-optimize")
    }
}
