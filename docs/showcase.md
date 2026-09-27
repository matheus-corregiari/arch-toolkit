# Arch Toolkit Showcase

An executable reference application for Android, iOS and Desktop, with shared
features in `commonMain`. Web is pending by explicit project decision: Easy
Navigation 1.0.1 has no JS/Wasm artifact. No alternative web navigator is provided.

Easy Navigation 1.0.1 uses Lumber's pre-1.2 binary signature. Sample configurations
select Lumber 1.1.0 and Storage 2.0.0-rc16 for compatibility; Storage stable 1.0.0
requires the incompatible Lumber 1.4 ABI. The published Toolkit library keeps its own
existing dependency version. Remove the sample pin only after Android, JVM and iOS
navigation tests pass with an upstream-compatible release.

## Run

Use JDK 21 and the configured Android SDK. Sample projects require `-PincludeSamples`.

```shell
./gradlew :sample:target:desktop:run -PincludeSamples
./gradlew :sample:target:android:installDebug -PincludeSamples
```

On an Apple Silicon Mac, install Xcode and XcodeGen, then:

```shell
xcodegen generate --spec sample/target/ios/project.yml
open sample/target/ios/Showcase.xcodeproj
```

Choose the Showcase scheme and a simulator. Its build phase compiles the shared
framework. Physical devices require your signing team and code signing enabled
locally. CI builds the unsigned simulator host and runs KMP tests on macOS; this
is not manual simulator or accessibility inspection.

## Explore

- **GitHub:** search, language filters, pagination and retry, repository details,
  and the last 20 opened repository identities persisted in Room. New searches
  cancel previous requests and reject late results. Page failures retain rows.
  History is local, with no synchronization or offline GitHub mirror.
- **Toolkit:** Lumber writes with a dedicated tree and a clearable 100-entry buffer;
  Storage create/read/update/delete with persistence across restarts. Snippets are
  generated from marked regions of the actual `ToolkitDemoRepository.kt` source.
- **Design:** the app's real theme tokens, typography, spacing, buttons, empty and
  error widgets. Change theme and contrast in Settings to inspect all states.
- **Settings:** persistent English/Portuguese (Brazil), theme and contrast. Changes
  apply at runtime. `AppText` centralizes both translations; unknown language tags
  fall back to English. Dates/counts follow the selected language. GitHub data and
  code are never translated.

## Navigation and links

Feature-owned serializable routes use Easy Navigation `@Route`, `@Scope`,
`@Deeplink` and `@ParentRoute`. The app aggregates generated registries in one
Navigation3 controller. Details require only owner/name and load independently.

```shell
adb shell am start -a android.intent.action.VIEW -d "archtoolkit://github/matheus-corregiari/arch-toolkit"
./gradlew :sample:target:desktop:run -PincludeSamples --args="archtoolkit://github/matheus-corregiari/arch-toolkit"
xcrun simctl openurl booted "archtoolkit://github/matheus-corregiari/arch-toolkit"
```

Other paths: `/github`, `/toolkit`, `/design`, `/settings`. Android handles initial
and new intents; iOS forwards SwiftUI URL events; Desktop accepts a launch argument.
Foreign schemes and invalid links are ignored. Navigation3 saves the typed stack;
GitHub saves query, filters and rows without restoring loading flags. Back returns
to the previous destination. Top-level tabs start a new stack.

## Architecture

| Path under `sample/` | Responsibility |
| --- | --- |
| `target/{android,ios,desktop,web}` | Platform launchers; web pending |
| `shared/app` | Composition, Koin, shell and global links |
| `shared/features/{github-sample,toolkit-sample,design-sample,settings}` | Views, ViewModels, state and routes |
| `shared/data/repository` | Repository objects (`RO`), mapping and source coordination |
| `shared/data/source/remote` | Ktorfit interfaces and transport models |
| `shared/data/source/local` | Room entities/DAO and persistence factories |
| `shared/structure/http` | Clients, engines, JSON, timeouts and transport setup |
| `shared/structure/design/core` | Tokens, theme and translated UI text |
| `shared/structure/design/widget` | Reusable presentation widgets |
| `shared/structure/core` | Small shared models and formatting |

Features depend on repository contracts and design, never other features or sources.
Repository state does not expose transport/Room models. Dependencies enter classes
through constructors; Koin resolves them at composition boundaries. Cancellation
remains cancellation. HTTP uses platform TLS validation. `verifyShowcaseBoundaries`
checks forbidden project dependencies and cycles as part of `ciLint`.

## Add a feature or demo

1. Use an existing feature's Gradle setup under `shared/features`; define its state,
   constructor-injected ViewModel and tests in `commonMain`/`commonTest`.
2. Declare serializable routes with small identities and annotated destinations.
   Add the generated registry and ViewModel registration to `shared/app`.
3. Reuse design tokens/widgets and add both translations to `AppText`.
4. For demos, expose an actual operation, controls, visible result, reset and failure
   handling. Mark the executable source and extend `generateDemoSnippets`.
5. Prefer a documented catalogue card when no meaningful portable demo exists.
   Never add an Android-only dependency to `commonMain`.

Official ecosystem references: [Arch Android](https://github.com/matheus-corregiari/arch-android),
[Event Observer](https://github.com/matheus-corregiari/arch-event-observer),
[Lumber](https://github.com/matheus-corregiari/arch-lumber),
[Storage](https://github.com/matheus-corregiari/arch-storage),
[Easy Navigation](https://github.com/Pedro-Bachiega/easy-navigation/).

## Use as a base

Retain the launchers/composition, substitute your features and repositories, and
keep constructor injection and shared design/test seams. Change application IDs,
display names and persistence namespaces before shipping. This guide creates no
separate application.

## Check

```shell
./gradlew ciBuild ciTest ciCoverage ciLint ciDocs -PincludeSamples -PreleaseVersion=2.0.0-rc19
python -m mkdocs build --strict
```

Tests exercise cancellation/out-of-order responses, page retry, saved list state,
repository mappings/errors, Storage CRUD, language restoration, bounded logging,
formatting and generated links. Desktop navigation tests exercise back and saved
stack restoration. See `SHOWCASE_PLAN.md` for the current validation record.
Lazy list rows and bounded logs/history are implementation choices, not measured
performance claims.
