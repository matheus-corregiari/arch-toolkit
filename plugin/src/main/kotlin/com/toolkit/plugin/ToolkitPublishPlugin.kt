package com.toolkit.plugin

import com.toolkit.plugin.util.missing
import com.toolkit.plugin.util.publishing
import com.toolkit.plugin.util.requireAny
import com.toolkit.plugin.util.setupJavadocAndSources
import com.toolkit.plugin.util.sign
import com.toolkit.plugin.util.versionName
import groovy.util.Node
import org.codehaus.groovy.runtime.DefaultGroovyMethods
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository
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
        get() {
            val sourcesPath = "$projectDir/build/libs/$name-sources.jar"
            val sourcesMultiPlatformPath = "$projectDir/build/libs/$name-kotlin-sources.jar"
            return when {
                File(sourcesPath).exists -> sourcesPath
                File(sourcesMultiPlatformPath).exists -> sourcesMultiPlatformPath
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
                "OSSRH_USERNAME",
                "OSSRH_PASSWORD",
                "signing.keyId",
                "signing.password",
                "signing.secretKeyRingFile",
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

            publications {
                it.register("Toolkit", MavenPublication::class.java) { pub ->
                    pub.groupId = target.properties["GROUP"] as String
                    pub.artifactId = target.name
                    pub.version = target.versionName

                    if (target.aar != null) {
                        pub.artifact(target.aar)
                    }
                    if (target.javadoc != null) {
                        pub.artifact(target.javadoc) { artifact ->
                            artifact.classifier = "javadoc"
                            artifact.extension = "jar"
                        }
                    }
                    if (target.sources != null) {
                        pub.artifact(target.sources) { artifact ->
                            artifact.classifier = "sources"
                            artifact.extension = "jar"
                        }
                    }
                    pub.pom { target.configurePom(it) }
                }
            }
        }

        // Setup Signing
        with(target.sign) {
            sign(target.publishing.publications)
            setRequired {
                // signing is only required if the artifacts are to be published
                target.gradle.taskGraph.allTasks.any { (it is PublishToMavenRepository) }
            }
        }
    }

    private fun RepositoryHandler.createLocalPathRepository(project: Project) = maven { maven ->
        maven.name = "LocalPath"
        maven.url = project.uri(project.rootProject.layout.buildDirectory.asFile.get().absolutePath)
    }

    private fun RepositoryHandler.createSonatypeRepository(project: Project) = maven { maven ->
        maven.name = "Sonatype"
        maven.url = project.uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
        maven.credentials { cred ->
            cred.username =
                System.getenv("OSSRH_USERNAME") ?: (project.properties["OSSRH_USERNAME"] as String)
            cred.password =
                System.getenv("OSSRH_PASSWORD") ?: (project.properties["OSSRH_PASSWORD"] as String)
        }
    }

    private fun Project.configurePom(pom: MavenPom) {
        // Main Configuration
        if (pom.name.orNull.isNullOrBlank() && hasProperty("NAME")) {
            pom.name.set(properties["NAME"] as String)
        }
        if (pom.description.orNull.isNullOrBlank() && hasProperty("DESCRIPTION")) {
            pom.description.set(properties["DESCRIPTION"] as String)
        }
        if (pom.url.orNull.isNullOrBlank() && hasProperty("REPO_URL")) {
            pom.url.set(properties["REPO_URL"] as String)
        }

        // SCM
        pom.scm {
            it.url.set(properties["REPO_GIT_URL"] as String)
        }

        // Developer Configuration
        pom.developers { developers ->
            developers.developer { dev ->
                dev.id.set("melete")
                dev.name.set("Melete")
                dev.email.set("melete@notValidEmail.com")
                dev.organization.set("Wonderland")
                dev.url.set(properties["DEV_URL"] as String)
            }
        }

        // License Configuration
        pom.licenses { licenses ->
            licenses.license { license ->
                license.name.set(properties["LICENCE_NAME"] as String)
                license.url.set(properties["LICENCE_URL"] as String)
                license.distribution.set(properties["LICENCE_DIST"] as String)
            }
        }

        val mapOfConfigurations = mapOf(
            "runtime" to "implementation",
            "compile" to "api",
            "provided" to "compileOnly"
        ).mapNotNull { (scope, configuration) ->
            configurations.findByName(configuration)?.let { scope to it }
        }.toMap()
        if (mapOfConfigurations.isNotEmpty()) {
            pom.withXml { xml ->
                val dependencyNode: Node = xml.asNode().appendNode("dependencies")
                mapOfConfigurations.forEach { (scope, configuration) ->
                    configuration.dependencies.forEach { dependencyNode.addDependency(it, scope) }
                }
            }
        }
    }

    private fun Node.addDependency(dependency: Dependency, scope: String) {
        val projectDependency =
            DefaultGroovyMethods.getProperties(dependency)["dependencyProject"] as? Project

        if (projectDependency != null) {
            val publishExtension = projectDependency.publishing
            publishExtension.publications.filterIsInstance<MavenPublication>().onEach { pub ->
                val node = appendNode("dependency")
                node.appendNode("groupId", pub.groupId)
                node.appendNode("artifactId", pub.artifactId)
                node.appendNode("version", pub.version)
                node.appendNode("scope", scope)
            }
        } else {
            val group = dependency.group.takeIf { it.isNullOrBlank().not() }
            val name = dependency.name.takeIf { it.isNullOrBlank().not() }
            val version = dependency.version.takeIf { it.isNullOrBlank().not() }

            if (group == null || name == null || version == null) return

            val node = appendNode("dependency")
            node.appendNode("groupId", group)
            node.appendNode("artifactId", name)
            node.appendNode("version", version)
            node.appendNode("scope", scope)
        }
    }
}
