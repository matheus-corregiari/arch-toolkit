# Migration and Version Updates

## Use One Version

All modules are released together. Do not mix Arch Toolkit versions unless a
release note explicitly says that combination is supported.

```toml
[versions]
arch-toolkit = "<latest-version>"
```

## Before Updating

1. Read the [changelog](../changelog/index.md) from the installed version to
   the target version.
2. Check target availability for every selected module.
3. Update all Arch Toolkit artifacts in the same change.
4. Compile every consumer target.
5. Exercise persistence, state restoration, and request cancellation paths
   affected by the update.

Release candidates may change before the stable `2.0.0` API. Treat an RC update
as a migration and review its release page.

## Manual Snippet Policy

Documentation snippets are intentionally maintained by hand. During release
preparation, compare them with current declarations and sample usage. The
repository-local `project-docs` skill performs this audit.
