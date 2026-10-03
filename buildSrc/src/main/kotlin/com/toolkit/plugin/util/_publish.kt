package com.toolkit.plugin.util

import groovy.util.Node
import org.codehaus.groovy.runtime.DefaultGroovyMethods
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPublication

internal fun RepositoryHandler.createLocalPathRepository(project: Project) = maven { maven ->
    maven.name = "LocalPath"
    maven.url = project.uri(project.rootProject.layout.buildDirectory.asFile.get().absolutePath)
}

internal fun Project.configurePom(pom: MavenPom, addDependencies: Boolean) {
    // Main Configuration
    if (pom.name.orNull.isNullOrBlank() && hasProperty("NAME")) {
        pom.name.set(providers.gradleProperty("NAME"))
    }
    if (pom.description.orNull.isNullOrBlank() && hasProperty("DESCRIPTION")) {
        pom.description.set(providers.gradleProperty("DESCRIPTION"))
    }
    if (pom.url.orNull.isNullOrBlank() && hasProperty("REPO_URL")) {
        pom.url.set(providers.gradleProperty("REPO_URL"))
    }

    // SCM
    pom.scm {
        it.url.set(providers.gradleProperty("REPO_GIT_URL"))
    }

    // Developer Configuration
    pom.developers { developers ->
        developers.developer { dev ->
            dev.id.set("melete")
            dev.name.set("Melete")
            dev.email.set("melete@notValidEmail.com")
            dev.organization.set("Wonderland")
            dev.url.set(providers.gradleProperty("DEV_URL"))
        }
    }

    // License Configuration
    pom.licenses { licenses ->
        licenses.license { license ->
            license.name.set(providers.gradleProperty("LICENCE_NAME"))
            license.url.set(providers.gradleProperty("LICENCE_URL"))
            license.distribution.set(providers.gradleProperty("LICENCE_DIST"))
        }
    }

    pom.ciManagement { ci ->
        ci.system.set("GitHub Actions")
        ci.url.set("${pom.url.orNull}/actions")
    }

    if (addDependencies.not()) return
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
        val allMaven = publishExtension.publications.filterIsInstance<MavenPublication>()
        val pub = allMaven.firstOrNull { it.artifactId.endsWith("-android") }
            ?: allMaven.firstOrNull()
        pub?.let {
            val node = appendNode("dependency")
            node.appendNode("groupId", pub.groupId)
            node.appendNode("artifactId", pub.artifactId)
            node.appendNode("version", pub.version)
            node.appendNode("scope", scope)
        }
    } else {
        val group = dependency.group.takeIf { it.isNullOrBlank().not() } ?: return
        val name = dependency.name.takeIf { it.isNotBlank() } ?: return
        val version = dependency.version.takeIf { it.isNullOrBlank().not() } ?: return

        val node = appendNode("dependency")
        node.appendNode("groupId", group)
        node.appendNode("artifactId", name)
        node.appendNode("version", version)
        node.appendNode("scope", scope)
    }
}
