---
name: project-docs
description: Audit and synchronize Arch Toolkit documentation for release preparation. Use when Codex needs to compare the current branch with Git tags, detect documentation drift in APIs, modules, targets, dependencies, build or samples, review README/MkDocs/KDoc/Dokka coverage, or update release documentation after a reviewed audit.
---

# Project Docs

Run from the Arch Toolkit repository root. This skill is a release-preparation
gate, not a required step for ordinary pull requests.

## Choose a Mode

- Use **Audit** to inspect and report. Never edit files in this mode.
- Use **Sync** only after a human has reviewed the Audit report.

## Audit

1. Refresh local understanding with `git status`, current branch, and tags.
2. Run:

   ```bash
   python .codex/skills/project-docs/scripts/audit_docs.py
   ```

3. Inspect changes from the latest tag to `HEAD`, including:
   - public APIs and KDoc
   - published modules and targets
   - version catalog and build logic
   - workflows and release behavior
   - samples and known limitations
4. Compare findings against root/module/sample READMEs, MkDocs pages, snippets,
   Dokka links, and changelog coverage.
5. Report facts with file paths and evidence. Separate informational changes
   from actionable drift.
6. Do not modify any file, generate Dokka, or write the report to disk unless
   the user explicitly requests an artifact.

## Sync

1. Require the reviewed Audit output and list the accepted findings.
2. Before editing KDoc, show the exact declarations and proposed contract
   changes. Apply them only after approval.
3. Update MkDocs first, then shorten READMEs to entry points.
4. Keep all public documentation in English and aligned with current `master`.
5. Use `<latest-version>` in dependency snippets. Use badges to expose current
   Git and Maven versions.
6. Preserve one changelog page per Git tag, including RC tags. Keep the index
   newest first.
7. Keep generated Dokka HTML and `site/` untracked.
8. Run:

   ```bash
   ./gradlew ciDocs
   python -m mkdocs build --strict
   python .codex/skills/project-docs/scripts/audit_docs.py --require-generated-api
   git diff --check
   ```

9. Fail the sync if validation or the final audit reports documentation drift.

## Documentation Contract

- MkDocs is the public source of truth.
- READMEs contain only purpose, artifact/targets, minimal installation, and
  links.
- Module pages cover purpose, use/non-use, installation, targets, behavior,
  real examples, architecture, limitations, troubleshooting, and Dokka.
- Manual snippets must match current declarations or sample usage.
- Public KDoc explains non-obvious contracts. Internal/private KDoc is reserved
  for complex rules.
- Dokka warnings and unresolved references are errors.
