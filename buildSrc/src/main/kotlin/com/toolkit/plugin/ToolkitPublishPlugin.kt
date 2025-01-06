package com.toolkit.plugin

import com.toolkit.plugin.util.createLocalPathRepository
import com.toolkit.plugin.util.createSonatypeRepository
import com.toolkit.plugin.util.hasPlugins
import com.toolkit.plugin.util.missing
import com.toolkit.plugin.util.publishing
import com.toolkit.plugin.util.requireAny
import com.toolkit.plugin.util.setupJavadocAndSources
import com.toolkit.plugin.util.sign
import com.toolkit.plugin.util.versionName
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository
import org.gradle.plugins.signing.Sign
import org.jetbrains.kotlin.konan.file.File

internal class ToolkitPublishPlugin : Plugin<Project> {

    private val Project.aar: String?
        get() {
            val aarPath = "$projectDir/build/outputs/aar/$name-release.aar"
            return when {
                File(aarPath).exists -> aarPath
                else -> null
            }
        }
    private val Project.javadoc: String?
        get() {
            val aarPath = "$projectDir/build/libs/$name-javadoc.jar"
            return when {
                File(aarPath).exists -> aarPath
                else -> null
            }
        }
    private val Project.sources: String?
        get() = if (this.hasPlugins("jetbrains-multiplatform")) {
            val sourcesMultiPlatformPath = "$projectDir/build/libs/$name-android-sources.jar"
            when {
                File(sourcesMultiPlatformPath).exists -> sourcesMultiPlatformPath
                else -> null
            }
        } else {
            val sourcesPath = "$projectDir/build/libs/$name-sources.jar"
            when {
                File(sourcesPath).exists -> sourcesPath
                else -> null
            }
        }

    override fun apply(target: Project) {
        target.requireAny(
            "toolkit-publish",
            "toolkit-android-library",
            "toolkit-multiplatform-library"
        )

        if (target.missing(
                "OSSRH_USERNAME", "OSSRH_PASSWORD",
                "signing.keyId", "signing.password", "signing.secretKeyRingFile",
            )
        ) {
            println("Missing env variables")
            return
        }

        target.plugins.apply("maven-publish")
        target.plugins.apply("signing")

        // Setup Javadoc and sources artifacts
        target.setupJavadocAndSources()

        // Setup Publishing
        with(target.publishing) {
            repositories { repo ->
                repo.createLocalPathRepository(target)
                repo.createSonatypeRepository(target)
            }

            publications { container ->
//                container.register("android", MavenPublication::class.java) { pub ->
//                    target.aar?.let(pub::artifact)
//                    target.javadoc?.let { file ->
//                        pub.artifact(file) { artifact ->
//                            artifact.classifier = "javadoc"
//                            artifact.extension = "jar"
//                        }
//                    }
//                    target.sources?.let { file ->
//                        pub.artifact(file) { artifact ->
//                            artifact.classifier = "sources"
//                            artifact.extension = "jar"
//                        }
//                    }
//                    pub.pom { target.configurePom(it) }
//                }
                container.withType(MavenPublication::class.java) { pub ->
                    val suffix = when{
                        pub.name.contains("android") -> "-android"
                        pub.name.contains("jvm") -> "-jvm"
                        else -> ""
                    }
                    pub.groupId = target.properties["GROUP"] as String
                    pub.artifactId = "${target.name}$suffix"
                    pub.version = target.versionName
                }
            }
        }

        // Setup Signing
        target.tasks.withType(AbstractPublishToMaven::class.java)
            .configureEach { it.dependsOn(it.project.tasks.withType(Sign::class.java)) }
        with(target.sign) {
            sign(target.publishing.publications)
            setRequired {
                // signing is only required if the artifacts are to be published
                target.gradle.taskGraph.allTasks.any { (it is PublishToMavenRepository) }
            }
        }
    }

}
