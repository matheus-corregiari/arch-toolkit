# Changelog

## 2.0.0-rc18 — Unreleased

- Remove the local State Handle module and all its publications.
- Migrate the sample to Event Observer State 2.3.0, serializable payloads and a new saved-state key.
- Update dependencies and build tools; see [Dependencies](dependencies.md).
- Correct module, platform, installation and migration documentation.
- Include samples and KMP sources in quality gates; enable Detekt rules and fix their findings.
- Refactor polling into focused helpers without changing its behavior, with common regression tests.
- Fix log-tag regular expressions that failed at runtime in JavaScript browsers.

## 2.0.0-rc17

- Move storage consumers to independently released Arch Storage artifacts.
- Remove the local storage modules and document their migration.
- Announce State Handle migration and add Maven relocation metadata for its publications.
