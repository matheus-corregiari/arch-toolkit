# Changelog

## Maintenance

- Updated the Gradle wrapper to 9.6.1.
- Updated Compose Multiplatform to 1.12.0.
- Aligned Arch Toolkit dependencies with Arch Android 1.2.1, Arch Lumber 1.3.0, and Arch Event Observer 2.1.1.
- Updated Kotlin, AGP, Koin, Ktor, and Coil to the versions validated by this refresh.
- Kept Kotlin at `2.3.21` because it is the compatible compiler ceiling for the complete JS/Wasm/iOS matrix.
- Added a CI gate that rejects duplicate or historical release tags.
- Preserved the CI, CodeQL, coverage, documentation, lint, and release workflow modernization.
