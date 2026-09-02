#!/usr/bin/env bash
set -euo pipefail

version="${1:-}"
if [[ -z "$version" ]]; then
  echo "Usage: $0 <version>" >&2
  exit 2
fi

git fetch --quiet --tags origin
latest="$(git tag --list | sort -V | tail -n 1)"
if [[ -n "$latest" ]] && [[ "$(printf '%s\n' "$latest" "$version" | sort -V | tail -n 1)" != "$version" || "$latest" == "$version" ]]; then
  echo "Release version $version is not newer than existing tag $latest." >&2
  exit 1
fi

echo "Release version $version is newer than existing tags (latest: ${latest:-none})."
