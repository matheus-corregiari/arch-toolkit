---
name: project-changelog
description: Generate and review Arch Toolkit changelog pages from Git tag ranges. Use when Codex needs to find missing release pages, update docs/changelog/index.md, or validate release documentation against Gradle and Git tags.
---

# Project Changelog

## Workflow

1. Inspect semantic version tags.
2. Compare tags with `docs/changelog/*.md`.
3. Generate each missing page from its previous-tag range.
4. Update `docs/changelog/index.md` newest-first.
5. Review generated text against the actual diff.
6. Run build and consistency checks.

## Generation

Run from the repository root:

```bash
python .codex/skills/project-changelog/scripts/generate_missing_changelogs.py
```

## Rules

- Keep pages factual and short.
- Use the release tag's version catalog, not current worktree values.
- Document compatibility changes only when the tag range contains them.
- Do not create pages for uncommitted work unless explicitly requested.
- Keep the target table aligned with the targets changed in the release.

## Validation

```powershell
./gradlew build --no-daemon
git diff --check
```
