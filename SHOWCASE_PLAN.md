# Arch Toolkit Showcase implementation

## Scope and ownership

Continue `release/2.0.0-rc19`, PR #157, without merging, tagging or publishing.
Single execution owned by Codex task `01a096a0-6411-7590-9a7a-3967129cdb19`.
Daily maintenance must not edit Toolkit while this checklist is in progress.
Preserve the pre-existing staged `.codex/` files; commit only explicit task paths.

## Checklist

- [x] 1. Inventory, baseline builds and durable evidence.
- [ ] 2. Verify published Easy Navigation compatibility; minimal Android, iOS,
  web and desktop integration, including macOS CI for iOS.
- [ ] 3. Reorganize Gradle modules/packages and enforce dependency boundaries.
- [ ] 4. Separate repository contracts, transport, persistence, HTTP, DI and errors.
- [ ] 5. Extract shared design tokens and reusable widgets.
- [ ] 6. GitHub search/filter/pagination/detail/deep links and navigation tests.
- [ ] 7. Persistent Portuguese (Brazil)/English with runtime switching.
- [ ] 8. Executable Lumber/Storage demos and verified ecosystem cards.
- [ ] 9. Design catalogue consuming the app's actual tokens/widgets.
- [ ] 10. Tests, documentation, release changelog, PR description and CI evidence.

## Decisions

- User decision: retain Easy Navigation for Android, iOS and desktop; web stays
  pending. Do not introduce a separate web navigation implementation.
- App owns composition, DI and global navigation. Features depend on repository
  contracts, never sources. Core must not re-export unrelated dependencies.
- Repository exposes its own RO models; remote models use Request/Response;
  Room persistence uses Entity/DAO. Preferences have explicit storage purpose.
- Keep cancellation intact; map technical errors below presentation.
- Preserve visual identity. No performance claim without measurement.
- Targets declared in Gradle are not proof of execution. Record build, tests and
  execution separately. iOS evidence comes from macOS CI, not this Windows host.
- Resolve compatibility before spreading a navigation dependency across modules.

## Evidence log

- Initial branch/head: `release/2.0.0-rc19` at `ca49c25221d6e78373b2c5bbd4a761e83b5ab017`.
- Live GitHub verification: PR #157 is open on the expected branch; fetch confirms
  local and origin heads match.
- Existing sample: Android/Desktop included; web commented out in settings;
  shared app has an Apple controller but no iOS host target directory.
- Pre-existing `.codex/` additions are staged and must remain untouched.
- Daily maintenance is attached to this same task; this file records exclusive
  Toolkit ownership during the implementation.

## Validation and remaining limitations

Baseline `ciBuild ciCoverage ciLint -PincludeSamples -PreleaseVersion=2.0.0-rc19`
passed (1009 tasks, 50 seconds). Runtime execution evidence remains pending.

Easy Navigation 1.0.1 Maven metadata publishes Android, JVM, iosArm64 and
iosSimulatorArm64, but no JS/Wasm. Versions 1.0.0 and beta04 also lack web.
Navigation3 UI and adaptive-navigation3 themselves publish JS/Wasm; upstream
Easy Navigation additionally uses `KClass.qualifiedName` and a non-web test
annotation artifact. A separate source probe was prepared outside the repository;
no upstream changes or fork publication are authorized or required by the chosen scope.

Found during HTTP inventory: JVM clients install a trust-all X509TrustManager and
change the global SSL socket factory; Apple request client overrides server trust.
Remove these overrides in the HTTP extraction and retain platform TLS validation.
