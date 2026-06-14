# Development and Quality

## Environment

- JDK 21
- Git
- The checked-in Gradle wrapper
- Python with `mkdocs-material==9.7.6` for local site validation

## Root Commands

```bash
./gradlew ciLint
./gradlew ciBuild
./gradlew ciTest
./gradlew ciCoverage
./gradlew ciDocs
python -m mkdocs build --strict
```

`ciLint` runs Detekt, ktlint, and Android lint where applicable. `ciBuild`
assembles publishable libraries. `ciTest` runs supported library test suites.
`ciCoverage` generates merged XML and HTML reports without enforcing a coverage
threshold.

## Samples

```bash
./gradlew -PincludeSamples=true ciSamples
```

Run this when sample code, sample dependencies, shared UI, or target
configuration changes. A known Ktorfit compiler-plugin issue can affect this
task; see the [sample limitations](../sample/limitations.md).

## Pull Requests

- Keep changes scoped and readable.
- Add tests for behavior changes and regressions.
- Update public docs when usage, contracts, targets, or compatibility changes.
- Do not hide lint failures with broad suppressions or baselines.
- Do not commit generated outputs.
- Ensure the branch is current enough to make CI results meaningful.
