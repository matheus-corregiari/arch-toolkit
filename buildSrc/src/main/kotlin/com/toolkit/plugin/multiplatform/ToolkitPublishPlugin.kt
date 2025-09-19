package com.toolkit.plugin.multiplatform

import com.toolkit.plugin.util.androidLibrary
import com.toolkit.plugin.util.applyPlugins
import com.toolkit.plugin.util.configurePom
import com.toolkit.plugin.util.createLocalPathRepository
import com.toolkit.plugin.util.multiplatform
import com.toolkit.plugin.util.publishing
import com.toolkit.plugin.util.requireAny
import com.toolkit.plugin.util.vanniktechPublish
import com.toolkit.plugin.util.versionName
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication

internal class ToolkitPublishPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.requireAny(
            "toolkit-android-library",
            "toolkit-multiplatform-library",
            "toolkit-multiplatform-publish"
        )

        // Applying necessary plugins
        target.plugins.apply("maven-publish")
        target.applyPlugins("jetbrains-dokka", "vanniktech-publish")

        // Setup Android Variant
        with(target.androidLibrary) {
            publishing.singleVariant("release") {
                withSourcesJar()
                withJavadocJar()
            }
        }

        // Setup Multiplatform properly
        with(target.multiplatform) {
            withSourcesJar(true)
            androidTarget().publishLibraryVariants("release")
        }

        // Setup Default Publishing
        with(target.publishing) {
            repositories { repo ->
                repo.createLocalPathRepository(target)
            }

            publications { container ->
                container.withType(MavenPublication::class.java) { pub ->
                    pub.groupId = target.properties["GROUP"] as String
                    pub.version = target.versionName
                    pub.pom { target.configurePom(it, false) }
                }
            }
        }

        // Setup Custom Publishing
        with(target.vanniktechPublish) {
            signAllPublications()
            publishToMavenCentral(true)
            configure(
                KotlinMultiplatform(
                    javadocJar = JavadocJar.Dokka("dokkaGenerate"),
                    sourcesJar = true,
                    androidVariantsToPublish = listOf("release"),
                )
            )
        }
    }
}
