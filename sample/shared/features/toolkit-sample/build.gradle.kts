plugins {
    id("toolkit-multiplatform-sample")
    alias(libs.plugins.jetbrains.compose.compiler)
    alias(libs.plugins.jetbrains.compose.kotlin)
    alias(libs.plugins.jetbrains.serialization)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.easy.navigation)
}

android.namespace = "br.com.arch.toolkit.sample.feature.toolkit"
android.androidResources.enable = false
android.buildFeatures.buildConfig = false

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":sample:shared:structure:core"))
                implementation(project(":sample:shared:structure:design:core"))
                implementation(project(":sample:shared:structure:design:widget"))
                implementation(project(":sample:shared:data:repository"))
                implementation(libs.easy.navigation)
                implementation("androidx.navigation3:navigation3-runtime:1.2.0-alpha04")
                implementation(libs.jetbrains.serialization)
                implementation(libs.di.koin.composeViewModel)
                implementation(libs.jetbrains.compose.runtime)
                implementation(libs.jetbrains.compose.runtime.saveable)
                implementation(libs.jetbrains.compose.ui)
                implementation(libs.jetbrains.compose.foundation)
                implementation(libs.jetbrains.compose.material3)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.jetbrains.kotlin.test)
                implementation(libs.jetbrains.coroutines.test)
            }
        }
    }
}

val snippetSource = rootProject.layout.projectDirectory.file(
    "sample/shared/data/repository/src/commonMain/kotlin/br/com/arch/toolkit/sample/github/shared/structure/repository/ToolkitDemoRepository.kt"
)
val snippetsDirectory = layout.buildDirectory.dir("generated/demoSnippets")
val generateDemoSnippets by tasks.registering {
    inputs.file(snippetSource)
    outputs.dir(snippetsDirectory)
    doLast {
        val source = snippetSource.asFile.readText()
        val fields = listOf("lumber", "storage").joinToString("\n") { name ->
            val snippet = source.substringAfter("// snippet:$name:start")
                .substringBefore("// snippet:$name:end").trimIndent().trim()
            require(snippet.isNotBlank()) { "Missing demo snippet: $name" }
            val escaped = snippet.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "").replace("$", "\\$")
            "    const val $name = \"$escaped\""
        }
        val output = snippetsDirectory.get().file(
            "br/com/arch/toolkit/sample/feature/toolkit/DemoSnippets.kt"
        ).asFile
        output.parentFile.mkdirs()
        output.writeText(
            "package br.com.arch.toolkit.sample.feature.toolkit\n\ninternal object DemoSnippets {\n$fields\n}\n"
        )
    }
}
kotlin.sourceSets.named("commonMain") { kotlin.srcDir(snippetsDirectory) }
tasks.configureEach {
    if (name.startsWith("compile") ||
        name.startsWith("ksp") ||
        name.contains("ktlint", ignoreCase = true) ||
        name.startsWith("detekt")
    ) {
        dependsOn(generateDemoSnippets)
    }
}
