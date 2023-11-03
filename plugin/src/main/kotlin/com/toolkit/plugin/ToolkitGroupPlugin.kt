package com.toolkit.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

internal class ToolkitGroupPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.applyPlugins("jetbrains-kover")

        target.subprojects.onEach {
            target.dependencies.add("kover", it)
        }
    }
}
