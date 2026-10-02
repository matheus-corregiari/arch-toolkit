# Changelog

## 2.0.0-rc19

Showcase and maintenance changes based on tag `2.0.0-rc18`.

### Showcase

- Add 84 Android Compose screenshot regression references covering GitHub, Settings, Toolkit, Design, adaptive navigation and shared widgets, with feature-owned tests and CI diff reports.

- Expand the sample into GitHub, Toolkit, Design and Settings destinations using
  Easy Navigation and Navigation3; separate composition, features, data and design.
- Add cancellable search, filters, pagination retry, direct-link details and Room history.
- Add persistent English/Portuguese settings and localized dates/counts.
- Add executable Lumber/Storage demos with source-generated snippets and a design catalogue.
- Add an iOS SwiftUI host and macOS CI build; keep web pending without JS/Wasm navigation artifacts.
- Remove trust-all TLS overrides and check module dependency boundaries.
- Pin Lumber 1.1.0 and Storage 2.0.0-rc16 in sample configurations for Easy Navigation binary compatibility;
  published Toolkit dependency versions are unchanged by this pin.
- Document running, extending and using the showcase as an app base.
- Organize destinations around shared DS pages, section cards, fields, choices and
  buttons; center reading content and results states, and separate demo inputs,
  results and source snippets. Use a sections menu for enlarged text in compact windows.
- Adapt Settings, Toolkit and Design to two readable card columns on landscape/tablet
  windows, scaling the required width with text size. Place GitHub search beside
  results when both panes fit and keep controls scrollable in short windows.
- Reserve the navigation drawer for windows from 1200 dp, retain a tablet rail,
  cap repository detail reading width, and verify edited inputs survive resizing.
- Preserve brand yellow with readable dark ink; define six complete light/dark
  contrast palettes with opaque surfaces and consistent Material roles. Add KMP
  contrast checks and document measured text/control contrast ratios.

### Changes

- Refresh Gradle to 9.8.0 (regenerated wrapper and official checksum), AGP to 9.4.1,
  Compose to 1.12.1 and Kover to 0.9.11. Compile Android against stable API 37.2;
  retain Build Tools 37.0.0, minimum API 28 and existing supported targets.
- Update Easy Navigation to 1.1.0, Event Observer to 3.0.0, Android to 1.3.2,
  and catalog selections for Lumber to 1.4.4 and Storage to 1.0.1. Navigation 1.1.0
  still uses the old Lumber ABI, so the scoped sample Lumber/Storage overrides remain.
- Migrate the sample blur to Haze 2.0.1's typed `hazeBlur` API and separate blur artifact,
  preserving explicit captured-source input, tint, progressive gradient and fallback.
- Refresh visually reviewed screenshot references for the shared UI and color system,
  including all six Settings palettes and layouts with font scaling up to 2.0.
- Remove obsolete Gradle extra-property delegates, Kotlin/JS compiler selection,
  publication sources boolean and inactive sample JS/Wasm source-set configuration.
- Replace deprecated Compose dependency accessors with explicit catalog aliases;
  retain the final Material Icons Extended 1.7.3 artifact.
- Configure toolchain resolution for buildSrc and remove unsupported Kotlin/Compose flags.
- Update Navigation3 runtime to stable 1.2.0 and centralize navigation coordinates.
- Read publication properties through Gradle providers and fix environment lookup to use
  the requested variable name instead of the literal `name`.
- Update Gradle CI setup to 6.4.0, Android SDK setup to 4.0.4, artifact upload to 7.0.1
  and Codecov to 7.1.1. Install catalog-selected SDK packages and consolidate Gradle caches.

- Update sample image loading and Ktor integration from Coil 3.6.2 to 3.6.3, including the upstream AGP 9.4/R8 Kotlin module metadata fix.

- Update the sample HTTP clients and serialization integration from Ktor 3.5.2 to 3.6.0.
- Replace the settings aggregation plugin with the Kover project plugin so `ciCoverage` generates the merged XML/HTML reports for Splinter.
- Share generated-class filters between Splinter and the root report, and upload only that XML to Codecov after release validation.
- Exclude only Android-generated `BuildConfig`, `R` and nested `R` classes; retain handwritten code and existing coverage floors.
- Configure Android test JVM access required by Robolectric 4.17 on JDK 21.
- Update the pinned Java setup action to 6.0.1.
- Extract this version section from the changelog when creating the GitHub Release, without a duplicate root notes file.
- Document coverage scope, local commands and the audited dependency versions.
- Verify the Gradle distribution with its official SHA-256 checksum.

### Dependencies

| Dependency | Before | After |
| --- | --- | --- |
| Gradle | `9.7.1` | `9.8.0` |
| AGP | `9.4.0` | `9.4.1` |
| Android compile API | `37` | `37.2` |
| Compose Multiplatform | `1.12.0` | `1.12.1` |
| Kover | `0.9.9` | `0.9.11` |
| Easy Navigation | `1.0.1` | `1.1.0` |
| Haze | `1.7.3` | `2.0.1` |
| Event Observer | `2.3.0` | `3.0.0` |
| Arch Android | `1.3.1` | `1.3.2` |
| Lumber (catalog) | `1.4.0` | `1.4.4` |
| Storage (catalog) | `1.0.0` | `1.0.1` |
| AndroidX Annotation | `1.10.0` | `1.11.0` |
| AndroidX Fragment | `1.9.0` | `1.9.1` |
| AndroidX Core | `1.19.0` | `1.19.1` |
| `androidx-compose-core` | `1.12.0` | `1.12.1` |
| `google-ksp` | `2.3.11` | `2.3.12` |
| `robolectric` | `4.16.1` | `4.17` |
| `ktor` | `3.5.2` | `3.6.0` |
| `coil` | `3.6.2` | `3.6.3` |

### Compatibility

No public API changes. Existing minimum Android SDK and KMP targets remain unchanged.
Coverage measures JVM/Android host tests; Apple, JS and Wasm tests remain in the test suite but do not contribute coverage counters.

Dependencies on other Arch repositories use versions available in Maven Central, rather than unpublished patch candidates.


## 2.0.0-rc18

- Remove the local State Handle module and all its publications.
- Migrate the sample to Event Observer State 2.3.0, serializable payloads and a new saved-state key.
- Update dependencies and build tools; see [Dependencies](dependencies.md).
- Correct module, platform, installation and migration documentation.
- Include samples and KMP sources in quality gates; enable Detekt rules and fix their findings.
- Refactor polling into focused helpers without changing its behavior, with common regression tests.
- Fix log-tag regular expressions that failed at runtime in JavaScript browsers.

## 2.0.0-rc17

- Move storage consumers to independently released Arch Storage artifacts.
- Remove the local storage modules and document their migration.
- Announce State Handle migration and add Maven relocation metadata for its publications.
