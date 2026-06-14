from __future__ import annotations

import re
import subprocess
from dataclasses import dataclass
from pathlib import Path


ROOT = Path(__file__).resolve().parents[4]
CHANGELOG_DIR = ROOT / "docs" / "changelog"
VERSION_RE = re.compile(r"^\d+\.\d+\.\d+(?:-[0-9A-Za-z.-]+)?$")


@dataclass(frozen=True)
class Release:
    version: str
    previous: str | None


def git(*args: str) -> str:
    result = subprocess.run(
        ["git", *args],
        cwd=ROOT,
        check=True,
        text=True,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
    )
    return result.stdout.strip()


def tags() -> list[str]:
    return [
        tag
        for tag in git("tag", "--sort=v:refname").splitlines()
        if VERSION_RE.fullmatch(tag)
    ]


def tag_file(tag: str, path: str) -> str | None:
    result = subprocess.run(
        ["git", "show", f"{tag}:{path}"],
        cwd=ROOT,
        check=False,
        text=True,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
    )
    return result.stdout if result.returncode == 0 else None


def catalog_value(contents: str | None, key: str) -> str:
    if contents is None:
        return "unknown"
    match = re.search(rf'^{re.escape(key)}\s*=\s*"([^"]+)"', contents, re.MULTILINE)
    return match.group(1) if match else "unknown"


def release_date(tag: str) -> str:
    return git("log", "-1", "--format=%cs", tag)


def commits(release: Release) -> list[str]:
    revision = release.version if release.previous is None else f"{release.previous}..{release.version}"
    return [line for line in git("log", "--format=%s", revision).splitlines() if line]


def changed_files(release: Release) -> list[str]:
    if release.previous is None:
        output = git("ls-tree", "-r", "--name-only", release.version)
    else:
        output = git("diff", "--name-only", f"{release.previous}..{release.version}")
    return [line for line in output.splitlines() if line]


def notable_changes(files: list[str], subjects: list[str]) -> list[str]:
    notes: list[str] = []
    file_set = set(files)

    if "gradle/wrapper/gradle-wrapper.properties" in file_set:
        notes.append("upgraded the Gradle wrapper")
    if "gradle/libs.versions.toml" in file_set:
        notes.append("updated build and library dependency versions")
    if any(path.startswith("toolkit/") for path in files):
        notes.append("updated toolkit modules and tests")
    if "README.md" in file_set:
        notes.append("refreshed project usage and compatibility documentation")
    if not notes:
        notes.extend(subject[0].lower() + subject[1:] for subject in subjects[:5] if subject)

    return notes or ["documented the release contents"]


def dependencies(tag: str) -> dict[str, str]:
    catalog = tag_file(tag, "gradle/libs.versions.toml")
    wrapper = tag_file(tag, "gradle/wrapper/gradle-wrapper.properties")
    gradle_match = re.search(r"gradle-([^-]+)-bin\.zip", wrapper or "")
    return {
        "Kotlin": catalog_value(catalog, "jetbrains-kotlin"),
        "Gradle wrapper": gradle_match.group(1) if gradle_match else "unknown",
        "Android plugin": catalog_value(catalog, "androidx-plugin"),
        "Dokka": catalog_value(catalog, "jetbrains-dokka"),
        "Detekt": catalog_value(catalog, "detekt"),
        "ktlint": catalog_value(catalog, "ktlint"),
        "Compose": catalog_value(catalog, "jetbrains-compose"),
        "minSdk": catalog_value(catalog, "build-sdk-min-toolkit"),
        "compileSdk": catalog_value(catalog, "build-sdk-compile"),
    }


def tag_matches(tag: str, pattern: str) -> bool:
    result = subprocess.run(
        ["git", "grep", "-q", "-E", pattern, tag, "--", "*.gradle", "*.gradle.kts", "*.kt"],
        cwd=ROOT,
        check=False,
        stdout=subprocess.DEVNULL,
        stderr=subprocess.DEVNULL,
    )
    return result.returncode == 0


def target_rows(tag: str, deps: dict[str, str]) -> str:
    rows: list[str] = []
    if tag_matches(tag, r"com\.android\.|androidTarget|withAndroid"):
        sdk = (
            f", `minSdk {deps['minSdk']}`, `compileSdk {deps['compileSdk']}`"
            if deps["minSdk"] != "unknown"
            else ""
        )
        rows.append(f"| Android | Declared in release sources{sdk} |")
    if tag_matches(tag, r"jvm[[:space:]]*[({]"):
        rows.append("| JVM | Declared in release sources |")
    if tag_matches(tag, r"iosArm64|iosX64|iosSimulatorArm64|macos"):
        rows.append("| Apple | Declared in release sources |")
    if tag_matches(tag, r"(^|[[:space:]])js[[:space:]]*\("):
        rows.append("| JS | Declared in release sources |")
    if tag_matches(tag, r"wasmJs"):
        rows.append("| WasmJS | Declared in release sources |")

    return "\n".join(rows) or "| Not recorded | No target declaration was inferred |"


def page(release: Release) -> str:
    files = changed_files(release)
    deps = dependencies(release.version)
    notes = "\n".join(f"- {note}" for note in notable_changes(files, commits(release)))
    rows = "\n".join(f"| {name} | `{version}` |" for name, version in deps.items())
    targets = target_rows(release.version, deps)

    return f"""# Changelog - {release.version}

**Release Date:** {release_date(release.version)}

## Motivation

Document the build, dependency, and toolkit changes shipped in {release.version}.

## Notable changes

{notes}

## Compatibility notes

- No migration requirement was identified from the release range.

## Dependency versions

| Area | Version |
|:-----|:--------|
{rows}

## Target compatibility

| Target | Support in {release.version} |
|:-------|:-----------------------------|
{targets}
"""


def rewrite_index(all_tags: list[str]) -> None:
    links = "\n".join(f"- [{tag}]({tag}.md)" for tag in reversed(all_tags))
    (CHANGELOG_DIR / "index.md").write_text(
        f"""# Changelog

This directory keeps one factual page per released version.

## Releases

{links}
""",
        encoding="utf-8",
    )


def main() -> None:
    all_tags = tags()
    releases = [
        Release(tag, all_tags[index - 1] if index else None)
        for index, tag in enumerate(all_tags)
    ]

    CHANGELOG_DIR.mkdir(parents=True, exist_ok=True)
    for release in releases:
        output = CHANGELOG_DIR / f"{release.version}.md"
        output.write_text(page(release), encoding="utf-8")
        print(f"updated {output.relative_to(ROOT)}")

    rewrite_index(all_tags)
    print(f"updated {(CHANGELOG_DIR / 'index.md').relative_to(ROOT)}")


if __name__ == "__main__":
    main()
