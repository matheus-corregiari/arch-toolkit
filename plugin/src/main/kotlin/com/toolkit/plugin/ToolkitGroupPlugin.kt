package com.toolkit.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.com.google.gson.JsonArray

internal class ToolkitGroupPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.evaluationDependsOnChildren()
        target.applyPlugins("jetbrains-kover")

        // Try to unify coverage reports
        target.subprojects.onEach {
            target.dependencies.add("kover", it)
        }

        target.tasks.register("publishModules") {
            it.group = "groupTask"
            it.doLast {
                val json = JsonArray()
                val file = target.layout.buildDirectory.file("modules.txt").get().asFile
                file.parentFile.mkdirs()
                if (file.exists()) {
                    file.delete()
                }

                target.subprojects
                    .filter { it.plugins.hasPlugin("toolkit-publish") }
                    .map { "toolkit:${it.name}" }
                    .onEach { json.add(it) }

                file.createNewFile()
                file.writeText(json.toString())
            }
        }
    }
}
