# Arch Toolkit Showcase implementation

## Scope and ownership

Continue `release/2.0.0-rc19`, PR #157, without merging, tagging or publishing.
Single execution owned by Codex task `01a096a0-6411-7590-9a7a-3967129cdb19`.
Daily maintenance must not edit Toolkit while this checklist is in progress.
Preserve the pre-existing staged `.codex/` files; commit only explicit task paths.

## Checklist

- [ ] 1. Inventory, baseline builds and durable evidence.
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

Fresh baseline, compatibility probes and runtime evidence pending.
