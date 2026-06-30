# Ecosystem Gitflow

Arch libraries are independent, but they follow one release contract. Normal work flows through
`develop`; release and hotfix merges into `master` are publication events.

```text
feature/* --+
config/*  --+--> develop ---> release/x.y.0[-rcN] --+
bugfix/*  --+                                       |
                                                  +--> master --> tag --> publish
hotfix/x.y.z[-rcN] -------------------------------+
```

## Branch Roles

| Branch | Responsibility |
|:-------|:---------------|
| `develop` | Receives normal development and keeps the next release candidate alive. |
| `master` | Represents published history. Release and hotfix merges create a tag and publish. |

`master` is not a parking lot. It receives release and hotfix branches, plus narrowly scoped
`config/*` pull requests needed to repair CI or repository configuration. Config merges do not
publish.

## Allowed Branches

```text
To develop:

feature/my-new-api
config/update-ci
bugfix/fix-empty-state

To master:

config/recover-release-ci
release/2.0.0
release/2.0.0-rc1
hotfix/2.0.1
hotfix/2.0.1-rc1
```

| Target | Accepted branch patterns | Meaning |
|:-------|:-------------------------|:--------|
| `develop` | `feature/*` | Product or API work. |
| `develop` | `config/*` | Build, CI, tooling, docs infrastructure, or repository configuration. |
| `develop` | `bugfix/*` | Fixes that are not emergency production patches. |
| `develop` | `master` | Automated back-merge after publication. |
| `master` | `config/*` | CI or repository recovery; never a publication trigger. |
| `master` | `release/x.y.0` | Stable major or minor release. |
| `master` | `release/x.y.0-rcN` | Release candidate for a major or minor release. |
| `master` | `hotfix/x.y.z` | Patch release, where `z >= 1`. |
| `master` | `hotfix/x.y.z-rcN` | Release candidate for a patch release. |

## Release vs Hotfix

Use a release branch when the version ends in patch `0`.

```text
release/1.4.0
release/2.0.0-rc1
```

Use a hotfix branch when the patch is `1` or higher.

```text
hotfix/1.4.1
hotfix/1.4.2-rc1
```

This keeps version intent visible before CI runs.

## Pull Request Gates

### Into develop

PRs into `develop` must pass the everyday quality gate:

```text
lint -> build -> tests -> docs when affected
```

Docs are affected when the PR changes:

- public API;
- user-visible behavior;
- setup or installation;
- CI or release flow;
- samples.

### Into master

PRs into `master` must pass the release gate:

```text
lint -> build -> tests -> coverage -> docs review -> affected samples
```

For `arch-toolkit`, the web sample is part of the release product. A tag publication must build:

```text
MkDocs + Dokka + web sample -> GitHub Pages
```

If the web sample does not build, the `arch-toolkit` release fails.

## CI Enforcement

Gitflow is enforced by GitHub Actions, not by convention alone.

```text
Pull request
     |
     v
Branch Policy
     |
     +-- develop accepts feature/*, config/*, bugfix/*, or master back-merge
     |
     +-- master accepts config/*, release/x.y.0[-rcN], hotfix/x.y.z[-rcN]
```

On `master`, a merged release or hotfix PR is the release trigger:

```text
merge release/hotfix PR
        |
        v
resolve version from branch
        |
        v
publish artifacts
        |
        v
create tag
        |
        v
create GitHub Release
```

The detailed CI and artifact flow lives in [CI and Release](ci-release.md).

## Master Merge Flow

```mermaid
flowchart TD
    A["release/x.y.0 or hotfix/x.y.z"] --> B["Pull request to master"]
    B --> C["Release quality gate"]
    C --> D["Merge into master"]
    D --> E["Extract version from branch"]
    E --> F["Publish artifacts"]
    F --> G["Create Git tag"]
    G --> H["Create GitHub Release"]
    D --> I["Open or update master to develop PR"]
    I --> J["Approve workflow runs"]
    J --> K["Required checks"]
    K --> L["Auto-merge into develop"]
```

The branch name is the version source. CI should not ask for a second version input.

## Mergeback

Every merged release or hotfix PR creates or updates a `master -> develop` back-merge PR. The
workflow uses the repository `GITHUB_TOKEN`, so GitHub requires a maintainer to approve the pending
workflow runs. Auto-merge completes the PR after the required checks pass.

```text
master tag 2.0.0
        |
        +-- generated changelog
        +-- release notes metadata
        +-- mergeback PR -> develop
```

Only one open back-merge PR is kept. Later merges into `master` reuse it instead of creating
duplicates. If the branches conflict, auto-merge stops and the PR remains open for manual conflict
resolution. The workflow never force-pushes or bypasses branch protection.

The mergeback is mandatory before the next release or hotfix. Do not patch published history on
`master`.

## GitHub Release Content

A good GitHub Release should be useful without opening the repository:

- short executive summary;
- highlights;
- breaking changes;
- migration notes when needed;
- artifact list;
- compatibility notes;
- links to docs, API reference, and samples;
- contributors;
- compare link;
- executed checks;
- known issues when any exist.

## Repository Rhythm

Each repository releases independently. There is no global ecosystem version.

`arch-toolkit` is the ecosystem hub:

- central standards live here;
- the official sample experience lives here;
- library docs link back here for ecosystem context and samples;
- new or unclear libraries can incubate here before extraction.

Extraction happens when a library has:

- stable API;
- clear purpose;
- isolated usage that makes sense outside `arch-toolkit`.
