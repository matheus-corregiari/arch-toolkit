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
- [x] Confirm remote macOS/iOS validation after pushing the branch.

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

## First remote validation

- Ubuntu validation and CodeQL passed at `5be6fe1` (run 35634000680).
- macOS completed native compilation and simulator tests; the app's two
  simulator link tests passed. The overall job failed on the JVM demo test
  asserting the Storage result before its asynchronous completion.
- The demo UI test now waits for the operation's explicit completion state
  before asserting the rendered result. The host build still requires a green run.

## Native validation follow-up (2026-09-27)

At `7016789`, run 36276708437 passed Ubuntu and the complete macOS Gradle
validation, including the corrected asynchronous UI test. The Xcode host then
failed because its generic simulator build requested x86_64, while the shared
framework and Compose resources support iosSimulatorArm64. The host now selects
ARM64 explicitly; its CI step runs first so integration failures surface earlier.

## Final requirement audit (2026-09-27)

[CI run 36314017755](https://github.com/matheus-corregiari/arch-toolkit/actions/runs/36314017755)
passed on Ubuntu and macOS at `c47ff6c`, including the ARM64 iOS host and the full
Gradle suite. The final audit changes documentation only. No code rebuild is
needed for this evidence record; strict documentation checks are run separately.

| Requirement | Implementation evidence | Verification and limits |
| --- | --- | --- |
| Modules and boundaries | [settings.gradle.kts](settings.gradle.kts), [boundary checks](gradle/showcase-boundaries.gradle.kts) | ciLint checks forbidden project edges and cycles; Android/Desktop builds and native compilation passed. |
| RO / VO / source models | [RepoRO.kt](sample/shared/data/repository/src/commonMain/kotlin/br/com/arch/toolkit/sample/github/shared/structure/repository/model/RepoRO.kt), [RepoVO.kt](sample/shared/features/github-sample/src/commonMain/kotlin/br/com/arch/toolkit/sample/feature/githubSample/ui/list/model/RepoVO.kt), [RepoResponse.kt](sample/shared/data/source/remote/src/commonMain/kotlin/br/com/arch/toolkit/sample/github/shared/structure/data/remote/model/RepoResponse.kt) | RepositoryTest verifies transport-to-repository mapping; feature presentation uses RepoVO. Room exposes Entity/DAO only below repository composition. |
| Constructor DI | [ListViewModel.kt](sample/shared/features/github-sample/src/commonMain/kotlin/br/com/arch/toolkit/sample/feature/githubSample/ui/list/ListViewModel.kt), [SettingsRepository.kt](sample/shared/data/repository/src/commonMain/kotlin/br/com/arch/toolkit/sample/github/shared/structure/repository/SettingsRepository.kt), [ToolkitViewModel.kt](sample/shared/features/toolkit-sample/src/commonMain/kotlin/br/com/arch/toolkit/sample/feature/toolkit/ToolkitViewModel.kt) | App initKoin registers constructor dependencies and platform factories; features do not depend on source modules. |
| HTTP and domain failures | [HttpClients.kt](sample/shared/structure/http/src/commonMain/kotlin/br/com/arch/toolkit/sample/http/HttpClients.kt), [RemoteGithubRepository.kt](sample/shared/data/repository/src/commonMain/kotlin/br/com/arch/toolkit/sample/github/shared/structure/repository/RemoteGithubRepository.kt) | RepositoryTest covers malformed responses, I/O failures and cancellation. JVM/Apple clients retain platform TLS validation and configured timeouts. |
| Room in application use | [ShowcaseDatabase.kt](sample/shared/data/source/local/src/commonMain/kotlin/br/com/arch/toolkit/sample/data/local/ShowcaseDatabase.kt), [RecentRepository.kt](sample/shared/data/repository/src/commonMain/kotlin/br/com/arch/toolkit/sample/github/shared/structure/repository/RecentRepository.kt), [DetailViewModel.kt](sample/shared/features/github-sample/src/commonMain/kotlin/br/com/arch/toolkit/sample/feature/githubSample/ui/detail/DetailViewModel.kt) | Successful detail loads update the last 20 identities. DatabaseTest closes/reopens a real temporary database and verifies trimming/order. |
| GitHub interactions | [ListViewModelTest.kt](sample/shared/features/github-sample/src/commonTest/kotlin/br/com/arch/toolkit/sample/feature/githubSample/ui/list/ListViewModelTest.kt), [RepositoryDetailScreen.kt](sample/shared/features/github-sample/src/commonMain/kotlin/br/com/arch/toolkit/sample/feature/githubSample/ui/detail/RepositoryDetailScreen.kt) | Tests cover filter reset, page failure/retry, out-of-order responses and saved query/results. Detail loads by owner/name; error/retry UI is wired to domain failures. |
| Links / back / restoration | [ShowcaseLinkTest.kt](sample/shared/app/src/commonTest/kotlin/br/com/arch/toolkit/sample/shared/ui/home/ShowcaseLinkTest.kt), [NavigationTest.kt](sample/shared/app/src/jvmTest/kotlin/br/com/arch/toolkit/sample/shared/NavigationTest.kt) | Generated route matching is tested on common targets. JVM tests exercise the real controller, back navigation and save/restore of its stack. Platform launchers forward links. |
| Lumber / Storage demos | [ToolkitDemoRepository.kt](sample/shared/data/repository/src/commonMain/kotlin/br/com/arch/toolkit/sample/github/shared/structure/repository/ToolkitDemoRepository.kt), [ToolkitUiTest.kt](sample/shared/app/src/jvmTest/kotlin/br/com/arch/toolkit/sample/shared/ToolkitUiTest.kt) | RepositoryTest covers CRUD and the bounded/unregistered Lumber tree. UI tests operate keyboard logging, clear, save and delete. generateDemoSnippets reads marked executable methods. |
| Shared design catalogue | [DesignScreen.kt](sample/shared/features/design-sample/src/commonMain/kotlin/br/com/arch/toolkit/sample/feature/design/DesignScreen.kt) | Uses actual AppTheme tokens and shared AppButton/EmptyState/ErrorState; interactive selection, disabled/loading states. Compose demo screenshot was inspected; no exhaustive accessibility audit claimed. |
| PT-BR / English | [SettingsRepository.kt](sample/shared/data/repository/src/commonMain/kotlin/br/com/arch/toolkit/sample/github/shared/structure/repository/SettingsRepository.kt), [ShowcaseApp.kt](sample/shared/app/src/commonMain/kotlin/br/com/arch/toolkit/sample/shared/ShowcaseApp.kt), [LocaleTest.kt](sample/shared/structure/core/src/commonTest/kotlin/br/com/arch/toolkit/sample/LocaleTest.kt) | Persistent language storage is collected at runtime into LocalAppLanguage. RepositoryTest covers restoration using the same provider; LocaleTest covers formatting/fallback. Process-restart/device UI localization is not separately automated. |
| Shared implementation / tests | [shared modules](sample/shared) | Feature logic is in commonMain; repository/search/locale/link tests run in commonTest. Final iOS Simulator reports: 11 tests, zero failures or skips. |
| Platform validation | [iOS host](sample/target/ios/project.yml), [CI](.github/workflows/pull-request.yml) | Windows/Ubuntu Android release and JVM checks passed; JVM Compose UI ran. macOS passed native tests and the unsigned ARM64 Swift/Xcode host build. Android device execution remains unavailable because the configured AVD image is absent. |
| Docs / release record | [showcase guide](docs/showcase.md), [changelog](docs/CHANGELOG.md), [dependencies](docs/dependencies.md) | README and MkDocs navigation link the guide; strict MkDocs passed. Existing PR #157 carries the changes and commit changelog; remains open without merge/tag/publication. |

Web remains deferred by the approved scope decision. iOS validation is CI build
and simulator tests, not manual device inspection. Performance choices (lazy
rows and bounded buffers/history) are not benchmark claims. Pre-existing staged
`.codex/` files remain outside showcase commits.

## Screenshot regression follow-up

- Official Compose Preview Screenshot Testing, with AndroidX previews, runs in
  the Android target. Four reviewed PNG references cover theme, locale, font scale
  and wide/high-contrast rendering of shared widgets.
- A deliberate title change failed all four comparisons; the source was restored.
- A dedicated Windows CI job validates existing references and publishes diffs.
- See the screenshot section in `docs/showcase.md` for commands and scope.

## Feature screenshot coverage follow-up

- Expanded four widget previews to 69 references across all features and the
  adaptive shell. The coverage matrix and source locations are in `docs/showcase.md`.
- Feature-owned screenshot sources run through the existing Android Layoutlib host.
  Production routes delegate to the same content rendered in tests.
- Covers loading, errors, empty/success, pagination, preferences, Storage outcomes,
  missing/long data, scrolled sections, themes, locales, narrow/wide/landscape
  layouts and 1.5x/2x fonts; not an exhaustive device matrix.
- Validation on 2026-09-28: 69 screenshot comparisons passed (zero failures/skips),
  8 JVM app/GitHub tests passed, full `ciLint` and strict MkDocs passed. A temporary
  GitHub query mutation failed exactly 9 comparisons; restoring the fixture returned
  the suite to green without updating references. All reference images were reviewed.
