# 2.0.0-rc19

Maintenance changes based on tag `2.0.0-rc18`.

## Changes

- Replace the settings aggregation plugin with the Kover project plugin so `ciCoverage` generates the merged XML/HTML reports for Splinter.
- Share generated-class filters between Splinter and the root report, and upload only that XML to Codecov after release validation.
- Exclude only Android-generated `BuildConfig`, `R` and nested `R` classes; retain handwritten code and existing coverage floors.
- Configure Android test JVM access required by Robolectric 4.17 on JDK 21.
- Update the pinned Java setup action to 6.0.1.
- Include these curated notes when the release workflow creates the GitHub Release.
- Document coverage scope, local commands and the audited dependency versions.
- Verify the existing Gradle 9.7.1 distribution with its official SHA-256 checksum.

## Dependencies

| Dependency | Before | After |
| --- | --- | --- |
| `androidx-compose-core` | `1.12.0` | `1.12.1` |
| `google-ksp` | `2.3.11` | `2.3.12` |
| `robolectric` | `4.16.1` | `4.17` |

## Compatibility

No public API changes. Existing minimum Android SDK and KMP targets remain unchanged.
Coverage measures JVM/Android host tests; Apple, JS and Wasm tests remain in the test suite but do not contribute coverage counters.

Dependencies on other Arch repositories use versions available in Maven Central, rather than unpublished patch candidates.
