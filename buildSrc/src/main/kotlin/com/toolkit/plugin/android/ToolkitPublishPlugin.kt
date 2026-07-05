package com.toolkit.plugin.android

import com.toolkit.plugin.util.applyPlugins
import com.toolkit.plugin.util.configurePom
import com.toolkit.plugin.util.createLocalPathRepository
import com.toolkit.plugin.util.publishing
import com.toolkit.plugin.util.requireAny
import com.toolkit.plugin.util.vanniktechPublish
import com.toolkit.plugin.util.versionName
import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.plugins.signing.Sign

/**
 * Configures publication for Android library modules.
 *
 * The plugin wires Maven publication, Dokka-backed javadocs, Maven Central, and repository POM
 * metadata for Android artifacts.
 */
internal class ToolkitPublishPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.requireAny(
            currentPluginName = "toolkit-android-publish",
            names = arrayOf("toolkit-android-library")
        )

        // Applying necessary plugins
        target.plugins.apply("maven-publish")
        target.applyPlugins("jetbrains-dokka", "vanniktech-publish")

        // Setup Default Publishing
        with(target.publishing) {
            repositories { repo ->
                repo.createLocalPathRepository(target)
            }

            publications { container ->
                container.withType(MavenPublication::class.java) { pub ->
                    pub.groupId = target.properties["GROUP"] as String
                    pub.artifactId = "${target.name}"
                    pub.version = target.versionName
                    pub.pom { target.configurePom(it, false) }
                }
            }
        }

        // Setup Custom Publishing
        val localPublishRequested = target.gradle.startParameter.taskNames.any {
            it == "ciPublishLocal" ||
                it == "publishToMavenLocal" ||
                it.endsWith("ToMavenLocal")
        }
        with(target.vanniktechPublish) {
            if (!localPublishRequested) signAllPublications()
            publishToMavenCentral(true)
            configure(AndroidSingleVariantLibrary())
        }

        target.tasks.withType(Sign::class.java).configureEach {
            it.onlyIf {
                val localPublish = target.gradle.taskGraph.allTasks.any { task ->
                    task.name == "ciPublishLocal" ||
                        task.name == "publishToMavenLocal" ||
                        task.name.endsWith("ToMavenLocal")
                }
                !localPublish
            }
        }
    }
}
