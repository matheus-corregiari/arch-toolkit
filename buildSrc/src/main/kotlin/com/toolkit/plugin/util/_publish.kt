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

internal fun RepositoryHandler.createSonatypeRepository(project: Project) {
    if (project.missing("OSSRH_USERNAME", "OSSRH_PASSWORD")) return
    maven { maven ->
        maven.name = "Sonatype"
        maven.url = project.uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
        maven.credentials { cred ->
            cred.username =
                System.getenv("OSSRH_USERNAME") ?: (project.properties["OSSRH_USERNAME"] as String)
            cred.password =
                System.getenv("OSSRH_PASSWORD") ?: (project.properties["OSSRH_PASSWORD"] as String)
        }
    }
}

internal fun Project.configurePom(pom: MavenPom, addDependencies: Boolean) {
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
