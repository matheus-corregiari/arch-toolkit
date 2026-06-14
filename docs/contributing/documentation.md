# Documentation

## Source of Truth

MkDocs is the main public documentation and represents current `master`.
READMEs are short entry points. Dokka is the per-module API reference. Changelog
pages preserve release history, but complete historical sites are not
maintained.

## Page Responsibilities

- `docs/using`: installation, selection, compatibility, and migration.
- `docs/modules`: behavior and decisions for each published artifact.
- `docs/sample`: sample architecture, execution, flows, and limitations.
- `docs/contributing`: repository maintenance and release process.
- `docs/api`: tracked API index plus ignored generated Dokka HTML.
- `docs/changelog`: one page per tag, newest first in the index.

## Module Page Template

Each module page must cover purpose, when to use, when not to use, installation,
targets, concepts, real examples, architecture, limitations, troubleshooting,
and the module Dokka link.

## Snippets

Markdown snippets are maintained manually. Compare them with current APIs and
sample usage during every release documentation sync. Use `<latest-version>`
instead of hardcoding a release.

## KDoc

- Document public classes with purpose, contract, and an example when useful.
- Document public functions when behavior is not evident from the declaration.
- Document internal or private code only when its rules are complex.
- Do not repeat declaration names as prose.
- Resolve every KDoc reference; Dokka warnings fail documentation validation.

## Local Validation

```bash
./gradlew ciDocs
python -m mkdocs build --strict
```

Use the repository `project-docs` skill in Audit mode during release
preparation, review its report, then run Sync mode if changes are required.
