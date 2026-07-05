@file:Suppress("UnstableApiUsage")

package com.toolkit.plugin.multiplatform

import com.toolkit.plugin.util.applyPlugins
import com.toolkit.plugin.util.multiplatform
import com.toolkit.plugin.util.projectJavaVersionCode
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

@OptIn(ExperimentalKotlinGradlePluginApi::class, ExperimentalWasmDsl::class)
internal class ToolkitBasePlugin : Plugin<Project> {

    override fun apply(target: Project) {
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
        }
    }
}
