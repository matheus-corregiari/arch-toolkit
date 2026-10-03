# CI and Release

This page explains how the ecosystem master-only release flow is enforced by GitHub Actions.

## Pull Request Validation

Arch Toolkit retains its branch policy workflow. The build workflow validates pull requests
against master on Linux and macOS, and also supports manual validation. It runs library builds,
Android/desktop samples, tests, coverage, Android lint, Detekt, Ktlint, Dokka and strict MkDocs.
macOS covers the configured iOS targets; Linux cannot execute their compiler or simulator tests.

```text
PR opened or updated
        |
        v
Branch Policy
        |
        +-- base master -> head must be feature/*, fix/*, bugfix/*, config/*,
                            docs/*, chore/*, dependabot/*, release/x.y.0[-rcN],
                            or hotfix/x.y.z[-rcN]
```

The branch policy runs before expensive build work. It keeps repository history aligned with the
release model.

## Master Gate

Pull requests into `master` use the normal development and release gate:

```text
branch policy -> lint -> build -> tests -> coverage -> docs -> affected samples
```

`release/*` and `hotfix/*` branches are publication candidates. Other branch types are normal code,
docs, dependency, or repository maintenance changes and do not publish artifacts.

## Automatic Tagging

When a release or hotfix PR is merged into `master`, CI reads the source branch name:

```text
release/2.0.0      -> 2.0.0
release/2.0.0-rc1  -> 2.0.0-rc1
hotfix/2.0.1       -> 2.0.1
hotfix/2.0.1-rc1   -> 2.0.1-rc1
```

The release workflow creates the annotated tag from that version.

The tag name does not use a `v` prefix.

## Artifact Flow

The main release path is a single CI run after the PR merge:

```text
merge into master
        |
        v
resolve version from branch
        |
        v
write the explicit version file
        |
        v
verify build and quality gates
        |
        v
publish artifacts
        |
        v
push tag
        |
        v
create GitHub Release
```

The tag-triggered workflow is not part of this path, which prevents duplicate publication. Gradle
receives the resolved version explicitly, and the remote tag is pushed only after every publisher
succeeds.

## Release Recovery

The master release workflow supports manual recovery after its workflow fix has reached `master`.
The operator must provide both the original `release/*` or `hotfix/*` branch name and the exact
master commit SHA. CI validates an existing tag against that commit, reuses it when correct, and
rejects it when it points elsewhere.

## Repository Differences

The branch and version rules are shared. Build and publication commands stay repository-specific.

| Repository | Release verification | Publication |
|:-----------|:---------------------|:------------|
| `arch-toolkit` | Linux/macOS build, lint, tests, docs and samples | Splinter variants only; Maven Central from macOS |
| `arch-android` | `./gradlew ciBuild` | `ciPublishMavenCentral`, `ciPublishGithubPackages` |
| `arch-event-observer` | `./gradlew ciBuild` | `ciPublishMavenCentral`, `ciPublishGithubPackages` |
| `arch-lumber` | `./gradlew build`, `:lumber:koverVerify` | Maven Central and GitHub Packages tasks |

## Tag Workflow Fallback

Repositories may keep a manually dispatched release workflow as an escape hatch. It must not react
to the tag pushed by the automatic master merge flow, otherwise both workflows can publish the same
version concurrently.

The normal path is still:

```text
release/hotfix PR -> master -> tag + publish in the same workflow
```

## Coverage and Codecov

`buildSrc/src/main/kotlin/com/toolkit/plugin/ToolkitCoveragePlugin.kt` is the single source of report exclusions. It applies the same Kover filter
to every covered module and the root report. Only Android-generated `*.BuildConfig`, `*.R`
and `*.R$*` are excluded: they contain generated constants/resources, not application behavior.
Do not exclude DTOs, state classes, Compose functions or entire packages just to raise coverage.

```sh
./gradlew ciCoverage
# Root report only (automatically runs the required JVM/Android host tests):
./gradlew :koverXmlReport :koverHtmlReport :koverVerify
```

Open `build/reports/kover/html/index.html` locally. Codecov receives only
`build/reports/kover/report.xml`, with `disable_search: true`; automatic discovery would also find
module or older reports and could merge excluded classes back into the result.
There is deliberately no second `ignore` list in `codecov.yml`: Codecov consumes the already-filtered
XML. Its `ignore` patterns describe source paths, while Kover filters describe JVM class names.
An IDE coverage run or another coverage tool must use this Gradle report to share these exclusions.

Compare the same commit and line metric. Codecov's treatment of partially covered lines can differ
from Kover, so equal file scope does not promise identical percentages. Existing Gradle verification
rules remain authoritative; Codecov provides visibility rather than an additional threshold.
JVM/Android host execution supplies the coverage counters. Apple, JS and Wasm tests still run in
the platform test suite but do not add Kover coverage. Coverage uploads occur only after successful
master validation (release validation for Toolkit).

References: [Kover report filtering](https://kotlin.github.io/kotlinx-kover/gradle-plugin/#filtering-reports),
[Codecov file search](https://docs.codecov.com/docs/file-search) and
[Codecov path ignores](https://docs.codecov.com/docs/ignoring-paths).

The root report covers the published Splinter module. Samples and test helpers are outside that
library report, while their tests still run through `ciTest`. The former settings aggregation plugin
used a different task contract; the project plugin supplies the tasks required by `ciCoverage`.

Toolkit currently has no numerical coverage floor configured. `ciCoverage` runs tests and generates
the reports; `koverVerify` becomes a percentage gate only when explicit rules are added.

The GitHub Release body is extracted from the exact version section in `docs/CHANGELOG.md`.
Historical versions without a section use generated GitHub notes. No root release-notes copy is maintained.
