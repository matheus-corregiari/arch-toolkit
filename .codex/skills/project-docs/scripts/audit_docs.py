#!/usr/bin/env python3
"""Read-only documentation audit for Arch Toolkit release preparation."""

from __future__ import annotations

import argparse
import re
import subprocess
import sys
from pathlib import Path


MODULES = (
    "storage-core",
    "storage-memory",
    "storage-datastore",
    "state-handle",
    "splinter",
)

REQUIRED_DOCS = (
    "docs/index.md",
    "docs/getting-started.md",
    "docs/core-concepts.md",
    "docs/recipes.md",
    "docs/troubleshooting.md",
    "docs/using/choosing-modules.md",
    "docs/using/compatibility.md",
    "docs/using/migration.md",
    "docs/sample/index.md",
    "docs/sample/architecture.md",
    "docs/sample/running.md",
    "docs/sample/flows.md",
    "docs/sample/limitations.md",
    "docs/contributing/architecture.md",
    "docs/contributing/development.md",
    "docs/contributing/documentation.md",
    "docs/contributing/dependencies.md",
    "docs/contributing/release.md",
    "docs/api/index.md",
    "docs/changelog/index.md",
)

MODULE_HEADINGS = (
    "## Purpose",
    "## Use It When",
    "## Installation",
    "## Targets",
    "## Concepts and Behavior",
    "## Example",
    "## Architecture",
    "## Known Limitations",
    "## Troubleshooting",
)


def git(root: Path, *args: str) -> list[str]:
    result = subprocess.run(
        ["git", *args],
        cwd=root,
        check=True,
        capture_output=True,
        text=True,
    )
    return [line for line in result.stdout.splitlines() if line]


def relative_files(root: Path, patterns: tuple[str, ...]) -> list[Path]:
    files: list[Path] = []
    for pattern in patterns:
        files.extend(path for path in root.glob(pattern) if path.is_file())
    return sorted(set(files))


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--require-generated-api", action="store_true")
    args = parser.parse_args()

    root = Path(__file__).resolve().parents[4]
    findings: list[str] = []

    tags = git(root, "tag", "--sort=-version:refname")
    latest = tags[0] if tags else None
    previous = tags[1] if len(tags) > 1 else None
    branch = git(root, "branch", "--show-current")
    branch_name = branch[0] if branch else "(detached)"

    print(f"Branch: {branch_name}")
    print(f"Latest tag: {latest or '(none)'}")
    print(f"Previous tag: {previous or '(none)'}")

    if latest:
        changed = git(root, "diff", "--name-only", f"{latest}...HEAD")
        print(f"Changed since {latest}: {len(changed)} files")
        areas = {
            "api": any("/src/" in path and path.endswith(".kt") for path in changed),
            "build": any(
                path.endswith((".gradle.kts", ".toml"))
                or path.startswith("build-logic/")
                for path in changed
            ),
            "workflows": any(path.startswith(".github/") for path in changed),
            "samples": any(path.startswith("sample/") for path in changed),
            "docs": any(
                path.startswith("docs/") or path.endswith("README.md")
                for path in changed
            ),
        }
        print(
            "Changed areas: "
            + ", ".join(name for name, changed_area in areas.items() if changed_area)
        )

    for path in REQUIRED_DOCS:
        if not (root / path).is_file():
            findings.append(f"missing required page: {path}")

    for module in MODULES:
        page = root / "docs" / "modules" / f"{module}.md"
        readme = root / "toolkit" / "multi" / module / "README.md"
        if not page.is_file():
            findings.append(f"missing module page: {page.relative_to(root)}")
            continue
        content = page.read_text(encoding="utf-8")
        for heading in MODULE_HEADINGS:
            if heading not in content:
                findings.append(f"{page.relative_to(root)} missing heading: {heading}")
        if f"api/{module}/html/index.html" not in content:
            findings.append(f"{page.relative_to(root)} missing Dokka link")
        if not readme.is_file():
            findings.append(f"missing module README: {readme.relative_to(root)}")
        else:
            readme_content = readme.read_text(encoding="utf-8")
            if "<latest-version>" not in readme_content:
                findings.append(f"{readme.relative_to(root)} lacks <latest-version>")
            if "/api/" not in readme_content or "/modules/" not in readme_content:
                findings.append(f"{readme.relative_to(root)} lacks public docs links")

        if args.require_generated_api:
            api_index = root / "docs" / "api" / module / "html" / "index.html"
            if not api_index.is_file():
                findings.append(f"missing generated API: {api_index.relative_to(root)}")

    tags_without_docs = [
        tag for tag in tags if not (root / "docs" / "changelog" / f"{tag}.md").is_file()
    ]
    for tag in tags_without_docs:
        findings.append(f"missing changelog page for tag: {tag}")

    current_docs = relative_files(
        root,
        ("README.md", "CONTRIBUTING.md", "docs/**/*.md", "toolkit/**/README.md"),
    )
    version_pattern = re.compile(
        r"io\.github\.matheus-corregiari:[a-z0-9-]+:(?!<latest-version>)[0-9]"
    )
    for path in current_docs:
        if "docs/changelog" in path.as_posix():
            continue
        if version_pattern.search(path.read_text(encoding="utf-8")):
            findings.append(
                f"hardcoded Arch Toolkit dependency version: {path.relative_to(root)}"
            )

    print("\nFindings:")
    if findings:
        for finding in findings:
            print(f"- {finding}")
        return 1

    print("- none")
    return 0


if __name__ == "__main__":
    sys.exit(main())
