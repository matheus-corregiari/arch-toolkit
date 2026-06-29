#!/usr/bin/env bash
set -euo pipefail

branch="${1:-}"

if [[ -z "$branch" ]]; then
  echo "Usage: $0 <release-or-hotfix-branch>" >&2
  exit 2
fi

if [[ "$branch" =~ ^release/([0-9]+)\.([0-9]+)\.0(-rc[0-9]+)?$ ]]; then
  version="${branch#release/}"
elif [[ "$branch" =~ ^hotfix/([0-9]+)\.([0-9]+)\.([1-9][0-9]*)(-rc[0-9]+)?$ ]]; then
  version="${branch#hotfix/}"
else
  echo "Invalid release branch: $branch" >&2
  echo "Expected release/x.y.0[-rcN] or hotfix/x.y.z[-rcN] where z >= 1." >&2
  exit 1
fi

if git rev-parse "refs/tags/$version" >/dev/null 2>&1; then
  tag_commit="$(git rev-list -n 1 "$version")"
  head_commit="$(git rev-parse HEAD)"

  if [[ "$tag_commit" != "$head_commit" ]]; then
    echo "Tag already exists on another commit: $version" >&2
    exit 1
  fi

  echo "Reusing tag on current commit: $version"
fi

prerelease=false
if [[ "$version" =~ -rc[0-9]+$ ]]; then
  prerelease=true
fi

echo "version=$version"
echo "prerelease=$prerelease"

if [[ -n "${GITHUB_OUTPUT:-}" ]]; then
  {
    echo "version=$version"
    echo "prerelease=$prerelease"
  } >> "$GITHUB_OUTPUT"
fi
