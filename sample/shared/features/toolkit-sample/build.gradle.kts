plugins {
    id("toolkit-multiplatform-sample")
    alias(libs.plugins.jetbrains.compose.compiler)
    alias(libs.plugins.jetbrains.compose.kotlin)
    alias(libs.plugins.jetbrains.serialization)
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
                api(libs.arch.event.observer)
                implementation(libs.arch.lumber)
                implementation(libs.androidx.lifecycle.runtime)
                implementation(libs.easy.navigation)
                implementation(libs.navigation3.runtime)
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
        androidMain.dependencies {
            implementation(libs.arch.android)
            implementation(libs.androidx.lifecycle.livedata)
            compileOnly(libs.square.retrofit.main)
        }
    }
}

// Compile the exact library sources against the Showcase's existing Lumber ABI pin.
// The published Splinter module keeps its current dependencies and publication metadata.
val splinterSources = rootProject.layout.projectDirectory.dir("toolkit/multi/splinter/src")
listOf("commonMain", "commonTest", "androidMain", "jvmMain", "appleMain").forEach { name ->
    kotlin.sourceSets.named(name) { kotlin.srcDir(splinterSources.dir("$name/kotlin")) }
}

val snippetSource = rootProject.layout.projectDirectory.file(
    "sample/shared/data/repository/src/commonMain/kotlin/br/com/arch/toolkit/sample/repository/ToolkitDemoRepository.kt"
)
val snippetsDirectory = layout.buildDirectory.dir("generated/demoSnippets")
val splinterSnippetSource = layout.projectDirectory.file(
    "src/commonMain/kotlin/br/com/arch/toolkit/sample/feature/toolkit/SplinterDemo.kt"
)
val generateDemoSnippets by tasks.registering {
    inputs.file(snippetSource)
    inputs.file(splinterSnippetSource)
    outputs.dir(snippetsDirectory)
    doLast {
        val source = snippetSource.asFile.readText()
        val splinterSource = splinterSnippetSource.asFile.readText()
        val timing = splinterSource.substringAfter("// snippet:timing:start")
            .substringBefore("// snippet:timing:end").trimIndent().trim()
        val fields = listOf("lumber", "storage", "oneShot", "polling").joinToString("\n") { name ->
            val snippet = (if (name == "oneShot" || name == "polling") splinterSource else source)
                .substringAfter("// snippet:$name:start")
                .substringBefore("// snippet:$name:end").trimIndent().trim()
            require(snippet.isNotBlank()) { "Missing demo snippet: $name" }
            val completeSnippet = if (name == "oneShot" ||
                name == "polling"
            ) {
                "$timing\n\n$snippet"
            } else {
                snippet
            }
            val escaped = completeSnippet.replace("\\", "\\\\").replace("\"", "\\\"")
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
        name == "generateEasyNavigation" ||
        name.contains("ktlint", ignoreCase = true) ||
        name.startsWith("detekt")
    ) {
        dependsOn(generateDemoSnippets)
    }
}
