@file:Suppress("UnstableApiUsage")

package com.toolkit.plugin.android

import com.toolkit.plugin.util.applyPlugins
import com.toolkit.plugin.util.projectJavaVersionCode
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

internal class ToolkitBasePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.applyPlugins("jetbrains-kotlin-android")
        with(target.kotlinExtension) {
            jvmToolchain(projectJavaVersionCode)
        }
    }
}
