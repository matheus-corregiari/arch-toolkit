package com.toolkit.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.com.google.gson.JsonArray
import java.nio.file.Files

internal class ToolkitGroupPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.evaluationDependsOnChildren()

        // Generate file containing all modules with publish plugin attached
        target.tasks.register("publishModules") { task ->
            task.group = "groupTask"
            // Store target directory into a variable to avoid project reference in the configuration cache
            val directory = task.project.layout.buildDirectory.get()
            val file = directory.file("modules.txt").asFile
            val fileMulti = directory.file("modules-multi.txt").asFile
            val publishMultiplatformLibraries = task.project.allMultiplatform()
            task.doLast { _ ->
                Files.createDirectories(directory.asFile.toPath())
                val json = JsonArray()
                val multiJson = JsonArray()
                publishMultiplatformLibraries.onEach(json::add)
                publishMultiplatformLibraries.onEach(multiJson::add)

                if (file.exists().not()) file.createNewFile()
                file.writeText(json.toString())
                println("ALL Publish module list generated at: $file")
                println(json.toString())

                if (fileMulti.exists().not()) fileMulti.createNewFile()
                fileMulti.writeText(multiJson.toString())
                println("MULTIPLATFORM Publish module list generated at: $file")
                println(multiJson.toString())
            }
        }
    }

    private fun Project.allMultiplatform(prefix: String = "toolkit"): List<String> =
        if (plugins.hasPlugin("toolkit-multiplatform-publish")) listOf("$prefix:$name")
        else subprojects.filter { it.subprojects.isEmpty() }
            .flatMap { it.allMultiplatform("$prefix:multi") }
}
