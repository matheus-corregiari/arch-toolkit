# Splinter RC19 validation

Validated on Windows with JDK 21 and the configured Android SDK.

The complete build, test, coverage, lint, docs and publication-manifest gates passed.
All 21 Splinter tests passed on each of Android, JVM, JS and Wasm, with no skips.
All 101 Android screenshot comparisons passed, including eleven Splinter cases.
Strict MkDocs passed after adding the real demo render to the Showcase guide.

## Regression coverage

- OneShot tests run with coroutine virtual time, without ignored classes or global dispatcher changes.
- Cancellation covers suspend callbacks, cache checks, minimum-duration waits, polling and restart. A request timeout remains an error result.
- All four execution policies are exercised with overlapping requests.
- History tests verify bounded replay and preservation of the current result.
- Polling can execute again after a previous success.
- Showcase controller tests cover snapshots, failure, cancellation, retry and repeated polling. JVM UI tests exercise the real controls.

## Commands

```shell
./gradlew build ciBuild ciTest ciCoverage ciLint ciDocs ciPublicationManifest :sample:target:android:updateDebugScreenshotTest -PincludeSamples -PreleaseVersion=2.0.0-rc19 --max-workers=4 --continue
./gradlew :sample:target:android:validateDebugScreenshotTest -PincludeSamples -PreleaseVersion=2.0.0-rc19 --max-workers=4
python -m mkdocs build --strict
```

The Showcase compiles the same Splinter sources and regression tests against its
existing Lumber compatibility pin. Android release/R8 and its JVM tests passed.
Published Splinter dependencies and publication coordinates are unchanged.

## Limits

iOS framework linking, simulator interaction and device inspection require the
macOS CI or a local Mac. Sample web targets remain deferred because the existing
navigation dependency has no JS/Wasm artifacts. No navigator migration was made.
Screenshot checks use actual Compose/Layoutlib renders, with existing thresholds.
History bounds retained entries; their byte size depends on the retained objects.
