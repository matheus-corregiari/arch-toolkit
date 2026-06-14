# Contributing to Arch Toolkit

Use JDK 21, the checked-in Gradle wrapper, and a focused branch from `master`.

Before opening a pull request, run the checks relevant to the change:

```bash
./gradlew ciLint ciBuild ciTest
./gradlew ciCoverage
./gradlew ciDocs
python -m mkdocs build --strict
```

Samples are opt-in:

```bash
./gradlew -PincludeSamples=true ciSamples
```

The complete contribution guide covers repository architecture, build logic,
quality commands, documentation, dependency updates, publishing, release
branches, tag validation, and pull-request expectations:

https://matheus-corregiari.github.io/arch-toolkit/contributing/

Do not commit generated build output, Dokka HTML, the MkDocs site, local
properties, or IDE files.
