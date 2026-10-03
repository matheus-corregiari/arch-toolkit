# Dependencies

Audited against Maven Central, Google Maven and the Gradle Plugin Portal on 2026-10-01 for `2.0.0-rc19`.
Updated runtime dependencies and AGP use stable releases. Detekt retains its existing alpha line.
The showcase retains Navigation3 UI 1.2.0-alpha02 and upstream adaptive-navigation3 1.3.0-beta02
for Easy Navigation's existing binary contract; Navigation3 runtime now uses stable 1.2.0.
Gradle **9.8.0**, JDK **21**, Kover **0.9.11**, MkDocs Material **9.7.7**.

Android compiles against stable API **37.2**, using Build Tools **37.0.0** and the existing minimum API **28**.
Kotlin **2.4.20** and KSP **2.3.12** already match their latest published stable versions.

| Alias | Version | Source |
| --- | --- | --- |
| `compose-screenshot-validation` | `0.0.1-alpha16` | [Metadata](https://dl.google.com/dl/android/maven2/com/android/tools/screenshot/screenshot-validation-api/maven-metadata.xml) |
| `room-runtime` | `2.8.5` | [Metadata](https://dl.google.com/dl/android/maven2/androidx/room/room-runtime/maven-metadata.xml) |
| `room-compiler` | `2.8.5` | [Metadata](https://dl.google.com/dl/android/maven2/androidx/room/room-compiler/maven-metadata.xml) |
| `sqlite-bundled` | `2.7.1` | [Metadata](https://dl.google.com/dl/android/maven2/androidx/sqlite/sqlite-bundled/maven-metadata.xml) |
| `easy-navigation` | `1.2.0` | [Metadata](https://repo.maven.apache.org/maven2/io/github/pedro-bachiega/easy-navigation-core/maven-metadata.xml) |
| `navigation3-runtime` | `1.2.0` | [Metadata](https://dl.google.com/dl/android/maven2/androidx/navigation3/navigation3-runtime/maven-metadata.xml) |
| `navigation3-ui` | `1.2.0-alpha02` | [Metadata](https://repo.maven.apache.org/maven2/org/jetbrains/androidx/navigation3/navigation3-ui/maven-metadata.xml) |
| `arch-storage-core` | `1.0.1` | [Metadata](https://repo.maven.apache.org/maven2/io/github/matheus-corregiari/storage-core/maven-metadata.xml) |
| `arch-storage-memory` | `1.0.1` | [Metadata](https://repo.maven.apache.org/maven2/io/github/matheus-corregiari/storage-memory/maven-metadata.xml) |
| `arch-storage-datastore` | `1.0.1` | [Metadata](https://repo.maven.apache.org/maven2/io/github/matheus-corregiari/storage-datastore/maven-metadata.xml) |
| `jetbrains-stdlib` | `2.4.20` | [Metadata](https://repo.maven.apache.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/maven-metadata.xml) |
| `jetbrains-reflect` | `2.4.20` | [Metadata](https://repo.maven.apache.org/maven2/org/jetbrains/kotlin/kotlin-reflect/maven-metadata.xml) |
| `jetbrains-serialization` | `1.11.0` | [Metadata](https://repo.maven.apache.org/maven2/org/jetbrains/kotlinx/kotlinx-serialization-json/maven-metadata.xml) |
| `jetbrains-coroutines-core` | `1.11.0` | [Metadata](https://repo.maven.apache.org/maven2/org/jetbrains/kotlinx/kotlinx-coroutines-core/maven-metadata.xml) |
| `jetbrains-coroutines-jvm` | `1.11.0` | [Metadata](https://repo.maven.apache.org/maven2/org/jetbrains/kotlinx/kotlinx-coroutines-swing/maven-metadata.xml) |
| `jetbrains-coroutines-android` | `1.11.0` | [Metadata](https://repo.maven.apache.org/maven2/org/jetbrains/kotlinx/kotlinx-coroutines-android/maven-metadata.xml) |
| `jetbrains-coroutines-test` | `1.11.0` | [Metadata](https://repo.maven.apache.org/maven2/org/jetbrains/kotlinx/kotlinx-coroutines-test/maven-metadata.xml) |
| `jetbrains-dokka` | `2.2.0` | [Metadata](https://repo.maven.apache.org/maven2/org/jetbrains/dokka/dokka-gradle-plugin/maven-metadata.xml) |
| `jetbrains-plugin` | `2.4.20` | [Metadata](https://repo.maven.apache.org/maven2/org/jetbrains/kotlin/kotlin-gradle-plugin/maven-metadata.xml) |
| `jetbrains-datetime` | `0.8.0` | [Metadata](https://repo.maven.apache.org/maven2/org/jetbrains/kotlinx/kotlinx-datetime/maven-metadata.xml) |
| `jetbrains-kotlin-test` | `2.4.20` | [Metadata](https://repo.maven.apache.org/maven2/org/jetbrains/kotlin/kotlin-test/maven-metadata.xml) |
| `jetbrains-compose-runtime` | `1.12.1` | [Metadata](https://repo.maven.apache.org/maven2/org/jetbrains/compose/runtime/runtime/maven-metadata.xml) |
| `jetbrains-compose-runtime-saveable` | `1.12.1` | [Metadata](https://repo.maven.apache.org/maven2/org/jetbrains/compose/runtime/runtime-saveable/maven-metadata.xml) |
| `jetbrains-compose-ui` | `1.12.1` | [Metadata](https://repo.maven.apache.org/maven2/org/jetbrains/compose/ui/ui/maven-metadata.xml) |
| `jetbrains-compose-ui-tooling` | `1.12.1` | [Metadata](https://repo.maven.apache.org/maven2/org/jetbrains/compose/ui/ui-tooling/maven-metadata.xml) |
| `jetbrains-compose-ui-tooling-preview` | `1.12.1` | [Metadata](https://repo.maven.apache.org/maven2/org/jetbrains/compose/ui/ui-tooling-preview/maven-metadata.xml) |
| `jetbrains-compose-ui-test-junit4` | `1.12.1` | [Metadata](https://repo.maven.apache.org/maven2/org/jetbrains/compose/ui/ui-test-junit4/maven-metadata.xml) |
| `jetbrains-compose-foundation` | `1.12.1` | [Metadata](https://repo.maven.apache.org/maven2/org/jetbrains/compose/foundation/foundation/maven-metadata.xml) |
| `jetbrains-compose-animation` | `1.12.1` | [Metadata](https://repo.maven.apache.org/maven2/org/jetbrains/compose/animation/animation/maven-metadata.xml) |
| `jetbrains-compose-resources` | `1.12.1` | [Metadata](https://repo.maven.apache.org/maven2/org/jetbrains/compose/components/components-resources/maven-metadata.xml) |
| `jetbrains-compose-desktop` | `1.12.1` | [Metadata](https://repo.maven.apache.org/maven2/org/jetbrains/compose/desktop/desktop/maven-metadata.xml) |
| `jetbrains-compose-material3` | `1.9.0` | [Metadata](https://repo.maven.apache.org/maven2/org/jetbrains/compose/material3/material3/maven-metadata.xml) |
| `jetbrains-compose-material3-navigation-suite` | `1.9.0` | [Metadata](https://repo.maven.apache.org/maven2/org/jetbrains/compose/material3/material3-adaptive-navigation-suite/maven-metadata.xml) |
| `jetbrains-compose-material-icons` | `1.7.3` | [Metadata](https://repo.maven.apache.org/maven2/org/jetbrains/compose/material/material-icons-extended/maven-metadata.xml) |
| `androidx-plugin` | `9.4.1` | [Metadata](https://dl.google.com/dl/android/maven2/com/android/tools/build/gradle/maven-metadata.xml) |
| `androidx-annotation` | `1.11.0` | [Metadata](https://dl.google.com/dl/android/maven2/androidx/annotation/annotation/maven-metadata.xml) |
| `androidx-appcompat` | `1.8.0` | [Metadata](https://dl.google.com/dl/android/maven2/androidx/appcompat/appcompat/maven-metadata.xml) |
| `androidx-fragment` | `1.9.1` | [Metadata](https://dl.google.com/dl/android/maven2/androidx/fragment/fragment/maven-metadata.xml) |
| `androidx-constraint` | `2.2.2` | [Metadata](https://dl.google.com/dl/android/maven2/androidx/constraintlayout/constraintlayout/maven-metadata.xml) |
| `androidx-recycler` | `1.4.0` | [Metadata](https://dl.google.com/dl/android/maven2/androidx/recyclerview/recyclerview/maven-metadata.xml) |
| `androidx-lifecycle-livedata` | `2.11.0` | [Metadata](https://dl.google.com/dl/android/maven2/androidx/lifecycle/lifecycle-livedata/maven-metadata.xml) |
| `androidx-lifecycle-runtime` | `2.11.0` | [Metadata](https://dl.google.com/dl/android/maven2/androidx/lifecycle/lifecycle-runtime/maven-metadata.xml) |
| `androidx-lifecycle-process` | `2.11.0` | [Metadata](https://dl.google.com/dl/android/maven2/androidx/lifecycle/lifecycle-process/maven-metadata.xml) |
| `androidx-window` | `1.5.1` | [Metadata](https://dl.google.com/dl/android/maven2/androidx/window/window/maven-metadata.xml) |
| `androidx-security` | `1.1.0` | [Metadata](https://dl.google.com/dl/android/maven2/androidx/security/security-crypto-ktx/maven-metadata.xml) |
| `androidx-startup` | `1.2.0` | [Metadata](https://dl.google.com/dl/android/maven2/androidx/startup/startup-runtime/maven-metadata.xml) |
| `androidx-datastore-core` | `1.2.1` | [Metadata](https://dl.google.com/dl/android/maven2/androidx/datastore/datastore/maven-metadata.xml) |
| `androidx-datastore-preferences` | `1.2.1` | [Metadata](https://dl.google.com/dl/android/maven2/androidx/datastore/datastore-preferences/maven-metadata.xml) |
| `androidx-test-core` | `2.2.0` | [Metadata](https://dl.google.com/dl/android/maven2/androidx/arch/core/core-testing/maven-metadata.xml) |
| `androidx-test-window` | `1.5.1` | [Metadata](https://dl.google.com/dl/android/maven2/androidx/window/window-testing/maven-metadata.xml) |
| `androidx-test-junit` | `1.3.0` | [Metadata](https://dl.google.com/dl/android/maven2/androidx/test/ext/junit-ktx/maven-metadata.xml) |
| `androidx-test-espresso-intents` | `3.7.0` | [Metadata](https://dl.google.com/dl/android/maven2/androidx/test/espresso/espresso-intents/maven-metadata.xml) |
| `androidx-test-espresso-core` | `3.7.0` | [Metadata](https://dl.google.com/dl/android/maven2/androidx/test/espresso/espresso-core/maven-metadata.xml) |
| `androidx-compose-material3-window` | `1.9.0` | [Metadata](https://repo.maven.apache.org/maven2/org/jetbrains/compose/material3/material3-window-size-class/maven-metadata.xml) |
| `androidx-compose-material3-adaptive` | `1.2.0` | [Metadata](https://repo.maven.apache.org/maven2/org/jetbrains/compose/material3/adaptive/adaptive/maven-metadata.xml) |
| `androidx-compose-activity` | `1.13.0` | [Metadata](https://dl.google.com/dl/android/maven2/androidx/activity/activity-compose/maven-metadata.xml) |
| `androidx-compose-lifecycle` | `2.11.0` | [Metadata](https://repo.maven.apache.org/maven2/org/jetbrains/androidx/lifecycle/lifecycle-runtime-compose/maven-metadata.xml) |
| `androidx-compose-testManifest` | `1.12.1` | [Metadata](https://dl.google.com/dl/android/maven2/androidx/compose/ui/ui-test-manifest/maven-metadata.xml) |
| `square-retrofit-main` | `3.0.0` | [Metadata](https://repo.maven.apache.org/maven2/com/squareup/retrofit2/retrofit/maven-metadata.xml) |
| `arch-lumber` | `1.4.4` | [Metadata](https://repo.maven.apache.org/maven2/io/github/matheus-corregiari/arch-lumber/maven-metadata.xml) |
| `arch-android` | `1.3.2` | [Metadata](https://repo.maven.apache.org/maven2/io/github/matheus-corregiari/arch-android/maven-metadata.xml) |
| `arch-event-observer` | `3.0.0` | [Metadata](https://repo.maven.apache.org/maven2/io/github/matheus-corregiari/event-observer/maven-metadata.xml) |
| `arch-event-observer-state` | `3.0.0` | [Metadata](https://repo.maven.apache.org/maven2/io/github/matheus-corregiari/event-observer-state/maven-metadata.xml) |
| `arch-event-observer-compose` | `3.0.0` | [Metadata](https://repo.maven.apache.org/maven2/io/github/matheus-corregiari/event-observer-compose/maven-metadata.xml) |
| `google-material` | `1.14.0` | [Metadata](https://dl.google.com/dl/android/maven2/com/google/android/material/material/maven-metadata.xml) |
| `di-koin-core` | `4.2.2` | [Metadata](https://repo.maven.apache.org/maven2/io/insert-koin/koin-core/maven-metadata.xml) |
| `di-koin-compose` | `4.2.2` | [Metadata](https://repo.maven.apache.org/maven2/io/insert-koin/koin-compose/maven-metadata.xml) |
| `di-koin-composeViewModel` | `4.2.2` | [Metadata](https://repo.maven.apache.org/maven2/io/insert-koin/koin-compose-viewmodel/maven-metadata.xml) |
| `di-koin-android` | `4.2.2` | [Metadata](https://repo.maven.apache.org/maven2/io/insert-koin/koin-android/maven-metadata.xml) |
| `mockk-test-android` | `1.14.11` | [Metadata](https://repo.maven.apache.org/maven2/io/mockk/mockk-android/maven-metadata.xml) |
| `mockk-test-agent` | `1.14.11` | [Metadata](https://repo.maven.apache.org/maven2/io/mockk/mockk/maven-metadata.xml) |
| `junit-test` | `4.13.2` | [Metadata](https://repo.maven.apache.org/maven2/junit/junit/maven-metadata.xml) |
| `robolectric-test` | `4.17` | [Metadata](https://repo.maven.apache.org/maven2/org/robolectric/robolectric/maven-metadata.xml) |
| `detekt` | `2.0.0-alpha.6` | [Metadata](https://repo.maven.apache.org/maven2/dev/detekt/detekt-gradle-plugin/maven-metadata.xml) |
| `ktlint` | `14.2.0` | [Metadata](https://plugins.gradle.org/m2/org/jlleitschuh/gradle/ktlint/org.jlleitschuh.gradle.ktlint.gradle.plugin/maven-metadata.xml) |
| `vanniktech-publish` | `0.37.0` | [Metadata](https://repo.maven.apache.org/maven2/com/vanniktech/gradle-maven-publish-plugin/maven-metadata.xml) |
| `ktorfit` | `2.7.5` | [Metadata](https://repo.maven.apache.org/maven2/de/jensklingenberg/ktorfit/ktorfit-lib/maven-metadata.xml) |
| `ktor-client-okhttp` | `3.6.0` | [Metadata](https://repo.maven.apache.org/maven2/io/ktor/ktor-client-okhttp/maven-metadata.xml) |
| `ktor-client-darwin` | `3.6.0` | [Metadata](https://repo.maven.apache.org/maven2/io/ktor/ktor-client-darwin/maven-metadata.xml) |
| `ktor-content-negotiation` | `3.6.0` | [Metadata](https://repo.maven.apache.org/maven2/io/ktor/ktor-client-content-negotiation/maven-metadata.xml) |
| `ktor-serialization-json` | `3.6.0` | [Metadata](https://repo.maven.apache.org/maven2/io/ktor/ktor-serialization-kotlinx-json/maven-metadata.xml) |
| `ktor-logging` | `3.6.0` | [Metadata](https://repo.maven.apache.org/maven2/io/ktor/ktor-client-logging/maven-metadata.xml) |
| `coil-core` | `3.6.3` | [Metadata](https://repo.maven.apache.org/maven2/io/coil-kt/coil3/coil-compose/maven-metadata.xml) |
| `coil-network` | `3.6.3` | [Metadata](https://repo.maven.apache.org/maven2/io/coil-kt/coil3/coil-network-ktor3/maven-metadata.xml) |
| `haze-core` | `2.0.1` | [Metadata](https://repo.maven.apache.org/maven2/dev/chrisbanes/haze/haze/maven-metadata.xml) |
| `haze-blur` | `2.0.1` | [Metadata](https://repo.maven.apache.org/maven2/dev/chrisbanes/haze/haze-blur/maven-metadata.xml) |
| `x-normalize-x001` | `1.13.0` | [Metadata](https://dl.google.com/dl/android/maven2/androidx/activity/activity/maven-metadata.xml) |
| `x-normalize-x002` | `1.6.0` | [Metadata](https://dl.google.com/dl/android/maven2/androidx/annotation/annotation-experimental/maven-metadata.xml) |
| `x-normalize-x003` | `2.2.0` | [Metadata](https://dl.google.com/dl/android/maven2/androidx/arch/core/core-common/maven-metadata.xml) |
| `x-normalize-x004` | `2.2.0` | [Metadata](https://dl.google.com/dl/android/maven2/androidx/arch/core/core-runtime/maven-metadata.xml) |
| `x-normalize-x005` | `1.6.0` | [Metadata](https://dl.google.com/dl/android/maven2/androidx/collection/collection/maven-metadata.xml) |
| `x-normalize-x006` | `1.19.1` | [Metadata](https://dl.google.com/dl/android/maven2/androidx/core/core/maven-metadata.xml) |
| `x-normalize-x007` | `1.19.1` | [Metadata](https://dl.google.com/dl/android/maven2/androidx/core/core-ktx/maven-metadata.xml) |
| `x-normalize-x008` | `1.2.0` | [Metadata](https://dl.google.com/dl/android/maven2/androidx/customview/customview/maven-metadata.xml) |
| `x-normalize-x009` | `1.2.0` | [Metadata](https://dl.google.com/dl/android/maven2/androidx/drawerlayout/drawerlayout/maven-metadata.xml) |
| `x-normalize-x010` | `2.11.0` | [Metadata](https://dl.google.com/dl/android/maven2/androidx/lifecycle/lifecycle-common/maven-metadata.xml) |
| `x-normalize-x011` | `1.1.0` | [Metadata](https://dl.google.com/dl/android/maven2/androidx/viewpager2/viewpager2/maven-metadata.xml) |
| `x-normalize-x012` | `1.8.0` | [Metadata](https://dl.google.com/dl/android/maven2/androidx/test/monitor/maven-metadata.xml) |
| `x-normalize-x013` | `1.7.0` | [Metadata](https://dl.google.com/dl/android/maven2/androidx/test/runner/maven-metadata.xml) |
| `x-normalize-x014` | `1.11.1` | [Metadata](https://repo.maven.apache.org/maven2/com/google/auto/value/auto-value-annotations/maven-metadata.xml) |
| `x-normalize-x015` | `3.0.2` | [Metadata](https://repo.maven.apache.org/maven2/com/google/code/findbugs/jsr305/maven-metadata.xml) |
| `x-normalize-x016` | `2.5.1` | [Metadata](https://repo.maven.apache.org/maven2/com/squareup/javawriter/maven-metadata.xml) |
| `x-normalize-x017` | `2.4.20` | [Metadata](https://repo.maven.apache.org/maven2/org/jetbrains/kotlin/kotlin-stdlib-common/maven-metadata.xml) |
| `x-normalize-x018` | `1.11.0` | [Metadata](https://repo.maven.apache.org/maven2/org/jetbrains/kotlinx/kotlinx-serialization-json-jvm/maven-metadata.xml) |
| `x-normalize-x019` | `1.11.0` | [Metadata](https://repo.maven.apache.org/maven2/org/jetbrains/kotlinx/kotlinx-serialization-core-jvm/maven-metadata.xml) |
| `x-normalize-x020` | `26.1.0` | [Metadata](https://repo.maven.apache.org/maven2/org/jetbrains/annotations/maven-metadata.xml) |
| `x-normalize-x021` | `5.5.0` | [Metadata](https://repo.maven.apache.org/maven2/com/squareup/okhttp3/okhttp/maven-metadata.xml) |

## Tooling sources

- [Gradle current release](https://services.gradle.org/versions/current)
- [MkDocs Material](https://pypi.org/project/mkdocs-material/)
- [JaCoCo](https://repo.maven.apache.org/maven2/org/jacoco/org.jacoco.core/maven-metadata.xml)

Robolectric 4.17 Android tests require `--add-opens=java.base/jdk.internal.access=ALL-UNNAMED`
on JDK 21. This option is scoped to test JVMs, following the
[Robolectric setup guide](https://robolectric.org/getting-started/).

## Showcase compatibility

The showcase uses Easy Navigation 1.2.0. Its published JVM binaries still call Lumber's pre-1.2
`tag` API. Sample-only resolution in `gradle/showcase-boundaries.gradle.kts` selects
Lumber 1.1.0 and Storage 2.0.0-rc16, whose published metadata uses that same ABI.
Storage stable 1.0.1 uses Lumber 1.4.4 and cannot be combined with the selected
navigation release. These pins do not change the published Toolkit library's
dependencies. Remove them only after Android release shrinking, JVM navigation
and iOS linking/tests pass with an upstream-compatible combination.

## Build conventions

Gradle 9.8 uses the regenerated wrapper and official distribution checksum. Shared build logic uses
explicit extra-property access, the compiler-independent `js {}` DSL, and the current `SourcesJar`
publication option. Unconfigured sample JS/Wasm source sets are no longer created.

CI uses `gradle/actions/setup-gradle` 6.4.0 as the single Gradle cache owner;
`android-actions/setup-android` 4.0.4 installs the SDK and Build Tools selected by the catalog.
Apple framework linking and simulator tests run on macOS; Windows compares all 101 Android screenshots.

The legacy Android KMP target still requires `android.builtInKotlin=false` and `android.newDsl=false`.
Migrating to `com.android.kotlin.multiplatform.library` requires coordinating convention plugins,
Android tests, KSP, publication and screenshot tooling before AGP 10 removes that compatibility path.
See the [official Android KMP migration guide](https://developer.android.com/kotlin/multiplatform/plugin).

Haze 2.0.1 uses `haze-blur`, immutable `HazeBlurStyle` and explicit captured-source input.
The sample retains its tint, gradient fallback and mobile/window policy; no experimental Glass or
native-backdrop features are enabled. See the [upstream migration guide](https://chrisbanes.github.io/haze/2.0.0/migrating-2.0/).


## Arch ecosystem recheck — 2026-10-02

Maven Central metadata was checked again for all Arch modules used here. The
catalog already selects the latest stable releases: Lumber **1.4.4**, Storage
core/memory/DataStore **1.0.1**, Event Observer core/state/Compose **3.0.0** and
Arch Android **1.3.2**. Easy Navigation's latest stable release at that check was **1.1.0**.
Storage metadata's `release` field points to `2.0.0-rc16`; select the highest
stable version rather than treating that field as a stable-version guarantee.

The sample's scoped Lumber/Storage ABI overrides described above remain required
with the current navigation artifact. The catalogue version and the version
actually resolved by the Showcase must not be confused. No snapshot/nightly or
Maven Local dependency was introduced.

## Navigation update — 2026-10-03

Update Easy Navigation runtime and Gradle plugin together to **1.2.0**, the latest
published stable release. Navigation now generates directions and platform
registries through its Kotlin compiler plugin, compatible with Kotlin **2.4.20**.
Remove KSP from the four navigation features; retain it for Room and Ktorfit.
Use the shared stable Navigation3 runtime **1.2.0** catalog alias in every feature.
See the [upstream migration guide](https://github.com/Pedro-Bachiega/easy-navigation#migrating-from-ksp).

The new generator resolves feature Android compile classpaths directly. Select
`android-classes-jar` explicitly for debug/release classpaths to avoid ambiguity
between AGP's classes, lint and manifest artifacts from project dependencies.
Wire Compose resource generation before navigation scanning, and order source
lint after navigation generation when both run, so Gradle tracks generated inputs.
Enforce LF for Kotlin files in Git and EditorConfig: the 1.2.0 syntax scanner
produced no GitHub destinations from CRLF input on Windows, then generated both
directions and platform registries after normalizing that input to LF.

The published Navigation 1.2.0 JVM bytecode still calls
`Lumber.OakWood.tag(String): Lumber.Oak`. It does not remove the Lumber/Storage
ABI constraint above; retain those overrides and the Splinter source compilation
adaptation. Maven metadata was rechecked for the complete catalog: the other
stable dependency selections remain current. Navigation3 UI remains on the
version required by Easy Navigation's published metadata.

Validation on Windows: Android release/R8 and all 14 app JVM tests passed,
including deeplinks, back navigation and stack restoration. The complete
build/test/coverage/lint/docs/publication gates, 101 unchanged screenshot
comparisons, strict MkDocs and three release-note extraction tests passed.
iOS framework linking and host execution remain in the macOS CI job.
