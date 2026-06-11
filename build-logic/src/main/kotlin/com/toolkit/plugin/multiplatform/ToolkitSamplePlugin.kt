package com.toolkit.plugin.multiplatform

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import com.toolkit.plugin.util.applyPlugins
import com.toolkit.plugin.util.libs
import com.toolkit.plugin.util.multiplatform
import com.toolkit.plugin.util.version
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

@ExperimentalWasmDsl
@OptIn(ExperimentalKotlinGradlePluginApi::class)
internal class ToolkitSamplePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.applyPlugins("android-kmp-library")
        target.plugins.apply("toolkit-multiplatform-base")

        val android = target.multiplatform.targets.getByName("android")
            as KotlinMultiplatformAndroidLibraryTarget
        with(android) {
            namespace = "br.com.arch.toolkit.sample.${target.name.replace("-", "")}"
            compileSdk = target.libs.version("build-sdk-compile").toInt()
            minSdk = target.libs.version("build-sdk-min-sample").toInt()
            buildToolsVersion = target.libs.version("build-tools")
            androidResources { enable = false }
        }

        with(target.multiplatform) {
            jvm {}
            wasmJs { browser() }
            js { browser() }
        }

        target.plugins.apply("toolkit-lint")
        target.plugins.apply("toolkit-test")
        target.plugins.apply("toolkit-optimize")
    }
}
