package com.toolkit.plugin.android

import com.toolkit.plugin.util.attachAllTasksIntoAssembleRelease
import com.toolkit.plugin.util.configurePom
import com.toolkit.plugin.util.createLocalPathRepository
import com.toolkit.plugin.util.createSonatypeRepository
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
        get() = "$projectDir/build/outputs/aar/$name-release.aar".takeIf { File(it).exists }
    private val Project.javadoc: String?
        get() = "$projectDir/build/libs/$name-javadoc.jar".takeIf { File(it).exists }
    private val Project.sources: String?
        get() = "$projectDir/build/libs/$name-release-sources.jar".takeIf { File(it).exists }

    override fun apply(target: Project) {
        target.requireAny(
            "toolkit-android-publish",
            "toolkit-android-library",
            "toolkit-multiplatform-library"
        )
        target.plugins.apply("maven-publish")

        // Setup Javadoc and sources artifacts
        target.setupJavadocAndSources()

        // Setup Publishing
        with(target.publishing) {
            repositories { repo ->
                repo.createLocalPathRepository(target)
                repo.createSonatypeRepository(target)
            }

            publications { container ->
                container.register("Toolkit", MavenPublication::class.java) { pub ->
                    pub.groupId = target.properties["GROUP"] as String
                    pub.artifactId = target.name
                    pub.version = target.versionName

                    target.aar?.let(pub::artifact)
                    target.javadoc?.let { file ->
                        pub.artifact(file) { artifact ->
                            artifact.classifier = "javadoc"
                            artifact.extension = "jar"
                        }
                    }
                    target.sources?.let { file ->
                        pub.artifact(file) { artifact ->
                            artifact.classifier = "sources"
                            artifact.extension = "jar"
                        }
                    }
                    pub.pom { target.configurePom(it) }
                }
            }
        }

        // Attach all needed tasks into assembleRelease task
        target.attachAllTasksIntoAssembleRelease()

        // Setup Signing
        if (target.missing("signing.keyId", "signing.password", "signing.secretKeyRingFile")) {
            println("Missing env variables")
            return
        }
        target.plugins.apply("signing")
        target.tasks.withType(AbstractPublishToMaven::class.java).configureEach {
            it.dependsOn(it.project.tasks.withType(Sign::class.java))
        }
        with(target.sign) {
            sign(target.publishing.publications)
            setRequired { target.gradle.taskGraph.allTasks.any { (it is PublishToMavenRepository) } }
        }
    }
}
