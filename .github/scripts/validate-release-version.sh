#!/usr/bin/env bash
set -euo pipefail

version="${1:-}"
if [[ -z "$version" ]]; then
  echo "Usage: $0 <version>" >&2
  exit 2
fi

if [[ ! "$version" =~ ^[0-9]+\.[0-9]+\.[0-9]+(-rc[0-9]+)?$ ]]; then
  echo "Invalid SemVer release: $version" >&2
  exit 2
fi

git fetch --quiet --tags origin
tag_ref="refs/tags/$version"
if git rev-parse "$tag_ref" >/dev/null 2>&1; then
  if [[ "$(git rev-parse "$tag_ref^{}")" == "$(git rev-parse HEAD)" ]]; then
    echo "Release tag $version already points to HEAD; retry is safe."
    exit 0
  fi
  echo "Release tag $version already exists on another commit." >&2
  exit 1
fi

semver_gt() {
  local left="$1" right="$2"
  local left_major left_minor left_patch left_rc right_major right_minor right_patch right_rc
  if [[ "$left" =~ ^([0-9]+)\.([0-9]+)\.([0-9]+)(-rc([0-9]+))?$ ]]; then
    left_major=${BASH_REMATCH[1]}; left_minor=${BASH_REMATCH[2]}; left_patch=${BASH_REMATCH[3]}; left_rc=${BASH_REMATCH[5]:-}
  else
    return 1
  fi
  if [[ "$right" =~ ^([0-9]+)\.([0-9]+)\.([0-9]+)(-rc([0-9]+))?$ ]]; then
    right_major=${BASH_REMATCH[1]}; right_minor=${BASH_REMATCH[2]}; right_patch=${BASH_REMATCH[3]}; right_rc=${BASH_REMATCH[5]:-}
  else
    return 1
  fi
  ((10#$left_major > 10#$right_major)) && return 0
  ((10#$left_major < 10#$right_major)) && return 1
  ((10#$left_minor > 10#$right_minor)) && return 0
  ((10#$left_minor < 10#$right_minor)) && return 1
  ((10#$left_patch > 10#$right_patch)) && return 0
  ((10#$left_patch < 10#$right_patch)) && return 1
  [[ -n "$right_rc" && -z "$left_rc" ]] && return 0
  [[ -z "$right_rc" || -z "$left_rc" ]] && return 1
  ((10#$left_rc > 10#$right_rc))
}

while IFS= read -r existing; do
  if semver_gt "$existing" "$version"; then
    echo "Release version $version is older than existing tag $existing." >&2
    exit 1
  fi
done < <(git tag --list)

echo "Release version $version is newer than existing tags."
