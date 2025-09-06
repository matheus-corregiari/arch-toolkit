package com.toolkit.plugin

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.Lint
import com.toolkit.plugin.util.androidApplication
import com.toolkit.plugin.util.androidLibrary
import com.toolkit.plugin.util.applyPlugins
import com.toolkit.plugin.util.detekt
import com.toolkit.plugin.util.ktLint
import com.toolkit.plugin.util.libs
import com.toolkit.plugin.util.projectJavaVersionName
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

internal class ToolkitLintPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.applyPlugins("detekt", "ktlint")

        // Detekt configuration
        target.dependencies.add(
            "detektPlugins",
            target.libs.findLibrary("detekt-formatting").get()
        )
        with(target.detekt) {
            parallel = true
            disableDefaultRuleSets = true
            buildUponDefaultConfig = true

            autoCorrect = true
            allRules = false
            config.setFrom("${target.rootDir}/tools/detekt-config.yml")
            baseline = File("${target.rootDir}/tools/detekt-baseline.xml")
        }
        with(target.tasks) {
            withType(Detekt::class.java).configureEach { detekt ->
                detekt.jvmTarget = projectJavaVersionName
                detekt.reports {
                    it.xml.required.set(true)
                    it.html.required.set(true)
                    it.md.required.set(true)
                    it.txt.required.set(true)
                    it.sarif.required.set(true)
                }
            }
            withType(DetektCreateBaselineTask::class.java).configureEach {
                it.jvmTarget = projectJavaVersionName
            }
        }

        // KtLint configuration
        with(target.ktLint) {
            android.set(true)
            outputColorName.set("RED")
        }

        // Regular Lint configuration
        kotlin.runCatching { setForApplication(target, target.androidApplication) }
        kotlin.runCatching { setForLibrary(target, target.androidLibrary) }
    }

    private fun setForApplication(target: Project, android: ApplicationExtension) = with(android) {
        lint { setup(target) }
    }

    private fun setForLibrary(target: Project, android: LibraryExtension) = with(android) {
        lint { setup(target) }
    }

    private fun Lint.setup(target: Project) {
        checkReleaseBuilds = true
        abortOnError = true
        ignoreWarnings = false
        absolutePaths = false
        warningsAsErrors = false

        htmlOutput = File("${target.rootDir}/build/reports/lint/html/${target.name}-lint.html")
        xmlOutput = File("${target.rootDir}/build/reports/lint/xml/${target.name}-lint.xml")
    }
}
