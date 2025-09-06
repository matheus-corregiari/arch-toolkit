package com.toolkit.plugin.util

import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.Project
import java.io.File

internal val Project.versionName: String
    get() = runGitCommand(
        fileName = "version-name.txt",
        command = "git describe",
        default = "0.0.0",
    )

@Suppress("DEPRECATION")
private val String.execute: Process get() = Runtime.getRuntime().exec(this)
private val Process.text: String get() = inputStream.bufferedReader().readText().trim()
private val String.executeWithText: String?
    get() {
        val process = execute
        if (process.waitFor() != 0) return null
        return process.text
    }

private fun Project.runGitCommand(
    fileName: String,
    command: String,
    default: String,
): String {
    val file = File("$rootDir/build", fileName)
    return when {
        file.exists().not() -> {
            when {
                validateGit() -> {
                    val output = command.executeWithText
                    if (output.isNullOrBlank()) {
                        default
                    } else {
                        file.parentFile.mkdirs()
                        file.writeText(output)
                        output
                    }
                }

                else -> default
            }
        }

        else -> file.readText().trim()
    }
}

private fun validateGit(): Boolean {
    val command = when {
        Os.isFamily(Os.FAMILY_WINDOWS) -> "git --version"
        else -> "whereis git"
    }
    return when (val output = command.executeWithText) {
        null -> false
        else -> (output.isEmpty() || output == "git:").not()
    }
}
