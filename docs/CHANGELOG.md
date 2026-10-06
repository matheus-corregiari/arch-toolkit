# Changelog

## Unreleased

### Showcase

- Initialize Android's ContextProvider during application startup so its sample can read the active application/activity context.

- Open the app on the Toolkit catalogue, with sample and GitHub links for all five libraries.
- Give each library its own destination and a fixed toolbar returning to the catalogue, including direct links and intermediate sample screens.
- Keep adaptive navigation outside destination transitions with a stable full-size container.
- Align sample Material Adaptive peers and use V2 window classification to fix the actual Navigation3 host's missing-method failure.
- Scroll all library content below the toolbar, including controls, results and expanded source.
- Use compact 16 dp page/card gaps, responsive card padding and a single toolbar title per sample.
- Crossfade top-level tabs and use short, bounded directional transitions inside each tab; keep predictive back driven by the platform gesture.
- Add executable DataResult and Android ContextProvider examples; explain Android availability on other targets.

## 2.0.0-rc19

Showcase and maintenance changes based on tag `2.0.0-rc18`.

### Splinter

- Reactivate the two OneShot tests with semantic state assertions and virtual time;
  remove stale log-format expectations and global test logger/dispatcher mutation.
- Propagate cancellation from suspend callbacks, cache validation/update, requests and
  await; preserve request timeout failures and ordinary best-effort callback errors.
- Use cancellable coroutine deadlines for minimum execution duration; retain the
  200 ms success default and avoid extra waiting after a longer virtual-time request.
- Allow polling to execute again after an earlier success, with overlap-policy regressions.
- Add optional bounded data/log history configuration, preserving 500/500/50 defaults
  and the current result; skip creating a Lumber logger when external logging is disabled.
- Add executable OneShot and polling Showcase demos with snapshots, simulated failure,
  cancellation/retry, EN/PT text, source-generated examples and visual regression checks.

### Showcase

- Add 101 Android Compose screenshot regression references covering GitHub, Settings, Toolkit, Design, adaptive navigation and shared widgets, with feature-owned tests and CI diff reports.

- Expand the sample into GitHub, Toolkit, Design and Settings destinations using
  Easy Navigation and Navigation3; separate composition, features, data and design.
- Add cancellable search, filters, pagination retry, direct-link details and Room history.
- Add persistent English/Portuguese settings and localized dates/counts.
- Add executable Lumber/Storage demos with source-generated snippets and a design catalogue.
- Add an iOS SwiftUI host and macOS CI build; keep web pending without JS/Wasm navigation artifacts.
- Remove trust-all TLS overrides and check module dependency boundaries.
- Retain Lumber 1.1.0 and Storage 2.0.0-rc16 in sample configurations for Easy Navigation 1.2.0 binary compatibility;
  published Toolkit dependency versions are unchanged by this pin.
- Document running, extending and using the showcase as an app base.
- Apply the new visual study: task headers, semantic typography, shared DS controls,
  six light/dark palettes and horizontally centered, top-aligned adaptive fill widths.
- Cap workspaces at 1120 dp and reading/settings at 760 dp. Use width/font-aware
  one/two-column Toolkit/Design grids; keep Settings in a continuous preference panel.
- Move GitHub search above results, keep its action visible in landscape, add keyboard
  submit and expandable history; show demo source only on request.
- Simplify Kotlin with immutable page mapping, sealed load status, pure Settings
  callbacks, direct repository composables and neutral shared package names.
- Add 300 ms search debounce while preserving immediate submit/filter, cancellation,
  late-response rejection and pagination. Separate draft/active query and persist
  only changed restorable snapshots, with legacy/corrupt-state tests.
- Configure Coil at bootstrap and preserve edited demo inputs through resizing.
- Update real screenshots, including expanded executable code, and document
  reproducible snapshot work measurements without claiming unmeasured frame gains.

- Explain all five Arch repositories in the Toolkit catalogue, with simple
  bilingual descriptions/use cases and GitHub-mark links. Add a link interaction
  test and four catalogue previews, retaining responsive grids and accessible labels.
- Recheck all Arch dependency metadata against the latest stable releases;
  preserve documented sample-only ABI overrides while Easy Navigation requires them.

### Changes

- Refresh Gradle to 9.8.0 (regenerated wrapper and official checksum), AGP to 9.4.1,
  Compose to 1.12.1 and Kover to 0.9.11. Compile Android against stable API 37.2;
  retain Build Tools 37.0.0, minimum API 28 and existing supported targets.
- Update Easy Navigation runtime/plugin to 1.2.0, Event Observer to 3.0.0, Android to 1.3.2,
  and catalog selections for Lumber to 1.4.4 and Storage to 1.0.1. Navigation 1.2.0
  still uses the old Lumber ABI, so the scoped sample Lumber/Storage overrides remain.
- Migrate navigation features from KSP to the Kotlin compiler generator, retaining KSP
  for Room/Ktorfit. Select Android class artifacts explicitly for generation and
  replace four hardcoded Navigation3 prerelease runtime declarations with the stable catalog alias.
- Track Compose-generated source inputs and enforce Kotlin LF line endings so
  Navigation's syntax scanner discovers destinations consistently on Windows.
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
| Easy Navigation | `1.0.1` | `1.2.0` |
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
