#!/bin/bash

output="../git-tag"
message=$(git show -s --format=%B)
parents=$(git show -s --format=%P | wc -w)

# Verify if the commit is a merge
merge_message=$(grep -E '(merge|Merge) pull request' <<< "$message" || true)
if [ -z "$merge_message" ]; then
    echo -e "MUST BE A MERGE MERGE!"
    echo "$message"
    exit 1
fi

# Verify if the commit is a merge following the pattern release/0.0.0 or hotfix/0.0.0
matched_message=$(grep -E '(release|hotfix)\/([0-9]+.[0-9]+.[0-9]+)' <<< "$message" || true)
if [ -z "$matched_message" ]; then
    echo -e "Are you sure that you created a proper branch name to merge with master?"
    echo "$message"
    exit 1
fi

# Get version in message
version=$(sed -E 's?.+(release|hotfix)/([0-9]+.[0-9]+.[0-9]+)?\2?g' <<< "$matched_message" | head -n1)
echo "Tag: $version"

# Create and push tag
git tag -a "$version" -m "$version"
git push -u origin "$version"