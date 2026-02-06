# Arch Toolkit · Roadmap

[![CI](https://github.com/matheus-corregiari/arch-toolkit/actions/workflows/pull-request.yml/badge.svg?branch=master)](https://github.com/matheus-corregiari/arch-toolkit/actions/workflows/pull-request.yml)
[![Coverage](https://img.shields.io/endpoint?url=https%3A%2F%2Fgist.githubusercontent.com%2Fmatheus-corregiari%2F4fbcfa4cec61deb2262b16c19ab14138%2Fraw%2Fcoverage-badge.json&logo=kotlin)](https://github.com/matheus-corregiari/arch-toolkit/actions/workflows/pull-request.yml)
[![License](https://img.shields.io/github/license/matheus-corregiari/arch-toolkit?style=flat-square)](../../LICENSE.md)

## Objective

Track upcoming features, module stabilisation efforts, and broader platform goals with a focus on Kotlin Multiplatform readiness.

## Installation

Refer to the [installation guide](../../README.md#-installation-gradle-kotlin-dsl) to ensure you are on the latest release while experimenting with roadmap items.

## Usage Examples

### Near Term (0–3 months)

* Promote `storage-datastore` to **Stable** with multiplatform integration tests.
* Deliver Compose Multiplatform samples showcasing `storage-core` and `event-observer` interoperability.
* Replace legacy Android-only storage helpers with thin adapters over the new KMP APIs.

### Mid Term (3–6 months)

* Stabilise `event-observer-compose` once Compose for iOS matures.
* Introduce build logic to keep dependencies automatically upgraded via Renovate or Dependabot.

### Long Term (6–12 months)

* Explore WebAssembly runtime support with real storage backends.
* Provide a unified configuration DSL that wires logging, storage, and state modules together.
* Evaluate alternative concurrency primitives for KMP (e.g., `kotlinx.coroutines` with structured concurrency on Native) to avoid leaks.

## License

The roadmap content is shared under the [Apache License 2.0](../../LICENSE.md). Contributions to the roadmap follow the same guidelines as code changes.

## Additional Resources

* [Overview](overview.md)
* [Contribution Guide](contribution-guide.md)
* [Artifact Publishing](artifact-publishing.md)
