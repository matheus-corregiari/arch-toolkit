@file:Suppress("UnstableApiUsage")

package com.toolkit.plugin.android

import org.gradle.api.Plugin
import org.gradle.api.Project

internal class ToolkitBasePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        // AGP 9 provides built-in Kotlin support for Android modules.
    }
}
