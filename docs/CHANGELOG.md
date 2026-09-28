# Changelog

## 2.0.0-rc19

Showcase and maintenance changes based on tag `2.0.0-rc18`.

### Showcase

- Add 69 Android Compose screenshot regression references covering GitHub, Settings, Toolkit, Design, adaptive navigation and shared widgets, with feature-owned tests and CI diff reports.

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

### Changes

- Update sample image loading and Ktor integration from Coil 3.6.2 to 3.6.3, including the upstream AGP 9.4/R8 Kotlin module metadata fix.

- Update the sample HTTP clients and serialization integration from Ktor 3.5.2 to 3.6.0.
- Replace the settings aggregation plugin with the Kover project plugin so `ciCoverage` generates the merged XML/HTML reports for Splinter.
- Share generated-class filters between Splinter and the root report, and upload only that XML to Codecov after release validation.
- Exclude only Android-generated `BuildConfig`, `R` and nested `R` classes; retain handwritten code and existing coverage floors.
- Configure Android test JVM access required by Robolectric 4.17 on JDK 21.
- Update the pinned Java setup action to 6.0.1.
- Extract this version section from the changelog when creating the GitHub Release, without a duplicate root notes file.
- Document coverage scope, local commands and the audited dependency versions.
- Verify the existing Gradle 9.7.1 distribution with its official SHA-256 checksum.

### Dependencies

| Dependency | Before | After |
| --- | --- | --- |
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
