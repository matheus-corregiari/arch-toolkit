# Arch Toolkit · Artifact Publishing

[![CI](https://github.com/matheus-corregiari/arch-toolkit/actions/workflows/pull-request.yml/badge.svg?branch=master)](https://github.com/matheus-corregiari/arch-toolkit/actions/workflows/pull-request.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.matheus-corregiari/storage-core?logo=apache-maven&style=flat-square)](https://central.sonatype.com/search?q=io.github.matheus-corregiari)
[![License](https://img.shields.io/github/license/matheus-corregiari/arch-toolkit?style=flat-square)](../../LICENSE.md)

## Objective

Describe the release workflow, credentials, and validation steps for safely publishing multiplatform artifacts.

## Installation

You need access tokens and signing keys configured as environment variables or Gradle properties:

```
ORG_GRADLE_PROJECT_signingKeyId=<key-id>
ORG_GRADLE_PROJECT_signingKey=<ascii-armored-key>
ORG_GRADLE_PROJECT_signingPassword=<passphrase>
ORG_GRADLE_PROJECT_sonatypeUsername=<username>
ORG_GRADLE_PROJECT_sonatypePassword=<password>
```

Import the provided `.gpg` keys in the repository root if you have access:

```bash
gpg --import public_key_matheus.gpg
gpg --allow-secret-key-import --import public_key_matheus_secret.gpg
```

## Usage Examples

1. Update versions in `gradle/libs.versions.toml` and affected module `build.gradle.kts` files.
2. Run the verification suite:
   ```bash
   ./gradlew clean check
   ```
3. Stage artifacts locally:
   ```bash
   ./gradlew publishToMavenLocal
   ```
4. Publish to Sonatype and close the staging repository:
   ```bash
   ./gradlew publishAllPublicationsToSonatypeRepository closeAndReleaseSonatypeStagingRepository
   ```
5. Verify that new versions are visible on [Maven Central](https://central.sonatype.com/search?q=io.github.matheus-corregiari).

## License

Artifacts inherit the [Apache License 2.0](../../LICENSE.md). Ensure third-party dependencies comply with the same or compatible licenses.

## Additional Resources

* [Overview](overview.md)
* [Contribution Guide](contribution-guide.md)
* [Roadmap](roadmap.md)
