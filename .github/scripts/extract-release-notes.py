"""Extract one exact version section from the repository changelog."""
from pathlib import Path
import sys


def extract(changelog: str, version: str) -> str:
    lines = changelog.splitlines(keepends=True)
    for index, line in enumerate(lines):
        if line.strip() == f"## {version}":
            end = next(
                (i for i in range(index + 1, len(lines)) if lines[i].startswith("## ")),
                len(lines),
            )
            return "".join(lines[index:end]).strip() + "\n"
    return ""


if __name__ == "__main__":
    version, source, destination = sys.argv[1:]
    path = Path(source)
    notes = extract(path.read_text(encoding="utf-8"), version) if path.exists() else ""
    Path(destination).write_text(notes, encoding="utf-8")
