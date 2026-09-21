# Arch Toolkit Showcase implementation

## Scope

Continue `release/2.0.0-rc19`, PR #157, without merging, tagging or publishing.
Preserve pre-existing staged `.codex/` files. Showcase changes only.

## Completed implementation

- [x] Organize targets, app composition, features, repository, sources and design modules.
- [x] Enforce module dependency boundaries and detect cycles.
- [x] Integrate Easy Navigation on Android, desktop and Apple shared code.
- [x] Add the SwiftUI/XcodeGen iOS host and macOS CI build.
- [x] Separate transport Response, repository RO and Room Entity models.
- [x] Preserve cancellation and transport failure causes; use platform TLS validation.
- [x] Implement GitHub search, filters, pagination, retry, details, links and bounded history.
- [x] Persist runtime Portuguese/English settings and localize counts and dates.
- [x] Provide executable Lumber/Storage demos and source-generated snippets.
- [x] Display actual shared design tokens and widgets in the design catalogue.
- [x] Add repository, persistence, locale, navigation and Compose UI tests.
- [x] Document running, extending and reusing the showcase; update the rc19 changelog.
- [ ] Confirm remote macOS/iOS validation after pushing the branch.

## Compatibility decisions

Web remains deferred by explicit user decision: published Easy Navigation 1.0.1
has no JS/Wasm artifacts. No alternative web navigation was introduced.

Easy Navigation 1.0.1 calls Lumber's older Oak-returning tag signature. Sample
configurations select Lumber 1.1.0 and Storage 2.0.0-rc16, which uses that ABI.
Storage 1.0.0 requires the newer Lumber ABI. These compatibility selections apply
only to sample configurations; published Toolkit dependencies are unchanged.
Android Context is injected through Koin rather than an Arch Android singleton.

## Validation evidence (2026-09-21)

- Full `ciBuild ciTest ciCoverage ciLint ciDocs -PincludeSamples
  -PreleaseVersion=2.0.0-rc19 --max-workers=4 --continue` passed:
  1857 actionable tasks, 238 executed, 1619 up-to-date, 2m 20s.
- Targeted app JVM/Android unit tests, repository JVM tests and Android release
  assembly passed, including R8 without suppression workarounds.
- Compose UI tests executed on JVM; the rendered demo screenshot was inspected.
- Real temporary Room database tests verify trimming and close/reopen persistence.
- Strict MkDocs build and actionlint passed.
- Android device execution was unavailable: the configured AVD lacks its system
  image. Android assembly and host-side unit tests are verified.
- Apple tests are skipped on Windows. The macOS CI and simulator host build must
  supply native evidence; local simulator execution is not claimed.

## Delivery

Focused implementation commits are on the existing release branch. The existing
PR must remain open. No merge, tag, release or package publication is authorized.
