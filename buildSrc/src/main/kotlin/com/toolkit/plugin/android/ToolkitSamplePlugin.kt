package com.toolkit.plugin.android

import com.toolkit.plugin.util.androidApplication
import com.toolkit.plugin.util.applyPlugins
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.AbstractTestTask

internal class ToolkitSamplePlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        // Register lint before AGP adds its screenshot Kotlin API plugin.
        // Otherwise ktlint detects both that plugin and kotlin-android and creates duplicate tasks.
        plugins.apply("toolkit-lint")
        applyPlugins("android-application")
        plugins.apply("toolkit-android-base")

        setupAndroidApplicationModule()
        androidApplication.regularSourceSets()

        plugins.apply("toolkit-test")
        plugins.apply("toolkit-optimize")

        tasks.withType(AbstractTestTask::class.java)
            .configureEach { it.failOnNoDiscoveredTests.set(false) }
    }
}
