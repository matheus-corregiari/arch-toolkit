package com.toolkit.plugin.web

import com.toolkit.plugin.util.applyPlugins
import com.toolkit.plugin.util.projectJavaVersionCode
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.AbstractTestTask
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

internal class ToolkitSamplePlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        applyPlugins("jetbrains-multiplatform")
        kotlinExtension.jvmToolchain(projectJavaVersionCode)

        tasks.withType(AbstractTestTask::class.java)
            .configureEach { it.failOnNoDiscoveredTests.set(false) }
    }
}
