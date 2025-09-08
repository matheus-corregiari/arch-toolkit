#!/bin/bash

message=$(git show -s --format=%B)

# Verify merge commit or squash
merge_message=$(grep -Ei '(merge pull request|release/|hotfix/)' <<< "$message" || true)
if [ -z "$merge_message" ]; then
    echo "Commit is not a merge or squash merge from release/hotfix branch"
    echo "$message"
    exit 1
fi

# Extract version (supports X.Y.Z and X.Y.Z-rcNN)
version=$(grep -oE '(release|hotfix)/[0-9]+\.[0-9]+\.[0-9]+(-rc[0-9]+)?' <<< "$message" \
          | sed -E 's/^(release|hotfix)\///' \
          | head -n1)

if [ -z "$version" ]; then
    echo "Could not extract version from commit message:"
    echo "$message"
    exit 1
fi

echo "Message: $merge_message"
echo "Tag: $version"

# Create and push tag
git tag -a "$version" -m "$version"
git push -u origin "$version"