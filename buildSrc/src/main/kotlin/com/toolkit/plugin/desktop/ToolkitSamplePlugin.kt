package com.toolkit.plugin.desktop

import com.toolkit.plugin.util.applyPlugins
import com.toolkit.plugin.util.projectJavaVersionCode
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

internal class ToolkitSamplePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.applyPlugins("jetbrains-kotlin-jvm")
        target.kotlinExtension.jvmToolchain(projectJavaVersionCode)
    }
}
