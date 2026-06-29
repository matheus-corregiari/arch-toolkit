# Arch Ecosystem Gitflow Standard

This is the source contract for Gitflow across the Arch ecosystem.

## Branches

| Branch | Role |
|:-------|:-----|
| `develop` | Integration branch for normal development. |
| `master` | Publication branch. Every merge into `master` must create a tag and publish. |

## Allowed Pull Requests

| Target | Allowed source branches |
|:-------|:------------------------|
| `develop` | `feature/*`, `config/*`, `bugfix/*` |
| `master` | `release/x.y.0`, `release/x.y.0-rcN`, `hotfix/x.y.z`, `hotfix/x.y.z-rcN` |

Rules:

- `release/*` branches must use patch `0`.
- `hotfix/*` branches must use patch `1` or higher.
- Release candidates use `-rcN`, for example `release/2.0.0-rc1`.
- `master` must not accept feature, config, or bugfix branches directly.
- A pending mergeback from the last `master` release blocks the next release or hotfix.

## Quality Gates

`develop` pull requests require:

- lint
- build
- tests
- docs when public API, behavior, setup, CI/release flow, or samples change

`master` pull requests require:

- lint
- build
- tests
- coverage
- docs review
- samples when affected

For `arch-toolkit`, the web sample is part of the release product and must build when a tag is published.

## Release Automation

When a release or hotfix branch is merged into `master`:

1. CI extracts the version from the source branch name.
2. CI verifies the release build.
3. CI creates the matching Git tag.
4. CI publishes artifacts in the same workflow run.
5. CI creates a polished GitHub Release.
6. CI opens a mergeback pull request into `develop` with generated changelog and release docs updates.

The main release workflow must not depend on a tag created by `GITHUB_TOKEN` to trigger a second
workflow. Tag-triggered release workflows may exist only as a fallback for externally created tags.

The mergeback must land before the next release or hotfix.

## Versioning

Each repository versions independently.

`arch-toolkit` acts as the ecosystem hub:

- it keeps official ecosystem standards;
- it hosts the central sample experience;
- it documents recommended compatibility across libraries;
- it incubates libraries until their API, purpose, and isolated usage are stable enough for extraction.
