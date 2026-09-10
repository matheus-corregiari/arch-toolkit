# RC18 validation

Branch: `release/2.0.0-rc18`, based on merged master `d85348b`.
This report records local validation before PR creation. No tag or artifact publication was performed.

## Verified locally on Windows — 2026-09-09

```shell
./gradlew build ciBuild ciTest ciCoverage ciLint ciDocs ciPublicationManifest -PincludeSamples -PreleaseVersion=2.0.0-rc18 --continue --no-daemon --console=plain
```

Result: **BUILD SUCCESSFUL**, 4m 10s, 1,160 tasks (207 executed, 953 up-to-date).
Log: `rc18-verified.log`.

- Android debug and release builds, including R8 and the unsigned release APK.
- Desktop JVM sample compilation and library JVM/Android/JS/Wasm builds.
- Detekt with rules enabled and KMP sources included, Ktlint and Android lint.
- Kover report generation and verification; Dokka generation.
- Six new common regression tests passed on JVM, Android unit tests, JS Chrome Headless
  and Wasm Chrome Headless: 24 successful executions with no new skipped tests.
- Three sample integration tests passed: loading/reloading, error retaining data and JSON
  restoration with isolation from the old saved-state key.
- Two pre-existing OneShot tests remain ignored per target. They are not counted as passed.
- All seven POMs, all seven Gradle module metadata files and the publication manifest contain only Splinter coordinates at `2.0.0-rc18`. Metadata generation passed in 24s (`rc18-metadata.log`), with no legacy state dependency.
- `python -m mkdocs build --strict`: passed (`rc18-docs.log`).
- actionlint 1.7.12: passed with no diagnostics (`rc18-workflows.log`).
- `git -c core.whitespace=cr-at-eol diff --check`: passed; the wrapper retains Windows CRLF.

The audit in `build/verify_release.py` checked the actual XML test results, POMs,
publication manifest, release APK and verification logs. Build output and audit scripts are ignored.

## Platform limitation accepted for this delivery

Kotlin compilation produced iOS ARM64 and Simulator ARM64 klibs on Windows, but framework
linking and `iosSimulatorArm64Test` were explicitly skipped by the host. These remain **not verified**.
Apple framework linking and simulator execution remain pending macOS CI and are not
claimed as part of the successful local Windows verification. The PR workflow includes
a macOS ARM64 runner for those checks.
