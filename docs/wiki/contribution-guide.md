# Arch Toolkit · Contribution Guide

[![CI](https://github.com/matheus-corregiari/arch-toolkit/actions/workflows/pull-request.yml/badge.svg?branch=master)](https://github.com/matheus-corregiari/arch-toolkit/actions/workflows/pull-request.yml)
[![Coverage](https://img.shields.io/endpoint?url=https%3A%2F%2Fgist.githubusercontent.com%2Fmatheus-corregiari%2F4fbcfa4cec61deb2262b16c19ab14138%2Fraw%2Fcoverage-badge.json&logo=kotlin)](https://github.com/matheus-corregiari/arch-toolkit/actions/workflows/pull-request.yml)
[![License](https://img.shields.io/github/license/matheus-corregiari/arch-toolkit?style=flat-square)](https://github.com/matheus-corregiari/arch-toolkit/blob/master/LICENSE.md)

## Objective

Explain how to contribute safely without breaking published APIs while maintaining the Kotlin Multiplatform-first vision.

## Installation

Clone the repository and bootstrap Gradle:

```bash
git clone https://github.com/matheus-corregiari/arch-toolkit.git
cd arch-toolkit
./gradlew tasks
```

Install the recommended toolchain:

* **JDK**: 17+
* **Kotlin**: Managed by the Gradle wrapper (see `gradle/libs.versions.toml`).
* **Android Studio / IntelliJ IDEA**: For IDE inspections and Compose previews.

## Usage Examples

Follow these practices when contributing:

1. **Target KMP first** – add behavior to shared modules before platform-specific forks.
2. **Keep implementations untouched** unless you pair them with unit tests and documentation updates.
3. **Write tests** – place them under the relevant `commonTest`, `androidUnitTest`, or `iosTest` source set.
4. **Run CI locally** – `./gradlew clean check` covers lint, tests, and static analysis.
5. **Document your changes** – update the module README and wiki pages if workflows change.

## Branching and Releases

- Target `master` for all pull requests.
- Use `feature/*`, `fix/*`, `bugfix/*`, `config/*`, `docs/*`, `chore/*`, or `dependabot/*` for normal work.
- Use `release/x.y.0[-rcN]` for major or minor releases.
- Use `hotfix/x.y.z[-rcN]` for patch releases, where `z >= 1`.
- Do not open mergeback pull requests. The repository does not use a long-lived `develop` branch.
- Release and hotfix merges into `master` are the only automatic publication trigger.
- The release workflow derives the tag from the branch name, publishes artifacts, creates the tag, and creates the GitHub Release.

## License

By contributing, you agree that your code will be released under the [Apache License 2.0](https://github.com/matheus-corregiari/arch-toolkit/blob/master/LICENSE.md).

## Pull Request Checklist

- [ ] Tests cover new behaviour.
- [ ] Documentation updated (README, KDoc, or wiki).
- [ ] No implementation regressions (verify with `./gradlew check`).
- [ ] Changelog entry or release note proposed when applicable.

## Additional Resources

* [Overview](overview.md)
* [Artifact Publishing](artifact-publishing.md)
* [Roadmap](roadmap.md)
