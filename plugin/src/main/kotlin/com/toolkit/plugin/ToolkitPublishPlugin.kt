package com.toolkit.plugin

import com.toolkit.plugin.util.versionName
import groovy.util.Node
import org.codehaus.groovy.runtime.DefaultGroovyMethods
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPublication
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
            val aarPath = "$projectDir/build/libs/$name-sources.jar"
            return when {
                File(aarPath).exists -> aarPath
                else -> null
            }
        }

    override fun apply(target: Project) {
        if (target.plugins.hasPlugin("toolkit-library").not()) {
            error("To use sample-compose plugin you must implement toolkit-library plugin")
        }
        target.plugins.apply("maven-publish")

        with(target.publishing) {
            repositories { repo ->
                repo.createGithubRepository(target)
                repo.createLocalPathRepository(target)
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

//        with(target.sign) {
//            setRequired {
//                // signing is only required if the artifacts are to be published
//                target.gradle.taskGraph.allTasks.any { (it is PublishToMavenRepository) }
//            }
//        }
    }

    private fun RepositoryHandler.createGithubRepository(project: Project) = maven { maven ->
        maven.name = "Github"
        maven.url = project.uri("https://maven.pkg.github.com/matheus-corregiari/arch-toolkit")
        maven.credentials { cred ->
            cred.username = "user"
            cred.password = "token"
        }
    }

    private fun RepositoryHandler.createLocalPathRepository(project: Project) = maven { maven ->
        maven.name = "LocalPath"
        maven.url = project.uri(project.rootProject.layout.buildDirectory.asFile.get().absolutePath)
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

        val configList = listOf("implementation", "api").mapNotNull(configurations::findByName)
        if (configList.isNotEmpty()) {
            pom.withXml { xml ->
                val dependencyNode: Node = xml.asNode().appendNode("dependencies")
                configList.forEach { config ->
                    config.dependencies.forEach { dependencyNode.addDependency(it) }
                }
            }
        }
    }

    private fun Node.addDependency(dependency: Dependency) {
        val projectDependency =
            DefaultGroovyMethods.getProperties(dependency)["dependencyProject"] as? Project

        if (projectDependency != null) {
            val publishExtension = projectDependency.publishing
            publishExtension.publications.filterIsInstance(MavenPublication::class.java)
                .onEach { pub ->
                    val node = appendNode("dependency")
                    node.appendNode("groupId", pub.groupId)
                    node.appendNode("artifactId", pub.artifactId)
                    node.appendNode("version", pub.version)
                    node.appendNode("scope", "runtime")
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
            node.appendNode("scope", "runtime")
        }
    }
}
