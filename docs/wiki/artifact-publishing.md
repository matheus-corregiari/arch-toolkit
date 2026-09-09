# Artifact publishing

Arch Toolkit rc18 publishes only Splinter and its Android, JVM, JS, Wasm and iOS variants.
The test module stays internal. State and storage have independent releases:
[State migration](../state-migration.md), [Storage migration](../storage-migration.md).

## Verification

Use JDK 21 and the wrapper. Run Apple checks on macOS with Xcode.

```shell
./gradlew ciLint ciBuild ciTest ciCoverage ciDocs -PincludeSamples
./gradlew ciPublicationManifest -PreleaseVersion=2.0.0-rc18
```

Inspect `build/ci/publications.tsv`: every coordinate must be Splinter at the intended version.
The `releaseVersion` property overrides `build/version-name.txt` for local verification.
The release workflow writes that file using the release branch name.

## Release flow

The existing release workflow runs after a release/hotfix PR merges into master, or through
its manual recovery entrypoint. It builds, publishes using `ciPublishMavenCentral`, pushes
an annotated tag and creates a GitHub Release. See [CI and release](../ecosystem/ci-release.md).
Publishing requires repository credentials and signing secrets.
Preparing or validating a candidate locally does not publish it.
