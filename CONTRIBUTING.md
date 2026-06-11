# Contributing to Arch Toolkit

## Setup

1. Use JDK 21.
2. Use the checked-in Gradle wrapper.
3. Create a focused branch from `master`.

## Validation

```bash
./gradlew ciBuild
./gradlew ciTest
./gradlew ciLint
./gradlew ciCoverage
./gradlew ciDocs
python -m mkdocs build --strict
```

Validate samples separately when sample code changes:

```bash
./gradlew -PincludeSamples=true ciSamples
```

## Release Branches

Release and hotfix branches must match one of these formats:

- `release/X.Y.Z`
- `release/X.Y.Z-rcNN`
- `hotfix/X.Y.Z`
- `hotfix/X.Y.Z-rcNN`

After a matching branch is merged into `master`, GitHub Actions creates the tag
and the tag starts the release workflow.

## Pull Requests

- Keep changes scoped.
- Add tests for behavior changes.
- Update public documentation when usage or compatibility changes.
- Do not commit generated build output, API HTML, local properties, or IDE files.
