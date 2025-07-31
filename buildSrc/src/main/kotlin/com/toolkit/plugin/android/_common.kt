package com.toolkit.plugin.android

import com.android.build.api.dsl.CommonExtension
import com.toolkit.plugin.util.projectJavaVersion

internal fun CommonExtension<*, *, *, *, *, *>.commonSetup() {
    androidResources { noCompress.add("") }

    compileOptions {
        sourceCompatibility(projectJavaVersion)
        targetCompatibility(projectJavaVersion)
    }

    packaging {
        resources.excludes.add("META-INF/LICENSE")
        resources.pickFirsts.add("protobuf.meta")
        jniLibs.keepDebugSymbols.addAll(setOf("*/mips/*.so", "*/mips64/*.so"))
    }
}

internal fun CommonExtension<*, *, *, *, *, *>.regularSourceSets() {
    sourceSets {
        maybeCreate("main").java.srcDirs("src/main/kotlin")
        maybeCreate("test").java.srcDirs("src/test/kotlin")
        maybeCreate("androidTest").java.srcDirs("src/androidTest/kotlin")
        maybeCreate("androidTest").resources.srcDirs("src/androidTest/res")
    }
}
