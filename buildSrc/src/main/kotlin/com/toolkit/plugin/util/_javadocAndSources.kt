package com.toolkit.plugin.util

import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import org.jetbrains.dokka.gradle.DokkaTask

internal fun Project.setupJavadocAndSources() {
    setupSources()
    setupJavadoc()
}

private fun Project.setupJavadoc() {
    applyPlugins("jetbrains-dokka")
    tasks.register("javadocJar", Jar::class.java) { task ->
        task.group = "documentation"
        val dokka = task.project.tasks.named("dokkaHtml", DokkaTask::class.java)
        task.dependsOn(dokka)
        task.from(dokka.flatMap(DokkaTask::outputDirectory))
        task.archiveClassifier.set("javadoc")
        task.archiveFileName.set("${task.project.name}-release-javadoc.jar")
    }.get()
}

private fun Project.setupSources() {
    tasks.whenTaskAdded { task ->
        if (task !is Jar) return@whenTaskAdded
        when (task.name) {
            "releaseSourcesJar" -> task.archiveFileName.set("${task.project.name}-release-sources.jar")
            "debugSourcesJar" -> task.archiveFileName.set("${task.project.name}-debug-sources.jar")
        }
    }
}
