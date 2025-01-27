package com.toolkit.plugin.multiplatform

import com.toolkit.plugin.util.createLocalPathRepository
import com.toolkit.plugin.util.createSonatypeRepository
import com.toolkit.plugin.util.missing
import com.toolkit.plugin.util.multiplatform
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

internal class ToolkitPublishPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.requireAny(
            "toolkit-android-library",
            "toolkit-multiplatform-library",
            "toolkit-multiplatform-publish"
        )
        target.plugins.apply("maven-publish")

        with(target.multiplatform){
            withSourcesJar(true)
            androidTarget().publishLibraryVariants("release")
        }

        // Setup Javadoc and sources artifacts
        target.setupJavadocAndSources()

        // Setup Publishing
        with(target.publishing) {
            repositories { repo ->
                repo.createLocalPathRepository(target)
                repo.createSonatypeRepository(target)
            }

            publications { container ->
                container.withType(MavenPublication::class.java) { pub ->
                    val suffix = when {
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
