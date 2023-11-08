package com.toolkit.plugin.util

import com.toolkit.plugin.androidLibrary
import org.gradle.api.Project
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.konan.file.File

internal fun Project.setupJavadocAndSources() {
    val sourcesJar = setupSources()
    val javadocJar = setupJavadoc()
    setupArtifacts(javadocJar, sourcesJar)

    tasks.whenTaskAdded {
        if (it.name == "assembleRelease") {
            it.finalizedBy(sourcesJar, javadocJar)
        }
    }
}

private fun Project.setupJavadoc(): Jar {
    configurations.maybeCreate("jacocoDeps")

    val javadoc = tasks.register("javadoc", Javadoc::class.java) {
        val list = ArrayList<java.io.File>()
        androidLibrary.sourceSets.forEach { set -> list.addAll(set.java.srcDirs) }

        it.isFailOnError = false
        it.setExcludes(listOf("**/*.kt", "**/*.java"))
        it.source(list)
        it.classpath += files(androidLibrary.bootClasspath.joinToString(separator = File.separator))
        it.classpath += configurations.named("jacocoDeps").get()
    }.get()

    return tasks.register("javadocJar", Jar::class.java) {
        it.dependsOn(javadoc)
        it.archiveClassifier.set("javadoc")
        it.from(javadoc.destinationDir)
    }.get()
}

private fun Project.setupSources() = tasks.register("sourcesJar", Jar::class.java) {
    val mainSource = androidLibrary.sourceSets.named("main").get().java.srcDirs
    it.from(mainSource)
    it.archiveClassifier.set("sources")
}.get()

private fun Project.setupArtifacts(javadoc: Jar, sources: Jar) {
    artifacts {
        it.add("archives", javadoc)
        it.add("archives", sources)
    }
}
