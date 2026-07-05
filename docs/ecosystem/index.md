# Arch Ecosystem

Arch is a set of focused Kotlin libraries that can be used independently and still fit together as
one ecosystem.

`arch-toolkit` is the hub:

- it owns shared standards;
- it hosts the central sample experience;
- it incubates libraries until they are stable enough to become independent repositories;
- it links library-specific docs, API references, and sample flows.

## Repository Roles

| Repository | Role |
|:-----------|:-----|
| `arch-toolkit` | Ecosystem hub, samples, shared standards, and incubating libraries. |
| `arch-android` | Android-specific architecture utilities. |
| `arch-event-observer` | Event, result, LiveData, Flow, and Compose observation APIs. |
| `arch-lumber` | Multiplatform logging. |

## Operating Model

Each repository releases independently. There is no global ecosystem version.

The hub keeps the public contract:

- [Gitflow](gitflow.md)
- [CI and Release](ci-release.md)
- central samples
- compatibility notes
- links to every library

Library repositories should link back to this hub instead of duplicating ecosystem rules.
