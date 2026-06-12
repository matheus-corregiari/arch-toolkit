# Dependency Updates

## Policy

Prefer the latest stable release for runtime and public dependencies. Alpha,
beta, or release-candidate versions are acceptable only for build tooling or
test dependencies when necessary. Keep the Android Gradle Plugin on a stable
release.

## Process

1. Review `gradle/libs.versions.toml` and plugin versions.
2. Check target variants, not only repository metadata.
3. Update related dependencies together when their compatibility is coupled.
4. Run lint, build, tests, coverage, and docs.
5. Run sample validation separately.
6. Update compatibility, troubleshooting, badges, changelog preparation, and
   KDoc when behavior or support changes.

## Multiplatform Checks

Confirm every published target exists for a dependency. A version can be
current on Maven Central and still remove a required native variant. Pay
special attention to Compose, AndroidX Lifecycle, DataStore, serialization, and
compiler plugins.

## Version Bumps

All Arch Toolkit artifacts use one release version. Release version changes are
driven by the validated release or hotfix branch name and tag workflow, not by
independent module bumps.
