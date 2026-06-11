# Core Concepts

## Small Modules

Each artifact owns one concern. Consumers install only the modules they use.

## Common Contracts

Storage providers implement the contracts from `storage-core`, so production and
test providers can be exchanged without changing callers.

## Reactive Values

Stored values expose Kotlin Flow-based observation while retaining immediate
read and write operations.

## Platform Fallbacks

When a dependency is unavailable on web targets, the module keeps the common API
compilable and documents the no-op behavior.

## One Release Train

All artifacts use the same Arch Toolkit version. This keeps inter-module
dependencies predictable.
