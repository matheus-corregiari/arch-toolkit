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
import dev.detekt.gradle.Detekt
import dev.detekt.gradle.DetektCreateBaselineTask
import dev.detekt.gradle.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

internal class ToolkitLintPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.applyPlugins("detekt", "ktlint")

        // Detekt configuration
        with(target.extensions.getByType(DetektExtension::class.java)) {
            parallel.set(true)
            buildUponDefaultConfig.set(true)

            allRules.set(false)
            config.setFrom("${target.rootDir}/tools/detekt-config.yml")
        }
        with(target.tasks) {
            withType(Detekt::class.java).configureEach { detekt ->
                detekt.jvmTarget.set(projectJavaVersionName)
                detekt.reports {
                    it.html.required.set(true)
                    it.checkstyle.required.set(true)
                    it.markdown.required.set(true)
                    it.sarif.required.set(true)
                }
            }
            withType(DetektCreateBaselineTask::class.java).configureEach {
                it.jvmTarget.set(projectJavaVersionName)
            }
            named("detekt").configure {
                it.dependsOn(withType(Detekt::class.java).matching { task ->
                    task.name.endsWith("SourceSet")
                })
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
