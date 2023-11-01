package com.toolkit.plugin

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.Lint
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.extensions.DetektReport
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

internal class ToolkitLintPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.applyPlugins("detekt", "ktlint")

        // Detekt configuration
        target.dependencies.add(
            "detektPlugins",
            target.libraries.findLibrary("detekt-formatting").get()
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
                detekt.jvmTarget = "1.8"
                detekt.reports.run { listOf(html, xml, txt, sarif, md) }.onEach { it.setup() }
            }
            withType(DetektCreateBaselineTask::class.java).configureEach {
                it.jvmTarget = "1.8"
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

    private fun DetektReport.setup() {
        required.set(true)
        outputLocation.set(File("build/reports/detekt.${type.extension}"))
    }
}
